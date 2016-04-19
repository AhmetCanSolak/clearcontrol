package rtlib.hardware.optomech.opticalswitch.devices.arduino;

import rtlib.com.serial.SerialDevice;
import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.variable.Variable;
import rtlib.core.variable.VariableSetListener;
import rtlib.hardware.optomech.opticalswitch.OpticalSwitchDeviceInterface;
import rtlib.hardware.optomech.opticalswitch.devices.arduino.adapters.ArduinoOpticalSwitchPositionAdapter;

public class ArduinoOpticalSwitchDevice extends SerialDevice implements
																														OpticalSwitchDeviceInterface

{

	private final Variable<Long> mCommandVariable;

	private final Variable<Boolean>[] mLightSheetOnOff;

	private static final long cAllClosed = 0;
	private static final long cAllOpened = 100;

	public ArduinoOpticalSwitchDevice(final int pDeviceIndex)
	{
		this(MachineConfiguration.getCurrentMachineConfiguration()
															.getSerialDevicePort(	"fiberswitch.optojena",
																										pDeviceIndex,
																										"NULL"));
	}

	public ArduinoOpticalSwitchDevice(final String pPortName)
	{
		super("ArduinoOpticalSwitch", pPortName, 250000);

		final ArduinoOpticalSwitchPositionAdapter lFiberSwitchPosition = new ArduinoOpticalSwitchPositionAdapter(this);

		mCommandVariable = addSerialVariable(	"OpticalSwitchPosition",
																					lFiberSwitchPosition);

		mLightSheetOnOff = new Variable[4];

		final VariableSetListener<Boolean> lBooleanVariableListener = (	u,
																																		v) -> {

			int lCount = 0;
			for (int i = 0; i < mLightSheetOnOff.length; i++)
				if (mLightSheetOnOff[i].get())
					lCount++;

			if (lCount == 1)
			{
				for (int i = 0; i < mLightSheetOnOff.length; i++)
					if (mLightSheetOnOff[i].get())
						mCommandVariable.set((long) (101 + i));
			}
			else
				for (int i = 0; i < mLightSheetOnOff.length; i++)
				{
					boolean lOn = mLightSheetOnOff[i].get();
					mCommandVariable.set((long) ((i + 1) * (lOn ? 1 : -1)));
				}
		};

		for (int i = 0; i < mLightSheetOnOff.length; i++)
		{

			mLightSheetOnOff[i] = new Variable<Boolean>(String.format("LightSheet%dOnOff",
																																i),
																									false);
			mLightSheetOnOff[i].addSetListener(lBooleanVariableListener);

		}

	}

	@Override
	public boolean open()
	{
		final boolean lIsOpened = super.open();
		mCommandVariable.set(cAllClosed);

		return lIsOpened;
	}

	@Override
	public boolean close()
	{
		final boolean lIsClosed = super.close();
		mCommandVariable.set(cAllClosed);

		return lIsClosed;
	}

	@Override
	public int getNumberOfSwitches()
	{
		return 4;
	}

	@Override
	public Variable<Boolean> getSwitchVariable(int pSwitchIndex)
	{
		return mLightSheetOnOff[pSwitchIndex];
	}

	@Override
	public String getSwitchName(int pSwitchIndex)
	{
		return "optical switch "+pSwitchIndex;
	}

}
