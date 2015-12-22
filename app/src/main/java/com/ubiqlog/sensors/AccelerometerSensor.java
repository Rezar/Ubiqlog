package com.ubiqlog.sensors;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

import com.ubiqlog.common.Setting;
import com.ubiqlog.core.DataAcquisitor;
import com.ubiqlog.utils.JsonEncodeDecode;

import java.util.Date;

/**
 * Created by AP on 12/17/2015.
 */
public class AccelerometerSensor extends Service implements SensorEventListener {

    IntentFilter mIntentFilter;
    DataAcquisitor mDataBuffer;
    DataAcquisitor mSA_batteryBuffer;
    private final String TAG = this.getClass().getSimpleName();
    private int lastVal;
    private long lastVal_timestamp;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private long lastUpdate;


    @Override
    public void onCreate() {
        Log.d("Accelerometer-Logging", "--- onCreate");
        super.onCreate();
        /*
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        accelerometerReceiver = new AccelerometerReceiver();
        lastVal_timestamp = System.currentTimeMillis();
        */

        // Direct the DataAcquisitor to the correct folder location
        mDataBuffer = new DataAcquisitor(Setting.DEFAULT_FOLDER, Setting.dataFileName_Accelerometer);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Accelerometer-Logging", "--- onStartCommand");
        /*
        if (intent != null) {
            this.registerReceiver(accelerometerReceiver, mIntentFilter);
        }
        */

        lastUpdate = 0;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDataBuffer.flush(true);
        Log.d("Accelerometer-Logging", "--- onDestroy");
        // Unregister accelerometer sensor
        senSensorManager.unregisterListener(this);
        senSensorManager = null;
        senAccelerometer = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            //float z = event.values[2];

            long curTime = System.currentTimeMillis();
            long diffTime = (curTime - lastUpdate);
            if (diffTime > Setting.ACCELEROMETER_SAVE_DELAY) {
                lastUpdate = curTime;

                // Save x and y axis changes
                String encoded = JsonEncodeDecode.EncodeAccelerometer(x, y, new Date());
                //Log.d("Accelerometer-encoded", encoded);
                mDataBuffer.insert(encoded, true, Setting.bufferMaxSize);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
