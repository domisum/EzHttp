package de.domisum.ezhttp.request;

import de.domisum.lib.auxilium.data.container.AbstractURL;
import de.domisum.lib.auxilium.mattp.MattpHeader;
import de.domisum.lib.auxilium.mattp.request.MattpMethod;
import de.domisum.lib.auxilium.util.java.annotations.API;
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
	private final MattpMethod method;
	@Getter
	private final AbstractURL url;

	private final List<MattpHeader> headers = new ArrayList<>();

	@Getter
	@Setter
	private EzHttpRequestBody body;


	// INIT
	@API
	public static EzHttpRequest get(AbstractURL url)
	{
		return new EzHttpRequest(MattpMethod.GET, url);
	}


	// GETTERS
	@API
	public List<MattpHeader> getHeaders()
	{
		return Collections.unmodifiableList(headers);
	}


	// SETTERS
	@API
	public void addHeader(CharSequence key, CharSequence value)
	{
		addHeader(new MattpHeader(key, value));
	}

	@API
	public void addHeader(MattpHeader header)
	{
		headers.add(header);
	}

}
