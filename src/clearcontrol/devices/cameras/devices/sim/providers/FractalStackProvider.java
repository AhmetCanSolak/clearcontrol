package clearcontrol.devices.cameras.devices.sim.providers;

import java.util.ArrayList;

import clearcontrol.devices.cameras.devices.sim.StackCameraDeviceSimulator;
import clearcontrol.devices.cameras.devices.sim.StackCameraSimulationProvider;
import clearcontrol.devices.cameras.devices.sim.StackCameraSimulationProviderBase;
import clearcontrol.stack.StackInterface;
import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;

/**
 * Fractal stack provider for stack camera simulator
 *
 * @author royer
 */
public class FractalStackProvider extends
                                  StackCameraSimulationProviderBase
                                  implements
                                  StackCameraSimulationProvider
{

  @Override
  protected void fillStackData(StackCameraDeviceSimulator pCamera,
                               ArrayList<Boolean> pKeepPlaneList,
                               final long pWidth,
                               final long pHeight,
                               final long pDepth,
                               final int pChannel,
                               final StackInterface pStack)
  {
    final byte time = (byte) pCamera.getCurrentStackIndex();

    final ContiguousMemoryInterface lContiguousMemory =
                                                      pStack.getContiguousMemory();
    final ContiguousBuffer lContiguousBuffer =
                                             new ContiguousBuffer(lContiguousMemory);

    for (int z = 0; z < pDepth; z++)
      if (pKeepPlaneList.get(z))
        for (int y = 0; y < pHeight; y++)
          for (int x = 0; x < pWidth; x++)
          {
            short lValue =
                         (short) (((byte) (x + time)
                                   ^ (byte) (y + (pHeight * pChannel)
                                                 / 3)
                                   ^ (byte) z
                                   ^ (time)));/**/
            if (lValue < 32)
              lValue = 0;
            lContiguousBuffer.writeShort(lValue);
          }
  }

}
