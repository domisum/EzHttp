package de.domisum.ezhttp;

import de.domisum.lib.auxilium.util.java.ThreadUtil;
import de.domisum.lib.auxilium.util.time.DurationUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.HttpUriRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EzHttpRequestTimeouter
{

	// STATUS
	private static final Set<RequestTimeoutTask> timeouts = ConcurrentHashMap.newKeySet();
	private static Thread timeoutThread = null;


	// TIMEOUT
	public static synchronized RequestTimeoutTask scheduleTimeout(HttpUriRequest reuqest, Duration timeoutDuration)
	{
		RequestTimeoutTask requestTimeoutTask = new RequestTimeoutTask(reuqest, timeoutDuration);
		timeouts.add(requestTimeoutTask);

		if(timeoutThread == null)
			timeoutThread = ThreadUtil.createAndStartDaemonThread(EzHttpRequestTimeouter::timeoutThreadRun,
					"ezHttpRequestTimeouter"
			);

		return requestTimeoutTask;
	}

	private static void timeoutThreadRun()
	{
		while(!Thread.currentThread().isInterrupted())
		{
			timeoutThreadTick();
			ThreadUtil.sleep(Duration.ofMillis(100));
		}
	}

	private static void timeoutThreadTick()
	{
		for(RequestTimeoutTask timeout : timeouts)
			timeout.tick();

		timeouts.removeIf(t->t.getStatus() != RequestTimeoutStatus.ACTIVE);
	}


	@RequiredArgsConstructor
	public static class RequestTimeoutTask
	{

		// REFERENCES
		private final HttpUriRequest request;
		@Getter
		private final Duration duration;
		private final Instant start = Instant.now();

		// STATUS
		@Getter
		private RequestTimeoutStatus status = RequestTimeoutStatus.ACTIVE;


		// TIMEOUT
		public synchronized boolean didTimeout()
		{
			return status == RequestTimeoutStatus.TIMED_OUT;
		}

		public synchronized void cancel()
		{
			if(status != RequestTimeoutStatus.ACTIVE)
				return;

			status = RequestTimeoutStatus.CANCELLED;
		}

		private synchronized void tick()
		{
			if(status != RequestTimeoutStatus.ACTIVE)
				return;

			if(DurationUtil.isOlderThan(start, duration))
			{
				request.abort();
				status = RequestTimeoutStatus.TIMED_OUT;
			}
		}

	}

	private enum RequestTimeoutStatus
	{

		ACTIVE,
		TIMED_OUT,
		CANCELLED

	}

}
