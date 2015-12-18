package com.ubiqlog.vis.extras.movement;

import com.ubiqlog.vis.common.DataCollectorEvent;

/**
 * 
 * @author Dorin Gugonatu
 * 
 */

public class MovementDataCollectorEvent extends DataCollectorEvent
{
	private static final long serialVersionUID = 3539329713630415955L;
	
	private MovementStateContainer movementStateContainer;

	public MovementDataCollectorEvent(Object source) 
	{
		super(source);
	}
	
	public MovementDataCollectorEvent(Object source, MovementStateContainer movementStateContainer) 
	{
		this(source);

		this.movementStateContainer = movementStateContainer;
	}

	public MovementStateContainer getMovementContainer() 
	{
		return this.movementStateContainer;
	}
}
