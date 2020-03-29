package io.domisum.lib.ezhttp.request;

import io.domisum.lib.ezhttp.request.url.EzUrl;
import io.domisum.lib.ezhttp.request.url.EzUrl.QueryParameter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.Collection;

public class EzUrlTest
{
	
	@Test
	public void test()
	{
		assertToStringEquals("http://www.google.com", new EzUrl("http", "www.google.com", null, null, null));
		assertToStringEquals("https://api.test.org:4388/path/asd_j?complex=true&color=purple",
				new EzUrl("https", "api.test.org", 4388, "/path/asd_j/", qp("complex", "true", "color", "purple")));
	}
	
	@Test
	public void testWrongProtocol()
	{
		assertCreationFails(()->new EzUrl(null, "google.com", null, null, null));
		assertCreationFails(()->new EzUrl("", "google.com", null, null, null));
	}
	
	@Test
	public void testWrongHost()
	{
		assertCreationFails(()->new EzUrl("http", null, null, null, null));
		assertCreationFails(()->new EzUrl("http", "", null, null, null));
	}
	
	@Test
	public void testWrongPort()
	{
		assertCreationFails(()->new EzUrl("http", "google.com", -8, null, null));
		assertCreationFails(()->new EzUrl("http", "google.com", 0, null, null));
		assertCreationFails(()->new EzUrl("http", "google.com", 100000, null, null));
	}
	
	@Test
	public void testWrongPath()
	{
		assertCreationFails(()->new EzUrl("http", "google.com", 80, "//", null));
		assertCreationFails(()->new EzUrl("http", "google.com", 80, "ok/nice//", null));
		assertCreationFails(()->new EzUrl("http", "google.com", 80, "ok/nice//this/", null));
	}
	
	@Test
	public void testWrongQueryParameters()
	{
		assertCreationFails(()->new EzUrl("http", "google.com", 80, "/ok/this", qp(null, "a", "b", "c")));
		assertCreationFails(()->new EzUrl("http", "google.com", 80, "/ok/this", qp("", "a", "b", "c")));
		assertCreationFails(()->new EzUrl("http", "google.com", 80, "/ok/this", qp("o", null, "b", "c")));
		assertCreationFails(()->new EzUrl("http", "google.com", 80, "/ok/this", qp("o", "", "b", "c")));
	}
	
	
	// ARRANGE
	public static Collection<QueryParameter> qp(String key1, String value1, String key2, String value2)
	{
		return Arrays.asList(new QueryParameter(key1, value1), new QueryParameter(key2, value2));
	}
	
	// ACT & ASSERT
	private void assertToStringEquals(String toStringExpected, EzUrl url)
	{
		Assertions.assertEquals(toStringExpected, url.toString());
	}
	
	private void assertCreationFails(Executable e)
	{
		Assertions.assertThrows(IllegalArgumentException.class, e);
	}
	
}
