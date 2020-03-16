package io.domisum.lib.ezhttp.auth.providers;

import io.domisum.lib.ezhttp.auth.EzHttpAuthProvider;
import org.apache.http.impl.client.HttpClientBuilder;

public class EzHttpNoAuthProvider extends EzHttpAuthProvider
{

	@Override
	public void provideAuthFor(HttpClientBuilder httpClientBuilder)
	{
		// do nothing
	}

}
