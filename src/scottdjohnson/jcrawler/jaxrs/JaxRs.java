package scottdjohnson.jcrawler.jaxrs;

import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import java.io.StringWriter;
import java.io.PrintWriter;

import scottdjohnson.jcrawler.JCrawler;

import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/urls")
public class JaxRs {

	private static final Logger logger = Logger.getLogger(JaxRs.class.getPackage().getName());

	/**
	* Return the HTML value for /urls/{key}, for the given key
	* @param key The key entered on the URL at /url/{key}
	* @return The String to display on the HTML page
	**/
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String postUrl(@FormParam("crawl_url") String crawlUrl) 
	{
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		logger.log(Level.INFO, "Crawling URL: " + crawlUrl);
		JCrawler.addUrl( crawlUrl );

		logger.log(Level.INFO, "Getting key: 0");
		JCrawler.getUrls(new Integer(0), printWriter );

		return stringWriter.toString();
	}

        /**
        * Return the JSON value for /urls/{key}, for the given key
        * @param key The key entered on the URL at /url/{key}
        * @return The String of JSON returned to the caller
        **/
        @DELETE
	@Produces(MediaType.APPLICATION_JSON)
        public String deleteAll()
        {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

                // This is necessary for AJAX requests to this function
                //response.setHeader("Access-Control-Allow-Origin","*");

		logger.log(Level.INFO, "Deleting URLs");
		JCrawler.deleteAllUrls();

		logger.log(Level.INFO, "Getting key: 0");
		JCrawler.getUrls(new Integer(0), printWriter );

		return stringWriter.toString();
       }
}
