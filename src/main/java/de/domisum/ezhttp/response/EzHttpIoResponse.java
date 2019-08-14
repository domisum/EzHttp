package de.domisum.ezhttp.response;

import de.domisum.lib.auxilium.contracts.source.io.IoOptional;

import java.io.IOException;

public class EzHttpIoResponse<T> extends IoOptional<EzHttpResponse<T>>
{

	// INIT
	public EzHttpIoResponse(EzHttpResponse<T> value, IOException exception)
	{
		super(value, exception);
	}

}
