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

public class JCrawler 
{	
	public static void getLinksFromURL( BinaryTree bt, String url, long parent)
	{		
		try{
			Document doc = Jsoup.connect(url).get();
			Elements links = doc.select("a[href]");
			int size		= links.size();
		
			// First we store all the URLs at the current level
			for (int i = 0; i < size; i++)
			{
				Element link = links.get(i);
				
				// We are going to assume that an absolute URL points to an external
				//	external web site. Not an entirely safe assumption but ok for this demo
				
				URLNode un = new URLNode( link.attr("abs:href"), parent);
				
				if ( !bt.isNodeInTree( un ) )
				{
					bt.insertNode( un );
					un.save();
					System.out.println("Inserting URL: " + link.attr("href"));
				}
			}				
			
			// We now go through the links again and look recursively, except for links we have already looked at
			for (int i = 0; i < size; i++)
			{
				Element link 	= links.get(i);
				URLNode un 	= new URLNode( link.attr("abs:href"), parent);
				URI u           = new URI(link.attr("href"));

				if ( !bt.isNodeInTree( un ) )
					if (!u.isAbsolute())
						getLinksFromURL( bt, un.getUrl(), un.getKey() );

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
