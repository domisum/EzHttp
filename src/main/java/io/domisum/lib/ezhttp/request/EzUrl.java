package io.domisum.lib.ezhttp.request;

import io.domisum.lib.auxiliumlib.annotations.API;
import lombok.RequiredArgsConstructor;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

@API
public class EzUrl
{
	
	// ATTRIBUTES
	private final String url;
	
	
	// INIT
	@API
	public EzUrl(String url)
	{
		String beingCleanedUrl = url;
		while(beingCleanedUrl.endsWith("/"))
			beingCleanedUrl = beingCleanedUrl.substring(0, beingCleanedUrl.length()-1);
		
		this.url = beingCleanedUrl;
	}
	
	@API
	public EzUrl(EzUrl base, String extension)
	{
		this(base.combineWith(extension));
	}
	
	private String combineWith(String extension)
	{
		if(!extension.startsWith("/"))
			extension = "/"+extension;
		return url+extension;
	}
	
	@API
	public EzUrl(EzUrl base, Collection<Parameter> parameters)
	{
		String url = base.url;
		for(var parameter : parameters)
		{
			String connector = url.contains("?") ? "&" : "?";
			url += connector+parameter.getAsEscapedKeyValuePair();
		}
		
		this.url = url;
	}
	
	@API
	public EzUrl(EzUrl base, Parameter... parameters)
	{
		this(base, Arrays.asList(parameters));
	}
	
	
	// OBJECT
	@Override
	public String toString()
	{
		return url;
	}
	
	
	// CONVERSION
	@API
	public URL toNet()
	{
		try
		{
			return new URL(url);
		}
		catch(MalformedURLException e)
		{
			throw new UncheckedIOException(e);
		}
	}
	
	
	// PARAMETER
	@RequiredArgsConstructor
	public static class Parameter
	{
		
		// ATTRIBUTES
		private final String key;
		private final String value;
		
		
		// GETTERS
		public String getAsEscapedKeyValuePair()
		{
			return escapeUrlParameterString(key)+"="+escapeUrlParameterString(value);
		}
		
	}
	
	@API
	public static String escapeUrlParameterString(String urlString)
	{
		return URLEncoder.encode(urlString, StandardCharsets.UTF_8);
	}
	
}
