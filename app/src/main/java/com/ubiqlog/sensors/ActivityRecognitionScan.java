package com.ubiqlog.sensors;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityRecognition;
import com.ubiqlog.utils.IOManager;
/**
 * check this sample:
 * https://github.com/diegofigueroa/activity-recognition-sample/blob/master/eclipse/activity-recognition-practice/src/com/example/arp/ActivityRecognitionManager.java
 * @author rr4874
 *
 */
public class ActivityRecognitionScan implements
												GoogleApiClient.ConnectionCallbacks,
												GoogleApiClient.OnConnectionFailedListener {

	IOManager ioerror = new IOManager(); // to log error
	private Context ctx;
	private static final String TAG = "ActivityRecognitionScan";
	//private static ActivityRecognitionClient actrecClient2;
	private static GoogleApiClient actrecClient2;
	private static PendingIntent callbackIntent;
	private long ACTIVITY_LOG_INTERVAL = 30000L;
	
	//private long ACTIVITY_LOG_INTERVAL=30000;
	public ActivityRecognitionScan(Context context, long ACTIVITY_LOG_INTERVAL) {
		ctx = context;
		this.ACTIVITY_LOG_INTERVAL = ACTIVITY_LOG_INTERVAL;
	}
	/**
	 * Call this to start a scan - don't forget to stop the scan once it's done.
	 * Note the scan will not start immediately, because it needs to establish a connection with Google's servers - you'll be notified of this at onConnected
	 */
	public void startActivityRecognitionScan(){
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(ctx);

		if(resp == ConnectionResult.SUCCESS){
			
			Intent intent = new Intent(ctx, ActivityResultHandler.class);
			callbackIntent = PendingIntent.getService(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			/*
			actrecClient2 = new ActivityRecognitionClient(ctx, this, this);
			if (!actrecClient2.isConnected() || !actrecClient2.isConnecting()){
			//	Log.e(TAG," ---Activity recognition client is connecting");
				actrecClient2.connect();
				
			} else{
			//	Log.e(TAG," ---Activity recognition client is already connected");
			}
			*/

			// Added by AP
			actrecClient2 =  new GoogleApiClient.Builder(ctx)
					.addApi(ActivityRecognition.API)
					.addConnectionCallbacks(this) //this is refer to connectionCallbacks interface implementation.
					.addOnConnectionFailedListener(this) //this is refer to onConnectionFailedListener interface implementation.
					.build();

			actrecClient2.connect();

		}else{
				Log.e("[Activity-Logging]", "Google Play Service hasn't installed");
		}
		//------------
	}

	public void stopActivityRecognitionScan(){
		try{
			if (actrecClient2.isConnected() || actrecClient2.isConnecting() ){
				//actrecClient2.removeActivityUpdates(callbackIntent);

				// Added by AP
				ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(actrecClient2, callbackIntent);


				actrecClient2.disconnect();
				Log.e(TAG, " acitivy recognition client has been stopped");
			}
			
			actrecClient2 = null;
			callbackIntent = null;
//			actrecClient.unregisterConnectionCallbacks(this);
//			actrecClient.unregisterConnectionFailedListener(this);
		} catch (Exception e){
			Log.e(TAG, "failed to stop activity recognition "+e.getMessage());
			actrecClient2 = null;
			e.printStackTrace();
		}
	}
	int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.e(TAG, "Connection Failed");
        if (result.hasResolution()) {
        	/*
             * Google Play services can resolve some errors it detects. 
             * If the error has a resolution, try sending an Intent to  start a Google Play services activity that can error.
             */
            try {
            	result.startResolutionForResult((Activity) ctx, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (SendIntentException e)  {
            	Log.e(TAG,e.getMessage());
    			ioerror.logError(e.getLocalizedMessage());
            }
        } 
        else {
        	 /*
             * If no resolution is available, display Google Play service error dialog. 
             * This may direct the user to Google Play Store if Google Play services is out of date.
             */
        	Log.d(TAG,"No solution avaiable.");
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                            result.getErrorCode(),
                            (Activity) ctx,CONNECTION_FAILURE_RESOLUTION_REQUEST);
            if (dialog != null) {
                dialog.show();
            }
        }

		actrecClient2 = null;
	}

	/**
	* Connection established - start listening now
	*/
	@Override
	public void onConnected(Bundle connectionHint) {
		try{
			Log.e(TAG," ---Activity recognition client finally has been connected ");

			//	Intent intent = new Intent(ctx, ActivitySensor.class);
			//	Bundle bundle = intent.getExtras();
			//	callbackIntent = PendingIntent.getService(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			//	if ( null!= bundle && bundle.containsKey("LOG_INTERVAL") ){
			//		interval = bundle.getLong("LOG_INTERVAL");
			//	}
			
			// actrecClient2.requestActivityUpdates(ACTIVITY_LOG_INTERVAL, callbackIntent);

			// Added by AP
			ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(actrecClient2 , 1 ,callbackIntent);
			//actrecClient2.disconnect();
		}catch(Exception ex){
			Log.e("[Activity-Logging]","Error in requesting Activity update "+ex.getMessage());
			ex.printStackTrace();
		}
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	/*
	@Override
	public void onDisconnected() {
		callbackIntent.cancel();
		actrecClient2 = null;
		Log.e("TAG","---onDisconnected");
	}
	*/
	

}
