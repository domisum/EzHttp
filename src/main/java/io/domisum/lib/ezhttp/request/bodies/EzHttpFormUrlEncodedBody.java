package io.domisum.lib.ezhttp.request.bodies;

import io.domisum.lib.auxiliumlib.datacontainers.AbstractURL;
import io.domisum.lib.auxiliumlib.util.StringUtil;
import io.domisum.lib.auxiliumlib.annotations.API;

import java.util.ArrayList;
import java.util.Map;

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
		var keyValuePairs = new ArrayList<String>();
		for(var entry : values.entrySet())
		{
			String escapedKey = AbstractURL.escapeUrlParameterString(entry.getKey());
			String escapedValue = AbstractURL.escapeUrlParameterString(entry.getValue());

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
