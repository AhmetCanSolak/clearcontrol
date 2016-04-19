package rtlib.hardware.optomech.filterwheels.devices.sim;

import rtlib.hardware.optomech.filterwheels.FilterWheelDeviceBase;
import rtlib.hardware.optomech.filterwheels.FilterWheelDeviceInterface;

public class FilterWheelDeviceSimulator extends FilterWheelDeviceBase	implements
																																			FilterWheelDeviceInterface
{

	public FilterWheelDeviceSimulator(String pDeviceName,
																		int[] pValidPositions)
	{
		super(pDeviceName, pValidPositions);

		mPositionVariable.addSetListener((o, n) -> {
			System.out.format("%s: new position: %d corresponding to filter '%s' \n",
												pDeviceName,
												n,
												getPositionName(n));
		});
	}

	@Override
	public boolean open()
	{
		return true;
	}

	@Override
	public boolean close()
	{
		return true;
	}

}
