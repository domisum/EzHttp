package io.domisum.lib.ezhttp.request;

import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.ezhttp.header.EzHttpHeader;
import io.domisum.lib.ezhttp.request.url.EzUrl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@API
@RequiredArgsConstructor
public class EzHttpRequest
{
	
	@Getter
	private final EzHttpMethod method;
	@Getter
	private final EzUrl url;
	
	private final List<EzHttpHeader> headers = new ArrayList<>();
	
	@Getter
	@Setter
	private EzHttpRequestBody body;
	
	
	// INIT
	@API
	public static EzHttpRequest get(EzUrl url)
	{
		return new EzHttpRequest(EzHttpMethod.GET, url);
	}
	
	@API
	public static EzHttpRequest post(EzUrl url)
	{
		return new EzHttpRequest(EzHttpMethod.POST, url);
	}
	
	
	// GETTERS
	@API
	public List<EzHttpHeader> getHeaders()
	{
		return new ArrayList<>(headers);
	}
	
	
	// MODIFICATION
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
	
}
