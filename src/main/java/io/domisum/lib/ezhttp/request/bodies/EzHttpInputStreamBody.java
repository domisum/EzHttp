package io.domisum.lib.ezhttp.request.bodies;

import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.ezhttp.request.EzHttpRequestBody;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;

@API
@RequiredArgsConstructor
public class EzHttpInputStreamBody
	implements EzHttpRequestBody
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
