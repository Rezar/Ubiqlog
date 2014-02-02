package com.ubiqlog.vis.utils;

import java.text.DateFormat;
import java.util.Date;

import com.ubiqlog.vis.utils.SensorState.Movement;

public class JsonEncodeDecode 
{
	public static String EncodeBluetooth(String deviceName, String deviceAddress, String bindState, Date timeStamp) 
	{
		StringBuilder encodedString = new StringBuilder("");
		encodedString = encodedString.append("{\"Bluetooth\":{\"name\":\""
				+ deviceName
				+ "\",\"address\":\""
				+ deviceAddress
				+ "\",\"bond status\":\""
				+ bindState
				+ "\",\"time\":\""
				+ DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(timeStamp) 
				+ "\"}}");

		return encodedString.toString();
	}

	public static String[] DecodeBluetooth(String jsonString) 
	{
		String[] split = jsonString.split("\"");

		String[] decodedString = new String[4];

		decodedString[0] = split[5];
		decodedString[1] = split[9];
		decodedString[2] = split[13];
		decodedString[3] = split[17];

		return decodedString;
	}

	public static String EncodeMovement(Date timestampStart, Date timestampEnd, Movement movementState) 
	{
		StringBuilder encodedString = new StringBuilder("");
		encodedString = encodedString.append("{\"Movement\":{\"start\":\""
				+ DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(timestampStart)
				+ "\",\"end\":\""
				+  DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(timestampEnd)
				+ "\",\"state\":\""
				+ movementState
				+ "\"}}");

		return encodedString.toString();
	}
	
	public static String[] DecodeMovement(String jsonString) 
	{
		String[] split = jsonString.split("\"");

		String[] decodedString = new String[3];

		decodedString[0] = split[5];
		decodedString[1] = split[9];
		decodedString[2] = split[13];

		return decodedString;
	}
}
