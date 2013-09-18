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

public class JCrawlerServlet extends HttpServlet 
{
  	public void doGet(HttpServletRequest request,HttpServletResponse response)
    	 throws ServletException, IOException 
	{
		String urlkey = request.getParameter("urlkey");

		org.hibernate.Session s = getSession();
                Query query;

		// If there is a specifc key, get it, otherwise get all of them
		if (null != urlkey)
			query = s.createQuery("from URLNode url_list where parent_key = " + urlkey);
		else
			query = s.createQuery("from URLNode url_list where parent_key = 0");

		response.setContentType("text/html");
		response.setHeader("Access-Control-Allow-Origin","*");

    		PrintWriter out = response.getWriter();

                List list =  query.list();
                s.close();

		for (int i = 0; i < list.size(); i++)
		{
                        int count =( (Integer) getSession().createQuery("select count(*) from URLNode where parent_key="
				+ ((URLNode)list.get(i)).getKey()).iterate().next() ).intValue();

			out.println("<a href='' onclick=\"return getAJAX(" + ((URLNode)list.get(i)).getKey() + ");\">" 
                        + ((URLNode)list.get(i)).getUrl() + "</a> (" + count + ")<br />");
		}
	}

        public void doPost(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException
	{
                response.setContentType("text/html");
                response.setHeader("Access-Control-Allow-Origin","*");

		String crawl_url = request.getParameter("crawl_url");

		BinaryTree bt = new BinaryTree();
		URLNode un = new URLNode(crawl_url);
		 
		bt.insertNode(un);
		un.save();
		
		JCrawler.getLinksFromURL(bt, crawl_url, un.getKey());
	}

	private org.hibernate.Session getSession()
	{
		return new Configuration().configure().buildSessionFactory().openSession();
	}
}

