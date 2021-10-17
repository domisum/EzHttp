package io.domisum.lib.ezhttp.header.s;

import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.ezhttp.header.EzHttpHeader;

@API
public class EzHttpHeader_BearerAuthentication
	extends EzHttpHeader
{
	
	// INIT
	@API
	public EzHttpHeader_BearerAuthentication(String token)
	{
		super("Authorization", "Bearer "+token);
	}
	
}
