package com.ubiqlog.sensors;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.ubiqlog.annotation.CallAnnotation;
import com.ubiqlog.common.Setting;
import com.ubiqlog.core.DataAcquisitor;
import com.ubiqlog.utils.IOManager;

import android.app.Service;

//import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * This Class is for TEST maybe I must remove it for reading call log and use a
 * better method
 * 
 * @author rezarawassizadeh
 * 
 */
public class CallSensor extends Service implements SensorConnector {

	private com.ubiqlog.core.DataAcquisitor dataAcq = new DataAcquisitor();
	private Cursor callCur;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.d("Call-Logging", "--- onCreate");
	}

	@Override
	public void onDestroy() {
		Log.d("Call-Logging", "--- onDestroy");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("Call-Logging", "--- onStart");
		readSensor();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("Call-Logging", "--- onStartCommand");
		readSensor();
		return START_STICKY;
	}
	
	public void readSensor() {
		Handler handler = new Handler();
		callCur = getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null,android.provider.CallLog.Calls.DATE + " DESC");
		callCur.registerContentObserver(new CALLContentObserver(handler));
	}

	class CALLContentObserver extends ContentObserver {

		int numberColumn = callCur.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
		int dateColumn = callCur.getColumnIndex(android.provider.CallLog.Calls.DATE);
		int durationColumn = callCur.getColumnIndex(android.provider.CallLog.Calls.DURATION);
		int typeColumn = callCur.getColumnIndex(android.provider.CallLog.Calls.TYPE); // Incoming, Outgoing or Missed

		public CALLContentObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean arg0) {
			try {
				super.onChange(arg0);
				callCur.requery();
				callCur.moveToFirst();

				if (Setting.CALL_ANNOTATION && !(Setting.GROUP_TEST)) {
					CallAnnotation callanot = new CallAnnotation(getApplicationContext());
					
					//String tmpDate = DateFormat.getDateTimeInstance().format(System.currentTimeMillis());
					SimpleDateFormat dateformat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
					Date tmpDate = new Date(callCur.getLong(dateColumn));
					String tmp = new String("{\"Call\": {\"Number\":\""+ callCur.getString(numberColumn)
							+ "\",\"Duration\":\""+ callCur.getInt(durationColumn)
							+ "\",\"Time\":\""+ dateformat.format(tmpDate)+ "\",\"Type\":\""
							+ callCur.getInt(typeColumn)+ "\", \"metadata\":{\"name\":\""
							+ callanot.annotate(callCur.getString(numberColumn)) + "\"}}}");
					dataAcq.dataBuff.add(tmp);
					Log.e("[Call-Logging]",tmp);
					tmpDate = null;
					dateformat=null;
				} else {
					if (Setting.GROUP_TEST){
						SimpleDateFormat dateformat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
						String hidedNum = callCur.getString(numberColumn);
						hidedNum = hidedNum.substring(0, hidedNum.length()-4);
						hidedNum = hidedNum + "####";
						Date tmpDate = new Date(callCur.getLong(dateColumn));
						String tmp = new String("{\"Call\": {\"Number\":\""
								+  hidedNum  + "\",\"Duration\":\""
								+ callCur.getInt(durationColumn) + "\",\"Time\":\""
								+ dateformat.format(tmpDate) + "\",\"Type\":\""
								+ callCur.getInt(typeColumn) + "\"}}");
						tmpDate = null;
						dataAcq.dataBuff.add(tmp);
						Log.e("[Call-Logging]",tmp);
					}else {
						SimpleDateFormat dateformat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
						Date tmpDate = new Date(callCur.getLong(dateColumn));
						String tmp = new String("{\"Call\": {\"Number\":\""
								+ callCur.getString(numberColumn)+ "\",\"Duration\":\""
								+ callCur.getInt(durationColumn) + "\",\"Time\":\""
								+ dateformat.format(tmpDate) + "\",\"Type\":\""
								+ callCur.getInt(typeColumn) + "\"}}");
						tmpDate = null;
						dataAcq.dataBuff.add(tmp);
					}
					
				}
			} catch (Exception e) {
				IOManager errlogger = new IOManager();
				errlogger.logError("[CallSensor] error:" + e.getMessage());
				e.printStackTrace();
			}

		}
	}

}
