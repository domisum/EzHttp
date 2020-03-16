package io.domisum.lib.ezhttp.request;

import java.io.InputStream;

public interface EzHttpRequestBody
{

	String getContentType();

	InputStream getAsInputStream();

}
