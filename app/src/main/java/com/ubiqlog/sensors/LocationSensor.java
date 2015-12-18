package com.ubiqlog.sensors;

import java.util.Date;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;

import com.ubiqlog.core.DataAcquisitor;
import com.ubiqlog.utils.IOManager;
import com.ubiqlog.utils.JsonEncodeDecode;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * This sensor will use specified time interval for logging. It checks if GPS is
 * available it will use the GPS for reading current location of the user,
 * otherwise it will calculate location via Cell IDs.
 * 
 * @author Reza Rawassizadeh
 * @modified by Victor Gugonatu
 * 
 */
public class LocationSensor extends Service implements SensorConnector {

	private LocationManager locationManager;
	private Location curLocation;
	private int enabledProviders = 0;
	private Handler objHandler = new Handler();
	private boolean isNew = false;
	
	private LocationListener loclistener = new LocationListener() {
		public void onLocationChanged(Location location) {
			
			//Log.d("Location-Logging", "Location received: Provider: " + location.getProvider() + "; Accuracy: " + location.getAccuracy() + "; Time: " + location.getTime());
			
			//if there is only one active provider then write the location
			//else compare the locations
			if(enabledProviders<2)
			{
				curLocation = location;
				isNew = true;
				readSensor();
			}
			else
			{
			if(curLocation==null){
					curLocation = location;
					isNew = true;
			}
			else{
			synchronized (curLocation) {
				if(curLocation.getProvider().equals(location.getProvider())){
					curLocation = location;
					isNew = true;
					//Log.d("Location-Logging", "Location changed due to same provider!");
				}
				else{
					//check if more accurate
					if(location.getAccuracy()<curLocation.getAccuracy() || (curLocation.getTime()-location.getTime())>9000){
						curLocation = location;
						isNew = true;
					//	Log.d("Location-Logging", "Location changed due to accuracy or time!");
					}			
				}
			}
			}
		//	Log.d("Location-Logging", "Current locationd: Provider: " + curLocation.getProvider() + "; Accuracy: " + curLocation.getAccuracy() + "; Time: " + curLocation.getTime());
			
			}
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.d("Location-Logging", "--- onCreate");
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);		
		List<String> providers = locationManager.getProviders(true);
		for (String prov : providers) {
			if(prov.equals(LocationManager.GPS_PROVIDER) || prov.equals(LocationManager.NETWORK_PROVIDER)){
				enabledProviders ++;
				Log.d("Location-Logging", "Request location updates for: " + prov);
				locationManager.requestLocationUpdates(prov,10000, 1, loclistener);
			}
		}
		if(enabledProviders>1){
			//set up a schedule in order to save the location
			objHandler.postDelayed(doLocationLogging, 10000);
		}
	}
	
	private Runnable doLocationLogging = new Runnable() {
		public void run() {
		//	Log.d("Location-Logging", "Write location...");
			readSensor();
			objHandler.postDelayed(doLocationLogging, 10000);
		}
	};
	

	@Override
	public void onDestroy() {
		objHandler.removeCallbacks(doLocationLogging);
		Log.d("Location-Logging", "--- onDestroy");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("Location-Logging", "--- onStart");
		readSensor();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("Location-Logging", "--- onStartCommand");
		readSensor();
		return START_STICKY;
	}
	public void readSensor() {
		try {
			if (curLocation != null && isNew) {
				isNew = false;
				String jsonString = JsonEncodeDecode.EncodeLocation("Location",curLocation.getLatitude(),curLocation.getLongitude(),
						curLocation.getAltitude(),new Date(),curLocation.getAccuracy(),curLocation.getProvider());
				DataAcquisitor.dataBuff.add(jsonString);
			} 
			
		} catch (Exception e) {
			IOManager errlogger = new IOManager();
			errlogger.logError("[LocationSensor] error:" + e.getMessage()+ " Stack:" + Log.getStackTraceString(e));
		}
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

}
