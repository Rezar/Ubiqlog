package com.ubiqlog.sensors;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ubiqlog.core.DataAcquisitor;
import com.ubiqlog.core.SensorCatalouge;
import com.ubiqlog.utils.IOManager;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class WiFiSensor extends Service implements SensorConnector {

	private static long WIFI_LOG_INTERVAL = 60000L;
	private WifiManager wifimanager;

	private com.ubiqlog.core.DataAcquisitor dataAcq;

	private Handler objHandler = new Handler();

	private Runnable doWifiLogging = new Runnable() {
		public void run() {
			readSensor();
			objHandler.postDelayed(doWifiLogging, WIFI_LOG_INTERVAL);
		}
	};
	public void onCreate() {
		Log.d("WIFI-Logging", "--- onCreate");
		SensorCatalouge sencat = new SensorCatalouge(getApplicationContext());
		try {
			ArrayList<SensorObj> sens = sencat.getAllSensors();
			for (int i = 0; i < sens.size(); i++) {
				if (sens.get(i).getSensorName().equalsIgnoreCase("WIFI")) {
					String[] configs = sens.get(i).getConfigData();
					for (int j = 0; j < configs.length; j++) {
						String tmp[] = configs[j].split("=");
						if (tmp[0].trim().equalsIgnoreCase("Record Interval in ms")) {
							WIFI_LOG_INTERVAL = Long.parseLong(tmp[1]);
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e("WIFI Sensor","----------Error reading the log interval from sensor catalouge-----"+ e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	
	public void onDestroy() {
		objHandler.removeCallbacks(doWifiLogging);
		Log.d("WiFi-Logging", "--- onDestroy");
	}

	public void onStart(Intent intent, int startId) {
		Log.d("Wifi-Logging", "--- onStart");
		readSensor();
		objHandler.postDelayed(doWifiLogging, WIFI_LOG_INTERVAL);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("Wifi-Logging", "--- onStartCommand");
		readSensor();
		objHandler.postDelayed(doWifiLogging, WIFI_LOG_INTERVAL);
		return START_STICKY;
	}



	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void readSensor() {
		try {
			wifimanager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//			if (wifimanager.isWifiEnabled() == false) {
//	            wifimanager.setWifiEnabled(true);
//	        } 
			if (wifimanager.isWifiEnabled() ) {
				List<ScanResult> resuls = wifimanager.getScanResults();
				
				StringBuilder jsonString ;
				Date currentDate = new Date();

				for (ScanResult res : resuls) {
//					Log.e("----WIFI Sensor run ---", res.toString());
					jsonString = new StringBuilder("");
					jsonString = jsonString.append("{\"WiFi\":{\"SSID\":\""
							+ res.SSID+ "\",\"BSSID\":\""+ res.BSSID+ 
							"\",\"capabilities\":\""+ res.capabilities+
							"\",\"level\":\""+res.level+
							"\",\"frequency\":\""+res.frequency+
							"\",\"time\":\""+ DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(currentDate)+ "\"}}");
//					Log.e("WIFI Sensor",jsonString.toString());
					DataAcquisitor.dataBuff.add(jsonString.toString());
					jsonString = null;
				} 
			}
		} catch (Exception e) {
			Log.e("[WiFiSensor] error:", e.getMessage()+ " Stack:" + Log.getStackTraceString(e));
		}
	}



}
