package io.domisum.lib.ezhttp.header.s;

import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.ezhttp.header.EzHttpHeader;

import java.util.Base64;

@API
public class EzHttpHeader_BasicAuthentication
	extends EzHttpHeader
{
	
	// INIT
	@API
	public EzHttpHeader_BasicAuthentication(String username, String password)
	{
		super("Authorization", "Basic "+encode(username, password));
	}
	
	private static String encode(String username, String password)
	{
		String valueUnencoded = username+":"+password;
		return Base64.getEncoder().encodeToString(valueUnencoded.getBytes());
	}
	
}
