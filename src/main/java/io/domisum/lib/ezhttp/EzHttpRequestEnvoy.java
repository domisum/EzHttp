package io.domisum.lib.ezhttp;

import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.auxiliumlib.display.DurationDisplay;
import io.domisum.lib.auxiliumlib.exceptions.IncompleteCodeError;
import io.domisum.lib.ezhttp.header.EzHttpHeader;
import io.domisum.lib.ezhttp.request.EzHttpRequest;
import io.domisum.lib.ezhttp.response.EzHttpIoResponse;
import io.domisum.lib.ezhttp.response.EzHttpResponse;
import io.domisum.lib.ezhttp.response.EzHttpResponseBodyReader;
import io.domisum.lib.ezhttp.response.bodyreaders.EzHttpStringBodyReader;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.Validate;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@API
@RequiredArgsConstructor
public class EzHttpRequestEnvoy<T>
{
	
	// CONSTANTS
	private final EzHttpResponseBodyReader<String> failureResponseBodyReader = new EzHttpStringBodyReader();
	
	// BASE SETTINGS
	private final EzHttpRequest request;
	private final EzHttpResponseBodyReader<T> successResponseBodyReader;
	
	// ADDTITIONAL SETTINGS
	@Setter
	private boolean cancelOnInterrupt = true;
	@Setter
	private Duration timeout = Duration.ofMinutes(1);
	@Setter
	private boolean followRedirects = true;
	
	private Double uploadSpeedCapMibitPerSecond = null;
	
	
	// SETTERS
	@API
	public void setUploadSpeedCapMibitPerSecond(@Nullable Integer uploadSpeedCap)
	{
		Double uploadSpeedCapDouble = (uploadSpeedCap == null) ?
				null :
				uploadSpeedCap.doubleValue();
		setUploadSpeedCapMibitPerSecond(uploadSpeedCapDouble);
	}
	
	@API
	public void setUploadSpeedCapMibitPerSecond(@Nullable Double uploadSpeedCap)
	{
		if(uploadSpeedCap != null)
			Validate.isTrue(uploadSpeedCap > 0, "upload speed cap has to be greater than zero, was "+uploadSpeedCap);
		
		uploadSpeedCapMibitPerSecond = uploadSpeedCap;
	}
	
	
	// SEND
	@API
	public EzHttpIoResponse<T> send()
	{
		var apacheRequest = buildApacheRequest();
		
		var watchdogTask = EzHttpRequestWatchdog.watch(apacheRequest, timeout, cancelOnInterrupt);
		try(var httpClient = buildHttpClient(); var response = httpClient.execute(apacheRequest))
		{
			var ezHttpResponse = readResponse(response);
			watchdogTask.cancel();
			return new EzHttpIoResponse<>(ezHttpResponse, null);
		}
		catch(IOException e)
		{
			var exception = e;
			if(watchdogTask.wasInterrupted())
				exception = new IoInterruptedException();
			else if(watchdogTask.didTimeout())
				exception = new IoTimeoutException(watchdogTask.getDuration());
			
			return new EzHttpIoResponse<>(null, exception);
		}
	}
	
	
	// BUILD CLIENT
	private CloseableHttpClient buildHttpClient()
	{
		var clientBuilder = HttpClients.custom();
		clientBuilder.setDefaultRequestConfig(buildRequestConfig());
		if(!followRedirects)
			clientBuilder.disableRedirectHandling();
		
		return clientBuilder.build();
	}
	
	private RequestConfig buildRequestConfig()
	{
		var requestConfigBuilder = RequestConfig.custom();
		requestConfigBuilder
				.setSocketTimeout((int) timeout.toMillis())
				.setConnectTimeout((int) timeout.toMillis())
				.setConnectionRequestTimeout((int) timeout.toMillis());
		
		return requestConfigBuilder.build();
	}
	
	
	// BUILD REQUEST
	private HttpUriRequest buildApacheRequest()
	{
		var apacheRequest = getRawMethodRequest();
		
		addHeadersToRequest(apacheRequest);
		if(request.getBody() != null)
			addBodyToRequest(apacheRequest);
		
		return apacheRequest;
	}
	
