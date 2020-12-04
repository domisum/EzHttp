package io.domisum.lib.ezhttp.response.bodyreaders;

import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.auxiliumlib.util.FileUtil;
import io.domisum.lib.ezhttp.response.EzHttpResponseBodyReader;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@API
@RequiredArgsConstructor
public class EzHttpWriteToTempFileBodyReader
	implements EzHttpResponseBodyReader<File>
{
	
	// READ
	@Override
	public File read(InputStream inputStream)
		throws IOException
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
