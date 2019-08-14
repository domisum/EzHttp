package de.domisum.ezhttp.response;

import de.domisum.ezhttp.EzHttpHeader;
import de.domisum.lib.auxilium.util.java.annotations.API;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class EzHttpResponse<T>
{

	@Getter
	private final int statusCode;
	private final List<EzHttpHeader> headers;
	private final T body;


	// GETTERS
	@API
	public List<EzHttpHeader> getHeaders()
	{
		return Collections.unmodifiableList(headers);
	}

}
