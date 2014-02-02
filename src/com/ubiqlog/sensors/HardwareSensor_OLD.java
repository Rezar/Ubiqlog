package com.ubiqlog.sensors;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.ubiqlog.core.DataAcquisitor;
import com.ubiqlog.utils.IOManager;
import com.ubiqlog.vis.utils.JsonEncodeDecode;
import com.ubiqlog.vis.utils.SensorState;
import com.ubiqlog.vis.utils.SensorState.Movement;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class HardwareSensor_OLD  extends Service implements SensorConnector, SensorEventListener {
	private SensorManager sensorManager;
	private Sensor sensorAccelerometer;
	private DataAcquisitor dataAcq = new DataAcquisitor();

	//private float current_tem, current_xO, current_yO, current_zO, current_xM, current_yM, current_zM;
	private final long MINUTE = 60000;
	private int STEPS_PER_MINUTE_STEADY_THRESHOLD = 60;
	private int STEPS_PER_MINUTE_SLOW_THRESHOLD = 180;
	
	private Timer accelerometerTimer;
	
	class AccelerometerData
	{
		float   limit = 10;
	    float   lastValue;
	    float   scale;
	    float   yOffset;
	    float   lastDirection;
	    float   lastExtremes[] = new float[2];
	    float   lastDiff;
	    int     lastMatch = -1;
	    Integer	steps = 0;
	    
	    AccelerometerData()
	    {
	    }
	};
	AccelerometerData accelerometerData;

	//@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) 
	{
		// TODO Auto-generated method stub

	}

	//@Override
	public void onSensorChanged(SensorEvent event) 
	{
		try {
			//String tmpDate = DateFormat.getDateTimeInstance().format(System.currentTimeMillis());
			int sensorType = event.sensor.getType();
			
			switch (sensorType) 
			{
			case Sensor.TYPE_ACCELEROMETER: 
			{
				AnalyzeAccelerometerData(event);
			}
			break;
//			case Sensor.TYPE_ORIENTATION: 
//			{
//				current_xO = event.values[SensorManager.DATA_X];
//				current_yO = event.values[SensorManager.DATA_Y];
//				current_zO = event.values[SensorManager.DATA_Z];
//				dataAcq.dataBuff.add("\"Hardware-ORIENTATION\":{\"X\":\""
//						+ current_xO + "\",\"Y\":\"" + current_yO + "\",\"Z\":"
//						+ current_zO + "\",\"time\":\"" + tmpDate + "\"}");
//				// Log.e("Hardware-Logging","ORIENTATION-------X:"+current_xO+"---Y:"+current_yO+"---Z:"+current_zO);
//			}
//			break;
//			case Sensor.TYPE_MAGNETIC_FIELD: 
//			{
//				current_xM = event.values[SensorManager.DATA_X];
//				current_yM = event.values[SensorManager.DATA_Y];
//				current_zM = event.values[SensorManager.DATA_Z];
//				dataAcq.dataBuff.add("\"Hardware-COMPASS\":{\"X\":\""
//						+ current_xM + "\",\"Y\":\"" + current_yM
//						+ "\",\"Z\":\"" + current_zM + "\",\"time\":\""
//						+ tmpDate + "\"}");
//				// Log.e("Hardware-Logging","COMPASS-------X:"+current_xM+"---Y:"+current_yM+"---Z:"+current_zM);
//			}
//			break;
//			case Sensor.TYPE_TEMPERATURE: 
//			{
//				current_tem = event.values[SensorManager.DATA_X];
//				dataAcq.dataBuff.add("\"Hardware-TEMPRATURE\":{\""
//						+ current_tem + "\",\"time\":\"" + tmpDate + "\"}");
//				// Log.e("Hardware-Logging","TEMPRATURE-------:"+current_tem);
//			}
//			break;
			}
		} 
		catch (Exception e) 
		{
			IOManager errlogger = new IOManager();
			errlogger.logError("[HardwareSensor] error:" + e.getMessage());
			e.printStackTrace();
		}

	}

	//@Override
	public void readSensor() 
	{
		// Sensor Data read from event, therefore in this case I can not read
		// them here.
	}

	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}

	@Override
	public void onCreate() 
	{
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		Log.d("Hardware-Logging", "--- onCreate");
	}

	@Override
	public void onDestroy() 
	{
		accelerometerTimer.cancel();
		sensorManager.unregisterListener(this);
		
		Log.d("Hardware-Logging", "--- onDestroy");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("Hardware-Logging", "--- onStart");
		
		accelerometerData = new AccelerometerData();
		accelerometerData.yOffset = 240.0f;
		accelerometerData.scale = -4.0f;
		
		accelerometerTimer = new Timer();
		accelerometerTimer.scheduleAtFixedRate(acceleromterTimerTask, MINUTE, MINUTE);
		
		sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_UI);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("Hardware-Logging", "--- onStartCommand");
		
		accelerometerData = new AccelerometerData();
		accelerometerData.yOffset = 240.0f;
		accelerometerData.scale = -4.0f;
		
		accelerometerTimer = new Timer();
		accelerometerTimer.scheduleAtFixedRate(acceleromterTimerTask, MINUTE, MINUTE);
		
		sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_UI);
		return START_STICKY;
	}
	
	private TimerTask acceleromterTimerTask = new TimerTask() 
	{
		//@Override
		public void run() {
			int steps;
			SensorState.Movement movementState;
			Calendar currentDateTime = Calendar.getInstance();
			Calendar startDateTime, endDateTime;
			
			currentDateTime.setTimeInMillis(System.currentTimeMillis());
			
			synchronized(accelerometerData.steps) {
        		steps = accelerometerData.steps;
        		accelerometerData.steps = 0;
        	}
			
			if (steps < STEPS_PER_MINUTE_STEADY_THRESHOLD) {
				movementState = Movement.STEADY;
			}
			else if (steps < STEPS_PER_MINUTE_SLOW_THRESHOLD) {
				movementState = Movement.SLOW;
			}
			else {
				movementState = Movement.FAST;
			}
			
			startDateTime = (Calendar)currentDateTime.clone();
			startDateTime.set(Calendar.SECOND, 0);
			startDateTime.set(Calendar.MILLISECOND, 0);
			
			int currentSecond = currentDateTime.get(Calendar.SECOND);
			if (currentSecond > 30) {
				startDateTime.add(Calendar.MINUTE, 1);
			}
			
			endDateTime = (Calendar)startDateTime.clone();
			endDateTime.add(Calendar.MINUTE, 1);
			
			dataAcq.dataBuff.add(JsonEncodeDecode.EncodeMovement(startDateTime.getTime(), endDateTime.getTime(), movementState));
		}
	};

	private void AnalyzeAccelerometerData(SensorEvent sensorEvent) {
		float vSum = 0;
        for (int i=0 ; i<3 ; i++) {
            final float v = accelerometerData.yOffset + sensorEvent.values[i] * accelerometerData.scale;
            vSum += v;
        }
        float v = vSum / 3;
        float direction = (v > accelerometerData.lastValue ? 1 : (v < accelerometerData.lastValue ? -1 : 0));
        
        if (direction == - accelerometerData.lastDirection) {
            // Direction changed
            int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
            accelerometerData.lastExtremes[extType] = accelerometerData.lastValue;
            float diff = Math.abs(accelerometerData.lastExtremes[extType] - accelerometerData.lastExtremes[1 - extType]);

            if (diff > accelerometerData.limit) {
                boolean isAlmostAsLargeAsPrevious = diff > (accelerometerData.lastDiff*2/3);
                boolean isPreviousLargeEnough = accelerometerData.lastDiff > (diff/3);
                boolean isNotContra = (accelerometerData.lastMatch != 1 - extType);
                
                if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                	synchronized(accelerometerData.steps) {
                		accelerometerData.steps++;
                	}
                    accelerometerData.lastMatch = extType;
                }
                else {
                	accelerometerData.lastMatch = -1;
                }
            }
            accelerometerData.lastDiff = diff;
        }
        accelerometerData.lastDirection = direction;
        accelerometerData.lastValue = v;
	}

}
