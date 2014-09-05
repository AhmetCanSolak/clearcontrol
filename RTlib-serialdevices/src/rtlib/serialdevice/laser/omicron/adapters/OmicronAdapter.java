package rtlib.serialdevice.laser.omicron.adapters;

import rtlib.serial.adapters.SerialTextDeviceAdapter;
import rtlib.serialdevice.laser.omicron.adapters.protocol.ProtocolXX;

public abstract class OmicronAdapter implements
																		SerialTextDeviceAdapter
{

	@Override
	public Character getGetValueReturnMessageTerminationCharacter()
	{
		return ProtocolXX.cMessageTerminationCharacter;
	}

	@Override
	public long getGetValueReturnWaitTimeInMilliseconds()
	{
		return ProtocolXX.cWaitTimeInMilliSeconds;
	}

	@Override
	public byte[] getSetValueCommandMessage(final double pOldValue,
																					final double pNewValue)
	{
		return null;
	}

	@Override
	public Character getSetValueReturnMessageTerminationCharacter()
	{
		return ProtocolXX.cMessageTerminationCharacter;
	}

	@Override
	public long getSetValueReturnWaitTimeInMilliseconds()
	{
		return ProtocolXX.cWaitTimeInMilliSeconds;
	}

	@Override
	public boolean checkAcknowledgementSetValueReturnMessage(final byte[] pMessage)
	{
		return pMessage[0] == '!';
	}

	@Override
	public boolean hasResponseForSet()
	{
		return true;
	}

	@Override
	public boolean hasResponseForGet()
	{
		return true;
	}

}
