package io.domisum.lib.ezhttp.header;

import io.domisum.lib.auxiliumlib.annotations.API;

@API
public class EzHttpHeaderBearerAuthentication
	extends EzHttpHeader
{
	
	// INIT
	@API
	public EzHttpHeaderBearerAuthentication(String token)
	{
		super("Authorization", "Bearer "+token);
	}
	
}
