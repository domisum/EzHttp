package de.domisum.ezhttp.request;

import java.io.InputStream;

public interface EzHttpRequestBody
{

	String getContentType();

	InputStream getAsInputStream();

}
