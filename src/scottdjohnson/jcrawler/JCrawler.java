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

import java.util.logging.Level;
import java.util.logging.Logger;

import scottdjohnson.node.URLNode;
import scottdjohnson.database.DBConnector;

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
	 * @param hashMap A HashMap which stores the URLs that we have crawled during this recursion, in order to avoid re-crawling them
	 * @param url The URL to crawl
	 * @param parent The parent key in the database for this URL
	 */
        public static void crawlLinksFromUrl( HashMap<String,URLNode> hashMap, String url, long parent)
	{		
		try
		{
			Document doc 	= Jsoup.connect(url).get();
			Elements links 	= doc.select("a[href]");
			int size	= links.size();
		
			// First we store all the URLs at the current level
			for (int i = 0; i < size; i++)
			{
				Element link = links.get(i);
				
				// We are going to assume that an absolute URL points to an external
				//	external web site. Not an entirely safe assumption but ok for this demo
				
				URLNode un 	= new URLNode( link.attr("abs:href"), parent);
				URI u		= new URI(link.attr("href"));
				
				// if link is not already in tree, store it and recurse	
				if ( !hashMap.containsKey( un.getUrl() ) )
				{
					hashMap.put(un.getUrl(), un);
					DBConnector.save( un );
					logger.log(Level.INFO, "Inserting URL: " + link.attr("href"));

					// Don't recurse absolute URLs, assume they are external
					if (!u.isAbsolute())
						crawlLinksFromUrl( hashMap, un.getUrl(), un.getKey() );
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
		DBConnector.open();
		DBConnector.deleteFromQuery("from URLNode url_list");
		DBConnector.close();
	}

	/**
	* Delete URL from key, and its children
	*
	* @param urlKey The key of the URL to delete
	**/
	public static void deleteUrlAndChildren(Integer urlKey)
	{
                DBConnector.open();
		deleteUrlAndChildrenRecurse(urlKey);
                DBConnector.close();
	}

        /**
        * Recursing function to delete a URL and its children
        *
        * @param urlKey The key of the URL to delete
        **/
	public static void deleteUrlAndChildrenRecurse(Integer urlKey)
	{
                // Get list of children with this as parent
                List list = getChildren(urlKey);

                // Recurse on children
                if (null != list)
                        for (int i = 0; i < list.size(); i++)
                                deleteUrlAndChildrenRecurse( (int)((URLNode)list.get(i)).getKey() );

                DBConnector.deleteFromQuery("from URLNode url_list where url_key=" + urlKey);
	}

	/**
	* Get all objects with this as the parent. Expects DBConnector to already be open
	* 
	* @param urlKey The key whose children this function returns
	**/
	public static List getChildren(Integer urlKey)
	{
		List list = null;

		try
		{
                	// If there is a specifc key, get it, otherwise get all of them
                	if (null != urlKey)
                	        list = DBConnector.getFromQuery("from URLNode url_list where parent_key = " + urlKey);
                	else
                	        list = DBConnector.getFromQuery("from URLNode url_list where parent_key = 0");
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
		DBConnector.open();

		List list = getChildren(urlKey);
		out.println("{\"URLs\":[");

		// Loop through all the results from the query
		for (int i = 0; i < list.size(); i++)
		{
			int currentKey = (int)((URLNode)list.get(i)).getKey();

			// Count the total number of results that have this URL as a parent
			int count =(int) ((DBConnector.getFromQuery("select count(*) from URLNode where parent_key="
				+ currentKey )).get(0));

			// Output JSON
			out.println("{");
			out.println("\"URL\": \"" + ((URLNode)list.get(i)).getUrl() + "\",");
			out.println("\"count\": " + count + ",");
			out.println("\"key\":" + currentKey );

			// Include comma unless this is the last item of the array			
			if (i < list.size() - 1)
				out.println("},");
			else
				out.println("}");
		}

		out.println("]}");

		// Don't close or we might close System.out!
		out.flush();
		DBConnector.close();		
	}

	/**
	* Add the URL and its children to the database
	*
	* @param url The URL to crawl and store in the database
	**/
	public static long addUrl(String url)
	{
		HashMap<String,URLNode> hashMap = new HashMap<String,URLNode>();
		URLNode un 			= new URLNode(url);
		long key			= 0; // Fail safe: return the top level item

		try
		{
			logger.log(Level.INFO, "Opening database connection...");
			DBConnector.open();

			logger.log(Level.INFO,"Putting URL into HashMap...");
			hashMap.put(un.getUrl(),un);

			logger.log(Level.INFO, "Saving URL: " + url);
			DBConnector.save(un);

			key = un.getKey();		
			JCrawler.crawlLinksFromUrl(hashMap, url, key);
			DBConnector.close();
		}
		catch (Exception e)
		{
			logger.log(Level.INFO, e.getMessage());
		}

		return key;
	}
}
