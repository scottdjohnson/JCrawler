package org.scottdjohnson.jcrawler;

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
import java.util.Set;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Date;
import java.sql.Timestamp;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonArrayBuilder;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.scottdjohnson.node.URLNode;
import org.scottdjohnson.database.UrlNodeDao;
import org.scottdjohnson.database.DaoFactory;

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
	 * @param uq The queue for holding the scanned URLs, to assure that they are scanned in order and only once per URL
	 * @param sb The session bundle for saving URLs to the database
	 */
        public static void crawlLinksFromUrl( UniqueMemQueue<URLNode,String> uq, UrlNodeDao urlNodeDao)
	{		
		// While the queue is not empty, grab the head of the queue, check its children and add them to the end
		// of the queue for later processing in the loop
		// This produces a breadth-first-search, which provides better results when displaying the scanned URLs
		while(!uq.isEmpty())
		{

			try {
				// Get the URL at the head of the queue
				URLNode unParent 	= uq.remove();
				Document doc    	= Jsoup.connect(unParent.getUrl()).get();
				Elements links  	= doc.select("a[href]");
				doc = null;

				// Look at each URL within the HTML page produced by the current URL
				for (Element link : links)
				{
					String strLink 		= link.attr("abs:href");
					String strLinkName 	= link.text();
					URI u           	= new URI(link.attr("href"));

					// If the value is not new to the queue, and not absolute, add it
					// We currently ignore absolute URLs assuming they are external
					if(!uq.containsValue(strLink) && !u.isAbsolute())
					{
						URLNode un = new URLNode(strLink, strLinkName, new Timestamp(new Date().getTime()), unParent.getKey());

						logger.log(Level.INFO, "Adding URL: " + un.getUrl());
						urlNodeDao.save(un);

						// Add this URL to the queue, process later in the loop
						uq.add(un, un.getUrl());
					}
				}

				logger.log(Level.INFO, "Queue size: " + uq.size());

			}
			catch(IOException e)
			{
				logger.log(Level.WARNING, "Could not connect to this URL; " + e.getMessage());
			}
			catch(URISyntaxException e)
			{
				logger.log(Level.WARNING, "This is not a valid URL; " + e.getMessage());
			}
		}
	}

	/**
	* Delete all the URLs currently stored in the database, including children
	* 
	**/
	public static void deleteAllUrls()
	{
		UrlNodeDao urlNodeDao = (UrlNodeDao)DaoFactory.getDao(DaoFactory.DaoType.URLNODE);
		urlNodeDao.deleteFromQuery("from URLNode url_list");
		urlNodeDao.close();
	}

	/**
	* Get all objects with this as the parent. Expects DBConnector to already be open
	* 
	* @param urlKey The key whose children this function returns
	**/
	public static List getChildren(Integer urlKey)
	{
		List list = null;
		UrlNodeDao urlNodeDao = (UrlNodeDao)DaoFactory.getDao(DaoFactory.DaoType.URLNODE);

		try
		{
                	// If there is a specifc key, get it, otherwise get all of them
                	if (null != urlKey)
                	        list = urlNodeDao.getFromQuery("from URLNode url_list where parent_key = " + urlKey);
                	else
                	        list = urlNodeDao.getFromQuery("from URLNode url_list where parent_key = 0");
		}
		catch (Exception e)
		{
			logger.log(Level.WARNING, e.getMessage());
		}
		finally
		{
			urlNodeDao.close();
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
		UrlNodeDao urlNodeDao = (UrlNodeDao)DaoFactory.getDao(DaoFactory.DaoType.URLNODE);

		List<URLNode> list = getChildren(urlKey);
		Iterator<URLNode> iterator = list.iterator();

		JsonObjectBuilder jsonBuilder 		= Json.createObjectBuilder();
		JsonArrayBuilder jsonArrayBuilder 	= Json.createArrayBuilder();

		// Loop through all the results from the query
		while (iterator.hasNext())
		{
			URLNode urlNode = iterator.next();
			int currentKey = (int)urlNode.getKey();

			// Count the total number of results that have this URL as a parent
			int count =(int) ((urlNodeDao.getFromQuery("select count(*) from URLNode where parent_key="
				+ currentKey )).get(0));
			logger.log(Level.INFO, "Count query retrieved.");

			JsonObjectBuilder jsonArrayItem = Json.createObjectBuilder();
			jsonArrayItem.add("URL", urlNode.getUrl());
			jsonArrayItem.add("name", StringEscapeUtils.escapeJson(urlNode.getName()));
			jsonArrayItem.add("timeCrawled", urlNode.getTimeCrawled().toString());
			jsonArrayItem.add("count", count);
			jsonArrayItem.add("key", currentKey );

			jsonArrayBuilder.add(jsonArrayItem.build());
		}

		jsonBuilder.add("URLs", jsonArrayBuilder.build());

		// This will fail if the database is reorganized to support multiple parents
		List<URLNode> l = (urlNodeDao.getFromQuery("from URLNode url_list where url_key=" + urlKey));

		urlNodeDao.close();

		if (null != l && l.size() == 1)
		{
			int parentKey = (int)l.get(0).getParentKey();
			jsonBuilder.add("parent", parentKey);
		}

		JsonObject json = jsonBuilder.build();
		logger.log(Level.INFO, "JSON from JsonBuilder: " + json);
		out.println(json);

		// Don't close or we might close System.out!
		out.flush();
	}

	/**
	* Add the URL and its children to the database
	*
	* @param url The URL to crawl and store in the database
	**/
	public static long addUrl(String url)
	{
		UniqueMemQueue<URLNode,String> uq 	= new UniqueMemQueue<URLNode,String>();

		long key                        = 0; // Fail safe: return the top level item
		URLNode un 			= new URLNode(url, url, new Timestamp(new Date().getTime()), key);

		UrlNodeDao urlNodeDao = (UrlNodeDao)DaoFactory.getDao(DaoFactory.DaoType.URLNODE);

		try
		{
			logger.log(Level.INFO,"Putting URL into Map...");
			uq.add(un, un.getUrl());

			logger.log(Level.INFO, "Saving URL: " + url);
			urlNodeDao.save(un);

			key = un.getKey();
			crawlLinksFromUrl(uq, urlNodeDao);
		}
		catch (Exception e)
		{
			logger.log(Level.INFO, e.getMessage());
		}
		finally
		{
			urlNodeDao.close();
		}

		return key;
	}

	/**
	*	Queue that does not allow the same item to be requeued after it has been queued up once.
	*	The generic values E and V represent elements and values. The element is the basic object type to be stored, while the value
	*	should be a smaller object, like a String, to minimize memory overhead. The V value is used to determine the uniqueness
	*	of the object being added to the Queue.
	**/
	private static class UniqueMemQueue<E,V>
	{
		Set<V> set;
		Queue<E> que;

		/**
		* Constructor, private to assure only this class can construct it
		**/
		private UniqueMemQueue()
		{
			set = new HashSet<V>();
			que = new LinkedList<E>();
		}

		/**
		* Add the object to the queue
		*
		* @param e The object to add to the queue
		* @param v The value to check for uniquenes. If this value has been previously added, e will not be added.
		* @return true if the operation succeeds, otherwise false.
		**/
		public boolean add(E e, V v)
		{
			if (!set.contains(v))
			{
				que.add(e);
				set.add(v);
			}

			return true;
		}

		/**
		* Remove the object at the head of the queue and return it.
		* @return The object from the head of the queue.
		**/
		public E remove()
		{
			return que.remove();
		}

		/**
		* Determine whether the queue is currently empty
		* @return true if empty, otherwise false
		**/
		public boolean isEmpty()
		{
			return que.isEmpty();
		}

		/**
		* Determine whether the queue has ever contained the value v
		* @param v The value to determine whether the object has been passed into the queue previously
		* @return true if the value has been contained in the queue, otherwise false.
		**/
		public boolean containsValue(V v)
		{
			return set.contains(v);
		}

		/**
		* The current size of the queue of objects still held in the queue.
		* @return The number of objects currently in the queue.
		**/
		public int size()
		{
			return que.size();
		}
	}


}
