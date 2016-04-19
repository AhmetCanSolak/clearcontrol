package rtlib.gui.video.video3d.demo;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import clearcuda.CudaContext;
import clearcuda.CudaDevice;
import clearcuda.CudaHostPointer;
import clearcuda.memory.CudaMemory;
import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;
import coremem.offheap.OffHeapMemory;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;
import rtlib.core.variable.Variable;
import rtlib.gui.video.video3d.Stack3DDisplay;
import rtlib.stack.ContiguousOffHeapPlanarStackFactory;
import rtlib.stack.OffHeapPlanarStack;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;

public class VideoFrame3DDisplayDemos
{
	private static final int cMaximumNumberOfObjects = 1024;

	@Test
	public void demoContiguousStackNoRecycler() throws InterruptedException
	{

		final long lResolutionX = 320;
		final long lResolutionY = lResolutionX + 1;
		final long lResolutionZ = lResolutionX / 2;

		final ContiguousMemoryInterface lContiguousMemory = OffHeapMemory.allocateShorts(lResolutionX * lResolutionY
																																											* lResolutionZ);

		final ContiguousBuffer lContiguousBuffer = new ContiguousBuffer(lContiguousMemory);

		@SuppressWarnings("unchecked")
		final OffHeapPlanarStack lStack = OffHeapPlanarStack.createStack(	lContiguousMemory,
																																			lResolutionX,
																																			lResolutionY,
																																			lResolutionZ);

		final Stack3DDisplay lVideoFrame3DDisplay = new Stack3DDisplay("Test");

		final Variable<StackInterface> lFrameReferenceVariable = lVideoFrame3DDisplay.getStackInputVariable();

		lVideoFrame3DDisplay.open();

		for (int i = 0; i < 32000 && lVideoFrame3DDisplay.isShowing(); i++)
		{

			lContiguousBuffer.rewind();
			for (int z = 0; z < lResolutionZ; z++)
			{
				for (int y = 0; y < lResolutionY; y++)
				{
					for (int x = 0; x < lResolutionX; x++)
					{
						final short lValue = (short) (i + x ^ y ^ z);
						lContiguousBuffer.writeShort(lValue);
					}
				}
			}

			lFrameReferenceVariable.set(lStack);
			Thread.sleep(10);
		}

		lVideoFrame3DDisplay.close();

		// Thread.sleep(1000);

	}

	@Test
	public void demoPinnedMemoryStackNoRecycler() throws InterruptedException
	{

		final CudaDevice lCudaDevice = new CudaDevice(0);
		final CudaContext lCudaContext = new CudaContext(	lCudaDevice,
																											false);

		final long lResolutionX = 320;
		final long lResolutionY = lResolutionX + 1;
		final long lResolutionZ = lResolutionX / 2;

		final CudaHostPointer lCudaHostPointer = CudaHostPointer.mallocPinned(2		* lResolutionX
																																							* lResolutionY
																																							* lResolutionZ,
																																					false,
																																					false);
		final ContiguousMemoryInterface lContiguousMemory = new CudaMemory(lCudaHostPointer);

		final ContiguousBuffer lContiguousBuffer = new ContiguousBuffer(lContiguousMemory);

		@SuppressWarnings("unchecked")
		final OffHeapPlanarStack lStack = OffHeapPlanarStack.createStack(	lContiguousMemory,
																																			lResolutionX,
																																			lResolutionY,
																																			lResolutionZ);

		final Stack3DDisplay lVideoFrame3DDisplay = new Stack3DDisplay("Test");

		final Variable<StackInterface> lFrameReferenceVariable = lVideoFrame3DDisplay.getStackInputVariable();

		lVideoFrame3DDisplay.open();

		for (int i = 0; i < 32000 && lVideoFrame3DDisplay.isShowing(); i++)
		{

			lContiguousBuffer.rewind();
			for (int z = 0; z < lResolutionZ; z++)
			{
				for (int y = 0; y < lResolutionY; y++)
				{
					for (int x = 0; x < lResolutionX; x++)
					{
						final short lValue = (short) (i + x ^ y ^ z);
						lContiguousBuffer.writeShort(lValue);
					}
				}
			}/**/

			lFrameReferenceVariable.set(lStack);
			Thread.sleep(1);
		}

		lVideoFrame3DDisplay.close();

		lCudaContext.close();

		lCudaDevice.close();

		// Thread.sleep(1000);

	}

	@Test
	public void demoStackRecycler() throws InterruptedException
	{

		for (int r = 0; r < 3; r++)
		{
			final long lResolutionX = 320;
			final long lResolutionY = lResolutionX + 1;
			final long lResolutionZ = lResolutionX / 2;

			final ContiguousOffHeapPlanarStackFactory lOffHeapPlanarStackFactory = new ContiguousOffHeapPlanarStackFactory();

			final RecyclerInterface<StackInterface, StackRequest> lRecycler = new BasicRecycler<StackInterface, StackRequest>(lOffHeapPlanarStackFactory,
																																																												cMaximumNumberOfObjects);

			final Stack3DDisplay lVideoFrame3DDisplay = new Stack3DDisplay("Test");

			final Variable<StackInterface> lFrameReferenceVariable = lVideoFrame3DDisplay.getStackInputVariable();

			lVideoFrame3DDisplay.open();

			for (int i = 0; i < 32000 && lVideoFrame3DDisplay.isShowing(); i++)
			{

				final StackInterface lStack = OffHeapPlanarStack.getOrWaitWithRecycler(	lRecycler,
																																								10,
																																								TimeUnit.MILLISECONDS,
																																								lResolutionX,
																																								lResolutionY,
																																								lResolutionZ);
				final ContiguousBuffer lContiguousBuffer = new ContiguousBuffer(lStack.getContiguousMemory());
				lContiguousBuffer.rewind();
				for (int z = 0; z < lResolutionZ; z++)
				{
					for (int y = 0; y < lResolutionY; y++)
					{
						for (int x = 0; x < lResolutionX; x++)
						{
							final short lValue = (short) (i + x ^ y ^ z);
							lContiguousBuffer.writeShort(lValue);
						}
					}
				}

				lFrameReferenceVariable.set(lStack);
				// Thread.sleep(1);

				if (i % 100 == 0)
				{
					System.out.println("lRecycler.getNumberOfAvailableObjects()=" + lRecycler.getNumberOfAvailableObjects());
					System.out.println("lRecycler.getNumberOfLiveObjects()=" + lRecycler.getNumberOfLiveObjects());
				}
			}

			lVideoFrame3DDisplay.close();

			// Thread.sleep(1000);

		}
	}

}
