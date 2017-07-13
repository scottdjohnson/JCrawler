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
import java.io.OutputStream;

import org.scottdjohnson.jcrawler.node.URLNode;
import org.scottdjohnson.jcrawler.model.JCrawler;

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
		try
		{
		if (args.length > 0)
		{
//			JCrawler.deleteUrlAndChildren(Integer.parseInt(args[0]));
			JCrawler.addUrl(args[0]);
		}
		else
		{
			JCrawler.getUrls(0, new PrintWriter((OutputStream)System.out));
			System.out.println("next");
			JCrawler.getUrls(1, new PrintWriter((OutputStream)System.out));
//		System.out.println("next");
//			JCrawler.deleteAllUrls();
		}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
