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
	public T getSuccessBodyOrThrowHttpIoException()
			throws IOException
	{
		ifFailedThrowHttpIoException();
		return successBody;
	}
	
	@API
	public T getSuccessBodyOrThrowHttpIoException(Function<IOException,IOException> wrapper)
			throws IOException
	{
		try
		{
			return getSuccessBodyOrThrowHttpIoException();
		}
		catch(IOException e)
		{
			throw wrapper.apply(e);
		}
	}
	
	@API
	public T getSuccessBodyOrThrowHttpIoException(String wrappedMessage)
			throws IOException
	{
		return getSuccessBodyOrThrowHttpIoException(e->new IOException(wrappedMessage, e));
	}
	
	
	@API
	public void ifFailedThrowHttpIoException()
			throws IOException
	{
		if(isSuccess())
			return;
		throw getFailureIoException();
	}
	
	@API
	public void ifFailedThrowHttpIoException(String wrappedMessage)
			throws IOException
	{
		try
		{
			ifFailedThrowHttpIoException();
		}
		catch(IOException e)
		{
			throw new IOException(wrappedMessage, e);
		}
	}
	
	@API
	public IOException getFailureIoException()
	{
		if(isSuccess())
			throw new IllegalStateException("can't get failureIoException of successful response");
		return new IOException("HTTP "+statusCode+", body:\n"+failureBody);
	}
	
}
