package io.domisum.lib.ezhttp.request.bodies;

import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.ezhttp.request.EzHttpRequestBody;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@API
@RequiredArgsConstructor
public class EzHttpRawBody
	implements EzHttpRequestBody
{
	
	private final byte[] raw;
	@Getter private final String contentType;
	
	
	@Override
	public InputStream getAsInputStream()
	{
		return new ByteArrayInputStream(raw);
	}
	
}
