package io.domisum.lib.ezhttp.header.s;

import io.domisum.lib.auxiliumlib.PHR;
import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.ezhttp.header.EzHttpHeader;

@API
public class EzHttpHeader_DownloadFileName
	extends EzHttpHeader
{
	
	// INIT
	@API
	public EzHttpHeader_DownloadFileName(String fileName)
	{
		super("Content-Disposition", PHR.r("attachment; filename=\"{}\"", fileName));
	}
	
}
