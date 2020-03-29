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
import java.util.HashSet;
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
	private final Set<QueryParameter> queryParameters;
	@Nullable
	private final String fragment;
	
	
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
	public EzUrl(String protocol, String host, Integer port, String path, Set<QueryParameter> queryParameters, String fragment)
	{
		path = cleanPath(path);
		if(queryParameters == null)
			queryParameters = new HashSet<>();
		if(fragment != null && fragment.isEmpty())
			fragment = null;
		
		this.protocol = protocol.toLowerCase();
		this.host = host.toLowerCase();
		this.port = port;
		this.path = path;
		this.queryParameters = Set.copyOf(queryParameters);
		this.fragment = fragment;
		
		ValidationUtil.notBlank(this.protocol, "protocol");
		ValidationUtil.notBlank(this.host, "host");
		if(this.port != null)
			ValidationUtil.validatePortInRange(this.port, "port");
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
	public EzUrl extendPath(String pathExtension)
	{
		String basePath = path;
		pathExtension = cleanPath(pathExtension);
		
		String path;
		if(basePath == null)
			path = pathExtension;
		else if(pathExtension == null)
			path = basePath;
		else
			path = basePath+"/"+pathExtension;
		
		return new EzUrl(protocol, host, port, path, queryParameters, fragment);
	}
	
	
	// OBJECT
	@Override
	public String toString()
	{
		String url = protocol+"://"+host;
		
		if(port != null)
			url += ":"+port;
		
		if(path != null)
			url += "/"+escapePath(path);
		
		if(queryParameters.size() > 0)
		{
			var queryParameterStrings = new ArrayList<String>();
			for(var queryParameter : queryParameters)
				queryParameterStrings.add(queryParameter.getAsEscapedKeyValuePair());
			
			String queryString = StringUtil.listToString(queryParameterStrings, "&");
			url += "?"+queryString;
		}
		
		if(fragment != null)
			url += "#"+fragment;
		
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
