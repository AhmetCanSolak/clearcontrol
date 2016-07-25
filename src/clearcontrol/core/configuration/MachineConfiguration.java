package clearcontrol.core.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.math3.analysis.UnivariateFunction;

import clearcontrol.core.math.functions.InvertibleFunction;
import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.bounded.BoundedVariable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MachineConfiguration
{
	private static final String cComments = "RTlib machine configuration file";
	private static final MachineConfiguration sConfiguration = new MachineConfiguration();
	private static ObjectMapper sObjectMapper = new ObjectMapper();

	public static MachineConfiguration getCurrentMachineConfiguration()
	{
		return sConfiguration;
	}

	private Properties mProperties;

	private File mRTLibFolder;
	private File mPersistentVariablesFolder;

	public MachineConfiguration()
	{
		super();

		try
		{
			final String lUserHome = System.getProperty("user.home");
			final File lUserHomeFolder = new File(lUserHome);
			mRTLibFolder = new File(lUserHomeFolder, "RTlib/");
			mRTLibFolder.mkdirs();
			mPersistentVariablesFolder = getFolder("PersistentVariables");

			final File lConfigurationFile = new File(	mRTLibFolder,
																								"configuration.txt");

			if (!lConfigurationFile.exists())
			{
				final Writer lWriter = new FileWriter(lConfigurationFile);
				mProperties.store(lWriter, cComments);
			}
			final FileInputStream lFileInputStream = new FileInputStream(lConfigurationFile);
			mProperties = new Properties();
			mProperties.load(lFileInputStream);
		}

		catch (final IOException e2)
		{
			e2.printStackTrace();
			mProperties = null;
		}
	}

	public Properties getProperties()
	{
		return mProperties;
	}

	public boolean containsKey(String pKey)
	{
		if (mProperties == null)
			return false;
		return mProperties.containsKey(pKey);
	}

	public String getStringProperty(String pKey, String pDefaultValue)
	{
		if (mProperties == null)
			return pDefaultValue;
		return mProperties.getProperty(pKey, pDefaultValue);
	}

	public Integer getIntegerProperty(String pKey, Integer pDefaultValue)
	{
		if (mProperties == null)
			return pDefaultValue;
		final String lProperty = mProperties.getProperty(pKey);
		if (lProperty == null)
			return pDefaultValue;

		return Integer.parseInt(lProperty);
	}

	public Long getLongProperty(String pKey, Long pDefaultValue)
	{
		if (mProperties == null)
			return pDefaultValue;
		final String lProperty = mProperties.getProperty(pKey);
		if (lProperty == null)
			return pDefaultValue;

		return Long.parseLong(lProperty);
	}

	public Double getDoubleProperty(String pKey, Double pDefaultValue)
	{
		if (mProperties == null)
			return pDefaultValue;
		final String lProperty = mProperties.getProperty(pKey);
		if (lProperty == null)
			return pDefaultValue;

		return Double.parseDouble(lProperty);
	}

	public boolean getBooleanProperty(String pKey, Boolean pDefaultValue)
	{
		if (mProperties == null)
			return pDefaultValue;
		final String lProperty = mProperties.getProperty(pKey);
		if (lProperty == null)
			return pDefaultValue;

		return Boolean.parseBoolean(lProperty.toLowerCase()) || lProperty.trim()
																																			.equals("1")
						|| lProperty.trim().toLowerCase().equals("on")
						|| lProperty.trim().toLowerCase().equals("present")
						|| lProperty.trim().toLowerCase().equals("true");
	}

	public File getFileProperty(String pKey, File pDefaultFile)
	{
		return new File(getStringProperty(pKey,
																			pDefaultFile == null ? null
																													: pDefaultFile.getPath()));
	}

	public String getSerialDevicePort(String pDeviceName,
																		int pDeviceIndex,
																		String pDefaultPort)
	{
		final String lKey = "device.serial." + pDeviceName.toLowerCase()
												+ "."
												+ pDeviceIndex;
		final String lPort = getStringProperty(lKey, pDefaultPort);
		return lPort;
	}

	public String[] getNetworkDeviceHostnameAndPort(String pDeviceName,
																									int pDeviceIndex,
																									String pDefaultHostNameAndPort)
	{
		final String lKey = "device.network." + pDeviceName.toLowerCase()
												+ "."
												+ pDeviceIndex;
		final String lHostnameAndPort = getStringProperty(lKey,
																											pDefaultHostNameAndPort);
		return lHostnameAndPort.split(":");
	}

	public Integer getIODevicePort(	String pDeviceName,
																	Integer pDefaultPort)
	{
		final String lKey = "device." + pDeviceName.toLowerCase();
		final Integer lPort = getIntegerProperty(lKey, pDefaultPort);
		return lPort;
	}

	public boolean getIsDevicePresent(String pDeviceName,
																		int pDeviceIndex)
	{
		final String lKey = "device." + pDeviceName.toLowerCase()
												+ "."
												+ pDeviceIndex;
		return getBooleanProperty(lKey, false);
	}

	public ArrayList<String> getList(String pPrefix)
	{
		final ArrayList<String> lList = new ArrayList<String>();
		for (int i = 0; i < Integer.MAX_VALUE; i++)
		{
			final String lKey = pPrefix + "." + i;
			final String lProperty = mProperties.getProperty(lKey, null);
			if (lProperty == null)
				break;
			lList.add(lProperty);
		}
		return lList;
	}

	public File getFolder(String pFolderName)
	{
		File lFolder = new File(mRTLibFolder, pFolderName);
		lFolder.mkdirs();
		return lFolder;
	}

	public File getPersistencyFolder()
	{
		return mPersistentVariablesFolder;
	}

	public File getPersistentVariableFile(String pVariableName)
	{
		return new File(getPersistencyFolder(), pVariableName);
	}

	public UnivariateAffineFunction getUnivariateAffineFunction(String pFunctionName)
	{
		String lAffineFunctionString = getStringProperty(	pFunctionName,
																											null);

		if (lAffineFunctionString == null)
		{
			System.out.println("Cannot find following function def in configuration file: " + pFunctionName);
			UnivariateAffineFunction lUnivariateAffineFunction = new UnivariateAffineFunction(1,
																																												0);
			return lUnivariateAffineFunction;
		}

		TypeReference<HashMap<String, Double>> lTypeReference = new TypeReference<HashMap<String, Double>>()
		{
		};

		try
		{
			HashMap<String, Double> lMap = sObjectMapper.readValue(	lAffineFunctionString,
																															lTypeReference);

			UnivariateAffineFunction lUnivariateAffineFunction = new UnivariateAffineFunction(lMap.get("a"),
																																												lMap.get("b"));

			return lUnivariateAffineFunction;

		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}

	}

	public <T extends Number, F extends UnivariateFunction> void getBoundsForVariable(String pBoundsName,
																																										BoundedVariable<T> pVariable)
	{
		getBoundsForVariable(pBoundsName, pVariable, null);
	}

	public <T extends Number, F extends UnivariateFunction> void getBoundsForVariable(String pBoundsName,
																																										BoundedVariable<T> pVariable,
																																										InvertibleFunction<F> pFunction)
	{
		String lAffineFunctionString = getStringProperty(	pBoundsName,
																											null);

		if (lAffineFunctionString == null)
		{
			System.out.println("Cannot find following bounds def in configuration file: " + pBoundsName);
			pVariable.setMinMax(-100.0, 100.0);

			return;
		}

		TypeReference<HashMap<String, Double>> lTypeReference = new TypeReference<HashMap<String, Double>>()
		{
		};

		try
		{
			HashMap<String, Double> lMap = sObjectMapper.readValue(	lAffineFunctionString,
																															lTypeReference);

			Double lMin = lMap.get("min");
			Double lMax = lMap.get("max");

			if (lMin == null || lMax == null)
			{
				System.out.println("Cannot find following bounds def in configuration file: " + pBoundsName);
				pVariable.setMinMax(-100.0, 100.0);
				return;
			}

			if (pFunction == null)
			{
				System.out.format("Function provided for setting bounds of %s is null! \n",
													pBoundsName);
				pVariable.setMinMax(-100.0, 100.0);
				return;
			}

			UnivariateFunction lInverse = pFunction.inverse();

			double lDomainMin = lInverse.value(lMin);
			double lDomainMax = lInverse.value(lMax);

			pVariable.setMinMax(lDomainMin, lDomainMax);

			Double lGranularity = lMap.get("granularity");
			if (lGranularity == null)
				pVariable.setGranularity(0);
			else
				pVariable.setGranularity(lGranularity);

			return;

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
}