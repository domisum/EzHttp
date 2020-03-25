package io.domisum.lib.ezhttp.request;

import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.auxiliumlib.datacontainers.AbstractUrl;
import io.domisum.lib.ezhttp.header.EzHttpHeader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@API
@RequiredArgsConstructor
public class EzHttpRequest
{
	
	@Getter
	private final EzHttpRequestMethod method;
	@Getter
	private final AbstractUrl url;
	
	private final List<EzHttpHeader> headers = new ArrayList<>();
	
	@Getter
	@Setter
	private EzHttpRequestBody body;
	
	
	// INIT
	@API
	public static EzHttpRequest get(AbstractUrl url)
	{
		return new EzHttpRequest(EzHttpRequestMethod.GET, url);
	}
	
	
	// GETTERS
	@API
	public List<EzHttpHeader> getHeaders()
	{
		return new ArrayList<>(headers);
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
