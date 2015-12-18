package com.ubiqlog.sensors;

import java.util.ArrayList;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.ubiqlog.core.SensorCatalouge;

/*check these examples: 
 * http://www.kpbird.com/2013/07/android-activityrecognition-example.html
 * http://opensignal.com/blog/2013/05/16/getting-started-with-activity-recognition-android-developer-guide/	
*/	
// IntentService is similar to Service but when the service returns result late. 
//The IntentService can be only triggered from the Main thread
public class ActivitySensor extends Service implements SensorConnector {


	private ActivityRecognitionScan myascan;
	private static long ACTIVITY_LOG_INTERVAL = 30000L;
	

	
//	@Override
	public void onCreate(){
		super.onCreate();		
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


	
	public void onDestroy(){
		super.onDestroy();
		Log.d("Activity-Logging", "--- onDestroy");
		myascan.stopActivityRecognitionScan();
		myascan=null;
		
	}

@Override
public IBinder onBind(Intent arg0) {
	// TODO Auto-generated method stub
	return null;
}



@Override
public void readSensor() {
	// empty
	// the reading part is done by the google play services
	// the result is logged by the ActivityResultHandler
	
}
}
