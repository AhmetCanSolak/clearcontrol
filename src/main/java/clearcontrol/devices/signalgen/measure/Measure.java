package clearcontrol.devices.signalgen.measure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.device.name.NameableBase;
import clearcontrol.devices.signalgen.staves.StaveInterface;
import clearcontrol.devices.signalgen.staves.ZeroStave;

/**
 * Measure implementation
 *
 * @author royer
 */
public class Measure extends NameableBase
                      implements MeasureInterface
{

  /**
   * Default number of staves pwe measure
   */
  public static final int cDefaultNumberOfStavesPerMeasure = 16;

  private volatile long mDurationInNanoseconds;
  private final HashMap<Integer, StaveInterface> mStaveListHashMap;
  private volatile boolean mIsSync = false;
  private volatile boolean mIsSyncOnRisingEdge = false;
  private volatile int mSyncChannel = 0;

  /**
   * Instantiates a measure with given name
   * 
   * @param pName
   *          name
   */
  public Measure(final String pName)
  {
    this(pName, cDefaultNumberOfStavesPerMeasure);
  }

  /**
   * Instantiates a measure with given name and number of staves
   * 
   * @param pName
   *          name
   * @param pNumberOfStaves
   *          number of staves
   */
  public Measure(final String pName, final int pNumberOfStaves)
  {
    super(pName);
    mStaveListHashMap = new HashMap<>(pNumberOfStaves);
    for (int i = 0; i < pNumberOfStaves; i++)
    {
      mStaveListHashMap.put(i, new ZeroStave());
    }
  }

  /**
   * Copy constructor
   * 
   * @param pMeasure
   *          measure to copy
   */
  public Measure(Measure pMeasure)
  {
    this(pMeasure.getName(), pMeasure.getNumberOfStaves());

    setSync(pMeasure.isSync());
    setSyncChannel(pMeasure.getSyncChannel());
    setSyncOnRisingEdge(pMeasure.isSyncOnRisingEdge());
    setDuration(pMeasure.getDuration(TimeUnit.NANOSECONDS),
                TimeUnit.NANOSECONDS);

    for (int i = 0; i < mStaveListHashMap.size(); i++)
    {
      final StaveInterface lStaveInterface =
                                           pMeasure.mStaveListHashMap.get(i);
      setStave(i, lStaveInterface.duplicate());
    }
  }

  @Override
  public MeasureInterface duplicate()
  {
    return new Measure(this);
  }

  @Override
  public void setStave(final int pStaveIndex,
                       final StaveInterface pNewStave)
  {
    mStaveListHashMap.replace(pStaveIndex, pNewStave);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <O extends StaveInterface> O ensureSetStave(int pStaveIndex,
                                                     O pNewStave)
  {
    if (mStaveListHashMap.get(pStaveIndex) != null
        && !(mStaveListHashMap.get(pStaveIndex) instanceof ZeroStave))
      return (O) mStaveListHashMap.get(pStaveIndex);
    else
    {
      setStave(pStaveIndex, pNewStave);
      return pNewStave;
    }
  }

  @Override
  public StaveInterface getStave(final int pStaveIndex)
  {
    return mStaveListHashMap.get(pStaveIndex);
  }

  @Override
  public int getNumberOfStaves()
  {
    return  mStaveListHashMap.size();
  }

  @Override
  public void setDuration(long pDuration, TimeUnit pTimeUnit)
  {
    mDurationInNanoseconds = TimeUnit.NANOSECONDS.convert(pDuration,
                                                          pTimeUnit);
  }

  @Override
  public long getDuration(TimeUnit pTimeUnit)
  {
    return pTimeUnit.convert(mDurationInNanoseconds,
                             TimeUnit.NANOSECONDS);
  }

  @Override
  public boolean isSync()
  {
    return mIsSync;
  }

  @Override
  public void setSync(boolean pIsSync)
  {
    mIsSync = pIsSync;
  }

  @Override
  public boolean isSyncOnRisingEdge()
  {
    return mIsSyncOnRisingEdge;
  }

  @Override
  public void setSyncOnRisingEdge(boolean pIsSyncOnRisingEdge)
  {
    mIsSyncOnRisingEdge = pIsSyncOnRisingEdge;
  }

  @Override
  public void setSyncChannel(int pSyncChannel)
  {
    mSyncChannel = pSyncChannel;
  }

  @Override
  public int getSyncChannel()
  {
    return mSyncChannel;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result =
           prime * result + (int) (mDurationInNanoseconds
                                   ^ (mDurationInNanoseconds >>> 32));
    result = prime * result + (mIsSync ? 1231 : 1237);
    result = prime * result + (mIsSyncOnRisingEdge ? 1231 : 1237);
    result = prime * result + mStaveListHashMap.hashCode();
    result = prime * result + mSyncChannel;
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Measure other = (Measure) obj;
    if (mDurationInNanoseconds != other.mDurationInNanoseconds)
      return false;
    if (mIsSync != other.mIsSync)
      return false;
    if (mIsSyncOnRisingEdge != other.mIsSyncOnRisingEdge)
      return false;
    if (!mStaveListHashMap.equals(other.mStaveListHashMap))
      return false;
    if (mSyncChannel != other.mSyncChannel)
      return false;
    return true;
  }
  /**/

  @Override
  public String toString()
  {
    return String.format("Measure[%s]", getName());
  }

}
