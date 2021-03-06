package io.domisum.lib.ezhttp.request.bodies;

import io.domisum.lib.auxiliumlib.annotations.API;

import java.util.Map;
import java.util.Map.Entry;

@API
public class EzHttpMultipartFormBody
	extends EzHttpPlaintextBody
{
	
	private final String delimiter;
	
	
	// INIT
	@API
	public EzHttpMultipartFormBody(Map<String, String> values)
	{
		super(valueMapToBodyString(values));
		delimiter = determineDelimiter(values);
	}
	
	private static String determineDelimiter(Map<String, String> values)
	{
		int delimiterAdditionalPartLength = 3;
		while(true)
		{
			// not random to make this method deterministic
			String randomDelimiterPart = "j".repeat(delimiterAdditionalPartLength);
			String delimiter = "----WebKitFormBoundary"+randomDelimiterPart;
			
			for(Entry<String, String> entry : values.entrySet())
				if(!entry.getValue().contains(delimiter))
					return delimiter;
			
			delimiterAdditionalPartLength++;
		}
	}
	
	private static String valueMapToBodyString(Map<String, String> values)
	{
		return valueMapToBodyString(values, determineDelimiter(values));
	}
	
	private static String valueMapToBodyString(Map<String, String> values, String delimiter)
	{
		String delimiterLine = "--"+delimiter;
		String asString = "";
		
		for(Entry<String, String> entry : values.entrySet())
		{
			asString += delimiterLine+"\n";
			asString += "Content-Disposition: form-data; name=\""+entry.getKey()+"\"\n";
			asString += "\n";
			asString += entry.getValue()+"\n";
		}
		asString += delimiterLine+"--";
		
		return asString;
	}
	
	
	// BODY
	@Override
	public String getContentType()
	{
		return "multipart/form-data; boundary="+delimiter;
	}
	
}
