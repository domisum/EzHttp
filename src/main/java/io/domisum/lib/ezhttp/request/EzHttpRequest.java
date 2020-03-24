package io.domisum.lib.ezhttp.request;

import io.domisum.lib.ezhttp.header.EzHttpHeader;
import io.domisum.lib.auxiliumlib.datacontainers.AbstractURL;
import io.domisum.lib.auxiliumlib.annotations.API;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class EzHttpRequest
{

	@Getter
	private final EzHttpRequestMethod method;
	@Getter
	private final AbstractURL url;

	private final List<EzHttpHeader> headers = new ArrayList<>();

	@Getter
	@Setter
	private EzHttpRequestBody body;


	// INIT
	@API
	public static EzHttpRequest get(AbstractURL url)
	{
		return new EzHttpRequest(EzHttpRequestMethod.GET, url);
	}


	// GETTERS
	@API
	public List<EzHttpHeader> getHeaders()
	{
		return Collections.unmodifiableList(headers);
	}


	// SETTERS
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

}
