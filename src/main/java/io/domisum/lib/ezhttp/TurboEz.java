package io.domisum.lib.ezhttp;

import io.domisum.lib.auxiliumlib.PHR;
import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.auxiliumlib.contracts.serdes.StringSerdes;
import io.domisum.lib.ezhttp.header.EzHttpHeader;
import io.domisum.lib.ezhttp.request.EzHttpMethod;
import io.domisum.lib.ezhttp.request.EzHttpRequest;
import io.domisum.lib.ezhttp.request.EzHttpRequestBody;
import io.domisum.lib.ezhttp.request.url.EzUrl;
import io.domisum.lib.ezhttp.response.EzHttpResponseBodyReader;
import io.domisum.lib.ezhttp.response.bodyreaders.EzHttpSerializedObjectBodyReader;
import io.domisum.lib.ezhttp.response.bodyreaders.EzHttpStringBodyReader;
import io.domisum.lib.ezhttp.response.bodyreaders.EzHttpVoidBodyReader;
import io.domisum.lib.ezhttp.response.bodyreaders.EzHttpWriteToFileBodyReader;
import io.domisum.lib.ezhttp.response.bodyreaders.EzHttpWriteToTempFileBodyReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@API
public class TurboEz
{
	
	// ESSENTIAL
	private final EzHttpMethod method;
	
	// MALLEABLE
	private EzUrl url;
	private final List<EzHttpHeader> headers = new ArrayList<>();
	private final List<Consumer<EzHttpRequestEnvoy<?>>> configures = new ArrayList<>();
	private String errorContextMessage;
	
	
	// INIT
	@API
	public static TurboEz get(EzUrl url)
	{
		return new TurboEz(EzHttpMethod.GET, url);
	}
	
	@API
	public static TurboEz post(EzUrl url)
	{
		return new TurboEz(EzHttpMethod.POST, url);
	}
	
	@API
	public TurboEz(EzHttpMethod method, EzUrl url)
	{
		this.method = method;
		this.url = url;
	}
	
	
	@API
	public TurboEz addParam(String key, String value)
	{
		url = url.withParameter(key, value);
		return this;
	}
	
	
	@API
	public TurboEz addHeader(CharSequence key, CharSequence value)
	{
		addHeader(new EzHttpHeader(key, value));
		return this;
	}
	
	@API
	public TurboEz addHeader(EzHttpHeader header)
	{
		headers.add(header);
		return this;
	}
	
	@API
	public TurboEz addHeaders(Iterable<EzHttpHeader> headers)
	{
		for(var header : headers)
			addHeader(header);
		
		return this;
	}
	
	
	@API
	public TurboEz setErrorContextMessage(String errorContextMessage)
	{
		this.errorContextMessage = errorContextMessage;
		return this;
	}
	
	
	// CONFIGURATION
	@API
	public TurboEz configure(Consumer<EzHttpRequestEnvoy<?>> configure)
	{
		configures.add(configure);
		return this;
	}
	
	
	// SEND
	@API
	public void send(EzHttpRequestBody body)
		throws IOException
	{
		var request = buildRequest();
		request.setBody(body);
		var envoy = new EzHttpRequestEnvoy<>(request, new EzHttpVoidBodyReader());
		configure(envoy);
		
		var ioResponse = envoy.send();
		
		String errorMessage = getErrorMessage("send");
		var response = ioResponse.getOrThrowWrapped(errorMessage);
		response.ifFailedThrowHttpException(errorMessage);
	}
	
	@API
	public void send()
		throws IOException
	{
		send(null);
	}
	
	@API
	public <T> T receive(EzHttpResponseBodyReader<T> responseBodyReader)
		throws IOException
	{
		return sendAndReceive(null, responseBodyReader);
	}
	
	@API
	public <T> T receive(StringSerdes<T> stringSerdes)
		throws IOException
	{
		return receive(new EzHttpSerializedObjectBodyReader<>(stringSerdes));
	}
	
	@API
	public <T> T sendAndReceive(EzHttpRequestBody body, EzHttpResponseBodyReader<T> responseBodyReader)
		throws IOException
	{
		var request = buildRequest();
		if(body != null)
			request.setBody(body);
		var envoy = new EzHttpRequestEnvoy<>(request, responseBodyReader);
		configure(envoy);
		
		var ioResponse = envoy.send();
		
		String errorMessage = getErrorMessage("receive");
		var response = ioResponse.getOrThrowWrapped(errorMessage);
		return response.getSuccessBodyOrThrowHttpException(errorMessage);
	}
	
	
	// SEND SHORTCUTS
	@API
	public String receiveString()
		throws IOException
	{
		return receive(new EzHttpStringBodyReader());
	}
	
	@API
	public String sendAndReceiveString(EzHttpRequestBody body)
		throws IOException
	{
		return sendAndReceive(body, new EzHttpStringBodyReader());
	}
	
	@API
	public void receiveToFile(File file)
		throws IOException
	{
		receive(new EzHttpWriteToFileBodyReader(file));
	}
	
	@API
	public File receiveToTempFile()
		throws IOException
	{
		return receive(new EzHttpWriteToTempFileBodyReader());
	}
	
	
	// UTIL
	private EzHttpRequest buildRequest()
	{
		var request = new EzHttpRequest(method, url);
		request.addHeaders(headers);
		return request;
	}
	
	private void configure(EzHttpRequestEnvoy<?> envoy)
	{
		for(var envoyConfigurator : configures)
			envoyConfigurator.accept(envoy);
	}
	
	private String getErrorMessage(String verb)
	{
		String errorMessage = PHR.r("Failed to {}: {} {}", verb, method, url);
		if(errorContextMessage != null)
			errorMessage = errorContextMessage+". Because: "+errorMessage;
		
		return errorMessage;
	}
	
}
