package rtlib.scripting.gui.demo;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.junit.Test;

import rtlib.core.concurrent.thread.ThreadUtils;
import rtlib.scripting.engine.ScriptingEngine;
import rtlib.scripting.gui.ScriptingPanel;
import rtlib.scripting.lang.jython.JythonScripting;

public class ScriptingPanelDemo
{

	@Test
	public void test() throws InvocationTargetException,
										InterruptedException
	{
		final JFrame lJFrame = new JFrame();

		final JythonScripting lJythonScripting = new JythonScripting();

		final ScriptingEngine lScriptingEngine = new ScriptingEngine(	lJythonScripting,
																																	null);

		final ScriptingPanel lScriptingPanel = new ScriptingPanel(lScriptingEngine);
		lJFrame.add(lScriptingPanel);

		SwingUtilities.invokeAndWait(new Runnable()
		{
			@Override
			public void run()
			{
				lJFrame.setSize(512, 512);
				lJFrame.setVisible(true);
			}
		});

		while (lJFrame.isVisible())
		{
			ThreadUtils.sleep(10, TimeUnit.MILLISECONDS);
		}
	}

}
