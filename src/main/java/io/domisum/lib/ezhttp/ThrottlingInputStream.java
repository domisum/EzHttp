package io.domisum.lib.ezhttp;

import io.domisum.lib.auxiliumlib.util.java.thread.ThreadUtil;
import io.domisum.lib.auxiliumlib.util.DurationUtil;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

@RequiredArgsConstructor
public class ThrottlingInputStream extends InputStream
{

	// INPUT
	private final InputStream backingStream;
	private final long bytesPerSecond;

	// STATUS
	private Instant start;
	private long bytesUsed = 0;


	// THROTTLING
	private long getBytesUseLimit()
	{
		final int secondsPerMinute = 60;

		var age = DurationUtil.toNow(start);
		double secondsSinceStart = DurationUtil.getMinutesDecimal(age)*secondsPerMinute;

		long totalBytesAvailable = Math.round(bytesPerSecond*secondsSinceStart);
		return totalBytesAvailable;
	}

	private int bytesUsable()
	{
		if(start == null)
			start = Instant.now();

		long usable = getBytesUseLimit()-bytesUsed;
		if(usable > (3*bytesPerSecond)) // prevent accumulation of usable bytes by backlog
		{
			bytesUsed += bytesPerSecond;
			usable -= bytesPerSecond;
		}

		if(usable < 0)
			usable = 0;

		return (int) usable;
	}


	// INPUT STREAM
	@Override
	public int read() throws IOException
	{
		var read = backingStream.read();
		if(read == -1)
			return -1;

		while(bytesUsable() <= 0)
			ThreadUtil.sleep(10);

		bytesUsed++;
		return read;
	}

	@Override
	public synchronized int available() throws IOException
	{
		long available = bytesUsable();

		int backingAvailable = backingStream.available();
		if(backingAvailable < available)
			available = backingAvailable;

		return (int) available;
	}

	@Override
	public void close() throws IOException
	{
		backingStream.close();
	}

}