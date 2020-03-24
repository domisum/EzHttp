package io.domisum.lib.ezhttp.request;

import io.domisum.lib.auxiliumlib.annotations.API;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@API
public enum EzHttpRequestMethod
{
	
	GET,
	HEAD,
	OPTIONS,
	
	POST,
	PUT,
	DELETE,
	PATCH
	
}
