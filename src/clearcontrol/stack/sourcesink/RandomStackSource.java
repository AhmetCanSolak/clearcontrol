package clearcontrol.stack.sourcesink;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.units.Magnitude;
import clearcontrol.core.variable.Variable;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.buffers.ContiguousBuffer;
import coremem.recycling.RecyclerInterface;

public class RandomStackSource implements StackSourceInterface
{

	private RecyclerInterface<StackInterface, StackRequest> mStackBasicRecycler;
	private final Variable<Long> mWidthVariable;
	private final Variable<Long> mHeightVariable;
	private final Variable<Long> mDepthVariable;

	public RandomStackSource(	long pWidth,
														long pHeight,
														long pDepth,
														final RecyclerInterface<StackInterface, StackRequest> pStackRecycler)
	{
		mWidthVariable = new Variable<Long>("Width",pWidth);
		mHeightVariable = new Variable<Long>("Height", pHeight);
		mDepthVariable = new Variable<Long>("Depth",pDepth);
		mStackBasicRecycler = pStackRecycler;
	}

	@Override
	public void setStackRecycler(final RecyclerInterface<StackInterface, StackRequest> pStackRecycler)
	{
		mStackBasicRecycler = pStackRecycler;
	}

	@Override
	public boolean update()
	{
		return true;
	}

	@Override
	public long getNumberOfStacks()
	{
		return Long.MAX_VALUE;
	}

	@Override
	public double getStackTimeStampInSeconds(long pStackIndex)
	{
		return Magnitude.nano2unit(System.nanoTime());
	}

	@Override
	public StackInterface getStack(final long pStackIndex)
	{
		return getStack(pStackIndex, 1, TimeUnit.NANOSECONDS);
	}

	@Override
	public StackInterface getStack(	final long pStackIndex,
																	long pTime,
																	TimeUnit pTimeUnit)
	{
		if (mStackBasicRecycler == null)
		{
			return null;
		}
		try
		{
			final long lWidth = getWidthVariable().get();
			final long lHeight = getWidthVariable().get();
			final long lDepth = getWidthVariable().get();
			
			final StackRequest lStackRequest = StackRequest.build(lWidth,
																														lHeight,
																														lDepth);

			final StackInterface lStack = mStackBasicRecycler.getOrWait(pTime,
																																	pTimeUnit,
																																	lStackRequest);

			if (lStack.getContiguousMemory() != null)
			{
				final ContiguousBuffer lContiguousBuffer = new ContiguousBuffer(lStack.getContiguousMemory());
				lContiguousBuffer.rewind();
				for (int z = 0; z < lDepth; z++)
				{
					for (int y = 0; y < lHeight; y++)
					{
						for (int x = 0; x < lWidth; x++)
						{
							final short lValue = (short) (pStackIndex + x ^ y ^ z);
							lContiguousBuffer.writeShort(lValue);
						}
					}
				}

			}

			lStack.setTimeStampInNanoseconds(System.nanoTime());
			lStack.setIndex(pStackIndex);

			return lStack;
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return null;
		}

	}

	public Variable<Long> getWidthVariable()
	{
		return mWidthVariable;
	}

	public Variable<Long> getHeightVariable()
	{
		return mHeightVariable;
	}

	public Variable<Long> getDepthVariable()
	{
		return mDepthVariable;
	}

}