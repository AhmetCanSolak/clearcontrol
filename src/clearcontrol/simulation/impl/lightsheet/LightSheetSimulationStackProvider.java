package clearcontrol.simulation.impl.lightsheet;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import clearcl.ClearCLBuffer;
import clearcontrol.core.variable.queue.VariableStateQueues;
import clearcontrol.devices.cameras.devices.sim.StackCameraDeviceSimulator;
import clearcontrol.devices.cameras.devices.sim.StackCameraSimulationProvider;
import clearcontrol.devices.cameras.devices.sim.StackCameraSimulationProviderBase;
import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeInterface;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.stack.StackInterface;
import coremem.ContiguousMemoryInterface;
import simbryo.synthoscopy.microscope.lightsheet.LightSheetMicroscopeSimulator;
import simbryo.synthoscopy.microscope.parameters.CameraParameter;
import simbryo.synthoscopy.microscope.parameters.DetectionParameter;
import simbryo.synthoscopy.microscope.parameters.IlluminationParameter;

/**
 * Lightsheet microscope simulation stack provider
 *
 * @author royer
 */
public class LightSheetSimulationStackProvider extends
                                               StackCameraSimulationProviderBase
                                               implements
                                               StackCameraSimulationProvider
{

  private LightSheetMicroscopeInterface mLightSheetMicroscope;
  private LightSheetMicroscopeSimulator mLightSheetMicroscopeSimulator;
  private int mCameraIndex;

  private DetectionArmInterface mDetectionArmDevice;
  private ArrayList<LightSheetInterface> mLightSheetList =
                                                         new ArrayList<>();

  private VariableStateQueues mDetectionStateQueues;
  private ConcurrentHashMap<Integer, VariableStateQueues> mLightSheetStateQueuesMap =
                                                                                    new ConcurrentHashMap<>();

  /**
   * Instanciates a lightsheet simulation stack provider.
   * 
   * @param pLightSheetMicroscope
   *          light sheet sample simulation device
   * @param pLightSheetMicroscopeSimulator
   *          light sheet microscope simulator
   * @param pCameraIndex
   *          camera index
   */
  public LightSheetSimulationStackProvider(LightSheetMicroscopeInterface pLightSheetMicroscope,
                                           LightSheetMicroscopeSimulator pLightSheetMicroscopeSimulator,
                                           int pCameraIndex)
  {
    mLightSheetMicroscope = pLightSheetMicroscope;
    mLightSheetMicroscopeSimulator = pLightSheetMicroscopeSimulator;
    mCameraIndex = pCameraIndex;

    mDetectionArmDevice =
                        mLightSheetMicroscope.getDevice(DetectionArmInterface.class,
                                                        mCameraIndex);

    int lNumberOfLightSheets =
                             mLightSheetMicroscope.getNumberOfDevices(LightSheetInterface.class);

    for (int l = 0; l < lNumberOfLightSheets; l++)
      mLightSheetList.add(mLightSheetMicroscope.getDevice(LightSheetInterface.class,
                                                          l));

  }

  @Override
  protected void fillStackData(StackCameraDeviceSimulator pCamera,
                               ArrayList<Boolean> pKeepPlaneList,
                               long pWidth,
                               long pHeight,
                               long pDepth,
                               int pChannel,
                               StackInterface pStack)
  {

    final byte time = (byte) pCamera.getCurrentStackIndex();

    int lWidth = (int) pStack.getWidth();
    int lHeight = (int) pStack.getHeight();
    int lDepth = (int) pStack.getDepth();

    mLightSheetMicroscopeSimulator.setNumberParameter(CameraParameter.ROIWidth,
                                                      mCameraIndex,
                                                      lWidth);
    mLightSheetMicroscopeSimulator.setNumberParameter(CameraParameter.ROIHeight,
                                                      mCameraIndex,
                                                      lHeight);

    final ContiguousMemoryInterface lContiguousMemory =
                                                      pStack.getContiguousMemory();

    int lLastZiKept = getLast(pKeepPlaneList);

    int lQueueLength = mLightSheetMicroscope.getQueueLength();

    collectDetectionStateQueues();

    for (int l = 0; l < mLightSheetList.size(); l++)
      collectIluminationStateQueues(l);
    /**/

    for (int zi = 0, i = 0; zi < lQueueLength; zi++)
    {

      passDetectionParameters(zi);

      for (int l = 0; l < mLightSheetList.size(); l++)
      {
        passIlluminationParameters(l, zi);
      } /**/

      mLightSheetMicroscopeSimulator.render(mCameraIndex, false);

      if (pKeepPlaneList.get(zi))
      {
        ClearCLBuffer lCameraImageBuffer =
                                         mLightSheetMicroscopeSimulator.getCameraImageBuffer(mCameraIndex);

        long lOffset = i++ * lCameraImageBuffer.getSizeInBytes();

        ContiguousMemoryInterface lImagePlane =
                                              lContiguousMemory.subRegion(lOffset,
                                                                          lCameraImageBuffer.getSizeInBytes());

        lCameraImageBuffer.writeTo(lImagePlane, zi == lLastZiKept);
      }
    }

  }

  private void collectDetectionStateQueues()
  {
    mDetectionStateQueues =
                          mDetectionArmDevice.getVariableStateQueues()
                                             .clone();
  }

  private void collectIluminationStateQueues(int pLightSheetIndex)
  {
    mLightSheetStateQueuesMap.put(pLightSheetIndex,
                                  mLightSheetList.get(pLightSheetIndex)
                                                 .getVariableStateQueues()
                                                 .clone());
  }

  private void passDetectionParameters(int zi)
  {

    float z = mDetectionStateQueues.getQueuedValue(
                                                   mDetectionArmDevice.getZFunction()
                                                                      .get(),
                                                   mDetectionArmDevice.getZVariable(),
                                                   zi)
                                   .floatValue();
    mLightSheetMicroscopeSimulator.setNumberParameter(DetectionParameter.FocusZ,
                                                      mCameraIndex,
                                                      z);

  }

  private void passIlluminationParameters(int pLightSheetIndex,
                                          int zi)
  {
    LightSheetInterface lLightSheet =
                                    mLightSheetList.get(pLightSheetIndex);

    float x =
            mLightSheetStateQueuesMap.get(pLightSheetIndex)
                                     .getQueuedValue(lLightSheet.getXFunction()
                                                                .get(),
                                                     lLightSheet.getXVariable(),
                                                     zi)
                                     .floatValue();

    mLightSheetMicroscopeSimulator.setNumberParameter(IlluminationParameter.X,
                                                      pLightSheetIndex,
                                                      x);

    float y =
            mLightSheetStateQueuesMap.get(pLightSheetIndex)
                                     .getQueuedValue(lLightSheet.getYFunction()
                                                                .get(),
                                                     lLightSheet.getYVariable(),
                                                     zi)
                                     .floatValue();

    mLightSheetMicroscopeSimulator.setNumberParameter(IlluminationParameter.Y,
                                                      pLightSheetIndex,
                                                      y);

    float z =
            mLightSheetStateQueuesMap.get(pLightSheetIndex)
                                     .getQueuedValue(lLightSheet.getZFunction()
                                                                .get(),
                                                     lLightSheet.getZVariable(),
                                                     zi)
                                     .floatValue();

    mLightSheetMicroscopeSimulator.setNumberParameter(IlluminationParameter.Z,
                                                      pLightSheetIndex,
                                                      z);

    float alpha = mLightSheetStateQueuesMap.get(pLightSheetIndex)

                                           .getQueuedValue(lLightSheet.getAlphaFunction()
                                                                      .get(),
                                                           lLightSheet.getAlphaInDegreesVariable(),
                                                           zi)
                                           .floatValue();

    mLightSheetMicroscopeSimulator.setNumberParameter(IlluminationParameter.Alpha,
                                                      pLightSheetIndex,
                                                      alpha);

    float beta = mLightSheetStateQueuesMap.get(pLightSheetIndex)

                                          .getQueuedValue(lLightSheet.getBetaFunction()
                                                                     .get(),
                                                          lLightSheet.getBetaInDegreesVariable(),
                                                          zi)
                                          .floatValue();

    mLightSheetMicroscopeSimulator.setNumberParameter(IlluminationParameter.Beta,
                                                      pLightSheetIndex,
                                                      beta);

    float height = mLightSheetStateQueuesMap.get(pLightSheetIndex)

                                            .getQueuedValue(lLightSheet.getHeightFunction()
                                                                       .get(),
                                                            lLightSheet.getHeightVariable(),
                                                            zi)
                                            .floatValue();

    mLightSheetMicroscopeSimulator.setNumberParameter(IlluminationParameter.Height,
                                                      pLightSheetIndex,
                                                      height);

    float lLightSheetPower =

                           mLightSheetStateQueuesMap.get(pLightSheetIndex)
                                                    .getQueuedValue(lLightSheet.getPowerFunction()
                                                                               .get(),
                                                                    lLightSheet.getPowerVariable(),
                                                                    zi)
                                                    .floatValue();

    LaserDeviceInterface lLaserDevice =
                                      mLightSheetMicroscope.getDevice(LaserDeviceInterface.class,
                                                                      0);

    float lLaserPower =
                      lLaserDevice.getCurrentPowerInMilliWattVariable()
                                  .get()
                                  .floatValue();

    mLightSheetMicroscopeSimulator.setNumberParameter(IlluminationParameter.Intensity,
                                                      pLightSheetIndex,
                                                      lLightSheetPower * lLaserPower);

  }

  private int getLast(ArrayList<Boolean> pKeepPlaneList)
  {
    int lSize = pKeepPlaneList.size();
    for (int i = lSize - 1; i >= 0; i--)
      if (pKeepPlaneList.get(i))
        return i;
    return 0;
  }

}
