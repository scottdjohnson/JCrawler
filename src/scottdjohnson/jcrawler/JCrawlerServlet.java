package scottdjohnson.jcrawler;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

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

public class JCrawlerServlet extends HttpServlet 
{
  	public void doGet(HttpServletRequest request,HttpServletResponse response)
    	 throws ServletException, IOException 
	{
		response.setContentType("text/html");
    		PrintWriter out = response.getWriter();
    		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 " +
                                        "Transitional//EN\">\n" +
                	"<HTML>\n" +
                	"<HEAD><TITLE>JCrawler Servlet</TITLE></HEAD>\n" +
                	"<BODY>\n" +
                	"<H1>JCrawler Servlet test</H1>\n" +
                	"</BODY></HTML>");
	}

        public void doPost(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException
	{
		String crawl_url = request.getParameter("crawl_url");

		BinaryTree bt = new BinaryTree();
		URLNode un = new URLNode(crawl_url);
		 
		bt.insertNode(un);
		un.save();
		
		JCrawler.getLinksFromURL(bt, crawl_url, un.getKey());

		response.sendRedirect(request.getHeader("referer"));
	}
}

