package io.domisum.lib.ezhttp.request;

import io.domisum.lib.auxiliumlib.annotations.API;
import lombok.RequiredArgsConstructor;

@API
@RequiredArgsConstructor
public enum EzHttpMethod
{
	
	GET,
	HEAD,
	OPTIONS,
	
	POST,
	PUT,
	DELETE,
	PATCH
	
}
