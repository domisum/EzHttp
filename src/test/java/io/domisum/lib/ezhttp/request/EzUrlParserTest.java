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
		assertParseUnescapedEquals(new EzUrl("https", "www.google.com", null, null, null, null), "https://www.google.com");
		assertParseUnescapedEquals(new EzUrl("https", "www.google.com", null, null, null, null), "https://www.google.com/");
		assertParseUnescapedEquals(new EzUrl("https", "www.google.com", null, null, null, null), "https://www.google.com/#");
		assertParseUnescapedEquals(new EzUrl("https", "www.google.com", null, null, null, null), "https://www.google.com#");
	}
	
	@Test
	public void testWrongQueryString()
	{
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("https://google.com?"));
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("https://google.com?asd="));
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("https://google.com?=jkl"));
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("https://google.com?ok=boomer&"));
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("https://google.com?&ben=libtards"));
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped("https://google.com?&&ben=libtards"));
	}
	
	
	// ASSERT
	private void assertParseUnescapedEquals(EzUrl url, String toParse)
	{
		var parsed = EzUrlParser.parseUnescaped(toParse);
		Assertions.assertEquals(url, parsed);
	}
	
}
