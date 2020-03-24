package io.domisum.lib.ezhttp.response;

import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.auxiliumlib.contracts.iosource.IoOptional;

import java.io.IOException;

@API
public class EzHttpIoResponse<T>
		extends IoOptional<EzHttpResponse<T>>
{
	
	// INIT
	public EzHttpIoResponse(EzHttpResponse<T> value, IOException exception)
	{
		super(value, exception);
	}
	
}
