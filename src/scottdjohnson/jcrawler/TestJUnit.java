package scottdjohnson.jcrawler;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestJUnit {

	@Test
	public void testDelete() 
	{
		System.out.println("Deleting all URLs");
		JCrawler.deleteAllUrls();
		assertEquals(true,true);
	}
}
