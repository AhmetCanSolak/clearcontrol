package score.test;

import java.io.IOException;

import org.junit.Test;

import score.Movement;
import score.Score;
import score.staves.CameraTriggerStave;
import score.staves.GalvoScannerStave;
import score.staves.LaserTriggerStave;



public class ScoreTests
{

	@Test
	public void test() throws IOException
	{

		Score lScore = new Score("Test Score");

		Movement lMovement = new Movement("Test Movement");

		CameraTriggerStave lCameraTriggerStave = new CameraTriggerStave("test");
		lCameraTriggerStave.mSyncStart=0.2;
		lCameraTriggerStave.mSyncStop=0.6;
		
				
		GalvoScannerStave lGalvoScannerStave = new GalvoScannerStave("test");
		lGalvoScannerStave.mSyncStart=0.1;
		lGalvoScannerStave.mSyncStop=0.7;
		lGalvoScannerStave.mStartValue=0;
		lGalvoScannerStave.mStopValue=1;
		
		LaserTriggerStave lLaserTriggerStave = new LaserTriggerStave("test");
		lLaserTriggerStave.mSyncStart=0.3;
		lLaserTriggerStave.mSyncStop=0.5;
		

		lMovement.setStave(0,lCameraTriggerStave);
		lMovement.setStave(1,lGalvoScannerStave);
		lMovement.setStave(2,lLaserTriggerStave);

		lScore.addMovementMultipleTimes(lMovement,1);
		
		System.out.println(lScore.getScoreBuffer());

	}

}
