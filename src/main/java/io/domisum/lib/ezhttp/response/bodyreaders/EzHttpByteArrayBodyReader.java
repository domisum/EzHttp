package io.domisum.lib.ezhttp.response.bodyreaders;

import io.domisum.lib.ezhttp.response.EzHttpResponseBodyReader;
import io.domisum.lib.auxiliumlib.annotations.API;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

@API
@RequiredArgsConstructor
public class EzHttpByteArrayBodyReader implements EzHttpResponseBodyReader<byte[]>
{

	// READ
	@Override
	public byte[] read(InputStream inputStream) throws IOException
	{
		return IOUtils.toByteArray(inputStream);
	}

}
