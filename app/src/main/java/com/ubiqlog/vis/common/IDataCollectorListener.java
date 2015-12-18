package com.ubiqlog.vis.common;

import java.util.EventListener;

/**
 * 
 * @author Dorin Gugonatu
 * 
 */

public interface IDataCollectorListener extends EventListener 
{
	public void completed(DataCollectorEvent dataCollectorEvent);
}
