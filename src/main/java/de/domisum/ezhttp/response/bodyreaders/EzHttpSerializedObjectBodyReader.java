package de.domisum.ezhttp.response.bodyreaders;

import de.domisum.ezhttp.response.EzHttpResponseBodyReader;
import de.domisum.lib.auxilium.contracts.serialization.ToStringSerializer;
import de.domisum.lib.auxilium.util.java.annotations.API;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;

@API
@RequiredArgsConstructor
public class EzHttpSerializedObjectBodyReader<T> implements EzHttpResponseBodyReader<T>
{

	private final EzHttpResponseBodyReader<String> stringReader;
	private final ToStringSerializer<T> toStringSerializer;


	// INIT
	@API
	public EzHttpSerializedObjectBodyReader(ToStringSerializer<T> toStringSerializer)
	{
		this(new EzHttpStringBodyReader(), toStringSerializer);
	}


	// READ
	@Override
	public T read(InputStream inputStream) throws IOException
	{
		String serialized = stringReader.read(inputStream);

		try
		{
			T object = toStringSerializer.deserialize(serialized);

			if(object == null)
				throw new IOException("deserialized object was null (json input: "+serialized+")");

			return object;
		}
		catch(RuntimeException e)
		{
			throw new IOException("failed to deserialize object: "+serialized, e);
		}
	}

}
