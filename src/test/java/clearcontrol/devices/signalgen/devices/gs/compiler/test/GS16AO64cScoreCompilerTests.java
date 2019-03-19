package clearcontrol.devices.signalgen.devices.gs.compiler.test;


import clearcontrol.devices.signalgen.devices.gs.compiler.GS16AO64cCompiledScore;
import clearcontrol.devices.signalgen.devices.gs.compiler.GS16AO64cScoreCompiler;
import clearcontrol.devices.signalgen.gui.swing.score.ScoreVisualizerJFrame;
import clearcontrol.devices.signalgen.measure.Measure;
import clearcontrol.devices.signalgen.score.Score;
import clearcontrol.devices.signalgen.staves.RampSteppingStave;
import clearcontrol.devices.signalgen.staves.SinusStave;
import clearcontrol.devices.signalgen.staves.TriggerStave;
import com.sun.jna.NativeLong;
import gsao64.GSBuffer;
import gsao64.GSConstants;
import gsao64.GSSplitterBuffer;
import gsao64.exceptions.*;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class GS16AO64cScoreCompilerTests
{
    static {
        GSConstants.id_off = new NativeLong(24);
        GSConstants.eog = new NativeLong(30);
        GSConstants.eof = new NativeLong(31);
    }

    @Test
    public void testCompilation() throws InterruptedException
    {
        // Get the sample score
        Score lScore = defineSampleScore(0,8);
        // Compiling score
        final GS16AO64cCompiledScore lGS16AO64cCompiledScore = new GS16AO64cCompiledScore(100000);
        GS16AO64cScoreCompiler.compile(lGS16AO64cCompiledScore, lScore);


        // Get the sample score
        Score lScore1 = defineSampleScore(16,31);
        // Compiling score
        final GS16AO64cCompiledScore lGS16AO64cCompiledScore1 = new GS16AO64cCompiledScore(100000);
        GS16AO64cScoreCompiler.compile(lGS16AO64cCompiledScore1, lScore);


        // Get the sample score
        Score lScore2 = defineSampleScore(0,63);
        // Compiling score
        final GS16AO64cCompiledScore lGS16AO64cCompiledScore2 = new GS16AO64cCompiledScore(100000);
        GS16AO64cScoreCompiler.compile(lGS16AO64cCompiledScore2, lScore);

//        assertEquals(4 * lNumberOfMovements, lGS16AO64cCompiledScore.getDeltaTimeBuffer().getSizeInBytes());
//        final ScoreVisualizerJFrame lVisualize = ScoreVisualizerJFrame.visualizeAndWait("test", lScore);
    }

    @Test
    public void testOutputVerification() throws InterruptedException
    {
        // Get the sample score
        Score lScore = defineSampleScore(0,15);

        // Compile it
        final GS16AO64cCompiledScore lGS16AO64cCompiledScore = new GS16AO64cCompiledScore(4096*2);
        GS16AO64cScoreCompiler.compile(lGS16AO64cCompiledScore, lScore);

        // Prepare expected arrayData
        GSSplitterBuffer data = null;
        try {
            data = new GSSplitterBuffer(2999 * 10);
        } catch (BufferTooLargeException e) {
            e.printStackTrace();
        } catch (BoardInitializeException e) {
            e.printStackTrace();
        }

        for (int loop = 0; loop < 2999 * 10; loop++) {
            for (int i = 0; i < 16; i++) {
                float value = (float) Math.sin(Math.PI * 2 * ((float)loop/(2999)));
                data.appendValue(value, i);
            }
            data.appendEndofTP();
        }
        data.appendEndofFunction();
        ArrayDeque<GSBuffer> expectedArrayData = data.getData();


        // assert equality of compiled and expected data buffers
        assertEquals(lGS16AO64cCompiledScore.getArrayData().size(), expectedArrayData.size());

        for (int i = 0; i < 1500; i++)
        {
            System.out.println("iteration number: " + i);
            assertEquals(lGS16AO64cCompiledScore.getArrayData().getFirst().getTPValues(i),expectedArrayData.getFirst().getTPValues(i));
        }

    }

    @Test
    public void testQuantization() throws InterruptedException
    {
//        final Score lScore = new Score("Test Score");
//
//        final Movement lMovement = new Movement("Test Movement");
//
//        final RampSteppingStave lGalvoScannerStave = new RampSteppingStave("galvo");
//        lGalvoScannerStave.setSyncStart(0.1f);
//        lGalvoScannerStave.setSyncStop(0.7f);
//        lGalvoScannerStave.setStartValue(0f);
//        lGalvoScannerStave.setStopValue(1f);
//        lGalvoScannerStave.setStepHeight(0.02f);
//
//        lMovement.setStave(1, lGalvoScannerStave);
//
//        lScore.addMovementMultipleTimes(lMovement, 1);
//
//        lMovement.setDuration(1, TimeUnit.SECONDS);
//
//        System.out.println("delta=" + GS16AO64cScoreCompiler.getDeltaTimeInNs(lMovement));
//        System.out.println("nbtp="  + GS16AO64cScoreCompiler.getNumberOfTimePoints(lMovement));
//
//        assertEquals(488281, GS16AO64cScoreCompiler.getDeltaTimeInNs(lMovement));
//        assertEquals(2048, GS16AO64cScoreCompiler.getNumberOfTimePoints(lMovement));
//
//        lMovement.setDuration(100, TimeUnit.MICROSECONDS);
//
//        System.out.println("delta=" + GS16AO64cScoreCompiler.getDeltaTimeInNs(lMovement));
//        System.out.println("nbtp=" + GS16AO64cScoreCompiler.getNumberOfTimePoints(lMovement));
//
//        assertEquals(3000, GS16AO64cScoreCompiler.getDeltaTimeInNs(lMovement));
//        assertEquals(33, GS16AO64cScoreCompiler.getNumberOfTimePoints(lMovement));

    /*
    ScoreVisualizerJFrame.visualizeAndWait("test", lScore);/**/

    }



    private Score defineSampleScore(int pFirstChannelIndex, int pSecondChannelIndex)
    {
        // Defining score
        final Score lScore = new Score("Test Score");

        final Measure lMeasure = new Measure("Test Movement", pSecondChannelIndex+1);

        final SinusStave lSinusStave = new SinusStave(
                "sinusTest",
                1.0f,
                0.0f,
                1.0f);

        for (int i = pFirstChannelIndex; i <= pSecondChannelIndex; i++)
            lMeasure.setStave(i, lSinusStave);

        lMeasure.setDuration(1, TimeUnit.SECONDS);

        lScore.addMeasureMultipleTimes(lMeasure, 10);

        return lScore;
    }
}
