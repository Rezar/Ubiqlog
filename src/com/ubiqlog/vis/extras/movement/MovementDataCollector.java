package com.ubiqlog.vis.extras.movement;

import java.util.Calendar;
import java.util.Date;

import com.ubiqlog.vis.common.IDataCollectorListener;
import com.ubiqlog.vis.extras.search.Searcher;

/**
 * 
 * @author Dorin Gugonatu
 * 
 */

public class MovementDataCollector implements Runnable 
{
	private String dataFolder;
	private Date startDate;
	private Date endDate;
	private MovementStateContainer movementStates;
	private IDataCollectorListener dataCollectorListener;

	public MovementDataCollector(IDataCollectorListener dataCollectorListener, String dataFolder, Date startDate, Date endDate) 
	{
		this.dataCollectorListener = dataCollectorListener;
		this.dataFolder = dataFolder;
		this.startDate = startDate;
		this.endDate = endDate;
		this.movementStates = new MovementStateContainer(startDate, endDate);
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

		String[] listMovementStates = dataSearcher.searchFolder(dataFolder, "Movement", startSearchDate.getTime(), endSearchDate.getTime());
		
		if (listMovementStates != null)
		{
			movementStates.parseJsonList(listMovementStates);
		}

		// raise event
		dataCollectorListener.completed(new MovementDataCollectorEvent(this, movementStates));
	}
}