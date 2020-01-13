package de.domisum.ezhttp.response.bodyreaders;

import de.domisum.ezhttp.response.EzHttpResponseBodyReader;
import de.domisum.lib.auxilium.util.FileUtil;
import de.domisum.lib.auxilium.util.java.annotations.API;
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
