package com.ubiqlog.sensors;

import java.util.ArrayList;
import java.util.Date;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

//import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import com.ubiqlog.core.DataAcquisitor;
import com.ubiqlog.core.SensorCatalouge;
import com.ubiqlog.utils.JsonEncodeDecode;

/*check these examples: 
 * http://www.kpbird.com/2013/07/android-activityrecognition-example.html
 * http://opensignal.com/blog/2013/05/16/getting-started-with-activity-recognition-android-developer-guide/	
*/	
//The IntentService can be only triggered from the Main thread
public class ActivitySensor2 extends Service implements SensorConnector {

//	private ActivityRecognitionClient actRecClient;
	private ActivityRecognitionScan myascan;
	private Intent inIntent;
	private static long ACTIVITY_LOG_INTERVAL = 30000L;
	private static JsonEncodeDecode jsonencoder = new JsonEncodeDecode(); 


	@Override
	public void onCreate(){
//		super.onCreate();
		Log.d("Activity-Logging", "--- onCreate");
		SensorCatalouge sencat = new SensorCatalouge(getApplicationContext());
		try {
			ArrayList<SensorObj> sens = sencat.getAllSensors();
			for (int i = 0; i < sens.size(); i++) {
				if (sens.get(i).getSensorName().equalsIgnoreCase("ACTIVITY")) {
					String[] configs = sens.get(i).getConfigData();
					for (int j = 0; j < configs.length; j++) {
						String tmp[] = configs[j].split("=");
						if (tmp[0].trim().equalsIgnoreCase(" Scan interval")) {
							ACTIVITY_LOG_INTERVAL = Long.parseLong(tmp[1]);
						}
					}
				}
			}
			myascan = new ActivityRecognitionScan(getApplicationContext(),ACTIVITY_LOG_INTERVAL);
			myascan.startActivityRecognitionScan();
		} catch (Exception e) {
			Log.e("[Activity-Logging]","----------Error reading the log interval from sensor catalouge."+e.getLocalizedMessage());
			e.printStackTrace();
		}	
	}

	
	@Override
	public void readSensor() {	    
		
		Log.e("Activity-Logging", "ActivityRecognitionResult.hasResult: "+String.valueOf(ActivityRecognitionResult.hasResult(inIntent)));
		
		if (ActivityRecognitionResult.hasResult(inIntent)) {
	    	ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(inIntent);
	    	DetectedActivity activity = result.getMostProbableActivity();
			final int type = activity.getType();
			String strType = new String();
			switch(type){
			  case DetectedActivity.IN_VEHICLE:
				  strType = "invehicle";
				  break;
	          case DetectedActivity.ON_BICYCLE:
	        	  strType ="onbicycle";
	        	  break;
	          case DetectedActivity.ON_FOOT:
	              strType = "onfoot";
	              break;
	          case DetectedActivity.STILL:
	        	  strType = "still";
	        	  break;
	          case DetectedActivity.TILTING:
	        	  strType ="tilting";
	        	  break;
	          case DetectedActivity.UNKNOWN:
	        	  strType ="unknown";
	        	  break;
			}
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			Editor edt = prefs.edit();
			String previousActv = prefs.getString("PREVIOUS_ACTIVIY","");
			long previousDate = prefs.getLong("PREVIOUS_DATE", 0);
			if (previousActv.length()==0){ // nothing was in the string and it is the first time just initialize
				previousActv = strType;
				previousDate = new Date().getTime();
				Log.e("-----FIRST TIME: type:", previousActv+" date:"+String.valueOf(previousDate));
				edt.putString("PREVIOUS_ACTIVIY", strType);
				edt.putLong("PREVIOUS_DATE", previousDate);
				edt.commit();
			}else {
				if (!strType.equalsIgnoreCase(previousActv)){
					Date readablePrevDate = new Date(previousDate);
					Date nowDate = new Date();
					String jsonstr = jsonencoder.EncodeActivity("Activity", readablePrevDate, nowDate, strType, activity.getConfidence());
					Log.e("[Activity-Logging] ----->",jsonstr);
					edt.putString("PREVIOUS_ACTIVIY", strType);
					edt.putLong("PREVIOUS_DATE", nowDate.getTime());
					edt.commit();
					DataAcquisitor.dataBuff.add(jsonstr);
				}
			}
	    }
	}
	

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("Activity-Logging", "--- onStartCommand");
		readSensor();
		return START_STICKY;
	}
	
	@Override
	public void onDestroy(){
		Log.d("Activity-Logging", "--- onDestroy");
		myascan.stopActivityRecognitionScan();
		myascan=null;
		//super.onDestroy();
	}


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
