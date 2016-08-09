package scottdjohnson.jcrawler.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import java.io.StringWriter;
import java.io.PrintWriter;

import scottdjohnson.jcrawler.JCrawler;

@Path("/urls/{key}")
public class JaxRsUrls {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String gwetHtml(@PathParam("key") String key) 
	{
		return getString(key);
	}

        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public String getJson(@PathParam("key") String key)
        {
                return getString(key);
        }

	private String getString(String key)
	{
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		JCrawler.getUrls(Integer.parseInt(key), printWriter );

		return stringWriter.toString();
	}
}
