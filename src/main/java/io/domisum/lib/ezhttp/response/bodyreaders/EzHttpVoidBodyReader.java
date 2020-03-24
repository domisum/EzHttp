package io.domisum.lib.ezhttp.response.bodyreaders;

import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.ezhttp.response.EzHttpResponseBodyReader;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;

@API
@RequiredArgsConstructor
public class EzHttpVoidBodyReader
		implements EzHttpResponseBodyReader<Void>
{
	
	// READ
	@Override
	public Void read(InputStream inputStream)
	{
		return null;
	}
	
}