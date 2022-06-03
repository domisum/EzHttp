package io.domisum.lib.ezhttp.response.bodyreaders;

import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.auxiliumlib.contracts.serdes.StringSerdes;
import io.domisum.lib.ezhttp.response.EzHttpResponseBodyReader;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;

@API
@RequiredArgsConstructor
public class EzHttpSerializedObjectBodyReader<T>
	implements EzHttpResponseBodyReader<T>
{
	
	// DEPENDENCIES
	private final EzHttpResponseBodyReader<String> stringReader;
	private final StringSerdes<T> stringSerdes;
	
	
	// INIT
	@API
	public EzHttpSerializedObjectBodyReader(StringSerdes<T> stringSerdes)
	{
		this(new EzHttpStringBodyReader(), stringSerdes);
	}
	
	
	// READ
	@Override
	public T read(InputStream inputStream)
		throws IOException
	{
		String serialized = stringReader.read(inputStream);
		try
		{
			T object = stringSerdes.deserialize(serialized);
			if(object == null)
				throw new IOException("Deserialized object was null (json input: "+serialized+")");
			return object;
		}
		catch(RuntimeException e)
		{
			throw new IOException("Failed to deserialize object: "+serialized, e);
		}
	}
	
}
