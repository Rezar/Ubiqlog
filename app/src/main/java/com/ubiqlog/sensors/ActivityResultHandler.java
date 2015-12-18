package com.ubiqlog.sensors;

import java.util.Date;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.ubiqlog.core.DataAcquisitor;
import com.ubiqlog.utils.JsonEncodeDecode;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;


public class ActivityResultHandler extends IntentService {


	public ActivityResultHandler() {
		super("ActivitySensor");
	}	


	protected void onHandleIntent(Intent inIntent) {
		Log.d("Activity-Logging", "--- onHandleIntent"+ "---"+inIntent.getAction());
		
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (ActivitySensor.class.getName().equals(service.service.getClassName())) 
			{ 
				//Activity service is running
				//log result

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
							String jsonstr = JsonEncodeDecode.EncodeActivity("Activity", readablePrevDate, nowDate, strType, activity.getConfidence());
							DataAcquisitor.dataBuff.add(jsonstr);
							Log.e("[Activity-Logging] ----->",jsonstr);
							edt.putString("PREVIOUS_ACTIVIY", strType);
							edt.putLong("PREVIOUS_DATE", nowDate.getTime());
							edt.commit();
						}
					}
			    }
			}
		}

//		intent.putExtra("LOG_INTERVAL",ACTIVITY_LOG_INTERVAL );
//		intent.putExtra("STOP",false);
//		inIntent = intent;
//		readSensor();
	}	

}
