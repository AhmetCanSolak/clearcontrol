package rtlib.core.concurrent.asyncprocs;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import rtlib.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
import rtlib.core.concurrent.executors.CompletingThreadPoolExecutor;
import rtlib.core.concurrent.executors.RTlibExecutors;
import rtlib.core.log.Loggable;

public class AsynchronousProcessorPool<I, O>	extends
																							AsynchronousProcessorBase<I, O>	implements
																																							AsynchronousProcessorInterface<I, O>,
																																							AsynchronousExecutorServiceAccess,
																																							AsynchronousSchedulerServiceAccess,
																																							Loggable
{

	private final ProcessorInterface<I, O> mProcessor;
	private CompletingThreadPoolExecutor mThreadPoolExecutor;

	public AsynchronousProcessorPool(	final String pName,
																		final int pMaxQueueSize,
																		final int pThreadPoolSize,
																		final ProcessorInterface<I, O> pProcessor)
	{
		super(pName, pMaxQueueSize);
		mThreadPoolExecutor = RTlibExecutors.getOrCreateThreadPoolExecutor(	this,
																																				Thread.NORM_PRIORITY,
																																				pThreadPoolSize,
																																				pThreadPoolSize,
																																				pMaxQueueSize);

		mProcessor = pProcessor;
	}

	public AsynchronousProcessorPool(	final String pName,
																		final int pMaxQueueSize,
																		final ProcessorInterface<I, O> pProcessor)
	{
		this(	pName,
					pMaxQueueSize,
					Runtime.getRuntime().availableProcessors(),
					pProcessor);
	}

	@Override
	public boolean start()
	{
		Runnable lRunnable = () -> {
			try
			{
				@SuppressWarnings("unchecked")
				Future<O> lFuture = (Future<O>) mThreadPoolExecutor.getFutur(	1,
																																			TimeUnit.NANOSECONDS);
				if (lFuture != null)
				{
					O lResult = lFuture.get();
					send(lResult);
				}
			}
			catch (InterruptedException e)
			{
				return;
			}
			catch (ExecutionException e)
			{
				error("Concurrent",
							"Exception while collecting processed items",
							e);
			}
		};

		scheduleAtFixedRate(lRunnable, 1, TimeUnit.NANOSECONDS);

		return super.start();
	}

	@Override
	public boolean stop()
	{
		try
		{
			stopScheduledThreadPoolAndWaitForCompletion(1, TimeUnit.SECONDS);
			return true;
		}
		catch (ExecutionException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public final O process(final I pInput)
	{
		Callable<O> lCallable = () -> {
			return mProcessor.process(pInput);
		};
		mThreadPoolExecutor.submit(lCallable);
		return null;
	}

}
