package clearcontrol.devices.signalgen.devices.gs.demo;

import clearcontrol.devices.signalgen.devices.gs.GS16AO64cSignalGenerator;
import clearcontrol.devices.signalgen.gui.swing.score.ScoreVisualizerJFrame;
import clearcontrol.devices.signalgen.measure.Measure;
import clearcontrol.devices.signalgen.score.Score;
import clearcontrol.devices.signalgen.score.ScoreInterface;
import clearcontrol.devices.signalgen.staves.SinusStave;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class GS16AO64cSignalGeneratorDemo
{
    @Test
    public void demo_Simple8ChannelSinus()
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

    @Test
    public void demo_16_48ChannelsVariousSinuses()
    {
        final GS16AO64cSignalGenerator lGS16AO64cSignalGenerator = new GS16AO64cSignalGenerator();
        assertTrue(lGS16AO64cSignalGenerator.open());

        final ScoreInterface lScore = buildScoreConstantDurationVariousSinuses();
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
        final Measure lMeasure = new Measure("Test Measure");

        final SinusStave lSinusStave1 = new SinusStave(
                "sinus1",
                1f,
                0f,
                1f);

        for (int i = 0; i < 8; i++)
            lMeasure.setStave(i, lSinusStave1);

        lMeasure.setDuration(1, TimeUnit.SECONDS);
        lScore.addMeasureMultipleTimes(lMeasure, 10);

        return lScore;
    }

    private ScoreInterface buildScoreConstantDurationVariousSinuses()
    {
        final Score lScore = new Score("Test Score");
        final Measure lMeasure = new Measure("Test Measure", 32);

        for (int i = 16; i < 24; i++)
            lMeasure.setStave(i, new SinusStave("sinus1", 1f, 0f, 1f));

        for (int i = 24; i < 32; i++)
            lMeasure.setStave(i, new SinusStave("sinus2", 1f, 0.5f, 1f));

        for (int i = 32; i < 40; i++)
            lMeasure.setStave(i, new SinusStave("sinus3", 1f, 0f, 0.5f));

        for (int i = 40; i < 48; i++)
            lMeasure.setStave(i, new SinusStave("sinus4", 0.5f, 0f, 1f));

        lMeasure.setDuration(1, TimeUnit.SECONDS);
        lScore.addMeasureMultipleTimes(lMeasure, 10);

        return lScore;
    }
}
