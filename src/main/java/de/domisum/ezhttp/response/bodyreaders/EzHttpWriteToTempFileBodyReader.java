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
public class EzHttpWriteToTempFileBodyReader implements EzHttpResponseBodyReader<File>
{

	// READ
	@Override
	public File read(InputStream inputStream) throws IOException
	{
		File file = FileUtil.createTemporaryFile();
		try
		{
			FileUtil.writeStreamUncaught(file, inputStream);
			return file;
		}
		catch(IOException e)
		{
			FileUtil.delete(file);
			throw e;
		}
	}

}
