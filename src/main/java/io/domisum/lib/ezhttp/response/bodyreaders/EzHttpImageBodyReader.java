package io.domisum.lib.ezhttp.response.bodyreaders;

import io.domisum.lib.ezhttp.response.EzHttpResponseBodyReader;
import io.domisum.lib.auxiliumlib.util.java.annotations.API;
import lombok.RequiredArgsConstructor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@API
@RequiredArgsConstructor
public class EzHttpImageBodyReader implements EzHttpResponseBodyReader<BufferedImage>
{

	// READ
	@Override
	public BufferedImage read(InputStream inputStream) throws IOException
	{
		return ImageIO.read(inputStream);
	}

}
