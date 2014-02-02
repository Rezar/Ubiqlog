package com.ubiqlog.vis.extras.bluetooth;

import java.util.Calendar;
import java.util.Date;

import com.ubiqlog.vis.common.IDataCollectorListener;
import com.ubiqlog.vis.extras.search.Searcher;

/**
 * 
 * @author Dorin Gugonatu
 * 
 */

public class BluetoothDataCollector implements Runnable 
{
	private String dataFolder;
	private Date startDate;
	private Date endDate;
	private IDataCollectorListener dataCollectorListener;
	private BluetoothDetectionContainer bluetoothDetections;

	public BluetoothDataCollector(IDataCollectorListener dataCollectorListener, String dataFolder, Date startDate, Date endDate) 
	{
		this.dataCollectorListener = dataCollectorListener;
		this.dataFolder = dataFolder;
		this.startDate = startDate;
		this.endDate = endDate;
		this.bluetoothDetections = new BluetoothDetectionContainer(this.startDate, this.endDate);
	}

	public BluetoothDataCollector(IDataCollectorListener dataCollectorListener, String dataFolder) 
	{
		this(dataCollectorListener, dataFolder, new Date(0, 1, 1), new Date(200, 1, 1));
	}

	public void run() 
	{	
		Searcher dataSearcher = new Searcher();
		
		Calendar startSearchDate = Calendar.getInstance();
		Calendar endSearchDate = Calendar.getInstance();
		
		startSearchDate.setTime((Date)startDate.clone());
		endSearchDate.setTime((Date)endDate.clone());
		
		startSearchDate.set(Calendar.HOUR_OF_DAY, 0);
		startSearchDate.set(Calendar.MINUTE, 0);
		startSearchDate.set(Calendar.SECOND, 0);
		startSearchDate.set(Calendar.MILLISECOND, 0);
		
		endSearchDate.set(Calendar.HOUR_OF_DAY, 23);
		endSearchDate.set(Calendar.MINUTE, 59);
		endSearchDate.set(Calendar.SECOND, 59);
		endSearchDate.set(Calendar.MILLISECOND, 999);

		String[] listMovementStates = dataSearcher.searchFolder(dataFolder, "Bluetooth", startSearchDate.getTime(), endSearchDate.getTime());
		
		if (listMovementStates != null)
		{
			bluetoothDetections.parseJsonList(listMovementStates);
		}

		// raise event
		dataCollectorListener.completed(new BluetoothDataCollectorEvent(this, bluetoothDetections));
	}
}