package io.domisum.lib.ezhttp.request;

import io.domisum.lib.auxiliumlib.annotations.API;

import java.io.InputStream;

@API
public interface EzHttpRequestBody
{
	
	String getContentType();
	
	InputStream getAsInputStream();
	
}
