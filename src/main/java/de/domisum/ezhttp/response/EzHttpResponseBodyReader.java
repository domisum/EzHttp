package de.domisum.ezhttp.response;

import java.io.IOException;
import java.io.InputStream;

public interface EzHttpResponseBodyReader<T>
{

	T read(InputStream inputStream) throws IOException;

}
