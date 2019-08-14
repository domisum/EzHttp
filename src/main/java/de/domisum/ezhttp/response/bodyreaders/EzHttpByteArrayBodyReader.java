package de.domisum.ezhttp.response.bodyreaders;

import de.domisum.ezhttp.response.EzHttpResponseBodyReader;
import de.domisum.lib.auxilium.util.java.annotations.API;
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
