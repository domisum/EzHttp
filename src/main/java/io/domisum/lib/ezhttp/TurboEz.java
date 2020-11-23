package io.domisum.lib.ezhttp;

import io.domisum.lib.auxiliumlib.PHR;
import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.auxiliumlib.contracts.serdes.StringSerdes;
import io.domisum.lib.ezhttp.header.EzHttpHeader;
import io.domisum.lib.ezhttp.request.EzHttpMethod;
import io.domisum.lib.ezhttp.request.EzHttpRequest;
import io.domisum.lib.ezhttp.request.url.EzUrl;
import io.domisum.lib.ezhttp.response.EzHttpResponseBodyReader;
import io.domisum.lib.ezhttp.response.bodyreaders.EzHttpSerializedObjectBodyReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@API
public class TurboEz
{
	
	// ESSENTIAL
	private final EzHttpMethod method;
	
	// MALLEABLE
	private EzUrl url;
	private final List<EzHttpHeader> headers = new ArrayList<>();
	
	
	// INIT
	public static TurboEz get(EzUrl url)
	{
		return new TurboEz(EzHttpMethod.GET, url);
	}
	
	@API
	public TurboEz(EzHttpMethod method, EzUrl url)
	{
		this.method = method;
		this.url = url;
	}
	
	
	// SETUP
	@API
	public TurboEz addParam(String key, String value)
	{
		url = url.withParameter(key, value);
		return this;
	}
	
	
	@API
	public void addHeader(CharSequence key, CharSequence value)
	{
		addHeader(new EzHttpHeader(key, value));
	}
	
	@API
	public void addHeader(EzHttpHeader header)
	{
		headers.add(header);
	}
	
	@API
	public void addHeaders(Collection<EzHttpHeader> headers)
	{
		this.headers.addAll(headers);
	}
	
	
	// SEND
	@API
	public <T> T receive(EzHttpResponseBodyReader<T> responseBodyReader)
		throws IOException
	{
		var request = new EzHttpRequest(method, url);
		request.addHeaders(headers);
		var envoy = new EzHttpRequestEnvoy<>(request, responseBodyReader);
		
		var ioResponse = envoy.send();
		
		String errorMessage = getErrorMessage("receive");
		var response = ioResponse.getOrThrowWrapped(errorMessage);
		return response.getSuccessBodyOrThrowHttpException(errorMessage);
	}
	
	@API
	public <T> T receive(StringSerdes<T> stringSerdes)
		throws IOException
	{
		return receive(new EzHttpSerializedObjectBodyReader<>(stringSerdes));
	}
	
	
	// UTIL
	private String getErrorMessage(String verb)
	{
		return PHR.r("Failed to {}: {} {}", verb, method, url);
	}
	
}
