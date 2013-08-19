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

/**
 * Example program to list links from a URL.
 */
public class JCrawlerApp 
{	
	public static void main(String[] args)
	{
		Validate.isTrue(args.length == 1, "usage: supply url to fetch");
		String url 	= args[0];
		
		getLinksFromURL(new BinaryTree(), url);

	}
	
	private static void getLinksFromURL( BinaryTree bt, String url)
	{		
		try{
			Document doc = Jsoup.connect(url).get();
			Elements links = doc.select("a[href]");
		
			for (Element link : links)
			{
				URI u		= new URI(link.attr("href"));
				
				// We are going to assume that an absolute URL points to an external
				//	external web site. Not an entirely safe assumption but ok for this demo
				if (u.isAbsolute())
				{
					bt.insertNode( new URLNode( link.attr("href") ) );
					System.out.println("Inserting absolute URL: " + link.attr("href"));
				}
				else
				{
					URLNode un = new URLNode( link.attr("abs:href") );

					// We do a bit of extra work by checking and inserting, but this is a simple way
					//	of assuring that we do not keeping looking at the same URL's in an infinite loop
					if ( !bt.isNodeInTree( un ) )
					{
						bt.insertNode( un );
						System.out.println("Inserting relative URL: " + un.getURL());
						getLinksFromURL( bt, un.getURL() );
					}					
				}
			}
		}
		catch(URISyntaxException e)
		{
			System.out.println("This is not a valid URL");
		}
		catch(IOException e)
		{
			System.out.println("Could not connect to this URL");

		}

	}

}
