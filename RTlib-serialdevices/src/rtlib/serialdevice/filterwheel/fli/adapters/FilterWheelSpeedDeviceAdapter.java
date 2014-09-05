package rtlib.serialdevice.filterwheel.fli.adapters;

import rtlib.serialdevice.filterwheel.fli.FLIFilterWheelDevice;

public class FilterWheelSpeedDeviceAdapter extends
																					FilterWheelDeviceAdapter
{

	public FilterWheelSpeedDeviceAdapter(final FLIFilterWheelDevice pFLIFilterWheelDevice)
	{
		super(pFLIFilterWheelDevice);
	}

	@Override
	public Double parseValue(final byte[] pMessage)
	{
		return parsePositionOrSpeedValue(pMessage, true);
	}

	@Override
	public byte[] getSetValueCommandMessage(final double pOldSpeed,
																					final double pNewSpeed)
	{
		return getSetPositionAndSpeedCommandMessage(mFLIFilterWheelDevice.getCachedPosition(),
																								(int) pNewSpeed);
	}

	@Override
	public boolean checkAcknowledgementSetValueReturnMessage(final byte[] pMessage)
	{
		return checkAcknowledgement(pMessage);
	}

}
