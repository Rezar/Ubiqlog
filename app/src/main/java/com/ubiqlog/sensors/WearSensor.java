package com.ubiqlog.sensors;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class WearSensor extends Service {

    private final String MESSAGE1_PATH = "/message1";
    private final String MESSAGE2_PATH = "/message2";
    private GoogleApiClient apiClient;
//    private NodeApi.NodeListener nodeListener;
    private String remoteNodeId;
    private MessageApi.MessageListener messageListener;


    public WearSensor() {
    }

    @Override
    public void onCreate() {
        Log.e("Wear-Logging", "--- onCreat");
        // Create NodeListener that enables buttons when a node is connected and disables buttons when a node is disconnected
//        nodeListener = new NodeApi.NodeListener() {
//            @Override
//            public void onPeerConnected(Node node) {
//                remoteNodeId = node.getId();
//            }
//
//            @Override
//            public void onPeerDisconnected(Node node) {
//
//            }
        };

        // Create MessageListener that receives messages sent from a mobile
        /*
        messageListener = new MessageApi.MessageListener() {
            @Override
            public void onMessageReceived(MessageEvent messageEvent) {
                Log.e("Wear-Logging", "wwwwwrrrrr");
                if (messageEvent.getPath().equals(MESSAGE1_PATH)) {
                    Log.e("Wear-Logging", "wwwwwrrrrr");
                    Toast.makeText(getBaseContext(),new String(messageEvent.getData()),Toast.LENGTH_LONG).show();
                }
            }
        };
        */
        // Create MessageListener that receives messages sent from a wearable
//        messageListener = new MessageApi.MessageListener() {
//            @Override
            public void onMessageReceived(final MessageEvent messageEvent) {
                //Log.e("Wear-Logging", "wwwwwrrrrr");

                if (messageEvent.getPath().equals(MESSAGE1_PATH)) {
                    Log.e("Wear-Logging", "wwwwwrrrrr");
                    String s = new String(messageEvent.getData());
                    SleepSensor.setWEAR_ACC(Boolean.getBoolean(s));
                    //Toast.makeText(getBaseContext(),new String(messageEvent.getData()),Toast.LENGTH_LONG).show();
                }else if (messageEvent.getPath().equals(MESSAGE2_PATH)) {
                    Log.e("Wear-Logging", "wwwwwrrrrr");
                    String s = new String(messageEvent.getData());
                    Float f = Float.parseFloat(s);
                    if(f>20)
                    {
                        SleepSensor.setWEAR_AMB(false);
                    }else{
                        SleepSensor.setWEAR_AMB(true);
                    }

                    //Toast.makeText(getBaseContext(),new String(messageEvent.getData()),Toast.LENGTH_LONG).show();
                }

            };
//        };
        // Create GoogleApiClient
//        apiClient = new GoogleApiClient.Builder(getApplicationContext()).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
//            @Override
//            public void onConnected(Bundle bundle) {
//                // Register Node and Message listeners
//                Wearable.NodeApi.addListener(apiClient, nodeListener);
//                Wearable.MessageApi.addListener(apiClient, messageListener);
//                // If there is a connected node, get it's id that is used when sending messages
//                Wearable.NodeApi.getConnectedNodes(apiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
//                    @Override
//                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
//                        if (getConnectedNodesResult.getStatus().isSuccess() && getConnectedNodesResult.getNodes().size() > 0) {
//                            remoteNodeId = getConnectedNodesResult.getNodes().get(0).getId();
//
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onConnectionSuspended(int i) {
//
//            }
//        }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
//            @Override
//            public void onConnectionFailed(ConnectionResult connectionResult) {
//                if (connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE)
//                    Toast.makeText(getApplicationContext(), "unava", Toast.LENGTH_LONG).show();
//            }
//        }).addApi(Wearable.API).build();
//
//
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Wear-Logging", "--- onStartCommand");
        // Check is Google Play Services available
        int connectionResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (connectionResult != ConnectionResult.SUCCESS) {
        } else {
            //Log.e("Wear-Logging", "wwwwwrrrrr3");
            apiClient.connect();
        }

        return START_NOT_STICKY; // If process died, it will <<NOT>> start again
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mDataBuffer.flush(true);
        Log.e("WEAR-Logging", "--- onDestroy");

//        Wearable.NodeApi.removeListener(apiClient, nodeListener);
        Wearable.MessageApi.removeListener(apiClient, messageListener);
        apiClient.disconnect();
    }
}
