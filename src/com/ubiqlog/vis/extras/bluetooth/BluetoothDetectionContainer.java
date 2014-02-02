package com.ubiqlog.vis.extras.bluetooth;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import com.ubiqlog.vis.utils.JsonEncodeDecode;
import com.ubiqlog.vis.utils.SensorState.Bluetooth;

/**
 * 
 * @author Dorin Gugonatu
 * 
 */

public class BluetoothDetectionContainer 
{
	private Hashtable<String, BluetoothDetection> hashDetections;
	private ArrayList<String> listDevicesAddresses;
	
	private Calendar dateLow;
	private Calendar dateHigh;
	private boolean bIsEmpty;

	public BluetoothDetectionContainer(Date startDate, Date endDate) 
	{
		this.hashDetections = new Hashtable<String, BluetoothDetection>();
		this.listDevicesAddresses = new ArrayList<String>(); 
		
		this.dateLow = Calendar.getInstance();
		this.dateLow.setTime(startDate);
		this.dateHigh = Calendar.getInstance();
		this.dateHigh.setTime(endDate);
		this.bIsEmpty = true;
	}

	public void parseJsonList(String[] jsonStringList) 
	{
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);

		try 
		{
			for (String jsonString : jsonStringList) 
			{
				String[] jsonDecoded = JsonEncodeDecode.DecodeBluetooth(jsonString);

				String deviceName = jsonDecoded[0];
				String deviceAddress = jsonDecoded[1];
				String deviceState = jsonDecoded[2];
				String timestampCurrent = jsonDecoded[3];
				
				Calendar currentEventCalendar = (Calendar)Calendar.getInstance().clone();

				currentEventCalendar.setTime(df.parse(timestampCurrent));

				if ((currentEventCalendar.compareTo(dateLow) >=0) && (currentEventCalendar.compareTo(dateHigh) <= 0))
				{
					BluetoothDetection currentDetection;
					
					if ((currentDetection = hashDetections.get(deviceAddress)) == null)
					{
						currentDetection = new BluetoothDetection(deviceName, deviceAddress);
						hashDetections.put(deviceAddress, currentDetection);
						listDevicesAddresses.add(deviceAddress);
					}

					Bluetooth bluetoothState = Bluetooth.valueOf(deviceState.toUpperCase());
					
					currentDetection.InsertDetection(currentEventCalendar.getTime().getTime() - dateLow.getTime().getTime(), bluetoothState);

					if (bIsEmpty)
					{
						bIsEmpty = !bIsEmpty;
					}
				}
			}
		} 
		catch (Exception ex) 
		{
		}
	}
	
	public ArrayList<Long> getAllDetectionDates(String deviceAddress, Bluetooth deviceState)
	{
		return hashDetections.get(deviceAddress).getDetectionDates(deviceState);
	}
	
	public int getNumberOfDevices()
	{
		return listDevicesAddresses.size();
	}

	public ArrayList<BluetoothDetection> getAllDevicesDetections() 
	{
		ArrayList<BluetoothDetection> toReturn = new ArrayList<BluetoothDetection>();
		
		for (BluetoothDetection bluetoothDetection : hashDetections.values())
		{
			toReturn.add(bluetoothDetection);
		}
		
		return toReturn;
	}

	public Calendar getLowestDate() 
	{
		return this.dateLow;
	}

	public Calendar getHighestDate() 
	{
		return this.dateHigh;
	}
}
