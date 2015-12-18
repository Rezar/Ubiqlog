package com.ubiqlog.sensors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.ubiqlog.core.DataAcquisitor;
import com.ubiqlog.core.SensorCatalouge;
import com.ubiqlog.utils.IOManager;
import com.ubiqlog.utils.JsonEncodeDecode;
import com.google.android.gms.location.LocationListener;

public class LocationGSSensor extends Service implements
														GoogleApiClient.ConnectionCallbacks,
														GoogleApiClient.OnConnectionFailedListener,
																			SensorConnector,
																			LocationListener  {
	
	//private LocationClient locationClient;

	// Added by AP
	private GoogleApiClient mGoogleApiClient = null;
	private Location mLastLocation = null;

	private Context mContext;
	private Boolean mRequestingLocationUpdates = false;
	private LocationRequest mLocationRequest;

	//private LocationRequest locationrequest;
	private ConnectionResult connectionResult;
	private static long UPDATE_INTERVAL = 30000;
	private boolean isNew = false;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.d("LocationGS-Logging", "--- onCreate");
		SensorCatalouge sencat = new SensorCatalouge(getApplicationContext());
		try {
			ArrayList<SensorObj> sens = sencat.getAllSensors();
			for (int i = 0; i < sens.size(); i++) {
				if (sens.get(i).getSensorName().equalsIgnoreCase("LOCATION_FUSE")) {
					String[] configs = sens.get(i).getConfigData();
					for (int j = 0; j < configs.length; j++) {
						String tmp[] = configs[j].split("=");
						if (tmp[0].trim().equalsIgnoreCase("Scan interval")) {
							UPDATE_INTERVAL = Long.parseLong(tmp[1]);
						}
					}
				}
			}	
		} catch (Exception e) {
			Log.e("[LocationGS-Logging]","----------Error reading the log interval from sensor catalouge."+e.getLocalizedMessage());
			e.printStackTrace();
		}
		/*
		locationClient = new LocationClient(getApplicationContext(), this, this);
		*/

		// Added by AP
		// Get context
		mContext = getApplicationContext();

		// Start GoogleApiClient
		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.addApi(LocationServices.API)
					.build();
		}

		/*
		locationrequest = LocationRequest.create();
	    // Use high accuracy
		locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	    // read the update interval, in this class default set to be 10 seconds
		//UPDATE_INTERVAL = 10000;
		locationrequest.setInterval(UPDATE_INTERVAL);
	    // Set the fastest update interval to the same interval
		locationrequest.setFastestInterval(UPDATE_INTERVAL);
		*/

		// Connect googleApiClient
		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}

		// Added by AP
		mRequestingLocationUpdates = true;
		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if (mLastLocation != null) {
			if (mRequestingLocationUpdates) {
				createLocationRequest();
				startLocationUpdates();
			}
		}
	}
	@Override
    public void onLocationChanged(Location location) {
		String jsonString = JsonEncodeDecode.EncodeLocationGS("Location",location.getLatitude(),location.getLongitude(),
				location.getAltitude(),new Date(),location.getAccuracy(),location.getProvider(), location.getSpeed());
		// Log.e("LocationGS-Logging",jsonString);
		DataAcquisitor.dataBuff.add(jsonString);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("LocationGS-Logging", "--- onStartCommand");

		/*
		if (locationClient.isConnected() || locationClient.isConnecting()){
//			readSensor();
		}else {
			locationClient.connect();	
		}
		*/

		// Connect googleApiClient
		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}

		if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
			createLocationRequest();
			startLocationUpdates();
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		/*
		if (locationClient.isConnected() ) {
	         // Remove location updates for a listener. The current Service is the listener, so the argument is "this".    
			 locationClient.removeLocationUpdates(this);
	    }
		locationClient.disconnect();
		*/
		Log.d("LocationGS-Logging", "--- onDestroy");
		super.onDestroy();

		// Added by AP
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
			mGoogleApiClient = null;
		} else {
			mGoogleApiClient = null;
		}
	}

	public void readSensor() {
		try {
			//Location currloc = locationClient.getLastLocation();
			//locationClient.requestLocationUpdates(locationrequest, this);

			// Added by AP
			Location currloc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
			LocationServices.FusedLocationApi.requestLocationUpdates(
					mGoogleApiClient, mLocationRequest, this);
// 				if (curLocation != null && isNew) {
//				isNew = false;
			String jsonString = JsonEncodeDecode.EncodeLocationGS("Location",currloc.getLatitude(),currloc.getLongitude(),
					currloc.getAltitude(),new Date(),currloc.getAccuracy(),currloc.getProvider(), currloc.getSpeed());
//			Log.e("-------CCC------",jsonString);
			DataAcquisitor.dataBuff.add(jsonString);
//			} 
		} catch (Exception e) {
			IOManager errlogger = new IOManager();
			errlogger.logError("[LocationGS-Logging] error:" + e.getMessage()+ " Stack:" + Log.getStackTraceString(e));
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
	//		if (connectionResult.hasResolution()) {
	//            try {
	//                // Start an Activity that tries to resolve the error
	//                connectionResult.startResolutionForResult(this,CONNECTION_FAILURE_RESOLUTION_REQUEST);
	//                /*
	//                 * Thrown if Google Play services canceled the original
	//                 * PendingIntent
	//                 */
	//            } catch (Exception e) {
	//                // Log the error
	//                e.printStackTrace();
	//            }
	//        } else {
	//            /*
	//             * If no resolution is available, display a dialog to the
	//             * user with the error.
	//             */
	//            Log.e("LocationGSSensor",String.valueOf(connectionResult.getErrorCode()));
	//        }
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		readSensor();
	}

	protected void createLocationRequest() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(UPDATE_INTERVAL);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}

	protected void startLocationUpdates() {
		Log.d(getClass().getSimpleName(), "Started location updates");
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);

	}

	protected void stopLocationUpdates() {
		LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	/*
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		//locationClient = null;

	}
	*/

}

