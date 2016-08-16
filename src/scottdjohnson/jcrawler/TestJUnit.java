package scottdjohnson.jcrawler;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.junit.runners.MethodSorters;
import org.junit.FixMethodOrder;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.PrintWriter;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestJUnit {

	@Test
	public void testDelete() 
	{
		System.out.println("Testing JCrawler.deleteAllUrls() and JCrawler.getUrls()");
		JCrawler.deleteAllUrls();

		System.out.println("Getting JSON from the root");
		JsonObject json = getJsonObjectFromKey(0);
	
		// TEST: No URLs returned after delete
		JsonArray jsonArray = json.getJsonArray("URLs");
		assertEquals(0, jsonArray.size());
		assertEquals(null, json.getJsonNumber("parent"));
	}

	@Test
	public void testNewCrawl()
	{
		System.out.println("Test JCrawler.addUrl() and JCrawler.getUrls()");
		JCrawler.addUrl("http://www.dag.com");

		System.out.println("Getting JSON from the root");
		JsonObject json = getJsonObjectFromKey(0);

		// TEST: URLs loaded
		JsonArray jsonArray = json.getJsonArray("URLs");
		assertEquals(1, jsonArray.size());
		assertEquals(11, jsonArray.getJsonObject(0).getInt("count"));

		int childKey = jsonArray.getJsonObject(0).getInt("key");
		System.out.println("KEY VALUE: " + childKey);
		json = getJsonObjectFromKey(childKey);
		jsonArray = json.getJsonArray("URLs");
		assertEquals(11, jsonArray.size());
		assertEquals(7, jsonArray.getJsonObject(0).getInt("count"));
		assertEquals(1, jsonArray.getJsonObject(1).getInt("count"));
		assertEquals(7, jsonArray.getJsonObject(2).getInt("count"));
		assertEquals(1, jsonArray.getJsonObject(3).getInt("count"));
		assertEquals(1, jsonArray.getJsonObject(4).getInt("count"));
		assertEquals(1, jsonArray.getJsonObject(5).getInt("count"));
		assertEquals(2, jsonArray.getJsonObject(6).getInt("count"));
		assertEquals(28, jsonArray.getJsonObject(7).getInt("count"));
		assertEquals(0, jsonArray.getJsonObject(8).getInt("count"));
		assertEquals(2, jsonArray.getJsonObject(9).getInt("count"));
		assertEquals(0, jsonArray.getJsonObject(10).getInt("count"));
//		assertEquals(0, json.getInt("parent"));
	}

	@Test
	public void testReDelete()
	{
		System.out.println("Testing JCrawler.deleteAllUrls() and JCrawler.getUrls()");
		JCrawler.deleteAllUrls();
		
		System.out.println("Getting JSON from the root");
		JsonObject json = getJsonObjectFromKey(0);

		// TEST: No URLs returned after delete
		JsonArray jsonArray = json.getJsonArray("URLs");
		assertEquals(0, jsonArray.size());
		assertEquals(null, json.getJsonNumber("parent"));
	}

	private JsonObject getJsonObjectFromKey(int key)
	{
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		JCrawler.getUrls(key, printWriter );

		JsonReader reader = Json.createReader(new StringReader(stringWriter.toString()));
		System.out.println("JSON return from key: " + key + "\n" + stringWriter.toString() + "\n\n");
		JsonObject json = reader.readObject();
		reader.close();

		return json;
	}

}
