package io.domisum.lib.ezhttp.response.bodyreaders;

import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.ezhttp.response.EzHttpResponseBodyReader;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;

@API
@RequiredArgsConstructor
public class EzHttpInputStreamBodyReader
	implements EzHttpResponseBodyReader<InputStream>
{
	
	@Override
	public InputStream read(InputStream inputStream)
		throws IOException
	{
		return inputStream;
	}
	
}
