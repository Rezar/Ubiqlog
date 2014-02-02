package com.ubiqlog.vis.extras.movement;

import java.util.ArrayList;
import java.util.Date;
import com.ubiqlog.vis.utils.SensorState.Movement;

/**
 * 
 * @author Dorin Gugonatu
 * 
 */

public class MovementState 
{
	private ArrayList<MovementInterval> fastMovement;
	private ArrayList<MovementInterval> slowMovement;
	private ArrayList<MovementInterval> steadyMovement;

	public MovementState() 
	{
		this.fastMovement = new ArrayList<MovementInterval>();
		this.slowMovement = new ArrayList<MovementInterval>();
		this.steadyMovement = new ArrayList<MovementInterval>();
	}

	public void InsertState(Date timestampStart, Date timestampEnd, Date refDate, Movement state) 
	{
		switch(state)
		{
		case FAST:
			fastMovement.add(new MovementInterval(timestampStart.getTime() - refDate.getTime(), timestampEnd.getTime() - refDate.getTime()));
			break;
			
		case SLOW:
			slowMovement.add(new MovementInterval(timestampStart.getTime() - refDate.getTime(), timestampEnd.getTime() - refDate.getTime()));
			break;
			
		case STEADY:
			steadyMovement.add(new MovementInterval(timestampStart.getTime() - refDate.getTime(), timestampEnd.getTime() - refDate.getTime()));
			break;
		}
	}
	
	public ArrayList<MovementInterval> getIntervals(Movement movementType)
	{
		ArrayList<MovementInterval> toReturn; 
		
		switch (movementType)
		{
		case FAST:
			toReturn = fastMovement;
			break;
			
		case SLOW:
			toReturn = slowMovement;
			break;
			
		case STEADY:
			toReturn = steadyMovement;
			break;
			
		default:
			toReturn = new ArrayList<MovementInterval>();
			break;
		}
		
		return toReturn;
	}
}