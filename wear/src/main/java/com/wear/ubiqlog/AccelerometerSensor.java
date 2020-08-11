package com.wear.ubiqlog;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

import java.util.ArrayList;

/**

 */
public class AccelerometerSensor extends Service implements SensorEventListener {


    //DataAcquisitor mDataBuffer;
    //DataAcquisitor mSA_AcclerometerBuffer;
    private final static float ACC_FIX=0.5f; // the fluctucations which presents moving
    private final String TAG = this.getClass().getSimpleName();
    private int lastVal;
    private long lastVal_timestamp;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private long lastUpdate;

    private ArrayList<Float> acc_array = new ArrayList<Float>();

    private final String MESSAGE1_PATH = "/message1";
    private final String MESSAGE2_PATH = "/message2";

    private GoogleApiClient apiClient;
    private EditText receivedMessagesEditText;
//    private NodeApi.NodeListener nodeListener;
    private MessageApi.MessageListener messageListener;
    private String remoteNodeId;

    @Override
    public void onCreate() {
        Log.d("Accelerometer-Logging", "--- onCreate");
        super.onCreate();

        // Direct the DataAcquisitor to the correct folder location
        //mDataBuffer = new DataAcquisitor(Setting.LOG_FOLDER_EX, Setting.dataFolderName_Accelerometer);
        //mSA_AcclerometerBuffer = new DataAcquisitor(Setting.LOG_FOLDER_EX, "SA/" + Setting.dataFolderName_Accelerometer);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

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
        Log.d("Accelerometer-Logging", "--- onStartCommand");
        lastUpdate = 0;

        // Check is Google Play Services available
        int connectionResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (connectionResult != ConnectionResult.SUCCESS) {
        } else {
            apiClient.connect();
        }
        return START_NOT_STICKY; // If process died, it will <<NOT>> start again

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mDataBuffer.flush(true);
        Log.d("Accelerometer-Logging", "--- onDestroy");
        // Unregister accelerometer sensor
        senSensorManager.unregisterListener(this);
        senSensorManager = null;
        senAccelerometer = null;

//        Wearable.NodeApi.removeListener(apiClient, nodeListener);
        Wearable.MessageApi.removeListener(apiClient, messageListener);
        apiClient.disconnect();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            //float y = event.values[1];
            //float z = event.values[2];

            long curTime = System.currentTimeMillis();
            long diffTime = (curTime - lastUpdate);
            if (diffTime > 2000) {
                lastUpdate = curTime;

                acc_array.add(x);
                Log.d("Accelerometer---", "---"+ acc_array.size());
                if(acc_array.size()>=30){
                    float total =0;
                    for(int i=0;i<acc_array.size();i++)
                    {
                        total=total+acc_array.get(i);
                    }
                    float result = total/acc_array.size();
                    String s = ""+checkdifference(result,acc_array);

                    Wearable.MessageApi.sendMessage(apiClient, remoteNodeId, MESSAGE1_PATH, s.getBytes()).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {

                        }
                    });
                    acc_array.clear();

                }
                // Save x and y axis changes
                //String encoded = JSONUtil.encodeAccelerometer(x, new Date());
                //Log.d("Accelerometer-encoded", encoded);
                //mDataBuffer.insert(encoded, true, Setting.bufferMaxSize);

                //String encoded_SA = SemanticTempCSVUtil.encodeAccelerometer(x, new Date());
                //mSA_AcclerometerBuffer.insert(encoded_SA, true, Setting.bufferMaxSize);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static boolean checkdifference(float f,ArrayList<Float> accArray){
        for(int i=0;i<accArray.size();i++)
        {
            if(accArray.get(i)>f+ACC_FIX||accArray.get(i)<f-ACC_FIX){
                return false;
            }
        }

        return true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
