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
			URLNode.open();

			Validate.isTrue(args.length == 1, "usage: supply url to fetch");
			String url 	= args[0];
			
			bt.insertNode(un);
			un.save();
			
			JCrawler.getLinksFromURL(bt, url, un.getKey());
			URLNode.close();
		}
		else
		{
        	        org.hibernate.Session s = new Configuration().configure().buildSessionFactory().openSession();
	                org.hibernate.Transaction tx = s.beginTransaction();

			System.out.println("else");

                	try
                	{
                		System.out.println("try");
			        Query query = s.createQuery("from URLNode url_list");
				System.out.println("after query");
                	        List list = query.list();

				System.out.println("size " + list.size());	
	                        for (int i = 0; i < list.size(); i++)
	                        {
					System.out.println(((URLNode)list.get(i)).getKey());
	                                s.delete( (URLNode)list.get(i) );
	                        }

				tx.commit();
                	}
                	catch (Exception e)
                	{
				System.out.println(e.getMessage());
                	}
                	finally
                	{
				s.flush();
                	        s.close();
                	}

		}
	}
}
