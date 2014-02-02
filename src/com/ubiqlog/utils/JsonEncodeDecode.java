package com.ubiqlog.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ubiqlog.utils.SensorState.Movement;

public class JsonEncodeDecode {

	public static final SimpleDateFormat dateformat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
	
	public static String EncodeApplication(String friendlyName, String processName, Date startTime, Date endTime) {	
		StringBuilder encodedString = new StringBuilder("");
		encodedString = encodedString.append("{\""+friendlyName+"\":{\"ProcessName\":\""
				+ processName + "\",\"Start\":\""
				+ dateformat.format(startTime) + "\",\"End\":\""
				+ dateformat.format(endTime) + "\"}}");
		return encodedString.toString();
	}
	

	public static String EncodeCall(String friendlyName, String phoneNumber, int duration, Date dateTime, int type,
			Boolean withAnnotation, String annotationName) {
		StringBuilder annotationString  = new StringBuilder();
		if(withAnnotation){
			annotationString.append(", \"metadata\": {\"name\": \"" + annotationName + "\"}");
		}
		
		StringBuilder encodedString = new StringBuilder("");
		encodedString = encodedString.append("{\""+friendlyName+"\":{\"Number\":\""
				+ phoneNumber+ "\",\"Duration\":\""
				+ duration+ "\",\"Time\":\""
				+ dateformat.format(dateTime) + "\",\"Type\":\""
				+ type + "\""+ annotationString+ "}}");
			
		return encodedString.toString();
	}
	
	public static String EncodeSms(String friendlyName, String phoneNumber, int type, Date dateTime, String body,
			Boolean withAnnotation, String annotationName) {
		StringBuilder annotationString  = new StringBuilder();
		if(withAnnotation && annotationName!=null){
			annotationString.append(", \"metadata\": {\"name\": \"" + annotationName + "\"}");
		}
		
		StringBuilder encodedString = new StringBuilder("");
		encodedString = encodedString.append("{\""+friendlyName+"\":{\"Address\":\""
				+ phoneNumber+ "\",\"type\":\""
				+ type+ "\",\"date\":\""
				+ dateformat.format(dateTime) + "\",\"body\":\""
				+ body+ "\",\"Type\":\""
				+ type + "\""+ annotationString+ "}}");

		return encodedString.toString();	
		
	}
	
	
	public static String EncodeLocation(String friendlyName, double latitude, double longitude, 
			double altitude,Date dateTime, float accuracy, String provider) {
		
		StringBuilder encodedString = new StringBuilder("");
		encodedString = encodedString.append("{\""+friendlyName+"\":{\"Latitude\":\""
				+ latitude + "\",\"Longtitude\":\""
				+ longitude + "\",\"Altitude\":\""
				+ altitude + "\",\"time\":\""
				+  dateformat.format(dateTime) + "\",\"Accuracy\":\""
				+ accuracy + "\",\"Provider\":\""
				+ provider + "\"}}");

		return encodedString.toString();
		
	}
	public static String EncodeLocationGS(String friendlyName, double latitude, double longitude, 
			double altitude,Date dateTime, float accuracy, String provider, float speed) {
		
		StringBuilder encodedString = new StringBuilder("");
		encodedString = encodedString.append("{\""+friendlyName+"\":{\"Latitude\":\""+ latitude + 
				"\",\"Longtitude\":\""+ longitude + 
				"\",\"Altitude\":\""+ altitude + 
				"\",\"time\":\""+  dateformat.format(dateTime) + 
				"\",\"Accuracy\":\""+ accuracy + 
				"\",\"Provider\":\""+ provider + 
				"\",\"Speed\":\""+ String.valueOf(speed) + 
				"\"}}");

		return encodedString.toString();
		
	}
	public static String EncodePicture(String friendlyName, String fullPath, Date dateTime) {
		
		StringBuilder encodedString = new StringBuilder("");
		encodedString = encodedString.append("{\""+friendlyName+"\":{\"FullPath\":\""
				+ fullPath + "\",\"Time\":\""
				+ dateformat.format(dateTime) + "\"}}");

		return encodedString.toString();
	}
	
	public static String EncodeAudio(String friendlyName, String fullPath, Date dateTime) {
		
		StringBuilder encodedString = new StringBuilder("");
		encodedString = encodedString.append("{\""+friendlyName+"\":{\"FullPath\":\""
				+ fullPath + "\",\"Time\":\""+ dateformat.format(dateTime) + "\"}}");

		return encodedString.toString();
	}
	
	
	public static String EncodeBluetooth(String friendlyName, String deviceName, 
			String deviceAddress, String bindState, Date timeStamp) {
		StringBuilder encodedString = new StringBuilder("");
		encodedString = encodedString.append("{\""+friendlyName+"\":{\"name\":\""
				+ deviceName + "\",\"address\":\""
				+ deviceAddress + "\",\"bond status\":\""
				+ bindState + "\",\"time\":\""
				+ dateformat.format(timeStamp) + "\"}}");

		return encodedString.toString();
	}

	public static String[] DecodeBluetooth(String jsonString) {
		String[] split = jsonString.split("\"");

		String[] decodedString = new String[4];

		decodedString[0] = split[5];
		decodedString[1] = split[9];
		decodedString[2] = split[13];
		decodedString[3] = split[17];

		return decodedString;
	}

	public static String EncodeMovement(String friendlyName, Date timestampStart, Date timestampEnd, Movement movementState) {
		StringBuilder encodedString = new StringBuilder("");
		encodedString = encodedString.append("{\""+friendlyName+"\":{\"start\":\""
				+ dateformat.format(timestampStart) + "\",\"end\":\""
				+  dateformat.format(timestampEnd) + "\",\"state\":\""
				+ movementState+ "\"}}");

		return encodedString.toString();
	}
	
	public static String[] DecodeMovement(String jsonString) {
		String[] split = jsonString.split("\"");
		String[] decodedString = new String[3];

		decodedString[0] = split[5];
		decodedString[1] = split[9];
		decodedString[2] = split[13];

		return decodedString;
	}
	
	public static String EncodeActivity(String friendlyName, Date  timestampStart, Date timestampEnd, String intype, int inconfidence) {
		StringBuilder encodedString = new StringBuilder("");
		encodedString = encodedString.append("{\""+friendlyName+"\":{\"start\":\""
				+ dateformat.format(timestampStart) + "\",\"end\":\""
				+ dateformat.format(timestampEnd) + "\",\"type\":\""
				+ intype+ "\",\"condfidence\":\""+inconfidence+"\"}}");

		return encodedString.toString();
	}
}

