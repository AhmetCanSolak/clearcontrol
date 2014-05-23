package rtlib.core.concurrent.asyncprocs;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import rtlib.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
import rtlib.core.concurrent.queues.BestBlockingQueue;
import rtlib.core.concurrent.timing.Waiting;
import rtlib.core.log.Loggable;

public abstract class AsynchronousProcessorBase<I, O> implements
																											AsynchronousProcessorInterface<I, O>,
																											AsynchronousExecutorServiceAccess,
																											AsynchronousSchedulerServiceAccess,
																											Loggable,
																											Waiting
{

	private String mName;
	private AsynchronousProcessorInterface<O, ?> mReceiver;
	private final BlockingQueue<I> mInputQueue;
	private AtomicReference<ScheduledFuture<?>> mScheduledFuture = new AtomicReference<>();

	public AsynchronousProcessorBase(	final String pName,
																		final int pMaxQueueSize)
	{
		super();
		mName = pName;
		mInputQueue = BestBlockingQueue.newQueue(pMaxQueueSize <= 0	? 1
																																: pMaxQueueSize);

	}

	@Override
	public void connectToReceiver(final AsynchronousProcessorInterface<O, ?> pAsynchronousProcessor)
	{
		mReceiver = pAsynchronousProcessor;
	}

	@Override
	public boolean start()
	{
		try
		{
			Runnable lRunnable = () -> {

				try
				{
					final I lInput = mInputQueue.poll(1, TimeUnit.SECONDS);
					if (lInput == null)
					{
						return;
					}
					final O lOutput = process(lInput);
					if (lOutput != null)
					{
						send(lOutput);
					}
				}
				catch (final Throwable e)
				{
					e.printStackTrace();
				}

			};

			mScheduledFuture.set(scheduleAtFixedRate(	lRunnable,
																								1,
																								TimeUnit.NANOSECONDS));

			return true;
		}
		catch (Exception e)
		{
			error("Concurrent",
						"Error while starting " + this.getClass().getSimpleName());
			return false;
		}
	}

	@Override
	public boolean stop()
	{
		try
		{
			stopScheduledThreadPoolAndWaitForCompletion(1, TimeUnit.SECONDS);
			mScheduledFuture.set(null);
			return true;
		}
		catch (ExecutionException e)
		{
			return stop();
		}
	}

	@Override
	public void close()
	{
		this.stop();
	}

	@Override
	public boolean passOrWait(final I pObject,
														final long pTimeOut,
														TimeUnit pTimeUnit)
	{
		waitFor(pTimeOut, pTimeUnit, () -> mScheduledFuture.get() != null);
		try
		{
			if (pObject == null)
				return false;
			mInputQueue.offer(pObject, pTimeOut, pTimeUnit);
		}
		catch (final InterruptedException e)
		{
			return passOrWait(pObject, pTimeOut, pTimeUnit);
		}
		return false;
	}

	@Override
	public boolean passOrWait(final I pObject)
	{
		waitFor(() -> mScheduledFuture.get() != null);
		try
		{
			if (pObject == null)
				return false;
			mInputQueue.put(pObject);
		}
		catch (final InterruptedException e)
		{
			return passOrWait(pObject);
		}
		return false;
	}

	@Override
	public boolean passOrFail(final I pObject)
	{
		if (mScheduledFuture.get() == null)
		{
			return false;
		}

		if (pObject != null)
			return mInputQueue.offer(pObject);
		else
			return false;
	}

	@Override
	public abstract O process(I pInput);

	protected void send(final O lOutput)
	{
		if (mReceiver != null)
		{
			mReceiver.passOrWait(lOutput);
		}
	}

	@Override
	public int getInputQueueLength()
	{
		return mInputQueue.size();
	}

	@Override
	public int getRemainingCapacity()
	{
		return mInputQueue.remainingCapacity();
	}

	@Override
	public boolean waitToFinish(final long pTimeOut, TimeUnit pTimeUnit)
	{
		waitFor(pTimeOut, pTimeUnit, () -> mInputQueue.isEmpty());
		return mInputQueue.isEmpty();
	}

	public BlockingQueue<I> getInputQueue()
	{
		return mInputQueue;
	}

}
