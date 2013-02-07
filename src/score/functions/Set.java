package score.functions;

import score.interfaces.StaveInterface;

public class Set
{

	public static void write(StaveInterface pStave, final double pValue)
	{
		final int lArrayLength = pStave.getNumberOfTimePoints();
		final short[] array = pStave.getStaveArray();

		final int lMaxIntegerAmplitude = pStave.getMaximalSignalIntegerAmplitude();

		final short lValue = (short) Math.round(lMaxIntegerAmplitude * pValue);

		for (int i = 0; i < lArrayLength; i++)
		{
			array[i] = lValue;
		}

	}

}
