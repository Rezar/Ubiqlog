package com.ubiqlog.vis.extras.bluetooth;

import java.util.ArrayList;
import java.util.Date;
import com.ubiqlog.vis.utils.SensorState;

/**
 * 
 * @author Dorin Gugonatu
 * 
 */

public class BluetoothDetection 
{
	private String deviceName;
	private String deviceAddress;
	private ArrayList<DetectionDate> detectionDates;

	public BluetoothDetection(String deviceName, String deviceAddress) 
	{		
		this.deviceName = deviceName;
		this.deviceAddress = deviceAddress;
		
		this.detectionDates = new ArrayList<DetectionDate>();
	}
	
	public void InsertDetection(long detectionTimestamp, SensorState.Bluetooth deviceState)
	{
		detectionDates.add(new DetectionDate(detectionTimestamp, deviceState));
	}
	
	public String getDeviceName()
	{
		return this.deviceName;
	}
	
	public String getDeviceAddress()
	{
		return this.deviceAddress;
	}
	
	public ArrayList<Long> getDetectionDates(SensorState.Bluetooth deviceState)
	{
		ArrayList<Long> toReturn = new ArrayList<Long>();
		
		for (DetectionDate detectionDate : detectionDates)
		{
			if (detectionDate.getDeviceState() == deviceState)
			{
				toReturn.add(detectionDate.getDetectionTimetamp());
			}
		}
		
		return toReturn;
	}
	
	
	class DetectionDate
	{
		private long detectionTimestamp;
		private SensorState.Bluetooth detectionState;
		
		public DetectionDate(long detectionTimestamp, SensorState.Bluetooth detectionState)
		{
			this.detectionTimestamp = detectionTimestamp;
			this.detectionState = detectionState;
		}
		
		public long getDetectionTimetamp()
		{
			return this.detectionTimestamp;
		}
		
		public SensorState.Bluetooth getDeviceState()
		{
			return this.detectionState;
		}
	}
}