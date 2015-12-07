package scottdjohnson.jcrawler;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

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

/**
 * A servlet class for posting new URLs to crawl and getting URL crawl results form the database
 *
 * @see JCrawler
 *
 * @author Scott Johnson
 **/
public class JCrawlerServlet extends HttpServlet
{
	/**
	 * Get the results of a previous JCrawler URL crawl from the database
	 * Delivers HTML which can be inserted into an web page
	 *
	 * We optionally allow a urlkey Get variable, which is the key of a previous URL that
	 * has already been crawled. If this is not delivered, we assume the requester wants
	 * all results
	 *
	 * @param request The request to get from the servlet
	 * @param response The response back from the servlet
	 **/
  	public void doGet(HttpServletRequest request, HttpServletResponse response)
    	 throws ServletException, IOException 
	{
                response.setContentType("application/json");
                // This is necessary for AJAX requests to this function
                response.setHeader("Access-Control-Allow-Origin","*");

		JCrawler.getUrls(Integer.parseInt( request.getParameter("urlkey")), response.getWriter() );
	}

	/**
	 * Post the q reuest to crawl a URL, given by the crawl_url parameter
	 *
	 * @param request The request to get from the servlet
	 * @param response The response back from the servlet
	 **/
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		response.setContentType("text/html");
		
		// This is necessary for AJAX requests to this function
		response.setHeader("Access-Control-Allow-Origin","*");

		JCrawler.addUrl( request.getParameter("crawl_url") );
	}

        /**
         * Delete the contents of the database
         *
         * @param request The request to get from the servlet
         * @param response The response back from the servlet
         **/
        public void doDelete(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException
	{
                response.setContentType("text/html");

                // This is necessary for AJAX requests to this function
                response.setHeader("Access-Control-Allow-Origin","*");

		JCrawler.deleteAllUrls();
	}	
}

