package io.domisum.lib.ezhttp.response;

import io.domisum.lib.auxiliumlib.annotations.API;

import java.io.IOException;
import java.io.InputStream;

@API
public interface EzHttpResponseBodyReader<T>
{
	
	T read(InputStream inputStream)
		throws IOException;
	
}
