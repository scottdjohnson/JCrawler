package scottdjohnson.jcrawler;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;

import scottdjohnson.binarytree.URLNode;
import scottdjohnson.binarytree.BinaryTree;
import scottdjohnson.jcrawler.JCrawler;
import scottdjohnson.database.DBConnector;

import org.hibernate.cfg.Configuration;
import org.hibernate.Query;
import org.hibernate.SessionFactory;

import java.util.List;


/**
 * Provides the main function for accessing JCrawler
 *
 * @see JCrawler
 *
 * @author Scott Johnson
 **/
public class JCrawlerApp 
{	
	/**
	 * Main function for launching JCrawler
	 * @param args a URL to crawl and store in the database
	 */
	public static void main(String[] args)
	{
		System.out.println(args.length);

		if (args.length > 0)
		{
			BinaryTree bt = new BinaryTree();
			URLNode un = new URLNode(args[0]);
			DBConnector.open();

			Validate.isTrue(args.length == 1, "usage: supply url to fetch");
			String url 	= args[0];
			
			bt.insertNode(un);
			DBConnector.save(un);
			
			JCrawler.getLinksFromURL(bt, url, un.getKey());
			DBConnector.close();
		}
		else
		{

			List list;
			String urlkey = "0";//request.getParameter("urlkey");
			//PrintWriter out = response.getWriter();

			DBConnector.open();


			// If there is a specifc key, get it, otherwise get all of them
	                if (null != urlkey)
        	        	list = DBConnector.getFromQuery("from URLNode url_list where parent_key = " + urlkey);
			else
				list = DBConnector.getFromQuery("from URLNode url_list where parent_key = 0");

			// Loop through all the results from the query
			for (int i = 0; i < list.size(); i++)
			{
				// Count the total number of results that have this URL as a parent
				int count =(int) ((DBConnector.getFromQuery("select count(*) from URLNode where parent_key="
					+ ((URLNode)list.get(i)).getKey())).get(0));

				// Print out this URL with the number of its children
				System.out.println("<a href='' onclick=\"return getAJAX(" + Integer.toString((int)((URLNode)list.get(i)).getKey() ) + ");\">" 
					+ ((URLNode)list.get(i)).getUrl() + "</a> (" + Integer.toString(count) + ")<br />");
			}


			DBConnector.close();
		}

	}
}
