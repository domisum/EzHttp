package io.domisum.lib.ezhttp.response.bodyreaders;

import io.domisum.lib.ezhttp.response.EzHttpResponseBodyReader;
import io.domisum.lib.auxiliumlib.util.file.FileUtil;
import io.domisum.lib.auxiliumlib.annotations.API;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@API
@RequiredArgsConstructor
public class EzHttpWriteToFileBodyReader implements EzHttpResponseBodyReader<File>
{

	// OUTPUT
	private final File outputFile;


	// READ
	@Override
	public File read(InputStream inputStream) throws IOException
	{
		try
		{
			FileUtil.writeStreamUncaught(outputFile, inputStream);
			return outputFile;
		}
		catch(IOException e)
		{
			FileUtil.delete(outputFile);
			throw e;
		}
	}

}
