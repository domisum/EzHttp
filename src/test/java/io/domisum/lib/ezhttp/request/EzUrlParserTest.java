package io.domisum.lib.ezhttp.request;

import io.domisum.lib.ezhttp.request.url.EzUrl;
import io.domisum.lib.ezhttp.request.url.EzUrlParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EzUrlParserTest
{
	
	@Test
	public void testBasic()
	{
		assertParseUnescapedEquals(new EzUrl("https", "www.google.com", null, null, null), "https://www.google.com");
		assertParseUnescapedEquals(new EzUrl("https", "www.google.com", null, null, null), "https://www.google.com/");
		assertParseUnescapedEquals(new EzUrl("https", "www.google.com", null, null, null), "https://www.google.com/");
		assertParseUnescapedEquals(new EzUrl("https", "www.google.com", null, null, null), "https://www.google.com");
	}
	
	@Test
	public void testGeneralWrong()
	{
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("https://google.com#"));
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("https://google.com#ok"));
	}
	
	@Test
	public void testWrongProtocol()
	{
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("www.google.com"));
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("https://www.google.com://ok"));
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("://www.google.com"));
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("://://www.google.com"));
	}
	
	@Test
	public void testWrongHost()
	{
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("http://"));
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("http:///path"));
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("http://:381"));
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("http://?key=value"));
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("http://#chapter1"));
	}
	
	@Test
	public void testWrongPort()
	{
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("http://localhost:"));
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("http://localhost:asdf"));
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("http://localhost:-ok"));
	}
	
	@Test
	public void testWrongPath()
	{
		// only way path can be wrong is already checked in EzUrlTest, parser cant' fail here
	}
	
	@Test
	public void testWrongQueryString()
	{
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("https://google.com?"));
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("https://google.com?&"));
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("https://google.com?="));
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("https://google.com?asd="));
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("https://google.com?=jkl"));
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("https://google.com?ok=boomer&"));
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("https://google.com?&ben=libtards"));
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("https://google.com?&&rekt=libtards"));
	}
	
	
	// ASSERT
	private void assertParseUnescapedEquals(EzUrl url, String toParse)
	{
		var parsed = EzUrlParser.parseUnescaped(toParse);
		Assertions.assertEquals(url, parsed);
	}
	
}
