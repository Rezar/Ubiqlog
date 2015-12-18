package com.ubiqlog.sensors;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.ubiqlog.core.SensorCatalouge;
import com.ubiqlog.vis.utils.JsonEncodeDecode;
import com.ubiqlog.vis.utils.SensorState;

/**
 * This class should be changed in a way not to turn on Bluetooth. 
 * @author Reza 
 *
 */
public class BluetoothSensor_NOTIMER extends Service implements SensorConnector {
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private BroadcastReceiver broadcastReceiver;
	private Handler objHandler = new Handler();
	private com.ubiqlog.core.DataAcquisitor dataAcq;
	private static long BT_LOG_INTERVAL = 10000; // 60000= default values is 10 min
//	private Timer bluetoothTimer;
	private Calendar currentDateTime = Calendar.getInstance();
	private IntentFilter filter;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	@Override
	public void onCreate() {
		Log.d("Bluetooth-Logging", "--- onCreate");
		SensorCatalouge sencat = new SensorCatalouge(getApplicationContext());
		try {
			SensorObj sensorObject = sencat.getSensorByName("BLUETOOTH");
			String[] configs = sensorObject.getConfigData();
			for (int index = 0; index < configs.length; index++) {
				String tmp[] = configs[index].split("=");
				if (tmp[0].trim().equalsIgnoreCase("Scan interval")) {
					BT_LOG_INTERVAL = parseScanInterval(tmp[1]);
//					Log.e ("BT --> BT_LOG_INTERVAL:",String.valueOf(BT_LOG_INTERVAL));
				}
			}
			bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if (bluetoothAdapter == null) {
				Log.e("[BluetoothSensor.onCreate]", "----- Bluetooth is not supported");
			}
			//------------------ initialize the broadcast receiver
			broadcastReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					// Log.e("Bluetooth-Logging",
					// "-----READ SENSOR------------------" +
					// intent.getAction());
					String action = intent.getAction();

					if (BluetoothDevice.ACTION_FOUND.equals(action)) {
						BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
						SensorState.Bluetooth stateBluetoothDevice = null;
						Log.e("......xxxx.....", device.getName());
						switch (device.getBondState()) {
						case BluetoothDevice.BOND_NONE:
							stateBluetoothDevice = SensorState.Bluetooth.NONE;
							break;
						case BluetoothDevice.BOND_BONDING:
							stateBluetoothDevice = SensorState.Bluetooth.BONDING;
							break;
						case BluetoothDevice.BOND_BONDED:
							stateBluetoothDevice = SensorState.Bluetooth.BONDED;
							break;
						}
						String deviceName = device.getName();
						if (deviceName == null) {
							deviceName = "NO_DEVICENAME";
						}
						
						String jsonString = JsonEncodeDecode.EncodeBluetooth(deviceName,device.getAddress(),stateBluetoothDevice.getState(),currentDateTime.getTime());
						Log.e("--------DETECTED BT-----",jsonString);
//						dataAcq.dataBuff.add(jsonString);
						jsonString = null;
					} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

					}
				}
			};
			filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
			filter.addAction(BluetoothDevice.ACTION_FOUND);
			registerReceiver(broadcastReceiver, filter);

			//------------------------------------------------------
		} 
		catch (Exception e){
			Log.e("[BluetoothSensor.onCreate]", "----------Error reading the log interval from sensor catalouge-----" 
					+ e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	private Runnable doBTLogging = new Runnable() {
		public void run() {
			readSensor();
			objHandler.postDelayed(doBTLogging, BT_LOG_INTERVAL);
		}
	};


	@Override
	public void readSensor() {
		try {
			if (bluetoothAdapter.isEnabled()) {
				bluetoothAdapter.startDiscovery();
				Log.e("--------BT-----readSensor----",bluetoothAdapter.getName()+" starts discovery");
				currentDateTime = Calendar.getInstance();
				currentDateTime.setTimeInMillis(System.currentTimeMillis());
				if (currentDateTime.get(Calendar.SECOND) > 30) {
					currentDateTime.add(Calendar.MINUTE, 1);
				}
				currentDateTime.set(Calendar.SECOND, 0);
				currentDateTime.set(Calendar.MILLISECOND, 0);
				
				
				bluetoothAdapter.cancelDiscovery();
			} else {
				Log.e("Bluetooth-Logging", "----------BT is disabled");
			}
			

		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e("Bluetooth-Logging",
					"----------Error inside run method of TimerTask");
		}
	};
	
	/*
	private Timer timer = new Timer();
	private class BTtimerTask extends TimerTask {		
		@Override
		public void run() {
			try {
				if (bluetoothAdapter.isEnabled()) {
					bluetoothAdapter.startDiscovery();
						
					currentDateTime = Calendar.getInstance();
					currentDateTime.setTimeInMillis(System.currentTimeMillis());
					if (currentDateTime.get(Calendar.SECOND) > 30) {
						currentDateTime.add(Calendar.MINUTE, 1);
					}
					currentDateTime.set(Calendar.SECOND, 0);
					currentDateTime.set(Calendar.MILLISECOND, 0);
	
					broadcastReceiver = new BroadcastReceiver() {
						@Override
						public void onReceive(Context context, Intent intent) {
	//						Log.e("Bluetooth-Logging", "-----READ SENSOR------------------" + intent.getAction());
							String action = intent.getAction();
								
							if (BluetoothDevice.ACTION_FOUND.equals(action)) {
								BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
								SensorState.Bluetooth stateBluetoothDevice = null;
//								Log.e("......xxxx.....",device.getName());
								switch (device.getBondState()) {
									case BluetoothDevice.BOND_NONE:
										stateBluetoothDevice = SensorState.Bluetooth.NONE;
										break;
									case BluetoothDevice.BOND_BONDING:
										stateBluetoothDevice = SensorState.Bluetooth.BONDING;
										break;
									case BluetoothDevice.BOND_BONDED:
										stateBluetoothDevice = SensorState.Bluetooth.BONDED;
										break;
								}
								String deviceName = device.getName();
								if (deviceName == null) {
									deviceName = "NO_DEVICENAME";
								}
								String jsonString = JsonEncodeDecode.EncodeBluetooth(deviceName, device.getAddress(), stateBluetoothDevice.getState(), currentDateTime.getTime());
								dataAcq.dataBuff.add(jsonString);
								jsonString = null;
							} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
									
							}
						}
					};
					IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
					filter.addAction(BluetoothDevice.ACTION_FOUND);
					
					registerReceiver(broadcastReceiver, filter);
				}else {
					Log.e("Bluetooth-Logging","----------BT is disabled");
				}
			}catch (Exception ex){
				ex.printStackTrace();
				Log.e("Bluetooth-Logging","----------Error inside run method of TimerTask");
			}	
			//timer.schedule(new BTtimerTask(), BT_LOG_INTERVAL);
		}
	};
	*/
	
	@Override 
	public void onStart(Intent intent, int startId) {
		Log.d("[Bluetooth-Logging]", "--- onStart");
//		readSensor();
		objHandler.postDelayed(doBTLogging, BT_LOG_INTERVAL);
//		timer.schedule(new BTtimerTask(), BT_LOG_INTERVAL, BT_LOG_INTERVAL); 
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("Bluetooth-Logging", "--- onStartCommand");
//		readSensor();
		objHandler.postDelayed(doBTLogging, BT_LOG_INTERVAL);
//		bluetoothTimer = new Timer();
//		bluetoothTimer.scheduleAtFixedRate(bluetoothTimerTask, BT_LOG_INTERVAL, BT_LOG_INTERVAL); 
//		timer.schedule(new BTtimerTask(), BT_LOG_INTERVAL, BT_LOG_INTERVAL); 
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.d("Bluetooth-Logging", "--- onDestroy");
		objHandler.removeCallbacks(doBTLogging);

		if (bluetoothAdapter != null) {
			bluetoothAdapter.cancelDiscovery();
		}
		
		try {
			this.unregisterReceiver(broadcastReceiver);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 
	 * check if it is second or minutes  
	 * @param strScanInterval
	 * @return
	 */
	private Long parseScanInterval(String strScanInterval) {
		String lowerCase = strScanInterval.toLowerCase();
		String toParse = "";
		int multiplier = 0;
		if (lowerCase.endsWith("m")) {
			toParse = lowerCase.split("m")[0];
			multiplier = 60000;
		} else if (lowerCase.endsWith("s")) {
			toParse = lowerCase.split("s")[0];
			multiplier = 1000;
		} else {
			toParse = lowerCase;
			multiplier = 1000;
		}
//		Log.e("--------toParse: ", toParse);
//		return (multiplier * Long.parseLong(toParse));
		return Long.parseLong(toParse);
	}

	public String getSensorName() {
		return "BLUETOOTH";
	}
	
}
