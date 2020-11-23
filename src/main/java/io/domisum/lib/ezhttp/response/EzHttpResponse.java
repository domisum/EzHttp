package io.domisum.lib.ezhttp.response;

import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.ezhttp.header.EzHttpHeader;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@API
public class EzHttpResponse<T>
{
	
	@Getter
	private final int statusCode;
	private final List<EzHttpHeader> headers;
	@Getter
	private final T successBody;
	@Getter
	private final String failureBody;
	
	
	// INIT
	public EzHttpResponse(int statusCode, List<EzHttpHeader> headers, T successBody, String failureBody)
	{
		this.statusCode = statusCode;
		this.headers = new ArrayList<>(headers);
		this.successBody = successBody;
		this.failureBody = failureBody;
		
		if((successBody != null) && (failureBody != null))
			throw new IllegalArgumentException("successBody and failureBody can't both be set at the same time");
	}
	
	
	// GETTERS
	@API
	public List<EzHttpHeader> getHeaders()
	{
		return new ArrayList<>(headers);
	}
	
	@API
	public boolean isSuccess()
	{
		return failureBody == null;
	}
	
	
	@API
	public T getSuccessBodyOrThrowHttpException()
		throws EzHttpException
	{
		ifFailedThrowHttpException();
		return successBody;
	}
	
	@API
	public <E extends IOException> T getSuccessBodyOrThrowHttpException(Function<IOException, E> wrapper)
		throws E
	{
		try
		{
			return getSuccessBodyOrThrowHttpException();
		}
		catch(IOException e)
		{
			throw wrapper.apply(e);
		}
	}
	
	@API
	public T getSuccessBodyOrThrowHttpException(String wrappedMessage)
		throws IOException
	{
		return getSuccessBodyOrThrowHttpException(e->new IOException(wrappedMessage, e));
	}
	
	
	@API
	public void ifFailedThrowHttpException()
		throws EzHttpException
	{
		if(isSuccess())
			return;
		throw getHttpException();
	}
	
	@API
	public void ifFailedThrowHttpException(String wrappedMessage)
		throws IOException
	{
		try
		{
			ifFailedThrowHttpException();
		}
		catch(IOException e)
		{
			throw new IOException(wrappedMessage, e);
		}
	}
	
	@API
	public EzHttpException getHttpException()
	{
		if(isSuccess())
			throw new IllegalStateException("Can't get failureIoException of successful response");
		
		return new EzHttpException(statusCode, failureBody);
	}
	
	
	// EXCEPTION
	@API
	public static class EzHttpException
		extends IOException
	{
		
		@Getter
		private final int statusCode;
		@Getter
		private final String responseBody;
		
		
		// INIT
		protected EzHttpException(int statusCode, String responseBody)
		{
			super("HTTP "+statusCode+", body:\n"+responseBody);
			this.statusCode = statusCode;
			this.responseBody = responseBody;
		}
		
		
		// GETTERS
		@API
		public boolean isClientError()
		{
			return getStatusCodeHundreds() == 4;
		}
		
		@API
		public boolean isServerError()
		{
			return getStatusCodeHundreds() == 5;
		}
		
		@API
		public int getStatusCodeHundreds()
		{
			return statusCode/100;
		}
		
	}
	
}
