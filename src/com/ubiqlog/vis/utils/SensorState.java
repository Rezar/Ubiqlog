package com.ubiqlog.vis.utils;

public class SensorState 
{
	public static enum Bluetooth 
	{
		NONE("none"),
		BONDING("bonding"),
		BONDED("bonded");
		
		private String state;
		
		Bluetooth(String state)
		{
			this.state = state;
		}
		
		public String getState()
		{
			return state;
		}
	}
	
	public static enum Movement 
	{
		FAST("fast"),
		SLOW("slow"),
		STEADY("steady");
		
		private String state;
		
		Movement(String state)
		{
			this.state = state;
		}
		
		public String getState()
		{
			return state;
		}
	}
}
