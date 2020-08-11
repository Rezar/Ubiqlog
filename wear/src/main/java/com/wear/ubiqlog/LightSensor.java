package com.wear.ubiqlog;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Date;

/**
/**
 * Class will take 3 lux readings every SensorConstant.LIGHT_SENSOR_INTERVAL
 * and write to file
 */

public class LightSensor extends Service implements SensorEventListener {
    private static final String LOG_TAG = LightSensor.class.getSimpleName();
    private Sensor mLight;
    private SensorManager mSensorManager;
    int count; // store number of samples
    float totalSum; // store sum of 3 sampling to get avg value by davide to 3 after 3rd sample

    private final String MESSAGE1_PATH = "/message1";
    private final String MESSAGE2_PATH = "/message2";

    private GoogleApiClient apiClient;
    private EditText receivedMessagesEditText;
//    private NodeApi.NodeListener nodeListener;
    private MessageApi.MessageListener messageListener;
    private String remoteNodeId;

    public LightSensor() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        count = 0;
        totalSum = 0f;
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        Log.d(LOG_TAG, "Light sensor started");

        // Create NodeListener that enables buttons when a node is connected and disables buttons when a node is disconnected
//        nodeListener = new NodeApi.NodeListener() {
//            @Override
//            public void onPeerConnected(Node node) {
//
//            }
//
//            @Override
//            public void onPeerDisconnected(Node node) {
//
//            }
//        };

        // Create MessageListener that receives messages sent from a mobile
        messageListener = new MessageApi.MessageListener() {
            @Override
            public void onMessageReceived(MessageEvent messageEvent) {
                if (messageEvent.getPath().equals(MESSAGE1_PATH)) {

                } else if (messageEvent.getPath().equals(MESSAGE2_PATH)) {

                }
            }
        };

        // Create GoogleApiClient
        apiClient = new GoogleApiClient.Builder(getApplicationContext()).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                // Register Node and Message listeners
//                Wearable.NodeApi.addListener(apiClient, nodeListener);
                Wearable.MessageApi.addListener(apiClient, messageListener);
                // If there is a connected node, get it's id that is used when sending messages
                Wearable.NodeApi.getConnectedNodes(apiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        if (getConnectedNodesResult.getStatus().isSuccess() && getConnectedNodesResult.getNodes().size() > 0) {
                            remoteNodeId = getConnectedNodesResult.getNodes().get(0).getId();
                        }
                    }
                });
            }

            @Override
            public void onConnectionSuspended(int i) {
            }
        }).addApi(Wearable.API).build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //SensorDelayNormal is 200,000 ms
        mSensorManager.registerListener(LightSensor.this, mLight, SensorManager.SENSOR_DELAY_FASTEST);

        // Check is Google Play Services available
        int connectionResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (connectionResult != ConnectionResult.SUCCESS) {
        } else {
            apiClient.connect();
        }
        return START_NOT_STICKY; // If process died, it will <<NOT>> start again
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        new SensorEventLoggerTask().execute(event);
        mSensorManager.unregisterListener(this);
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

            if (count >= 3) {
                Date date = new Date();
                float avg = totalSum / count;
                Log.e("LIGHT",""+avg);
                //Encode the lux value and date
                //String encoded = JSONUtil.encodeLight(avg, date);
                //Log.d(LOG_TAG, encoded);

                //add encoded string to buffer
                //mDataBuffer.insert(encoded, true, Setting.bufferMaxSize); // 1 for BufferMaxSize causes to flush Buffer automatically after inserting value

                //String encoded_SA = SemanticTempCSVUtil.encodeLight(avg, date);
                //mSA_lightBuffer.insert(encoded_SA, true, Setting.bufferMaxSize); // 1 for BufferMaxSize causes to flush Buffer automatically after inserting value
                String s = ""+avg;
                Wearable.MessageApi.sendMessage(apiClient, remoteNodeId, MESSAGE2_PATH, s.getBytes()).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {

                    }
                });

                totalSum = 0;
                count = 0;

                // stop the service. The service will run after 15min by ServiceMonitor class(AlarmService)
                //stopSelf();
                try {
                    //sleep current thread for about 30sec to get a new sample
                    Thread.sleep(300000);
                    // register a listener to waiting for onSensorChanged() event
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    //sleep current thread for about 30sec to get a new sample
                    Thread.sleep(30000);

                    // register a listener to waiting for onSensorChanged() event
                    mSensorManager.registerListener(LightSensor.this, mLight, SensorManager.SENSOR_DELAY_FASTEST);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //ErrorCollector.Log("1501", "InterruptedException:" + count + "," + e.getMessage());
                }
            }
            return null;
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onDestroy() {
        //mDataBuffer.flush(true);
        //Unregister the listener
//        Wearable.NodeApi.removeListener(apiClient, nodeListener);
        Wearable.MessageApi.removeListener(apiClient, messageListener);
        apiClient.disconnect();
        mSensorManager.unregisterListener(this);
        //Log.d(LOG_TAG, "Light sensor stopped");
        super.onDestroy();


    }
}
