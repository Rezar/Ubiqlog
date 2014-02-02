package com.ubiqlog.vis.common;

public class LevelOfDetail 
{
	private LodState lodState;
	
	private float millisPerPixelQuarterHour = 0.0f;
	private float millisPerPixelHalfHour = 0.0f;
	private float millisPerPixelHour = 0.0f;
	private float millisPerPixelDay = 0.0f;
	private float millisPerPixelWeek = 0.0f;
	private float millisPerPixelMonth = 0.0f;
	private float millisPerPixelWhole = 0.0f;
	
	private float fNorm = 0.5f;
	
	public static final float MILLIS_PER_SECOND = 1000.0f;
	public static final float MILLIS_PER_MINUTE = 60.0f * MILLIS_PER_SECOND;
	public static final float MILLIS_PER_QUARTER_HOUR = 15.0f * MILLIS_PER_MINUTE;
	public static final float MILLIS_PER_HALF_HOUR = 2.0f * MILLIS_PER_QUARTER_HOUR;
	public static final float MILLIS_PER_HOUR = 2.0f * MILLIS_PER_HALF_HOUR;
	public static final float MILLIS_PER_DAY = 24.0f * MILLIS_PER_HOUR;
	public static final float MILLIS_PER_WEEK = 7.0f * MILLIS_PER_DAY;
	public static final float MILLIS_PER_MONTH = 28.0f * MILLIS_PER_WEEK;
	
	public static enum LodState
	{
		INIT(0),
		WHOLE(1),
		YEAR(2),
		MONTH(3),
		WEEK(4),
		DAY(5),
		HOUR(6),
		HALF_HOUR(7),
		QUARTER_HOUR(8);
		
		private int lod;
		
		LodState(int lod)
		{
			this.lod = lod;
		}
		
		public int getLod()
		{
			return lod;
		}
	}
	
	public LevelOfDetail()
	{
		lodState = LodState.INIT;
	}
	
	public void init(float timeline)
	{
		millisPerPixelQuarterHour = MILLIS_PER_QUARTER_HOUR / timeline;
		millisPerPixelHalfHour = MILLIS_PER_HALF_HOUR / timeline;
		millisPerPixelHour = MILLIS_PER_HOUR / timeline;
		millisPerPixelDay = MILLIS_PER_DAY / timeline;
		millisPerPixelWeek = MILLIS_PER_WEEK / timeline;
		millisPerPixelMonth = MILLIS_PER_MONTH / timeline;
	}
	
	public LodState getState()
	{
		return lodState;
	}
	
	public void submitChange(float millisPerPixel)
	{
		LodState lodResult = LodState.YEAR;
		
		float millisPerPixelTmp = millisPerPixel * fNorm;
		
		if (millisPerPixelTmp <= millisPerPixelMonth)
		{
			lodResult = LodState.MONTH;
		}
		
		if (millisPerPixelTmp <= millisPerPixelWeek)
		{
			lodResult = LodState.WEEK;
		}
		
		if (millisPerPixelTmp <= millisPerPixelDay)
		{
			lodResult = LodState.DAY;
		}
		
		if (millisPerPixelTmp <= millisPerPixelHour)
		{
			lodResult = LodState.HOUR;
		}
		
		if (millisPerPixelTmp <= millisPerPixelHalfHour)
		{
			lodResult = LodState.HALF_HOUR;
		}
		
		if (millisPerPixelTmp <= millisPerPixelQuarterHour)
		{
			lodResult = LodState.QUARTER_HOUR;
		}
		
		if (millisPerPixel >= millisPerPixelWhole)
		{
			lodResult = LodState.WHOLE;
		}
		
		lodState = lodResult;
	}

	public void setWholeParamter(float millisPerPixelWhole) 
	{
		this.millisPerPixelWhole = millisPerPixelWhole;
	}
}
