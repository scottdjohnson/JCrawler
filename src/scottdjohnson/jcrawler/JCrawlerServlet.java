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
		List list;
		String urlkey = request.getParameter("urlkey");
		PrintWriter out = response.getWriter();

		DBConnector.open();

		response.setContentType("text/html");
                // This is necessary for AJAX requests to this function
                response.setHeader("Access-Control-Allow-Origin","*");

		// If there is a specifc key, get it, otherwise get all of them
                if (null != urlkey)
                	list = DBConnector.getFromQuery("from URLNode url_list where parent_key = " + urlkey);
		else
			list = DBConnector.getFromQuery("from URLNode url_list where parent_key = 0");

		// Loop through all the results from the query
		for (int i = 0; i < list.size(); i++)
		{
			// Count the total number of results that have this URL as a parent
			int count =  (int)(DBConnector.getFromQuery("select count(*) from URLNode where parent_key="
				+ ((URLNode)list.get(i)).getKey())).get(0);

			// Print out this URL with the number of its children
			out.println("<a href='' onclick=\"return getAJAX(" + ((URLNode)list.get(i)).getKey() + ");\">" 
				+ ((URLNode)list.get(i)).getUrl() + "</a> (" + count + ")<br />");
		}


		DBConnector.close();
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

		String crawl_url = request.getParameter("crawl_url");

		// Store this first URL in the binary tree and save it to the DB
		BinaryTree bt = new BinaryTree();
		URLNode un = new URLNode(crawl_url);		 
		DBConnector.open();

		bt.insertNode(un);
		DBConnector.save( un );
		
		// Crawl the URL and its children
		JCrawler.getLinksFromURL(bt, crawl_url, un.getKey());

		DBConnector.close();
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

		DBConnector.open();
		DBConnector.deleteFromQuery("from URLNode url_list");
		DBConnector.close();
	}	

	/**
	 * Create a new Hibernate session
	 **/
	private org.hibernate.Session getSession()
	{
		return new Configuration().configure().buildSessionFactory().openSession();
	}
}

