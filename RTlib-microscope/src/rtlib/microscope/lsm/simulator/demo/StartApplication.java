package rtlib.microscope.lsm.simulator.demo;

import javafx.application.Application;
import javafx.stage.Stage;
import rtlib.microscope.lsm.simulator.LightSheetMicroscopeSimulator;

/**
 * Created by moon on 1/11/16.
 */
public class StartApplication extends Application
{
	@Override
	public void start(Stage primaryStage)
	{
		primaryStage.setOnCloseRequest( event -> System.exit( 0 ) );
		LightSheetMicroscopeSimulator sim = new LightSheetMicroscopeSimulator();

		try
		{
			sim.init( primaryStage );
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception
	{
		launch(args);
	}
}