	private HttpRequestBase getRawMethodRequest()
	{
		var method = request.getMethod();
		var url = request.getUrl().toStringEscaped();
		switch(method)
		{
			case GET: return new HttpGetAllowingBody(url);
			case HEAD: return new HttpHead(url);
			case POST: return new HttpPost(url);
			case PUT: return new HttpPut(url);
			case DELETE: return new HttpDelete(url);
			case OPTIONS: return new HttpOptions(url);
			case PATCH: return new HttpPatch(url);
			default: throw new IncompleteCodeError("no request defined for method "+method);
		}
	}
	
	private void addHeadersToRequest(HttpMessage apacheRequest)
	{
		for(var header : request.getHeaders())
			apacheRequest.addHeader(header.getKey(), header.getValue());
	}
	
	private void addBodyToRequest(HttpMessage apacheRequest)
	{
		apacheRequest.addHeader("Content-Type", request.getBody().getContentType());
		
		var bodyInputStream = request.getBody().getAsInputStream();
		if(uploadSpeedCapMibitPerSecond != null)
		{
			final double kibi = 1024d;
			double mibitToByteFactor = (kibi*kibi)/8;
			long bytesPerSecond = (long) Math.ceil(uploadSpeedCapMibitPerSecond*mibitToByteFactor);
			bodyInputStream = new ThrottlingInputStream(bodyInputStream, bytesPerSecond);
		}
		((HttpEntityEnclosingRequest) apacheRequest).setEntity(new InputStreamEntity(bodyInputStream));
	}
	
	
	// READ RESPONSE
	private EzHttpResponse<T> readResponse(HttpResponse response)
			throws IOException
	{
		int statusCode = response.getStatusLine().getStatusCode();
		int statusCodeFirstDigit = statusCode/100;
		boolean successful = (statusCodeFirstDigit == 2) || (statusCodeFirstDigit == 3);
		
		var headers = readResponseHeaders(response);
		T successResponseBody = successful ? onSuccessReadResponseBody(response) : null;
		String failureResponseBody = successful ? null : onFailureReadResponseBody(response);
		
		return new EzHttpResponse<>(statusCode, headers, successResponseBody, failureResponseBody);
	}
	
	private List<EzHttpHeader> readResponseHeaders(HttpResponse response)
	{
		var headers = new ArrayList<EzHttpHeader>();
		for(var header : response.getAllHeaders())
			headers.add(new EzHttpHeader(header.getName(), header.getValue()));
		return headers;
	}
	
	private T onSuccessReadResponseBody(HttpResponse response)
			throws IOException
	{
		if(response.getEntity() == null)
			return null;
		
		try(var responseBodyStream = response.getEntity().getContent())
		{
			return successResponseBodyReader.read(responseBodyStream);
		}
	}
	
	private String onFailureReadResponseBody(HttpResponse response)
			throws IOException
	{
		try(var responseBodyStream = response.getEntity().getContent())
		{
			return failureResponseBodyReader.read(responseBodyStream);
		}
	}
	
	
	// ABORTION
	@API
	public static class IoTimeoutException
			extends IOException
	{
		
		// INIT
		public IoTimeoutException(Duration timeout)
		{
			super("request aborted after timeout of "+DurationDisplay.display(timeout));
		}
		
	}
	
	@API
	public static class IoInterruptedException
			extends IOException
	{
		
		// INIT
		public IoInterruptedException()
		{
			super("Request aborted due to thread interrupt");
		}
		
	}
	
	
	// APACHE FIX
	public static class HttpGetAllowingBody
			extends HttpEntityEnclosingRequestBase
	{
		
		// CONSTANTS
		public static final String METHOD_NAME = "GET";
		
		
		// INIT
		public HttpGetAllowingBody(String uri)
		{
			setURI(URI.create(uri));
		}
		
		
		// GETTERS
		@Override
		public String getMethod()
		{
			return METHOD_NAME;
		}
		
	}
	
}
