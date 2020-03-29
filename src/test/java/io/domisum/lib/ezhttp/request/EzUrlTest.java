package io.domisum.lib.ezhttp.request;

import io.domisum.lib.ezhttp.request.url.EzUrl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EzUrlTest
{
	
	@Test
	public void testSimpleUrlParsing()
	{
		Assertions.assertEquals("http://www.google.com", new EzUrl("http://www.google.com").toString());
		Assertions.assertEquals("http://www.google.com", new EzUrl("http://www.google.com/").toString());
		
		Assertions.assertEquals("http://www.google.com/test", new EzUrl("http://www.google.com/test").toString());
		Assertions.assertEquals("http://www.google.com/test", new EzUrl("http://www.google.com/test/").toString());
	}
	
	@Test
	public void testUrlExtension()
	{
		EzUrl googleBase = new EzUrl("http://www.google.com");
		
		Assertions.assertEquals("http://www.google.com/kek", new EzUrl(googleBase, "kek").toString());
		Assertions.assertEquals("http://www.google.com/kek", new EzUrl(googleBase, "kek/").toString());
		Assertions.assertEquals("http://www.google.com/kek", new EzUrl(googleBase, "/kek/").toString());
		Assertions.assertEquals("http://www.google.com/somefile.html", new EzUrl(googleBase, "somefile.html").toString());
	}
	
	@Test
	public void testJavaNetConversion()
	{
		Assertions.assertEquals("http://www.google.com", new EzUrl("http://www.google.com").toNet().toString());
		
		Assertions.assertThrows(Exception.class, ()->new EzUrl("invalidurl").toNet());
	}
	
	
	@Test
	public void testToString()
	{
		Assertions.assertEquals("http://www.google.com", new EzUrl("http://www.google.com").toString());
		Assertions.assertEquals("http://www.google.com", new EzUrl("http://www.google.com/").toString());
	}
	
}
