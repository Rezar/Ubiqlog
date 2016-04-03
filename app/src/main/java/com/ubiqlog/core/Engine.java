package com.ubiqlog.core;

import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.ubiqlog.common.Setting;
import com.ubiqlog.sensors.AccelerometerSensor;
import com.ubiqlog.sensors.ActivitySensor;
import com.ubiqlog.sensors.AmbientLightSensor;
import com.ubiqlog.sensors.ApplicationSensor;
import com.ubiqlog.sensors.AudioSensor;
import com.ubiqlog.sensors.BatterySensor;
import com.ubiqlog.sensors.BluetoothSensor;
import com.ubiqlog.sensors.CallSensor;
import com.ubiqlog.sensors.InteractionSensor;
import com.ubiqlog.sensors.LocationGSSensor;
import com.ubiqlog.sensors.LocationSensor;
import com.ubiqlog.sensors.PictureSensor;
import com.ubiqlog.sensors.RawAudioSensor;
import com.ubiqlog.sensors.SMSSensor;
import com.ubiqlog.sensors.SensorObj;
import com.ubiqlog.sensors.SleepSensor;
import com.ubiqlog.sensors.WiFiSensor;
import com.ubiqlog.ui.UbiqlogStatusBar;
import com.ubiqlog.utils.FeatureCheck;
import com.ubiqlog.utils.IOManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

//import com.ubiqlog.sensors.BluetoothSensor_NOTIMER;

