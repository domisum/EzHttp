package io.domisum.lib.ezhttp.request.bodies;

import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.auxiliumlib.datacontainers.tuple.Duo;
import io.domisum.lib.auxiliumlib.util.StringListUtil;
import io.domisum.lib.ezhttp.request.url.EzUrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@API
public class EzHttpFormUrlEncodedBody
	extends EzHttpPlaintextBody
{
	
	// INIT
	@API
	public EzHttpFormUrlEncodedBody(List<Duo<String>> entries)
	{
		super(encodeEntries(entries));
	}
	
	@API
	public EzHttpFormUrlEncodedBody(Map<String, String> entries)
	{
		super(encodeEntries(entries));
	}
	
	private static String encodeEntries(Map<String, String> entries)
	{
		var duoEntries = entries.entrySet().stream()
			.map(e->new Duo<>(e.getKey(), e.getValue()))
			.collect(Collectors.toList());
		
		return encodeEntries(duoEntries);
	}
	
	private static String encodeEntries(List<Duo<String>> entries)
	{
		var keyValuePairs = new ArrayList<String>();
		for(var entry : entries)
		{
			String escapedKey = EzUrl.escapeString(entry.getA());
			String escapedValue = EzUrl.escapeString(entry.getB());
			
			String keyValuePair = escapedKey+"="+escapedValue;
			keyValuePairs.add(keyValuePair);
		}
		
		return StringListUtil.list(keyValuePairs, "&");
	}
	
	
	// BODY
	@Override
	public String getContentType()
	{
		return "application/x-www-form-urlencoded";
	}
	
}
