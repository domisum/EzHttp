package io.domisum.lib.ezhttp;

import io.domisum.lib.auxiliumlib.util.DurationUtil;
import io.domisum.lib.auxiliumlib.util.thread.ThreadUtil;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

@RequiredArgsConstructor
class ThrottlingInputStream
		extends InputStream
{
	
	// INPUT
	private final InputStream backingStream;
	private final long bytesPerSecond;
	
	// STATUS
	private Instant readStart;
	private long byteReadSlotsUsed = 0;
	
	
	// THROTTLING
	private long getBytesUseLimit()
	{
		final int secondsPerMinute = 60;
		
		var age = DurationUtil.toNow(readStart);
		double secondsSinceStart = DurationUtil.getMinutesDecimal(age)*secondsPerMinute;
		long totalBytesAvailable = Math.round(bytesPerSecond*secondsSinceStart);
		return totalBytesAvailable;
	}
	
	private int bytesUsable()
	{
		if(readStart == null)
			readStart = Instant.now();
		
		long usable = getBytesUseLimit()-byteReadSlotsUsed;
		if(usable>(3*bytesPerSecond)) // prevent accumulation of usable bytes by backlog
		{
			byteReadSlotsUsed += bytesPerSecond;
			usable -= bytesPerSecond;
		}
		if(usable<0)
			usable = 0;
		
		return (int) usable;
	}
	
	
	// INPUT STREAM
	@Override
	public int read()
			throws IOException
	{
		var read = backingStream.read();
		if(read == -1)
			return -1;
		
		while(bytesUsable()<=0)
			ThreadUtil.sleep(10);
		
		byteReadSlotsUsed++;
		return read;
	}
	
	@Override
	public synchronized int available()
			throws IOException
	{
		int available = bytesUsable();
		int backingAvailable = backingStream.available();
		if(backingAvailable<available)
			available = backingAvailable;
		
		return available;
	}
	
	@Override
	public void close()
			throws IOException
	{
		backingStream.close();
	}
	
}
