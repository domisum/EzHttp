package io.domisum.lib.ezhttp.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EasyUrlTest
{
	
	@Test
	public void testSimpleUrlParsing()
	{
		Assertions.assertEquals("http://www.google.com", new EasyUrl("http://www.google.com").toString());
		Assertions.assertEquals("http://www.google.com", new EasyUrl("http://www.google.com/").toString());
		
		Assertions.assertEquals("http://www.google.com/test", new EasyUrl("http://www.google.com/test").toString());
		Assertions.assertEquals("http://www.google.com/test", new EasyUrl("http://www.google.com/test/").toString());
	}
	
	@Test
	public void testUrlExtension()
	{
		EasyUrl googleBase = new EasyUrl("http://www.google.com");
		
		Assertions.assertEquals("http://www.google.com/kek", new EasyUrl(googleBase, "kek").toString());
		Assertions.assertEquals("http://www.google.com/kek", new EasyUrl(googleBase, "kek/").toString());
		Assertions.assertEquals("http://www.google.com/kek", new EasyUrl(googleBase, "/kek/").toString());
		Assertions.assertEquals("http://www.google.com/somefile.html", new EasyUrl(googleBase, "somefile.html").toString());
	}
	
	@Test
	public void testJavaNetConversion()
	{
		Assertions.assertEquals("http://www.google.com", new EasyUrl("http://www.google.com").toNet().toString());
		
		Assertions.assertThrows(Exception.class, ()->new EasyUrl("invalidurl").toNet());
	}
	
	
	@Test
	public void testToString()
	{
		Assertions.assertEquals("http://www.google.com", new EasyUrl("http://www.google.com").toString());
		Assertions.assertEquals("http://www.google.com", new EasyUrl("http://www.google.com/").toString());
	}
	
}
