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
import java.io.OutputStream;

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
		if (args.length > 0)
		{
			JCrawler.addUrl(args[0]);
		}
		else
		{
			JCrawler.getUrls(0, new PrintWriter((OutputStream)System.out));
		System.out.println("next");
			JCrawler.deleteAllUrls();
		}

	}
}
