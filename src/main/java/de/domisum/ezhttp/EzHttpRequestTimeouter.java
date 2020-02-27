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
	static synchronized RequestTimeoutTask scheduleTimeout(
			HttpUriRequest request, Duration timeoutDuration, boolean cancelOnInterrupt)
	{
		var threadToWatchForInterrupt = cancelOnInterrupt ? Thread.currentThread() : null;
		RequestTimeoutTask requestTimeoutTask = new RequestTimeoutTask(request, threadToWatchForInterrupt, timeoutDuration);
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
	static class RequestTimeoutTask
	{

		private final HttpUriRequest request;
		private final Thread threadToWatchForInterrupt;
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

		public synchronized boolean wasRequestThreadInterrupted()
		{
			return status == RequestTimeoutStatus.REQUEST_THREAD_INTERRUPTED;
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

			if((threadToWatchForInterrupt != null) && threadToWatchForInterrupt.isInterrupted())
			{
				request.abort();
				status = RequestTimeoutStatus.REQUEST_THREAD_INTERRUPTED;
				return;
			}

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
		REQUEST_THREAD_INTERRUPTED,
		CANCELLED

	}

}
