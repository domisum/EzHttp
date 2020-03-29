package io.domisum.lib.ezhttp.request;

import io.domisum.lib.ezhttp.request.url.EzUrl;
import io.domisum.lib.ezhttp.request.url.EzUrl.QueryParameter;
import io.domisum.lib.ezhttp.request.url.EzUrlParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;

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
	public void testFull()
	{
		assertParseUnescapedEquals(new EzUrl("https", "build.domisum.io", 381, "ok/this/is/epic", qp("asdf", "nice", "noob", "okok1")),
				"https://build.domisum.io:381/ok/this/is/epic?asdf=nice&noob=okok1");
		assertParseUnescapedEquals(new EzUrl("ftp", "www.op.gg", 20901, "this/that", qp("j", "p", "jkl", "0391")),
				"ftp://www.op.gg:20901/this/that?j=p&jkl=0391");
		assertParseUnescapedEquals(new EzUrl("http", "127.0.0.1", 80, "adv/antage", qp("build", "7", "filter", "none")),
				"http://127.0.0.1:80/adv/antage?build=7&filter=none");
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
	
	
	// ARRANGE
	private static Collection<QueryParameter> qp(String key1, String value1, String key2, String value2)
	{
		return Arrays.asList(new QueryParameter(key1, value1), new QueryParameter(key2, value2));
	}
	
	// ASSERT
	private void assertParseUnescapedEquals(EzUrl url, String toParse)
	{
		var parsed = EzUrlParser.parseUnescaped(toParse);
		Assertions.assertEquals(url, parsed);
	}
	
}
