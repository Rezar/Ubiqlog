package com.ubiqlog.vis.extras.movement;

public class MovementInterval 
{
	public long startInitial, endInitial;
	public long startCurrent, endCurrent;
	
	public MovementInterval(long start, long end)
	{
		this.startInitial = start;
		this.endInitial = end;
		
		this.startCurrent = startInitial;
		this.endCurrent = endInitial;
	}
	
	public long getStart()
	{
		return this.startCurrent;
	}
	
	public long getEnd()
	{
		return this.endCurrent;
	}
	
	public void scale(float fScaleFactor)
	{
		this.startCurrent = (long)(startCurrent * fScaleFactor);
		this.endCurrent = (long)(endCurrent * fScaleFactor);
	}
	
	public void reset()
	{
		this.startCurrent = startInitial;
		this.endCurrent = endInitial;
	}
}
