package scottdjohnson.jcrawler;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import java.util.logging.Level;
import java.util.logging.Logger;

import scottdjohnson.node.URLNode;
import scottdjohnson.database.SessionBundle;
import scottdjohnson.database.SessionBundleFactory;

/**
 * JCrawler provides the basic functionlity for crawling a Web URL.
 * 
 * @author Scott Johnson
 **/
public class JCrawler 
{
	private static final Logger logger = Logger.getLogger(JCrawler.class.getPackage().getName());	
	/**
	 * Crawl a URL and store it and its children in a database
	 * Absolute URLs are assumed to be external to the site, relative
	 * URLs are considered internal and are also crawled recursively
	 * The hashMap assures that we do not crawl a URL more than once
	 * otherwise we will likely end up in an ifinite loop
	 * 
	 * @param map A map which stores the URLs that we have crawled during this recursion, in order to avoid re-crawling them
	 * @param url The URL to crawl
	 * @param parent The parent key in the database for this URL
	 */
        public static void crawlLinksFromUrl( Map<String,URLNode> map, String url, long parent, SessionBundle sb)
	{		
		try
		{
			Document doc 	= Jsoup.connect(url).get();
			Elements links 	= doc.select("a[href]");
		
			// First we store all the URLs at the current level
			for (Element link : links)
			{
				// We are going to assume that an absolute URL points to an external
				//	external web site. Not an entirely safe assumption but ok for this demo
				
				URLNode un 	= new URLNode( link.attr("abs:href"), parent);
				URI u		= new URI(link.attr("href"));

				logger.log(Level.INFO,"Getting link - name: " + link.text() + " url: " + link.attr("href"));				
				// if link is not already in tree, store it and recurse	
				if ( !map.containsKey( un.getUrl() ) )
				{
					map.put(un.getUrl(), un);
					sb.save( un);
					logger.log(Level.INFO, "Inserting URL: " + link.attr("href"));

					// Don't recurse absolute URLs, assume they are external
					if (!u.isAbsolute())
						crawlLinksFromUrl( map, un.getUrl(), un.getKey(), sb );
				}
			}				
		}
		catch(URISyntaxException e)
		{
			logger.log(Level.WARNING, "This is not a valid URL; " + e.getMessage());
		}
		catch(IOException e)
		{
			logger.log(Level.WARNING, "Could not connect to this URL; " + e.getMessage());

		}

	}

	/**
	* Delete all the URLs currently stored in the database, including children
	* 
	**/
	public static void deleteAllUrls()
	{
		SessionBundle sb = SessionBundleFactory.getNewSession();
		sb.deleteFromQuery("from URLNode url_list");
		sb.close();
	}

	/**
	* Delete URL from key, and its children
	*
	* @param urlKey The key of the URL to delete
	**/
/*
	public static void deleteUrlAndChildren(Integer urlKey)
	{
		DBConnector.open();
		deleteUrlAndChildrenRecurse(urlKey);
                DBConnector.close();
	}
*/
        /**
        * Recursing function to delete a URL and its children
        *
        * @param urlKey The key of the URL to delete
        **/
/*
	public static void deleteUrlAndChildrenRecurse(Integer urlKey)
	{
                // Get list of children with this as parent
                List<URLNode> list = getChildren(urlKey);

                // Recurse on children
                if (null != list)
                        for (int i = 0; i < list.size(); i++)
                                deleteUrlAndChildrenRecurse( (int)(list.get(i)).getKey() );

                DBConnector.deleteFromQuery("from URLNode url_list where url_key=" + urlKey);
	}
*/
	/**
	* Get all objects with this as the parent. Expects DBConnector to already be open
	* 
	* @param urlKey The key whose children this function returns
	**/
	public static List getChildren(Integer urlKey, SessionBundle sb)
	{
		List list = null;

		try
		{
                	// If there is a specifc key, get it, otherwise get all of them
                	if (null != urlKey)
                	        list = sb.getFromQuery("from URLNode url_list where parent_key = " + urlKey);
                	else
                	        list = sb.getFromQuery("from URLNode url_list where parent_key = 0");
		}
		catch (Exception e)
		{
			logger.log(Level.WARNING, e.getMessage());
		}

		return list;
	}

	/**
	* Get the URL in the database, and its children, designated by the key, write results to out
	*
	* @param urlKey The key of the URL to get from the database
	* @param out The Writer to write the results to
	**/
	public static void getUrls(Integer urlKey, PrintWriter out)
	{
		SessionBundle sb = SessionBundleFactory.getNewSession();

		logger.log(Level.INFO, "SessionBundle created.");

		List<URLNode> list = getChildren(urlKey, sb);
		Iterator<URLNode> iterator = list.iterator();

		out.println("{\"URLs\":[");

		// Loop through all the results from the query
		while (iterator.hasNext())
		{
			URLNode urlNode = iterator.next();
			int currentKey = (int)urlNode.getKey();

			// Count the total number of results that have this URL as a parent
			int count =(int) ((sb.getFromQuery("select count(*) from URLNode where parent_key="
				+ currentKey )).get(0));
			logger.log(Level.INFO, "Count query retrieved.");

			// Output JSON
			out.println("{");
			out.println("\"URL\": \"" + urlNode.getUrl() + "\",");
			out.println("\"count\": " + count + ",");
			out.println("\"key\":" + currentKey );

			// Include comma unless this is the last item of the array			
			if(iterator.hasNext())
				out.println("},");
			else
				out.println("}");
		}

		out.println("]");

		// This will fail if the database is reorganized to support multiple parents
		List<URLNode> l = (sb.getFromQuery("from URLNode url_list where url_key=" + urlKey));
		if (null != l && l.size() == 1)
		{
			int parentKey = (int)l.get(0).getParentKey();
			out.println(",\n\"parent\": \"" + parentKey +  "\"");
		}
		out.println("}");

		// Don't close or we might close System.out!
		out.flush();
		sb.close();
	}

	/**
	* Add the URL and its children to the database
	*
	* @param url The URL to crawl and store in the database
	**/
	public static long addUrl(String url)
	{
		Map<String,URLNode> map = new HashMap<String,URLNode>();
		URLNode un 			= new URLNode(url);
		long key			= 0; // Fail safe: return the top level item

		try
		{
			logger.log(Level.INFO, "Opening database connection...");
			SessionBundle sb = SessionBundleFactory.getNewSession();

			logger.log(Level.INFO,"Putting URL into Map...");
			map.put(un.getUrl(),un);

			logger.log(Level.INFO, "Saving URL: " + url);
			sb.save(un);

			key = un.getKey();		
			JCrawler.crawlLinksFromUrl(map, url, key, sb);
			sb.close();
		}
		catch (Exception e)
		{
			logger.log(Level.INFO, e.getMessage());
		}

		return key;
	}
}
