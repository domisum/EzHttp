package io.domisum.lib.ezhttp.request.url;

import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.auxiliumlib.util.StringUtil;
import io.domisum.lib.ezhttp.request.url.EzUrl.QueryParameter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@API
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EzUrlParser
{
	
	// INPUT
	private final String url;
	private final boolean escaped;
	
	// STATUS
	private String separatorBeforeRemainder = null;
	private String remainder = null;
	
	
	// STATIC USAGE
	@API
	public static EzUrl parseUnescaped(String url)
	{
		return new EzUrlParser(url, false).parse();
	}
	
	@API
	public static EzUrl parseEscaped(String url)
	{
		return new EzUrlParser(url, true).parse();
	}
	
	
	// PARSE
	private EzUrl parse()
	{
		if(url.contains("#"))
			throw parseFail("fragments are not supported");
		
		remainder = url;
		String protocol = readProtocol();
		String host = readComponent(null, this::parseHost, ':', '/', '?');
		Integer port = readComponent(":", this::parsePort, '/', '?');
		String path = readComponent("/", this::parsePath, '?');
		List<QueryParameter> queryParameters = readComponent("?", this::parseQueryParameters);
		
		return new EzUrl(protocol, host, port, path, queryParameters);
	}
	
	private String readProtocol()
	{
		final var separator = "://";
		
		var splitAfterProtocol = StringUtil.split(remainder, separator);
		if(splitAfterProtocol.size() == 0)
			throw parseFail("url has to contain protocol");
		if(splitAfterProtocol.size() == 1)
			throw parseFail("url has to contain a protocol and a remaining part");
		if(splitAfterProtocol.size() > 2)
			throw parseFail("url contains too many protocol separators");
		
		String protocol = splitAfterProtocol.get(0);
		
		separatorBeforeRemainder = separator;
		remainder = splitAfterProtocol.get(1);
		
		return protocol;
	}
	
	private <T> T readComponent(String separatorBefore, Function<String,T> parse, char... separatorsAfter)
	{
		if(separatorBefore != null)
			if(!separatorBeforeRemainder.equals(separatorBefore))
				return null;
		
		Integer separatorAfterComponentIndex = minIndexOf(remainder, separatorsAfter);
		if(separatorAfterComponentIndex == null)
		{
			T componentParsed = parse.apply(remainder);
			
			separatorBeforeRemainder = "";
			remainder = "";
			
			return componentParsed;
		}
		else
		{
			String componentString = remainder.substring(0, separatorAfterComponentIndex);
			T componentParsed = parse.apply(componentString);
			
			separatorBeforeRemainder = remainder.charAt(separatorAfterComponentIndex)+"";
			remainder = remainder.substring(separatorAfterComponentIndex+1);
			
			return componentParsed;
		}
	}
	
	private static Integer minIndexOf(String in, char... chars)
	{
		int[] indices = new int[chars.length];
		Arrays.setAll(indices, i->in.indexOf(chars[i]));
		
		return minIndex(indices);
	}
	
	private static Integer minIndex(int... indices)
	{
		Integer min = null;
		for(int index : indices)
			if(index != -1 && (min == null || index < min))
				min = index;
		
		return min;
	}
	
	
	// COMPONENT PARSING
	private String parseHost(String host)
	{
		if("".equals(host))
			throw parseFail("host can't be blank");
		
		return host;
	}
	
	private int parsePort(String portString)
	{
		try
		{
			return Integer.parseInt(portString);
		}
		catch(NumberFormatException ignored)
		{
			throw parseFail("invalid port: '"+portString+"'");
		}
	}
	
	private String parsePath(String path)
	{
		if(escaped)
			path = EzUrl.unescapePath(path);
		
		return path;
	}
	
	private List<QueryParameter> parseQueryParameters(String queryString)
	{
		if(queryString.isEmpty())
			throw parseFail("query string can't be empty");
		
		var parametersAsString = StringUtil.split(queryString, "&");
		var queryParameters = new ArrayList<QueryParameter>();
		for(String parameter : parametersAsString)
		{
			if(parameter.isEmpty())
				throw parseFail("parameter in query string can't be empty");
			
			var parameterSplit = StringUtil.split(parameter, "=");
			if(parameterSplit.size() != 2)
				throw parseFail("parameter in query string does not follow key=value schema: '"+parameter+"'");
			
			String key = parameterSplit.get(0);
			String value = parameterSplit.get(1);
			
			if(key.isEmpty())
				throw parseFail("parameter key in query string can't be empty");
			if(value.isEmpty())
				throw parseFail("parameter value in query string can't be empty");
			
			if(escaped)
			{
				key = EzUrl.unescapeString(key);
				value = EzUrl.unescapeString(value);
			}
			
			queryParameters.add(new QueryParameter(key, value));
		}
		
		return queryParameters;
	}
	
	private String parseFragment(String fragment)
	{
		return fragment;
	}
	
	
	// UTIL
	private IllegalArgumentException parseFail(String message)
	{
		return new IllegalArgumentException(message+": "+url);
	}
	
}
