package rtlib.hardware.sensors.devices.tc01;

import org.bridj.Pointer;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.variable.Variable;
import rtlib.hardware.sensors.TemperatureSensorDeviceBase;
import rtlib.hardware.sensors.devices.tc01.bridj.TC01libLibrary;

public class TC01 extends TemperatureSensorDeviceBase
{

	private NIThermoCoupleType mThermoCoupleNIType = NIThermoCoupleType.K;
	private final boolean mIsDevicePresent;
	private final Pointer<Byte> mPhysicalChannelPointer;

	public TC01(String pPhysicalChannel,
							NIThermoCoupleType pNIThermoCoupleType,
							final int pDeviceIndex)
	{
		super("TC01");
		mThermoCoupleNIType = pNIThermoCoupleType;
		mIsDevicePresent = MachineConfiguration.getCurrentMachineConfiguration()
																						.getIsDevicePresent("ni.tc01",
																																pDeviceIndex);

		mPhysicalChannelPointer = Pointer.pointerToCString(pPhysicalChannel);
	}

	@Override
	protected boolean loop()
	{
		if (!mIsDevicePresent)
			return false;
		final Variable<Double> lTemperatureInCelciusVariable = getTemperatureInCelciusVariable();
		final double lTemperatureInCelcius = TC01libLibrary.tC01lib(mPhysicalChannelPointer,
																																mThermoCoupleNIType.getValue());
		// System.out.println(lTemperatureInCelcius);
		lTemperatureInCelciusVariable.set(lTemperatureInCelcius);
		return true;
	}

	@Override
	public boolean open()
	{
		if (!mIsDevicePresent)
			return false;
		return true;
	}

	@Override
	public boolean close()
	{
		if (!mIsDevicePresent)
			return false;
		return true;
	}

}