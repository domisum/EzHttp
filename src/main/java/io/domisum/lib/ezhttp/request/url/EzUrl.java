package io.domisum.lib.ezhttp.request.url;

import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.auxiliumlib.util.StringUtil;
import io.domisum.lib.auxiliumlib.util.ValidationUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.annotation.Nullable;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@API
@EqualsAndHashCode
public class EzUrl
{
	
	// everything here is stored unencoded
	@Getter
	private final String protocol; // case insensitive -> always lowercase
	@Getter
	private final String host; // case insensitive -> always lowercase
	@Nullable
	private final Integer port;
	@Nullable
	private final String path;
	private final List<QueryParameter> queryParameters;
	
	
	// UTIL INIT
	@API
	public static EzUrl parseUnescaped(String url)
	{
		return EzUrlParser.parseUnescaped(url);
	}
	
	@API
	public static EzUrl parseEscaped(String url)
	{
		return EzUrlParser.parseEscaped(url);
	}
	
	
	// INIT
	@API
	public EzUrl(String protocol, String host, Integer port, String path, Collection<QueryParameter> queryParameters)
	{
		ValidationUtil.notBlank(protocol, "protocol");
		ValidationUtil.notBlank(host, "host");
		if(port != null)
			ValidationUtil.validatePortInRange(port, "port");
		if(path != null && path.contains("//"))
			throw new IllegalArgumentException("path contains empty segment: '"+path+"'");
		
		this.protocol = protocol.toLowerCase();
		this.host = host.toLowerCase();
		this.port = port;
		this.path = cleanPath(path);
		this.queryParameters = queryParameters == null ? Collections.emptyList() : List.copyOf(queryParameters);
	}
	
	@API
	public EzUrl(String protocol, String host, int port, String path)
	{
		this(protocol, host, port, path, null);
	}
	
	@API
	public EzUrl(String protocol, String host, String path)
	{
		this(protocol, host, null, path, null);
	}
	
	private static String cleanPath(String path)
	{
		if(path == null)
			return null;
		
		if(path.startsWith("/"))
			path = path.substring(1);
		if(path.endsWith("/"))
			path = path.substring(0, path.length()-1);
		
		if(path.isEmpty())
			return null;
		return path;
	}
	
	
	// DERIVE
	@API
	public EzUrl withExtendedPath(String pathExtension)
	{
		// TODO validate input
		
		String basePath = path;
		pathExtension = cleanPath(pathExtension);
		
		String path;
		if(basePath == null)
			path = pathExtension;
		else if(pathExtension == null)
			path = basePath;
		else
			path = basePath+"/"+pathExtension;
		
		return new EzUrl(protocol, host, port, path, queryParameters);
	}
	
	@API
	public EzUrl withParameters(QueryParameter... parameters)
	{
		var newParameters = new ArrayList<>(queryParameters);
		newParameters.addAll(Arrays.asList(parameters));
		
		return new EzUrl(protocol, host, port, path, newParameters);
	}
	
	@API
	public EzUrl withParameter(String key, String value)
	{
		return withParameters(new QueryParameter(key, value));
	}
	
	
	// OBJECT
	@Override
	public String toString()
	{
		return toStringEscaped();
	}
	
	@API
	public String toStringEscaped()
	{
		return toString(true);
	}
	
	@API
	public String toStringUnescaped()
	{
		return toString(false);
	}
	
	private String toString(boolean escaped)
	{
		String url = protocol+"://"+host;
		
		if(port != null)
			url += ":"+port;
		
		url += "/";
		if(path != null)
		{
			String path = escaped ? escapePath(this.path) : this.path;
			url += path;
		}
		
		if(queryParameters.size() > 0)
		{
			var queryParameterStrings = new ArrayList<String>();
			for(var queryParameter : queryParameters)
			{
				String queryParameterString = escaped ? queryParameter.getAsEscapedKeyValuePair() : queryParameter.getAsKeyValuePair();
				queryParameterStrings.add(queryParameterString);
			}
			
			String queryString = StringUtil.listToString(queryParameterStrings, "&");
			url += "?"+queryString;
		}
		
		return url;
	}
	
	
	// GETTERS
	@API
	public Optional<Integer> getPort()
	{
		return Optional.ofNullable(port);
	}
	
	@API
	public Optional<String> getPath()
	{
		return Optional.ofNullable(path);
	}
	
	@API
	public Set<String> getQueryParameterKeys()
	{
		var keys = new HashSet<String>();
		for(var queryParameter : queryParameters)
			keys.add(queryParameter.getKey());
		
		return keys;
	}
	
	@API
	public Set<String> getQueryParameterValues(String queryParameterKey)
	{
		var values = new HashSet<String>();
		for(var queryParameter : queryParameters)
			if(queryParameter.getKey().equals(queryParameterKey))
				values.add(queryParameter.getValue());
		
		return values;
	}
	
	
	// PARAMETER
	@EqualsAndHashCode
	public static class QueryParameter
	{
		
		// ATTRIBUTES
		@Getter
		private final String key;
		@Getter
		private final String value;
		
		
		// INIT
		public QueryParameter(String key, String value)
		{
			ValidationUtil.notBlank(key, "key");
			ValidationUtil.notBlank(value, "value");
			
			this.key = key;
			this.value = value;
		}
		
		
		// OBJECT
		@Override
		public String toString()
		{
			return "'"+key+"'='"+value+"'";
		}
		
		
		// GETTERS
		public String getAsKeyValuePair()
		{
			return key+"="+value;
		}
		
		public String getAsEscapedKeyValuePair()
		{
			return escapeString(key)+"="+escapeString(value);
		}
		
	}
	
	
	// UTIL
	@API
	public static String escapeString(String string)
	{
		return URLEncoder.encode(string, StandardCharsets.UTF_8);
	}
	
	@API
	public static String unescapeString(String escapedString)
	{
		return URLDecoder.decode(escapedString, StandardCharsets.UTF_8);
	}
	
	@API
	public static String escapePath(String path)
	{
		var escapedSegments = new ArrayList<String>();
		for(String segment : path.split("/"))
			escapedSegments.add(escapeString(segment));
		
		return StringUtil.listToString(escapedSegments, "/");
	}
	
	@API
	public static String unescapePath(String escapedPath)
	{
		var unescapedSegments = new ArrayList<String>();
		for(String segment : escapedPath.split("/"))
			unescapedSegments.add(unescapeString(segment));
		
		return StringUtil.listToString(unescapedSegments, "/");
	}
	
}
