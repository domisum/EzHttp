package de.domisum.ezhttp;

import de.domisum.ezhttp.auth.EzHttpAuthProvider;
import de.domisum.ezhttp.auth.providers.EzHttpNoAuthProvider;
import de.domisum.ezhttp.request.EzHttpRequest;
import de.domisum.ezhttp.response.EzHttpIoResponse;
import de.domisum.ezhttp.response.EzHttpResponse;
import de.domisum.ezhttp.response.EzHttpResponseBodyReader;
import de.domisum.ezhttp.response.bodyreaders.EzHttpStringBodyReader;
import de.domisum.lib.auxilium.data.container.AbstractURL;
import de.domisum.lib.auxilium.display.DurationDisplay;
import de.domisum.lib.auxilium.util.java.annotations.API;
import de.domisum.lib.auxilium.util.java.exceptions.ShouldNeverHappenError;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
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
	private Duration timeout = Duration.ofMinutes(1);
	@Setter
	private EzHttpAuthProvider authProvider = new EzHttpNoAuthProvider();
	@Setter
	private boolean followRedirects = true;


	// SEND
	@API
	public EzHttpIoResponse<T> send()
	{
		HttpUriRequest apacheRequest = buildApacheRequest();

		EzHttpRequestTimeouter requestTimeouter = new EzHttpRequestTimeouter(apacheRequest, timeout);
		requestTimeouter.start();

		try(CloseableHttpClient httpClient = buildHttpClient();
				CloseableHttpResponse response = httpClient.execute(apacheRequest))
		{
			if(requestTimeouter.didTimeOutAndEnd())
				throw new IoTimeoutException(requestTimeouter.getTimeout());

			EzHttpResponse<T> ezHttpResponse = readResponse(response);
			return new EzHttpIoResponse<>(ezHttpResponse, null);
		}
		catch(IOException e)
		{
			return new EzHttpIoResponse<T>(null, e);
		}
	}


	// BUILD CLIENT
	private CloseableHttpClient buildHttpClient()
	{
		HttpClientBuilder clientBuilder = HttpClients.custom();
		authProvider.provideAuthFor(clientBuilder);
		clientBuilder.setDefaultRequestConfig(buildRequestConfig().build());

		if(!followRedirects)
			clientBuilder.disableRedirectHandling();

		return clientBuilder.build();
	}

	private Builder buildRequestConfig()
	{
		Builder requestConfigBuilder = RequestConfig.custom();

		// TODO what exactly is this used for? it had some function, but I'm unsure what exactly it does
		// noinspection deprecation
		requestConfigBuilder.setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY);
		requestConfigBuilder
				.setSocketTimeout((int) timeout.toMillis())
				.setConnectTimeout((int) timeout.toMillis())
				.setConnectionRequestTimeout((int) timeout.toMillis());

		return requestConfigBuilder;
	}


	// BUILD REQUEST
	private HttpUriRequest buildApacheRequest()
	{
		HttpRequestBase apacheRequest = getRawMethodRequest();

		addHeadersToRequest(apacheRequest);
		if(request.getBody() != null)
			addBodyToRequest(apacheRequest);

		return apacheRequest;
	}

	private HttpRequestBase getRawMethodRequest()
	{
		AbstractURL url = request.getUrl();

		switch(request.getMethod())
		{
			case GET:
				return new HttpGet(url.toString());
			case HEAD:
				return new HttpHead(url.toString());
			case POST:
				return new HttpPost(url.toString());
			case PUT:
				return new HttpPut(url.toString());
			case DELETE:
				return new HttpDelete(url.toString());
			case OPTIONS:
				return new HttpOptions(url.toString());
			case PATCH:
				return new HttpPatch(url.toString());
		}

		throw new ShouldNeverHappenError();
	}

	private void addHeadersToRequest(HttpMessage apacheRequest)
	{
		for(EzHttpHeader header : request.getHeaders())
			apacheRequest.addHeader(header.getKey(), header.getValue());
	}

	private void addBodyToRequest(HttpMessage apacheRequest)
	{
		apacheRequest.addHeader("Content-Type", request.getBody().getContentType());
		((HttpEntityEnclosingRequest) apacheRequest).setEntity(new InputStreamEntity(request.getBody().getAsInputStream()));
	}


	// READ RESPONSE
	private EzHttpResponse<T> readResponse(HttpResponse response) throws IOException
	{
		int statusCode = response.getStatusLine().getStatusCode();
		int statusCodeFirstDigit = statusCode/100;
		boolean successful = (statusCodeFirstDigit == 2) || (statusCodeFirstDigit == 3);

		List<EzHttpHeader> headers = readResponseHeaders(response);
		T successResponseBody = successful ? onSuccessReadResponseBody(response) : null;
		String failureResponseBody = successful ? null : onFailureReadResponseBody(response);

		return new EzHttpResponse<>(statusCode, headers, successResponseBody, failureResponseBody);
	}

	private List<EzHttpHeader> readResponseHeaders(HttpResponse response)
	{
		List<EzHttpHeader> headers = new ArrayList<>();

		for(Header header : response.getAllHeaders())
			headers.add(new EzHttpHeader(header.getName(), header.getValue()));

		return headers;
	}

	private T onSuccessReadResponseBody(HttpResponse response) throws IOException
	{
		try(InputStream responseBodyStream = response.getEntity().getContent())
		{
			return successResponseBodyReader.read(responseBodyStream);
		}
	}

	private String onFailureReadResponseBody(HttpResponse response) throws IOException
	{
		try(InputStream responseBodyStream = response.getEntity().getContent())
		{
			return failureResponseBodyReader.read(responseBodyStream);
		}
	}


	// TIMEOUT
	private static class IoTimeoutException extends IOException
	{

		// INIT
		public IoTimeoutException(Duration timeout)
		{
			super("Request aborted after timeout of "+DurationDisplay.display(timeout));
		}

	}

}
