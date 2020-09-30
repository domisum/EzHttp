package io.domisum.lib.ezhttp.response.bodyreaders;

import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.auxiliumlib.contracts.IoConsumer;
import io.domisum.lib.ezhttp.response.EzHttpResponseBodyReader;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;

@API
@RequiredArgsConstructor
public class EzHttpInputStreamConsumerBodyReader
	implements EzHttpResponseBodyReader<Void>
{
	
	// INPUT
	private final IoConsumer<InputStream> inputStreamAction;
	
	
	// READ
	@Override
	public Void read(InputStream inputStream)
		throws IOException
	{
		inputStreamAction.accept(inputStream);
		return null;
	}
	
}
