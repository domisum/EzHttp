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
	}
	
	@Test
	public void testFull()
	{
		assertParseUnescapedEquals(new EzUrl("https", "build.domisum.io", 381, "ok/this/is/epic", EzUrlTest.qp("asdf", "nice", "noob", "okok1")),
			"https://build.domisum.io:381/ok/this/is/epic?asdf=nice&noob=okok1");
		assertParseUnescapedEquals(new EzUrl("ftp", "www.op.gg", 20901, "this/that", EzUrlTest.qp("j", "p", "jkl", "0391")),
			"ftp://www.op.gg:20901/this/that?j=p&jkl=0391");
		assertParseUnescapedEquals(new EzUrl("http", "127.0.0.1", 80, "adv/antage", EzUrlTest.qp("build", "7", "filter", "none")),
			"http://127.0.0.1:80/adv/antage?build=7&filter=none");
	}
	
	
	@Test
	public void testGeneralWrong()
	{
		assertParseUnescapedFails("https://google.com#");
		assertParseUnescapedFails("https://google.com#ok");
	}
	
	@Test
	public void testWrongProtocol()
	{
		assertParseUnescapedFails("www.google.com");
		assertParseUnescapedFails("https://www.google.com://ok");
		assertParseUnescapedFails("://www.google.com");
		assertParseUnescapedFails("://://www.google.com");
	}
	
	@Test
	public void testWrongHost()
	{
		assertParseUnescapedFails("http://");
		assertParseUnescapedFails("http:///path");
		assertParseUnescapedFails("http://:381");
		assertParseUnescapedFails("http://?key=value");
		assertParseUnescapedFails("http://#chapter1");
	}
	
	@Test
	public void testWrongPort()
	{
		assertParseUnescapedFails("http://localhost:");
		assertParseUnescapedFails("http://localhost:asdf");
		assertParseUnescapedFails("http://localhost:-ok");
	}
	
	@Test
	public void testWrongPath()
	{
		// only way path can be wrong is already checked in EzUrlTest, parser can't fail here
	}
	
	@Test
	public void testWrongQueryString()
	{
		assertParseUnescapedFails("https://google.com?");
		assertParseUnescapedFails("https://google.com?&");
		assertParseUnescapedFails("https://google.com?=");
		assertParseUnescapedFails("https://google.com?asd=");
		assertParseUnescapedFails("https://google.com?=jkl");
		assertParseUnescapedFails("https://google.com?ok=boomer&");
		assertParseUnescapedFails("https://google.com?&ben=libtards");
		assertParseUnescapedFails("https://google.com?&&rekt=libtards");
	}
	
	
	// ACT & ASSERT
	private void assertParseUnescapedEquals(EzUrl url, String toParse)
	{
		var parsed = EzUrlParser.parseUnescaped(toParse);
		Assertions.assertEquals(url, parsed);
	}
	
	private void assertParseUnescapedFails(String url)
	{
		Assertions.assertThrows(IllegalArgumentException.class, ()->EzUrlParser.parseUnescaped(url));
	}
	
}
