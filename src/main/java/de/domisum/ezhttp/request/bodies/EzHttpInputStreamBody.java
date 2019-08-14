package de.domisum.ezhttp.request.bodies;

import de.domisum.ezhttp.request.EzHttpRequestBody;
import de.domisum.lib.auxilium.util.java.annotations.API;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;

@API
@RequiredArgsConstructor
public class EzHttpInputStreamBody implements EzHttpRequestBody
{

	private final InputStream inputStream;


	// BODY
	@Override
	public String getContentType()
	{
		return "application/octet-stream";
	}

	@Override
	public InputStream getAsInputStream()
	{
		return inputStream;
	}

}
