package clearcontrol.devices.signalgen.devices.gs.compiler;

import gsao64.GSBuffer;

import java.util.ArrayDeque;

public class GS16AO64cCompiledScore
{
    private volatile long mNumberOfMeasures;
    private ArrayDeque<GSBuffer> mArrayData;

    public int mSamplingRate;


    public GS16AO64cCompiledScore(int pSamplingRate)
    {
        if (mArrayData == null)
        {
            mArrayData = new ArrayDeque<GSBuffer>();
            mSamplingRate = pSamplingRate;
            addNewBufferToArrayData();
        }
    }

    // Getters, Setters and Attr Helpers

    public void setNumberOfMeasures(long pNumberOfMeasures)
    {
        mNumberOfMeasures = pNumberOfMeasures;
    }

    public long getNumberOfMeasures()
    {
        return mNumberOfMeasures;
    }

    public ArrayDeque<GSBuffer> getArrayData()
    {
        return mArrayData;
    }

    public void addNewBufferToArrayData()
    {
        try {
            GSBuffer newBuffer = new GSBuffer(2999);
            mArrayData.addLast(newBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean addValueToArrayData(double pValue, int pChannelIndex)
    {
        System.out.println(mArrayData.peekLast().getNumTPWritten());
        if (mArrayData.peekLast().getNumTPWritten() == 2999) {
            this.addNewBufferToArrayData();
        }

        boolean newValueAdded = false;
        try {
            newValueAdded = mArrayData.peekLast().appendValue(pValue,pChannelIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newValueAdded;
    }

}
