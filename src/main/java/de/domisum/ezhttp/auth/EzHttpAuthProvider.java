package de.domisum.ezhttp.auth;

import org.apache.http.impl.client.HttpClientBuilder;

public abstract class EzHttpAuthProvider
{

	public abstract void provideAuthFor(HttpClientBuilder httpClientBuilder);

}