public class Engine extends Service {
	
//	private Context ctx = getApplicationContext();
	private Context ctx = getBaseContext();
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate(){
		try {
			IntentFilter filter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
			BroadcastReceiver asubiqlog = new AutoStartUbiqlog() ;
			registerReceiver(asubiqlog,filter);
			
			Log.e("[Engine]","---onCreate 4m UBIQLOG");
			startRecording(getApplicationContext(), getApplication());
			
			if (!(Setting.SILENT_MODE)) {
				Intent notiI = new Intent();
				notiI.setClass(getApplicationContext(), UbiqlogStatusBar.class);
				startService(notiI);
			}

			String msgToast = "Start the Logging Process.";
			Toast toast = Toast.makeText(getApplicationContext(), msgToast, Toast.LENGTH_SHORT);
			toast.show();

		} catch (Exception e) {
			IOManager ioerror = new IOManager();
			ioerror.logError(e.getLocalizedMessage());
			Log.e("[Engine]", "-----------ERROR:" + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	/**
	 * Run UbiqLog after boot
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		Log.e("[Engine]","----onStartCommand 4m UBIQLOG");
		try {
			startRecording(getApplicationContext(), getApplication());

			String msgToast = "Start the Logging Process.";
			Toast toast = Toast.makeText(getApplicationContext(), msgToast, Toast.LENGTH_SHORT);
			toast.show();

		} catch (Exception e) {
			IOManager ioerror = new IOManager();
			ioerror.logError(e.getLocalizedMessage());
			Log.e("[Engine]", "-----------ERROR:" + e.getLocalizedMessage());
			e.printStackTrace();
		}
		return START_STICKY;
	}
	
	public static void startRecording(Context ctx, Application app) throws Exception {

		File audioDir = new File(com.ubiqlog.common.Setting.Instance(ctx).getLogFolder());
		if ((audioDir.mkdir())) {
			Log.d("[core.Engine.startRecording]", "-------AUDIO DIRECTORY ALREADY CREATED--------");
		} else {
			Log.d("[core.Engine.startRecording]","-------AUDIO DIRECTORY IS NOT CREATED PROBABLY IT ALREADY EXISTS--------");
		}

		SensorCatalouge senCat = new SensorCatalouge(ctx);
		ArrayList<SensorObj> allsens = senCat.getAllSensors();

		// class names of enabled sensors and annotation classnames
		HashSet<SensorObj> sensors = new HashSet<SensorObj>(); 
		
		for (int i = 0; i < allsens.size(); i++) {
			String sensorName = allsens.get(i).getSensorName();
			String[] configdata = allsens.get(i).getConfigData();
			String[] isenable = configdata[0].split("=");
			isenable[1] = isenable[1].trim();
			
			if (isenable[1].equalsIgnoreCase("yes")) {
//				Log.d("[core.Engine.startRecording]","------------------Sensor:" + sensorName+ " is enable");
				sensors.add(allsens.get(i));
			}
			String annotation = allsens.get(i).getAnnotationCalss();
			if (sensorName.equals("SMS") && annotation != null) {
				com.ubiqlog.common.Setting.Instance(ctx).SMS_ANNOTATION = true;
			}
			if (sensorName.equals("CALL") && annotation != null) {
				com.ubiqlog.common.Setting.Instance(ctx).CALL_ANNOTATION = true;
			}
		}
		
		Intent i8 = new Intent();
		i8.setClass(app, DataAggregator.class);
		ctx.startService(i8);

		Iterator<SensorObj> itr = sensors.iterator();
		while (itr.hasNext()) {
			SensorObj cs = itr.next();
			//Log.e("Check",""+cs.getSensorName());
			try {
				if (cs.getSensorName().equalsIgnoreCase("ACTIVITY")){
					int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(ctx);
					if(resp == ConnectionResult.SUCCESS){
						Intent i = new Intent();
						i.setClassName(app, cs.getClassName()) ;
						ctx.startService(i);
					}else{
						Log.e("[Engine]", "Google Play Service hasn't installed");
					}
				} else if (cs.getSensorName().contains("LOCATION")){
					int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(ctx);
					if(resp == ConnectionResult.SUCCESS){
						Log.e("[Engine]", "Google Play Service is installed and available.");
						Intent i = new Intent();
						i.setClassName(app,  com.ubiqlog.sensors.LocationGSSensor.class.getName()) ;
						ctx.startService(i);
					}else{
						Log.e("[Engine]", "Google Play Service hasn't installed.");
						Intent i = new Intent();
						i.setClassName(app,  com.ubiqlog.sensors.LocationSensor.class.getName()) ;
						ctx.startService(i);
					}
				} else if (cs.getSensorName().contains("AMBIENT") && FeatureCheck.hasLightFeature(ctx)) { // other sensors and not Google API based
					Intent i = new Intent();
					i.setClassName(app, cs.getClassName());
					ctx.startService(i);
				} else { // other sensors and not Google API based
					Intent i = new Intent();
					i.setClassName(app, cs.getClassName()) ;
					ctx.startService(i);
				}





			} catch (Exception e) {
				IOManager ioerror = new IOManager();
				ioerror.logError(e.getLocalizedMessage());
				Log.e("[core.Engine.startRecording]", "----Error starting " + cs.getSensorName()+ "----");
				e.printStackTrace();
			}
		}
		senCat.closeDB(ctx);
	}

	public static void stopRecording(Context ctx, Application app) {

		Intent i = new Intent();
		i.setClass(app, CallSensor.class);
		ctx.stopService(i);

		Intent i2 = new Intent();
		i2.setClass(app, SMSSensor.class);
		ctx.stopService(i2);

		Intent i3 = new Intent();
		i3.setClass(app, ApplicationSensor.class);
		ctx.stopService(i3);

		Intent i4 = new Intent();
		i4.setClass(app, LocationSensor.class);
		ctx.stopService(i4);
		
		Intent i5 = new Intent();
		i5.setClass(app, LocationGSSensor.class);
		ctx.stopService(i5);

		Intent i6 = new Intent();
		i6.setClass(app, BluetoothSensor.class);
		ctx.stopService(i6);
		
		Intent i7 = new Intent();
		i7.setClass(app, WiFiSensor.class);
		ctx.stopService(i7);
		
		Intent i8 = new Intent();
//		i8.setClass(app, HardwareSensor_OLD.class);
		i8.setClass(app, ActivitySensor.class);
		ctx.stopService(i8);
		
		Intent i9 = new Intent();
		i9.setClass(app, PictureSensor.class);
		ctx.stopService(i9);
		
		Intent i10 = new Intent();
		i10.setClass(app, AudioSensor.class);
		ctx.stopService(i10);

		Intent i11= new Intent();
		i11.setClass(app, DataAggregator.class);
		ctx.stopService(i11);

		Intent i12 = new Intent();
		i12.setClass(app, BatterySensor.class);
		ctx.stopService(i12);

		Intent i13 = new Intent();
		i13.setClass(app, InteractionSensor.class);
		ctx.stopService(i13);

		Intent i14 = new Intent();
		i14.setClass(app, AccelerometerSensor.class);
		ctx.stopService(i14);

		Intent i15 = new Intent();
		i15.setClass(app, RawAudioSensor.class);
		ctx.stopService(i15);

		if (FeatureCheck.hasLightFeature(ctx)) {
			Intent i16 = new Intent();
			i16.setClass(app, AmbientLightSensor.class);
			ctx.stopService(i16);
		}
	}

}