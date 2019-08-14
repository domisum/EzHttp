package de.domisum.ezhttp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.HttpUriRequest;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

@RequiredArgsConstructor
public class EzHttpRequestTimeouter
{

	// INPUT
	private final HttpUriRequest request;
	@Getter
	private final Duration timeout;

	// STATUS
	private final Timer timer = new Timer(true);
	private boolean ended = false;
	private final Object lock = new Object();


	// PROCESS
	public void start()
	{
		TimerTask abortTask = new TimerTask()
		{
			@Override
			public void run()
			{
				synchronized(lock)
				{
					if(ended)
						return;

					ended = true;
					request.abort();
				}
			}
		};

		timer.schedule(abortTask, timeout.toMillis());
	}

	public boolean didTimeOutAndEnd()
	{
		synchronized(lock)
		{
			if(ended)
				return true;

			ended = true;
			timer.cancel();

			return false;
		}
	}

}
