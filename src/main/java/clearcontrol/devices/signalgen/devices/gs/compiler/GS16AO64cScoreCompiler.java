package clearcontrol.devices.signalgen.devices.gs.compiler;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorFeature;
import clearcontrol.devices.signalgen.measure.MeasureInterface;
import clearcontrol.devices.signalgen.score.ScoreInterface;
import clearcontrol.devices.signalgen.staves.StaveInterface;
import gsao64.exceptions.FlagException;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class GS16AO64cScoreCompiler implements AsynchronousExecutorFeature
{

    public static void compile(GS16AO64cCompiledScore pGS16AO64cCompiledScore,
                               ScoreInterface pScore)
    {
        final ArrayList<MeasureInterface> lMeasures = pScore.getMeasures();

        for (final MeasureInterface lMeasure: lMeasures)
            compileMeasure(pGS16AO64cCompiledScore, lMeasure);

    }

    private static void compileMeasure(GS16AO64cCompiledScore pGS16AO64cCompiledScore,
                                        MeasureInterface lMeasure)
    {
        // compute required sample number for measure
        // long t = lMeasure.getDuration(TimeUnit.SECONDS);
        // long lTimePointsNeeded = t * pGS16AO64cCompiledScore.mSamplingRate;
        long lTimePointsNeeded = 2999;


        try
        {
            for (int iter = 0; iter < lTimePointsNeeded; iter++)
            {
                for (int i = 0; i < lMeasure.getNumberOfStaves(); i++) {
                    StaveInterface lStave = lMeasure.getStave(i);
                    System.out.println(lStave.getValue(i) + ": " + i);
                    pGS16AO64cCompiledScore.addValueToArrayData((float)lStave.getValue((float)iter/lTimePointsNeeded),i);
                }
                pGS16AO64cCompiledScore.getArrayData().peekLast().appendEndofTP();
            }
            pGS16AO64cCompiledScore.getArrayData().peekLast().appendEndofFunction();
        }
        catch (FlagException e)
        {
            e.printStackTrace();
        }
    }

}
