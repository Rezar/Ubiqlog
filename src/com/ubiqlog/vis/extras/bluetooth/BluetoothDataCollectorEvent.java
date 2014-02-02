package com.ubiqlog.vis.extras.bluetooth;

import com.ubiqlog.vis.common.DataCollectorEvent;

/**
 * 
 * @author Dorin Gugonatu
 * 
 */

public class BluetoothDataCollectorEvent extends DataCollectorEvent 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -450276825688343502L;
	private BluetoothDetectionContainer bluetoothDevicesContainer;

	public BluetoothDataCollectorEvent(Object source) 
	{
		super(source);
	}

	public BluetoothDataCollectorEvent(Object source, BluetoothDetectionContainer bluetoothDevicesContainer) 
	{
		this(source);

		this.bluetoothDevicesContainer = bluetoothDevicesContainer;
	}

	public BluetoothDetectionContainer getBluetoothContainer() 
	{
		return this.bluetoothDevicesContainer;
	}
}
