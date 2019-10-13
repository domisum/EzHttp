package de.domisum.ezhttp.response.bodyreaders;

import de.domisum.ezhttp.response.EzHttpResponseBodyReader;
import de.domisum.lib.auxilium.util.java.annotations.API;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;

@API
@RequiredArgsConstructor
public class EzHttpVoidBodyReader implements EzHttpResponseBodyReader<Void>
{

	// READ
	@Override
	public Void read(InputStream inputStream)
	{
		return null;
	}

}
