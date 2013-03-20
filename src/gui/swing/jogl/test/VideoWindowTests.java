package gui.swing.jogl.test;

import static org.junit.Assert.*;

import gui.swing.jogl.VideoCanvas;
import gui.swing.jogl.VideoWindow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.swing.JFrame;

import org.junit.Test;

public class VideoWindowTests
{
	static
	{
		System.setProperty("sun.awt.noerasebackground", "true");
	}

	@Test
	public void simpleRandomDataTest() throws InterruptedException
	{
		VideoWindow lVideoWindow = new VideoWindow(1, 512, 512);
		lVideoWindow.setDisplayOn(true);

		ByteBuffer lVideoByteBuffer = ByteBuffer.allocateDirect(lVideoWindow.getMaxBufferLength())
																						.order(ByteOrder.nativeOrder());

		lVideoWindow.setSourceBuffer(lVideoByteBuffer);

		lVideoWindow.setVisible(true);
		while (true)
		{
			generateNoiseBuffer(lVideoByteBuffer);
			if (Math.random() > 0.5)
			{
				lVideoWindow.setWidth(512);
				lVideoWindow.setHeight(512);
			}
			else
			{
				lVideoWindow.setWidth(256);
				lVideoWindow.setHeight(256);
			}
			lVideoWindow.notifyNewFrame();
			lVideoWindow.display();
			Thread.sleep(1000);
		}

	}

	private void generateNoiseBuffer(ByteBuffer pVideoByteBuffer)
	{
		pVideoByteBuffer.clear();

		final int lBufferLength = pVideoByteBuffer.limit();
		for (int i = 0; i < lBufferLength; i++)
		{
			final byte lValue = (byte) ((int) (Math.random() * 256) % 256);
			// System.out.print(lValue);
			pVideoByteBuffer.put(lValue);
		}
	}
}
