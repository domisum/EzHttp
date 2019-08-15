package de.domisum.ezhttp.response.bodyreaders;

import de.domisum.ezhttp.response.EzHttpResponseBodyReader;
import de.domisum.lib.auxilium.util.java.annotations.API;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;

@API
@RequiredArgsConstructor
public class EzHttpEmptyBodyReader implements EzHttpResponseBodyReader<String>
{

	// READ
	@Override
	public String read(InputStream inputStream)
	{
		return "";
	}

}
