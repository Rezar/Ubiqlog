package com.ubiqlog.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ubiqlog.common.Setting;
import com.ubiqlog.utils.SensorState.Movement;

import org.json.JSONException;
import org.json.JSONObject;

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


	/** Author: AP
	 * obj[0]: appname
	 * obj[1]: startTime
	 * obj[2]: endTime
	 *
	 * @param  , Date
	 * @return encoded app usage string
	 */
	public static String EncodeScreenUsage(String startHour, String endHour, Date starttimeStamp, Date endtimeStamp, Double Tmin
			, Double MinStartHour, Double MinEndHour) {

		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put("start_hour", startHour);
			jsonObject.put("end_hour", endHour);
			jsonObject.put("start_timestamp", Setting.timestampFormat.format(starttimeStamp));
			jsonObject.put("end_timestamp", Setting.timestampFormat.format(endtimeStamp));
			jsonObject.put("min_elapsed", Tmin);
			jsonObject.put("min_start_hour", MinStartHour);
			jsonObject.put("min_end_hour", MinEndHour);

			return jsonObject.toString();

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** Author: AP
	 * @param encoded JSON String
	 * @return obj[0] : Date timestamp
	 * obj[1] : int percent
	 * obj[2] : boolean charging
	 */
	public Object[] DecodeScreenUsage(String encoded) {

		try {
			JSONObject jObj = new JSONObject(encoded);

			String startHour = jObj.getString("start_hour");
			String endHour = jObj.getString("end_hour");
			Date startTime = Setting.timestampFormat.parse(jObj.get("start_timestamp").toString());
			Date endTime = Setting.timestampFormat.parse(jObj.get("end_timestamp").toString());
			Double TminutesElapsed = jObj.getDouble("min_elapsed");
			Double start_minutesElapsed = jObj.getDouble("min_start_hour");
			Double end_minutesElapsed = jObj.getDouble("min_end_hour");

			return new Object[]{startHour, endHour, startTime, endTime, TminutesElapsed, start_minutesElapsed, end_minutesElapsed};

		} catch (JSONException e) {
			e.printStackTrace();

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String EncodeBattery(int percent, boolean charging, Date timeStamp) {
		JSONObject jsonObject = new JSONObject();
		JSONObject sensorDataObj = new JSONObject();
		try {
			jsonObject.put("sensor_name", "Battery");
			jsonObject.put("timestamp", Setting.timestampFormat.format(timeStamp));

			sensorDataObj.put("percent", percent);
			sensorDataObj.put("charging", charging);

			jsonObject.put("sensor_data", sensorDataObj);

			return jsonObject.toString();

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** Battery
	 * @param encoded
	 * @return obj[0] : Date timestamp
	 * obj[1] : int percent
	 * obj[2] : boolean charging
	 */
	public Object[] DecodeBattery(String encoded) {
		if (encoded != null && !encoded.isEmpty()) {
			try {
				JSONObject jObj = new JSONObject(encoded);
				Date date = Setting.timestampFormat.parse(jObj.get("timestamp").toString());

				JSONObject sensorData = jObj.getJSONObject("sensor_data");

				int percent = sensorData.getInt("percent");
				boolean charging = sensorData.getBoolean("charging");

				return new Object[]{date, percent, charging};

			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;

	}

}

