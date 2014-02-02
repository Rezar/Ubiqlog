package com.ubiqlog.vis.extras.movement;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import com.ubiqlog.vis.utils.JsonEncodeDecode;
import com.ubiqlog.vis.utils.SensorState;

/**
 * 
 * @author Dorin Gugonatu
 * 
 */

public class MovementStateContainer 
{
	private ArrayList<MovementState> listMovementStates;
	private Calendar dateLow;
	private Calendar dateHigh;
	private boolean bIsEmpty;

	public MovementStateContainer(Date startDate, Date endDate) 
	{
		this.listMovementStates = new ArrayList<MovementState>();
		this.dateLow = Calendar.getInstance();
		this.dateLow.setTime(startDate);
		this.dateHigh = Calendar.getInstance();
		this.dateHigh.setTime(endDate);
		this.bIsEmpty = true;
	}

	public void parseJsonList(String[] jsonStringList) 
	{
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
	
		MovementState currentState;
		Long dateTimeCurrent;

		Hashtable<Long, MovementState> hashMovementStates = new Hashtable<Long, MovementState>();

		try 
		{
			for (String jsonString : jsonStringList) 
			{
				String[] jsonDecoded = JsonEncodeDecode.DecodeMovement(jsonString);

				String timestampStart = jsonDecoded[0];
				String timestampEnd = jsonDecoded[1];
				String movementState = jsonDecoded[2];
				
				Calendar startEventCalendar = (Calendar)Calendar.getInstance().clone();
				Calendar endEventCalendar = (Calendar)Calendar.getInstance().clone();

				startEventCalendar.setTime(df.parse(timestampStart));
				endEventCalendar.setTime(df.parse(timestampEnd));
				
				if (startEventCalendar.before(dateLow) && endEventCalendar.after(dateLow))
				{
					startEventCalendar.setTime(dateLow.getTime());
				}
				
				if (startEventCalendar.before(dateHigh) && endEventCalendar.after(dateHigh))
				{
					endEventCalendar.setTime(dateHigh.getTime());
				}

				if ((startEventCalendar.compareTo(dateLow) >=0) && (endEventCalendar.compareTo(dateHigh) <= 0))
				{
					Calendar currentEventCalendar = (Calendar)startEventCalendar.clone();
					
					currentEventCalendar.set(Calendar.HOUR, 0);
					currentEventCalendar.set(Calendar.MINUTE, 0);
					currentEventCalendar.set(Calendar.SECOND, 0);
					currentEventCalendar.set(Calendar.MILLISECOND, 0);

					dateTimeCurrent = currentEventCalendar.getTime().getTime();

					if ((currentState = hashMovementStates.get(dateTimeCurrent)) == null)
					{
						hashMovementStates.put(dateTimeCurrent, new MovementState());
					}

					currentState = hashMovementStates.get(dateTimeCurrent);

					currentState.InsertState(startEventCalendar.getTime(), endEventCalendar.getTime(), dateLow.getTime(), SensorState.Movement.valueOf(movementState));

					if (bIsEmpty)
					{
						bIsEmpty = !bIsEmpty;
					}
				}
			}
		} 
		catch (Exception ex) 
		{
		}

		if (!bIsEmpty)
		{
			Calendar currentEventCalendar = (Calendar)dateLow.clone();
			currentEventCalendar.set(Calendar.HOUR, 0);
			currentEventCalendar.set(Calendar.MINUTE, 0);
			currentEventCalendar.set(Calendar.SECOND, 0);
			currentEventCalendar.set(Calendar.MILLISECOND, 0);
			int iResult;

			do
			{
				if ((currentState = hashMovementStates.get(currentEventCalendar.getTime().getTime())) != null)
				{
					listMovementStates.add(currentState);
				}

				currentEventCalendar.add(Calendar.DAY_OF_YEAR, 1);

				iResult = currentEventCalendar.compareTo(dateHigh);
			}
			while (iResult == 0 || iResult == -1);
		}
	}

	public ArrayList<MovementInterval> getMovementIntervals(SensorState.Movement movementType)
	{		
		ArrayList<MovementInterval> listResults = new ArrayList<MovementInterval>();

		for (MovementState movementState : listMovementStates)
		{
			listResults.addAll(movementState.getIntervals(movementType));
		}

		return listResults;
	}

	public boolean IsEmpty()
	{
		return bIsEmpty;
	}

	public Calendar getLowestDate() 
	{
		return this.dateLow;
	}

	public Calendar getHighestDate() 
	{
		return this.dateHigh;
	}
}
