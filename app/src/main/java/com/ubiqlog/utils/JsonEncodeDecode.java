package com.ubiqlog.utils;

import com.ubiqlog.common.Setting;
import com.ubiqlog.utils.SensorState.Movement;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	 * obj[0]:
	 * obj[1]:
	 * obj[2]:
	 *
	 * @param  , Date
	 * @return encoded screen usage string
	 */
	public static String EncodeScreenUsage(String startHour, String endHour, Date starttimeStamp, Date endtimeStamp, Double Tmin
			, Double MinStartHour, Double MinEndHour) {

		JSONObject jsonObject = new JSONObject();
		JSONObject screenobj = new JSONObject();

		try {

			screenobj.put("start_hour", startHour);
			screenobj.put("end_hour", endHour);
			screenobj.put("start_timestamp", Setting.timestampFormat.format(starttimeStamp));
			screenobj.put("end_timestamp", Setting.timestampFormat.format(endtimeStamp));
			screenobj.put("min_elapsed", Tmin);
			screenobj.put("min_start_hour", MinStartHour);
			screenobj.put("min_end_hour", MinEndHour);
			jsonObject.put("Screen_Interaction", screenobj);
			return jsonObject.toString();

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** Author: AP
	 * @param encoded JSON String
	 * @return obj[0] : Date timestamp
	 * obj[1] :
	 * obj[2] :
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

	/** Author: AP
	 * Battery
	 * obj[0]: x
	 * obj[1]: y
	 * obj[2]: timeStamp
	 *
	 * @param  --float , float , Date
	 * @return encoded accelerometer string
	 */
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

	/** Author: AP
	 *  Accelerometer
	 * obj[0]: x
	 * obj[1]: y
	 * obj[2]: timeStamp
	 *
	 * @param  --float , float , Date
	 * @return encoded accelerometer string
	 */
	public static String EncodeAccelerometer(float x, float y, Date timeStamp) {
		JSONObject jsonObject = new JSONObject();
		JSONObject sensorDataObj = new JSONObject();
		try {
			jsonObject.put("sensor_name", "Accelerometer");
			jsonObject.put("timestamp", Setting.timestampFormat.format(timeStamp));

			sensorDataObj.put("x-axis", x);
			sensorDataObj.put("y-axis", y);

			jsonObject.put("sensor_data", sensorDataObj);

			return jsonObject.toString();

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	//recoding element
	public static String EncodeAccelerometerIn_X(float collect_interval,String array,Date starttimeStamp) {
		JSONObject jsonObject = new JSONObject();
		JSONObject sensorobj = new JSONObject();
		try {


			sensorobj.put("start_timestamp", Setting.timestampFormat.format(starttimeStamp));
			sensorobj.put("collect_interval", collect_interval/1000+"seconds");
			sensorobj.put("Accelerometer_axis", "X-axis");
			sensorobj.put("sensor_data_array", array);
			jsonObject.put("Accelerometer",sensorobj);
			return jsonObject.toString();

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** Accelerometer
	 * @param encoded
	 * @return obj[0] : Date timestamp
	 * obj[1] : float x
	 * obj[2] : float y
	 */
	public Object[] DecodeAccelerometer(String encoded) {
		if (encoded != null && !encoded.isEmpty()) {
			try {
				JSONObject jObj = new JSONObject(encoded);
				Date date = Setting.timestampFormat.parse(jObj.get("timestamp").toString());

				JSONObject sensorData = jObj.getJSONObject("sensor_data");

				float x = Float.valueOf(String.format(sensorData.get("x-axis").toString(), ".2f"));
				float y = Float.valueOf(String.format(sensorData.get("y-axis").toString(), ".2f"));

				return new Object[]{date, x, y};

			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/** Author: AP
	 *  RawAudio
	 * obj[0]: encodedData
	 * obj[1]: timeStamp
	 *
	 * @param  --String , Date
	 * @return encoded RawAudio string
	 */
	public static String EncodeRawAudio(String encodedData, Date timeStamp) {
		JSONObject jsonObject = new JSONObject();
		JSONObject sensorDataObj = new JSONObject();
		try {
			jsonObject.put("sensor_name", "RawAudio");
			jsonObject.put("timestamp", Setting.timestampFormat.format(timeStamp));

			sensorDataObj.put("data", encodedData);

			jsonObject.put("sensor_data", sensorDataObj);

			return jsonObject.toString();

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** Raw Audio
	 * @param encoded
	 * @return obj[0] : Date timestamp
	 * obj[1] :  String encodedData
	 *
	 */
	public Object[] DecodeRawAudio(String encoded) {
		if (encoded != null && !encoded.isEmpty()) {
			try {
				JSONObject jObj = new JSONObject(encoded);
				Date date = Setting.timestampFormat.parse(jObj.get("timestamp").toString());

				JSONObject sensorData = jObj.getJSONObject("sensor_data");

				// Base64 representation of byte[]
				String encodedData = sensorData.getString("data");

				return new Object[]{date, encodedData};

			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/** Author: AP
	 *  AmbientLight
	 * obj[0]: encodedData
	 * obj[1]: timeStamp
	 *
	 * @param  --String , Date
	 * @return encoded RawAudio string
	 */
	public static String EncodeAmbientLight(float lux, Date timeStamp) {
		JSONObject jsonObject = new JSONObject();
		JSONObject sensorDataObj = new JSONObject();
		try {
			sensorDataObj.put("lux", lux);
			sensorDataObj.put("timestamp", Setting.timestampFormat.format(timeStamp));
			jsonObject.put("AmbientLight",sensorDataObj);
			return jsonObject.toString();

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** Raw Audio
	 * @param encoded
	 * @return obj[0] : Date timestamp
	 * obj[1] :  String encodedData
	 *
	 */
	public Object[] DecodeAmbientLight(String encoded) {
		if (encoded != null && !encoded.isEmpty()) {
			try {
				JSONObject jObj = new JSONObject(encoded);
				Date date = Setting.timestampFormat.parse(jObj.get("timestamp").toString());

				JSONObject sensorData = jObj.getJSONObject("sensor_data");

				float lux = Float.valueOf(String.format(sensorData.get("lux").toString(), ".2f"));

				return new Object[]{date, lux};

			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String EncodeSleep(Date timeStamp1, Date timeStamp2) {
		JSONObject jsonObject = new JSONObject();
		JSONObject sensorDataObj = new JSONObject();
		try {
			sensorDataObj.put("starttimestamp", Setting.timestampFormat.format(timeStamp1));
			sensorDataObj.put("endtimestamp", Setting.timestampFormat.format(timeStamp2));
			jsonObject.put("SleepSensor",sensorDataObj);
			return jsonObject.toString();

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String EncodeApplication(String appname, Date timeStamp) {
		JSONObject jsonObject = new JSONObject();
		JSONObject sensorDataObj = new JSONObject();
		try {
			sensorDataObj.put("appname", appname);
			sensorDataObj.put("timestamp", Setting.timestampFormat.format(timeStamp));
			jsonObject.put("ApplicationSensor",sensorDataObj);
			return jsonObject.toString();

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}

