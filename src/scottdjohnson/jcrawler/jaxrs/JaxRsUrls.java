package scottdjohnson.jcrawler.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.StringWriter;
import java.io.PrintWriter;

import scottdjohnson.jcrawler.JCrawler;

@Path("/urls/{key}")
public class JaxRsUrls {

	/**
	* Return the HTML value for /urls/{key}, for the given key
	* @param key The key entered on the URL at /url/{key}
	* @return The String to display on the HTML page
	**/
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getHtml(@PathParam("key") String key) 
	{
		return Response.ok(getString(key)).build();
	}

        /**
        * Return the JSON value for /urls/{key}, for the given key
        * @param key The key entered on the URL at /url/{key}
        * @return The String of JSON returned to the caller
        **/
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public Response getJson(@PathParam("key") String key)
        {
                return Response.ok(getString(key)).build();
        }


        /**
        * Produce the String value to return for the JAX-RS GET commands
        * @param key The key entered on the URL at /url/{key}, passed from the GET methods
        * @return The String of JSON produced by looking up URL children of this key
        **/
	private String getString(String key)
	{
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		JCrawler.getUrls(Integer.parseInt(key), printWriter );

		return stringWriter.toString();
	}
}
