package io.domisum.lib.ezhttp.header;

import io.domisum.lib.auxiliumlib.util.StringUtil;
import io.domisum.lib.auxiliumlib.util.java.annotations.API;

import java.util.ArrayList;
import java.util.Map;

@API
public class EzHttpHeaderCookie extends EzHttpHeader
{

	// INIT
	@API
	public EzHttpHeaderCookie(Map<String, String> cookies)
	{
		super("Cookie", cookiesToValueString(cookies));
	}

	private static String cookiesToValueString(Map<String, String> cookies)
	{
		var cookieKeyValuePairs = new ArrayList<String>();
		for(var cookie : cookies.entrySet())
			cookieKeyValuePairs.add(cookie.getKey()+"="+cookie.getValue());

		return StringUtil.listToString(cookieKeyValuePairs, "; ");
	}

}
