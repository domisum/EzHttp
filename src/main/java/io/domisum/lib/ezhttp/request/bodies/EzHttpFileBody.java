package io.domisum.lib.ezhttp.request.bodies;

import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.ezhttp.request.EzHttpRequestBody;
import lombok.RequiredArgsConstructor;

import java.io.*;

@API
@RequiredArgsConstructor
public class EzHttpFileBody
	implements EzHttpRequestBody
{
	
	private final File file;
	
	
	// BODY
	@Override
	public String getContentType()
	{
		return "application/octet-stream";
	}
	
	@Override
	public InputStream getAsInputStream()
	{
		try
		{
			var fis = new FileInputStream(file);
			var bis = new BufferedInputStream(fis);
			return bis;
		}
		catch(FileNotFoundException e)
		{
			throw new UncheckedIOException(e);
		}
	}
	
}
