package com.ubiqlog.sensors;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.ubiqlog.common.Setting;
import com.ubiqlog.core.DataAcquisitor;
import com.ubiqlog.core.SensorCatalouge;
import com.ubiqlog.utils.IOManager;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

/**
 * This sensor will use time interval for logging
 * @author Reza Rawassizadeh
 * 
 */
public class ApplicationSensor extends Service implements SensorConnector {

	private static long APP_LOG_INTERVAL = 10000L;
	private static long APP_LOG_INTERVAL_2 = 300000L;
	
	private com.ubiqlog.core.DataAcquisitor dataAcq;

	private Handler objHandler = new Handler();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private Runnable doAppLogging = new Runnable() {
		public void run() {
			readSensor();
			objHandler.postDelayed(doAppLogging, APP_LOG_INTERVAL);
		}
	};

	@Override
	public void onCreate() {
		Log.d("Application-Logging", "--- onCreate");
		SensorCatalouge sencat = new SensorCatalouge(getApplicationContext());
		try {
			ArrayList<SensorObj> sens = sencat.getAllSensors();
			for (int i = 0; i < sens.size(); i++) {
				if (sens.get(i).getSensorName().equalsIgnoreCase("APPLICATION")) {
					String[] configs = sens.get(i).getConfigData();
					for (int j = 0; j < configs.length; j++) {
						String tmp[] = configs[j].split("=");
						//TODO: check this Record Interval in ms doesn't existed anymore
						if (tmp[0].trim().equalsIgnoreCase("Record Interval in ms")) {
							APP_LOG_INTERVAL = Long.parseLong(tmp[1]);
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e("Application Sensor","----------Error reading the log interval from sensor catalouge-----"+ e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		objHandler.removeCallbacks(doAppLogging);
		Log.d("Application-Logging", "--- onDestroy");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("Application-Logging", "--- onStart");
		readSensor();
		objHandler.postDelayed(doAppLogging, APP_LOG_INTERVAL);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("Application-Logging", "--- onStartCommand");
		readSensor();
		objHandler.postDelayed(doAppLogging, APP_LOG_INTERVAL);
	    return START_STICKY;
	}

	/* old one from Reza
	public void readSensor() {
		try {
			ActivityManager activeMan = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningAppProcessInfo> activityList = activeMan.getRunningAppProcesses();
			dataAcq = new DataAcquisitor();

			int numberOfTasks = 1;
			if ( !isExcluded(activeMan.getRunningTasks(numberOfTasks).get(0).topActivity.getPackageName().toString()) ) 
			{
				//String tmpDate = DateFormat.getDateTimeInstance().format(System.currentTimeMillis());
				SimpleDateFormat dateformat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
				String tmp = new String("{\"Application\": {\"ProcessName\":\""+ activeMan.getRunningTasks(numberOfTasks).get(0).topActivity.getPackageName().toString()+ 
						"\",\"Time\":\"" + dateformat.format(new Date()) + "\"}}");
				dateformat = null;
				dataAcq.dataBuff.add(tmp);
			}
//			for (int i = 0; i < activityList.size(); i++) {
//				if ( 	//.equalsIgnoreCase(activityList.get(i).processName) &&
//						(activityList.get(i).importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND ) &&  
//					    ( !isExcluded(activityList.get(i).processName) ) ) 
//				{
//					//String tmpDate = DateFormat.getDateTimeInstance().format(System.currentTimeMillis());
//					SimpleDateFormat dateformat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
//					String tmp = new String("{\"Application\": {\"ProcessName\":\""+ activityList.get(i).processName+ "\",\"Time\":\"" + dateformat.format(new Date()) + "\"}}");
//					dateformat = null;
//					dataAcq.dataBuff.add(tmp);
//				}
//			}
		} catch (Exception e) {
			IOManager errlogger = new IOManager();
			errlogger.logError("[ApplicationSensor] error:" + e.getMessage());
			e.printStackTrace();
		}
	}
	*/

	public void readSensor() {
		try {
			
			ActivityManager activeMan = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningAppProcessInfo> activityList = activeMan.getRunningAppProcesses();
			Date _currentDate = new Date();
			ArrayList<String> _foundApps = new ArrayList<String>();
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			for (int i = 0; i < activityList.size(); i++) {
				if ((activityList.get(i).importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) && 
						! containSysProc(activityList.get(i).processName) && pm.isScreenOn()) {
				if(!ApplicationSensorHelper.Instance()._apps.containsKey(activityList.get(i).processName)){
					ApplicationSensorHelper.Instance()._apps.put(activityList.get(i).processName, _currentDate);
				}
				_foundApps.add(activityList.get(i).processName);
				}
			}
			ApplicationSensorHelper.Instance().logApps(_foundApps, "Application",APP_LOG_INTERVAL_2,_currentDate);
			
		} catch (Exception e) {
			IOManager errlogger = new IOManager();
			errlogger.logError("[ApplicationSensor.readSensor] error:" + e.getMessage()+ " Stack:" + Log.getStackTraceString(e));
		}
	}

	private boolean containSysProc(String input) {
		for (int j = 0; j < Setting.Instance(this).getCoreProcs().length; j++) {
			if (input.equalsIgnoreCase(Setting.Instance(this).getCoreProcs()[j])) {
				return true;
			}
		}
		return false;
	}

	
//	private boolean isExcluded(String input) 
//	{
//		for (int j = 0; j < com.ubiqlog.common.Setting.coreProcs.length; j++) {
//			if (input.equalsIgnoreCase(com.ubiqlog.common.Setting.coreProcs[j]) 
//					|| Setting.vectorExcludedApplications.contains(input.toLowerCase()) ) 
//			{
//				return true;
//			}
//		}
//		return false;
//		//return (Setting.vectorExcludedApplications.contains(input.toLowerCase()) ;
//	}
	
	public String getSensorName() {
		return "APPLICATION";
	}
	
}
