package rtlib.hardware.lasers.devices.cobolt.adapters;

import rtlib.com.serial.adapters.SerialTextDeviceAdapter;
import rtlib.hardware.lasers.devices.cobolt.adapters.protocol.ProtocolCobolt;

public class GetWorkingHoursAdapter extends CoboltAdapter<Integer> implements
																																	SerialTextDeviceAdapter<Integer>
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return ProtocolCobolt.cGetWorkingHoursCommand.getBytes();
	}

	@Override
	public Integer parseValue(final byte[] pMessage)
	{
		return (int) ProtocolCobolt.parseFloat(pMessage);
	}

}
