package com.ubiqlog.sensors;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.ubiqlog.common.Setting;
import com.ubiqlog.core.DataAcquisitor;
import com.ubiqlog.utils.JsonEncodeDecode;

import java.util.Date;

/**
 * Class will take 3 lux readings every SensorConstant.LIGHT_SENSOR_INTERVAL
 * and write to file
 */
public class AmbientLightSensor extends Service  implements SensorEventListener {

    private static final String LOG_TAG = AmbientLightSensor.class.getSimpleName();
    private Sensor mLight;
    private SensorManager mSensorManager;
    int count; // store number of samples
    float totalSum; // store sum of 3 sampling to get avg value by davide to 3 after 3rd sample
    private DataAcquisitor mDataBuffer;
    private long lastVal_timestamp;
    private boolean isSamping;

    public AmbientLightSensor() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Log.d(LOG_TAG, "Light sensor started");
        Log.d("AmbientLight-Logging", "--- onCreate");
        count = 0;
        totalSum = 0f;
        mDataBuffer = new DataAcquisitor(Setting.DEFAULT_FOLDER, Setting.dataFileName_AmbientLightSensor);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lastVal_timestamp = 0;
        isSamping = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("AmbientLight-Logging", "--- onStartCommand");
        //SensorDelayNormal is 200,000 ms
        mSensorManager.registerListener(AmbientLightSensor.this, mLight, SensorManager.SENSOR_DELAY_FASTEST);

        return START_STICKY; // If process died, it will <<NOT>> start again
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("AmbientLight-Logging", "--- onSensorChanged");
        Sensor mySensor = event.sensor;
        if (mySensor.getType() == Sensor.TYPE_LIGHT) {
            long curTime = System.currentTimeMillis();
            long diffTime = (curTime - lastVal_timestamp);
            if (diffTime >= Setting.LIGHT_SENSOR_SAMPLE_INTERVAL || isSamping) {
                if (!isSamping) {
                    lastVal_timestamp = System.currentTimeMillis();
                }
                isSamping = true;
                Log.d("AmbientLight-Logging", "--- onSensorChanged in if ");
                new SensorEventLoggerTask().execute(event);
            }
        }
    }

    private class SensorEventLoggerTask extends
            AsyncTask<SensorEvent, Void, Void> {
        @Override
        protected Void doInBackground(SensorEvent... events) {
            SensorEvent event = events[0];
            float lux = event.values[0];
            totalSum += lux;
            count++;
            //Log.d(LOG_TAG, "Sample count:" + count + ", lux:" + lux + ", total:" + totalSum);

            if (count >= Setting.LIGHT_SAMPLE_AMNT) {
                Date date = new Date();
                float avg = totalSum / count;

                //Encode the lux value and date
                SleepSensor.setAmbientData(avg);
                String encoded = JsonEncodeDecode.EncodeAmbientLight(avg, date);
                //Log.d(getClass().getSimpleName(), encoded);
                //add encoded string to buffer
                //mDataBuffer.insert(encoded, true, Setting.bufferMaxSize); // 1 for BufferMaxSize causes to flush Buffer automatically after inserting value
                Log.e("AmbientLight",encoded);
                DataAcquisitor.dataBuff.add(encoded);

                totalSum = 0;
                count = 0;
                isSamping = false;
            }
            return null;
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onDestroy() {
        Log.d("AmbientLight-Logging", "--- onDestroy");
        mDataBuffer.flush(true);
        //Unregister the listener
        mSensorManager.unregisterListener(this);
        //Log.d(LOG_TAG, "Light sensor stopped");
        super.onDestroy();

        Intent in = new Intent();
        in.setAction("LightSensor.StartkilledService");
        sendBroadcast(in);
    }
}

