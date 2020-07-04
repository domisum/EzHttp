package io.domisum.lib.ezhttp.header;

import io.domisum.lib.auxiliumlib.annotations.API;

import java.util.Base64;

@API
public class EzHttpHeaderBasicAuthentication
		extends EzHttpHeader
{
	
	// INIT
	@API
	public EzHttpHeaderBasicAuthentication(String username, String password)
	{
		super("Authorization", "Basic "+encode(username, password));
	}
	
	private static String encode(String username, String password)
	{
		String valueUnencoded = username+":"+password;
		return Base64.getEncoder().encodeToString(valueUnencoded.getBytes());
	}
	
}
