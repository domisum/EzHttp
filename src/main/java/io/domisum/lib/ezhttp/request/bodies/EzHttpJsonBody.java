package io.domisum.lib.ezhttp.request.bodies;

import io.domisum.lib.auxiliumlib.annotations.API;

import java.nio.charset.Charset;

@API
public class EzHttpJsonBody extends EzHttpPlaintextBody
{

	// INIT
	@API
	public EzHttpJsonBody(String text, Charset charset)
	{
		super(text, charset);
	}

	@API
	public EzHttpJsonBody(String text)
	{
		super(text);
	}


	// BODY
	@Override
	public String getContentType()
	{
		return "application/json";
	}

}
