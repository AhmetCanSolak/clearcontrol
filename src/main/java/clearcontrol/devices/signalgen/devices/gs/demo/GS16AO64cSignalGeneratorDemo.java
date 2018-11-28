package clearcontrol.devices.signalgen.devices.gs.demo;

import clearcontrol.devices.signalgen.devices.gs.GS16AO64cSignalGenerator;
import clearcontrol.devices.signalgen.gui.swing.score.ScoreVisualizer;
import clearcontrol.devices.signalgen.gui.swing.score.ScoreVisualizerJFrame;
import clearcontrol.devices.signalgen.movement.Movement;
import clearcontrol.devices.signalgen.score.Score;
import clearcontrol.devices.signalgen.score.ScoreInterface;
import clearcontrol.devices.signalgen.staves.SinusStave;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class GS16AO64cSignalGeneratorDemo
{
    @Test
    public void demo1()
    {
        final GS16AO64cSignalGenerator lGS16AO64cSignalGenerator = new GS16AO64cSignalGenerator();
        assertTrue(lGS16AO64cSignalGenerator.open());

        final ScoreInterface lScore = buildScore();
        final ScoreVisualizerJFrame lVisualize = ScoreVisualizerJFrame.visualize("demo1",lScore);

        for (int i = 0; i < 1000000000 && lVisualize.isVisible(); i++)
        {
            lGS16AO64cSignalGenerator.playScore(lScore);
            System.out.println(i);
        }

        lVisualize.dispose();

        assertTrue(lGS16AO64cSignalGenerator.close());
    }

    private ScoreInterface buildScore()
    {
        final Score lScore = new Score("Test Score");
        final Movement lMovement = new Movement("Test Movement");

        final SinusStave lSinusStave1 = new SinusStave(
                "sinus1",
                1f,
                0f,
                0.5f);
        final SinusStave lSinusStave2 = new SinusStave(
                "sinus2",
                0.25f,
                0f,
                0.25f);
        final SinusStave lSinusStave3 = new SinusStave(
                "sinus3",
                0.125f,
                0f,
                1f);

        for (int i = 0; i < 1; i++)
    	    lMovement.setStave(i, lSinusStave1);
        for (int i = 1; i < 2; i++)
            lMovement.setStave(i, lSinusStave2);
        for (int i = 1; i < 8; i++)
            lMovement.setStave(i, lSinusStave3);

        lScore.addMovementMultipleTimes(lMovement, 10);
        lMovement.setDuration(1, TimeUnit.MILLISECONDS);

        return lScore;
    }
}
