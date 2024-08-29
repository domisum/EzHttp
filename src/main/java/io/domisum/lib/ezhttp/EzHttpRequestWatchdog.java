package io.domisum.lib.ezhttp;

import io.domisum.lib.auxiliumlib.util.ThreadUtil;
import io.domisum.lib.auxiliumlib.time.TimeUtil;
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
final class EzHttpRequestWatchdog
{
	
	// STATUS
	private static final Set<RequestWatchdogTask> watchdogTasks = ConcurrentHashMap.newKeySet();
	private static Thread watchdogThread = null;
	
	
	// TIMEOUT
	static RequestWatchdogTask watch(HttpUriRequest request, Duration timeoutDuration, boolean cancelOnInterrupt)
	{
		var requestThread = cancelOnInterrupt ?
			Thread.currentThread() :
			null;
		
		var requestTimeoutTask = new RequestWatchdogTask(request, requestThread, timeoutDuration);
		watchdogTasks.add(requestTimeoutTask);
		ensureThreadRunning();
		
		return requestTimeoutTask;
	}
	
	private static synchronized void ensureThreadRunning()
	{
		if(watchdogThread == null)
			watchdogThread = ThreadUtil.createAndStartDaemonThread(EzHttpRequestWatchdog::watchdogThreadRun, "ezHttpRequestWatchdog");
	}
	
	private static void watchdogThreadRun()
	{
		while(!Thread.currentThread().isInterrupted())
		{
			watchdogThreadTick();
			ThreadUtil.sleep(Duration.ofMillis(100));
		}
	}
	
	private static void watchdogThreadTick()
	{
		watchdogTasks.removeIf(t->t.getStatus() != RequestWatchdogTaskStatus.ACTIVE);
		
		for(var watchdogTask : watchdogTasks)
			watchdogTask.tick();
	}
	
	
	@RequiredArgsConstructor
	static class RequestWatchdogTask
	{
		
		// ATTRIBUTES
		private final HttpUriRequest request;
		private final Thread requestThread;
		@Getter
		private final Duration duration;
		private final Instant start = Instant.now();
		
		// STATUS
		@Getter
		private RequestWatchdogTaskStatus status = RequestWatchdogTaskStatus.ACTIVE;
		
		
		// TIMEOUT
		public synchronized boolean didTimeout()
		{
			return status == RequestWatchdogTaskStatus.TIMED_OUT;
		}
		
		public synchronized boolean wasInterrupted()
		{
			return status == RequestWatchdogTaskStatus.INTERRUPTED;
		}
		
		public synchronized void cancel()
		{
			if(status != RequestWatchdogTaskStatus.ACTIVE)
				return;
			status = RequestWatchdogTaskStatus.CANCELLED;
		}
		
		private synchronized void tick()
		{
			if(status != RequestWatchdogTaskStatus.ACTIVE)
				return;
			
			// interrupted
			if((requestThread != null) && requestThread.isInterrupted())
			{
				request.abort();
				status = RequestWatchdogTaskStatus.INTERRUPTED;
				return;
			}
			
			// timed out
			if(TimeUtil.isOlderThan(start, duration))
			{
				request.abort();
				status = RequestWatchdogTaskStatus.TIMED_OUT;
			}
		}
		
	}
	
	private enum RequestWatchdogTaskStatus
	{
		
		ACTIVE,
		TIMED_OUT,
		INTERRUPTED,
		CANCELLED
		
	}
	
}
