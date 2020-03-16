package io.domisum.lib.ezhttp.response.bodyreaders;

import io.domisum.lib.ezhttp.response.EzHttpResponseBodyReader;
import io.domisum.lib.auxiliumlib.util.java.annotations.API;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@API
@RequiredArgsConstructor
public class EzHttpStringBodyReader implements EzHttpResponseBodyReader<String>
{

	// CONSTANTS
	@API
	public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

	// SETTINGS
	private final Charset charset;


	// INIT
	@API
	public EzHttpStringBodyReader()
	{
		charset = DEFAULT_CHARSET;
	}


	// READ
	@Override
	public String read(InputStream inputStream) throws IOException
	{
		return IOUtils.toString(inputStream, charset);
	}

}
