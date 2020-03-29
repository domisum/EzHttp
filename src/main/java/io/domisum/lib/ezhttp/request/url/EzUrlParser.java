package io.domisum.lib.ezhttp.request.url;

import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.ezhttp.request.url.EzUrl.QueryParameter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@API
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EzUrlParser
{
	
	// INPUT
	private final String url;
	private final boolean escaped; // TODO
	
	// STATUS
	private String separatorBeforeRemainder = null;
	private String remainder = url;
	
	// COMPONENTS
	private String protocol;
	private String host;
	private Integer port = null;
	private String path = null;
	private Set<QueryParameter> queryParameters = null;
	private String fragment = null;
	
	
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
		readProtocol();
		readHost();
		readPort();
		readPath();
		readQueryParameters();
		readFragment();
		
		return new EzUrl(protocol, host, port, path, queryParameters, fragment);
	}
	
	private void readProtocol()
	{
		final var separator = "://";
		
		var splitAfterProtocol = remainder.split(separator);
		if(splitAfterProtocol.length == 0)
			throw parseFail("url has to contain protocol");
		if(splitAfterProtocol.length == 1)
			throw parseFail("url has to contain a protocol and a remaining part");
		if(splitAfterProtocol.length > 2)
			throw parseFail("url contains too many protocol separators");
		
		protocol = splitAfterProtocol[0];
		
		separatorBeforeRemainder = separator;
		remainder = splitAfterProtocol[1];
	}
	
	private void readHost()
	{
		Integer separatorAfterHostIndex = minIndexOf(remainder, ':', '/', '?', '#');
		if(separatorAfterHostIndex == null)
		{
			host = remainder;
			
			separatorBeforeRemainder = "";
			remainder = "";
		}
		else
		{
			host = remainder.substring(0, separatorAfterHostIndex);
			
			separatorBeforeRemainder = remainder.charAt(separatorAfterHostIndex)+"";
			remainder = remainder.substring(separatorAfterHostIndex+1);
		}
		
		if("".equals(host))
			throw parseFail("host can't be blank");
	}
	
	private void readPort()
	{
		if(!":".equals(separatorBeforeRemainder))
			return;
		
		Integer separatorAfterPortIndex = minIndexOf(remainder, '/', '?', '#');
		if(separatorAfterPortIndex == null)
		{
			port = parsePortString(remainder);
			
			separatorBeforeRemainder = "";
			remainder = "";
		}
		else
		{
			port = parsePortString(remainder.substring(0, separatorAfterPortIndex));
			
			separatorBeforeRemainder = remainder.charAt(separatorAfterPortIndex)+"";
			remainder = remainder.substring(separatorAfterPortIndex+1);
		}
	}
	
	private int parsePortString(String portString)
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
	
	private void readPath()
	{
		if(!"/".equals(separatorBeforeRemainder))
			return;
		
		Integer separatorAfterPathIndex = minIndexOf(remainder, '?', '#');
		if(separatorAfterPathIndex == null)
		{
			path = remainder;
			
			separatorBeforeRemainder = "";
			remainder = "";
		}
		else
		{
			path = remainder.substring(0, separatorAfterPathIndex);
			
			separatorBeforeRemainder = remainder.charAt(separatorAfterPathIndex)+"";
			remainder = remainder.substring(separatorAfterPathIndex+1);
		}
	}
	
	private void readQueryParameters()
	{
		if(!"?".equals(separatorBeforeRemainder))
			return;
		
		Integer separatorAfterQueryStringIndex = minIndexOf(remainder, '#');
		if(separatorAfterQueryStringIndex == null)
		{
			queryParameters = parseQueryParameters(remainder);
			
			separatorBeforeRemainder = "";
			remainder = "";
		}
		else
		{
			String queryString = remainder.substring(0, separatorAfterQueryStringIndex);
			queryParameters = parseQueryParameters(queryString);
			
			separatorBeforeRemainder = remainder.charAt(separatorAfterQueryStringIndex)+"";
			remainder = remainder.substring(separatorAfterQueryStringIndex+1);
		}
	}
	
	private Set<QueryParameter> parseQueryParameters(String queryString)
	{
		String[] parametersString = queryString.split("&");
		if(parametersString.length == 0)
			throw parseFail("query string can't be empty");
		
		var queryParameters = new HashSet<QueryParameter>();
		for(String parameter : parametersString)
		{
			String[] parameterSplit = parameter.split("=");
			if(parameterSplit.length != 2)
				throw parseFail("parameter does not follow key=value schema: '"+parameter+"'");
			
			String key = parameterSplit[0];
			String value = parameterSplit[1];
			queryParameters.add(new QueryParameter(key, value));
		}
		
		return queryParameters;
	}
	
	private void readFragment()
	{
		fragment = remainder;
	}
	
	
	private IllegalArgumentException parseFail(String message)
	{
		return new IllegalArgumentException(message+": "+url);
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
	
}
