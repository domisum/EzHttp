package io.domisum.lib.ezhttp;

import io.domisum.lib.ezhttp.auth.EzHttpAuthProvider;
import io.domisum.lib.ezhttp.auth.providers.EzHttpNoAuthProvider;
import io.domisum.lib.ezhttp.request.EzHttpRequest;
import io.domisum.lib.ezhttp.response.EzHttpIoResponse;
import io.domisum.lib.ezhttp.response.EzHttpResponse;
import io.domisum.lib.ezhttp.response.EzHttpResponseBodyReader;
import io.domisum.lib.ezhttp.response.bodyreaders.EzHttpStringBodyReader;
import io.domisum.lib.auxiliumlib.datacontainers.AbstractURL;
import io.domisum.lib.auxiliumlib.display.DurationDisplay;
import io.domisum.lib.auxiliumlib.util.java.ThreadUtil;
import io.domisum.lib.auxiliumlib.util.java.annotations.API;
import io.domisum.lib.auxiliumlib.util.java.exceptions.ShouldNeverHappenError;
import io.domisum.lib.auxiliumlib.util.time.DurationUtil;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.Validate;
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

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
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
	private EzHttpAuthProvider authProvider = new EzHttpNoAuthProvider();
	@Setter
	private boolean followRedirects = true;

	private Double uploadSpeedCapMibitPerSecond = null;


	// SETTERS
	@API
	public void setUploadSpeedCapMibitPerSecond(@Nullable Integer uploadSpeedCap)
	{
		Double uploadSpeedCapDouble = (uploadSpeedCap == null) ? null : uploadSpeedCap.doubleValue();
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
		HttpUriRequest apacheRequest = buildApacheRequest();

		EzHttpRequestTimeouter.RequestTimeoutTask timeoutTask = EzHttpRequestTimeouter.scheduleTimeout(apacheRequest, timeout, cancelOnInterrupt);
		try(CloseableHttpClient httpClient = buildHttpClient();
				CloseableHttpResponse response = httpClient.execute(apacheRequest))
		{
			EzHttpResponse<T> ezHttpResponse = readResponse(response);
			timeoutTask.cancel();
			return new EzHttpIoResponse<>(ezHttpResponse, null);
		}
		catch(IOException e)
		{
			IOException exception = e;
			if(timeoutTask.wasRequestThreadInterrupted())
				exception = new IoInterruptedException();
			else if(timeoutTask.didTimeout())
				exception = new IoTimeoutException(timeoutTask.getDuration());

			return new EzHttpIoResponse<>(null, exception);
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

	@SuppressWarnings("deprecation")
	private Builder buildRequestConfig()
	{
		Builder requestConfigBuilder = RequestConfig.custom();

		// TODO what exactly is this used for? it had some function, but I'm unsure what exactly it does
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


	// ABORTION
	public static class IoTimeoutException extends IOException
	{

		// INIT
		public IoTimeoutException(Duration timeout)
		{
			super("Request aborted after timeout of "+DurationDisplay.display(timeout));
		}

	}

	public static class IoInterruptedException extends IOException
	{

		// INIT
		public IoInterruptedException()
		{
			super("Request aborted due to thread interrupt");
		}

	}


	// THROTTLING
	@RequiredArgsConstructor
	private static class ThrottlingInputStream extends InputStream
	{

		private final InputStream backingStream;
		private final long bytesPerSecond;

		// STATUS
		private Instant start;
		private long bytesUsed = 0;


		// THROTTLING
		private long getBytesUseLimit()
		{
			Duration age = DurationUtil.toNow(start);
			final int secondsPerMinute = 60;
			double secondsSinceStart = DurationUtil.getMinutesDecimal(age)*secondsPerMinute;

			long totalBytesAvailable = Math.round(bytesPerSecond*secondsSinceStart);
			return totalBytesAvailable;
		}


		// INPUT STREAM
		@Override
		public int read() throws IOException
		{
			var read = backingStream.read();
			if(read == -1)
				return -1;

			while(bytesUsable() <= 0)
				ThreadUtil.sleep(10);

			bytesUsed++;
			return read;
		}

		@Override
		public synchronized int available() throws IOException
		{
			long available = bytesUsable();

			int backingAvailable = backingStream.available();
			if(backingAvailable < available)
				available = backingAvailable;

			return (int) available;
		}

		private int bytesUsable()
		{
			if(start == null)
				start = Instant.now();

			long usable = getBytesUseLimit()-bytesUsed;
			if(usable > (3*bytesPerSecond)) // prevent accumulation of usable bytes by backlog
			{
				bytesUsed += bytesPerSecond;
				usable -= bytesPerSecond;
			}

			if(usable < 0)
				usable = 0;

			return (int) usable;
		}

		@Override
		public void close() throws IOException
		{
			backingStream.close();
		}

	}

}
