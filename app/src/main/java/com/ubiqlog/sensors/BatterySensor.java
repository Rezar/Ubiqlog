package com.ubiqlog.sensors;

/**
 * Created by Aaron on 12/16/2015.
 */
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

import com.ubiqlog.common.Setting;
import com.ubiqlog.core.DataAcquisitor;
import com.ubiqlog.utils.JsonEncodeDecode;

import java.util.Date;


public class BatterySensor extends Service {
    IntentFilter mIntentFilter;
    BatteryReceiver batteryReceiver;
    DataAcquisitor mDataBuffer;
    DataAcquisitor mSA_batteryBuffer;
    private final String TAG = this.getClass().getSimpleName();
    private int lastVal;
    private long lastVal_timestamp;
    private boolean isRegistered = false;

    @Override
    public void onCreate() {
        Log.d("Battery-Logging", "--- onCreate");
        super.onCreate();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        batteryReceiver = new BatteryReceiver();
        mDataBuffer = new DataAcquisitor(Setting.LOG_FOLDER, Setting.dataFileName_Battery);
        //mSA_batteryBuffer = new DataAcquisitor("SA/BatterySensor");
        lastVal_timestamp = System.currentTimeMillis();
        isRegistered = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Battery-Logging", "--- onStartCommand");
        if (intent != null) {
            this.registerReceiver(batteryReceiver, mIntentFilter);
            isRegistered = true;

        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDataBuffer.flush(true);
        //mSA_batteryBuffer.flush(true);
        if (batteryReceiver != null && isRegistered) {
            unregisterReceiver(batteryReceiver);
            isRegistered = false;
        } else {
            batteryReceiver = null;
            isRegistered = false;
        }

        Intent in = new Intent();
        in.setAction("BatterySensor.StartkilledService");
        sendBroadcast(in);
    }

    /* This class receives updates from the System
       If the level is a multiple of 5, write to file
     */
    private class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent batteryStatus) {
            long timeGap = System.currentTimeMillis() - lastVal_timestamp;
            if (batteryStatus.getAction().equalsIgnoreCase(Intent.ACTION_BATTERY_CHANGED)) {
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                if (level == lastVal && timeGap < Setting.BATTERY_MIN_SAMPLE_INTERVAL) { // If timestamp between last sampling was very short, return and don't register Level
                    return;
                }
                lastVal = level;
                lastVal_timestamp = System.currentTimeMillis();

                boolean isCharging = (status == BatteryManager.BATTERY_STATUS_CHARGING) ||
                        (status == BatteryManager.BATTERY_STATUS_FULL);

                SleepSensor.setisCharge(isCharging);

                if (level % 5 == 0) {
                    //store in buff
                    //Log.d(TAG, "Level:" + level + ", Charging:" + isCharging);
                    String encoded = JsonEncodeDecode.EncodeBattery(level, isCharging, new Date());
                    //mDataBuffer.insert(encoded, true, Setting.bufferMaxSize);
                    DataAcquisitor.dataBuff.add(encoded);
                    //mDataBuffer.flush(true);

                    //String encoded_SA = SemanticTempCSVUtil.encodeBattery(level, isCharging, new Date());
                    //mSA_batteryBuffer.insert(encoded_SA, true, Setting.bufferMaxSize);
                    //mSA_batteryBuffer.flush(true);

                }

                //String encoded = JsonEncodeDecode.EncodeBattery(level, isCharging, new Date());
                //mDataBuffer.insert(encoded, true, Setting.bufferMaxSize);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
