package de.domisum.ezhttp.auth.providers;

import de.domisum.ezhttp.auth.EzHttpAuthProvider;
import org.apache.http.impl.client.HttpClientBuilder;

public class EzHttpNoAuthProvider extends EzHttpAuthProvider
{

	@Override
	public void provideAuthFor(HttpClientBuilder httpClientBuilder)
	{
		// do nothing
	}

}
