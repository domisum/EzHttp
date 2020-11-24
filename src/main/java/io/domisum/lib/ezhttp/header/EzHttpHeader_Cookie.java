package io.domisum.lib.ezhttp.header;

import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.auxiliumlib.util.StringListUtil;

import java.util.ArrayList;
import java.util.Map;

@API
public class EzHttpHeader_Cookie
	extends EzHttpHeader
{
	
	// INIT
	@API
	public EzHttpHeader_Cookie(Map<String, String> cookies)
	{
		super("Cookie", cookiesToValueString(cookies));
	}
	
	private static String cookiesToValueString(Map<String, String> cookies)
	{
		var cookieKeyValuePairs = new ArrayList<String>();
		for(var cookie : cookies.entrySet())
			cookieKeyValuePairs.add(cookie.getKey()+"="+cookie.getValue());
		
		return StringListUtil.listHorizontally(cookieKeyValuePairs, "; ");
	}
	
}
