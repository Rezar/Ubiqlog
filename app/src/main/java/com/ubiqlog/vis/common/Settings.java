package com.ubiqlog.vis.common;

import com.ubiqlog.common.Setting;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Settings
 * 
 * @author Victor Gugonatu
 * @date 10.2010
 * @version 1.0
 */
public class Settings {

	public static final String googleMapKey = "0U0FROSv7CVVKcfYcZY_7wDf4OBYAgX9HsueySA";
	public static final String PREF_FILE_NAME = "UbiqlogVisPrefFile";
	public static final String LOG_FOLDER = Setting.LOG_FOLDER;
	public static final String SensorLocation = "Location";
	public static final String SensorApplication = "Application";
	public static final String SensorCall = "Call";
	public static final String SensorSms = "SMS";
	public static final int application_timeinterval = 11000;

	public static int location_timeFrame = 5000;
	public static int call_timeFrame = 5000;
	public static int sms_timeFrame = 5000;

	// Initialize preferences (from pref file or default)
	public static void initialise(Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				PREF_FILE_NAME, 0);
		location_timeFrame = settings.getInt("location_timeFrame",
				location_timeFrame);
		call_timeFrame = settings.getInt("call_timeFrame", call_timeFrame);
		sms_timeFrame = settings.getInt("sms_timeFrame", sms_timeFrame);
	}

	// save preferences in pref file
	public static void savePreferences(Context context,
			int _location_timeFrame, int _call_timeFrame, int _sms_timeFrame) {
		SharedPreferences settings = context.getSharedPreferences(
				PREF_FILE_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		location_timeFrame = _location_timeFrame;
		call_timeFrame = _call_timeFrame;
		sms_timeFrame = _sms_timeFrame;

		editor.putInt("location_timeFrame", _location_timeFrame);
		editor.putInt("call_timeFrame", _call_timeFrame);
		editor.putInt("sms_timeFrame", _sms_timeFrame);

		// Commit the edits!
		editor.commit();

	}

}
