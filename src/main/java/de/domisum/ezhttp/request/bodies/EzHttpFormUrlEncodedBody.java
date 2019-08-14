package de.domisum.ezhttp.request.bodies;

import de.domisum.lib.auxilium.util.StringUtil;
import de.domisum.lib.auxilium.util.java.annotations.API;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@API
public class EzHttpFormUrlEncodedBody extends EzHttpPlaintextBody
{

	// INIT
	@API
	public EzHttpFormUrlEncodedBody(Map<String, String> values)
	{
		super(encodeValueMap(values));
	}

	private static String encodeValueMap(Map<String, String> values)
	{
		List<String> keyValuePairs = new ArrayList<>();

		for(Entry<String, String> entry : values.entrySet())
		{
			String escapedKey = StringUtil.escapeUrlParameterString(entry.getKey());
			String escapedValue = StringUtil.escapeUrlParameterString(entry.getValue());

			String keyValuePair = escapedKey+"="+escapedValue;
			keyValuePairs.add(keyValuePair);
		}

		return StringUtil.listToString(keyValuePairs, "&");
	}


	// BODY
	@Override
	public String getContentType()
	{
		return "application/x-www-form-urlencoded";
	}

}
