package io.domisum.lib.ezhttp.response;

import io.domisum.lib.auxiliumlib.contracts.source.io.IoOptional;

import java.io.IOException;

public class EzHttpIoResponse<T> extends IoOptional<EzHttpResponse<T>>
{

	// INIT
	public EzHttpIoResponse(EzHttpResponse<T> value, IOException exception)
	{
		super(value, exception);
	}

}
