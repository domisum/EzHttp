package de.domisum.ezhttp.response;

import de.domisum.ezhttp.EzHttpHeader;
import de.domisum.lib.auxilium.util.java.annotations.API;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

		if((successBody == null) && (failureBody == null))
			throw new IllegalArgumentException("successBody and failureBody can't both be null at the same time");

		if((successBody != null) && (failureBody != null))
			throw new IllegalArgumentException("successBody and failureBody can't both be set at the same time");
	}


	// GETTERS
	@API
	public List<EzHttpHeader> getHeaders()
	{
		return Collections.unmodifiableList(headers);
	}

	@API
	public boolean isSuccess()
	{
		return successBody != null;
	}

	@API
	public T getSuccessBodyOrThrowHttpIoException(String wrappedMessage) throws IOException
	{
		try
		{
			return getSuccessBodyOrThrowHttpIoException();
		}
		catch(IOException e)
		{
			throw new IOException(wrappedMessage, e);
		}
	}

	public T getSuccessBodyOrThrowHttpIoException() throws IOException
	{
		if(!isSuccess())
			throw new IOException("HTTP code "+statusCode+" response:\n"+failureBody);

		return successBody;
	}

}
