package com.ubiqlog.sensors;

import java.util.Date;
import com.ubiqlog.annotation.SMSAnnotation;
import com.ubiqlog.common.Setting;
import com.ubiqlog.core.DataAcquisitor;
import com.ubiqlog.utils.IOManager;
import com.ubiqlog.utils.JsonEncodeDecode;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class SMSSensor extends Service implements SensorConnector {

	private static String lastId = "";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.d("SMS-Logging", "--- onCreate");
	}

	@Override
	public void onDestroy() {
		Log.d("SMS-Logging", "--- onDestroy");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("SMS-Logging", "--- onStart");
		readSensor();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("SMS-Logging", "--- onStartCommand");
		readSensor();
		return START_STICKY;
	}
	
	Cursor smsCur;

	public void readSensor() {

		Handler handler = new Handler();
		smsCur = getContentResolver().query(Uri.parse("content://sms"), null,
				null, null, null); //
		smsCur.registerContentObserver(new SMSContentObserver(handler, this));
	}

	class SMSContentObserver extends ContentObserver {
		int id = smsCur.getColumnIndex("_id");
		int address = smsCur.getColumnIndex("address"); // phone number
		int person = smsCur.getColumnIndex("person");
		int date = smsCur.getColumnIndex("date"); // date
		int protocol = smsCur.getColumnIndex("protocol");
		int status = smsCur.getColumnIndex("status");
		int type = smsCur.getColumnIndex("type");
		int body = smsCur.getColumnIndex("body"); // msg's content
		int reply_path_present = smsCur.getColumnIndex("reply_path_present");
		int subject = smsCur.getColumnIndex("subject");
		int service_center = smsCur.getColumnIndex("service_center");
		private Context _ctx = null;

		public SMSContentObserver(Handler handler, Context ctx) {
			super(handler);
			_ctx = ctx;
		}

		@Override
		public void onChange(boolean arg0) {
			try {
				smsCur.requery();

				// it can happen that smsCur is empty! not null!
				if (smsCur.moveToFirst()) {
					SMSAnnotation smsannot = new SMSAnnotation(getApplicationContext());
					String jsonString = "";
					if (Setting.GROUP_TEST){
						// It is group test and SMS content will be removed + number getremoved
						String tmpsmsAdd = smsCur.getString(address);
						if (smsCur.getString(address).length() >8){
							tmpsmsAdd = tmpsmsAdd.substring(0, tmpsmsAdd.length()-5);
							tmpsmsAdd = tmpsmsAdd + "#####";
						}
						jsonString = JsonEncodeDecode.EncodeSms(
								"SMS",
								tmpsmsAdd,
								smsCur.getInt(type),
								new Date(smsCur.getLong(date)),
								"ANONYMIZED",
								Setting.Instance(_ctx).SMS_ANNOTATION,
								Setting.Instance(_ctx).SMS_ANNOTATION ? smsannot.annotate(smsCur.getString(address)) : "");
						
					}else {
						 jsonString = JsonEncodeDecode.EncodeSms(
								"SMS",
								smsCur.getString(address),
								smsCur.getInt(type),
								new Date(smsCur.getLong(date)),
								smsCur.getString(body),
								Setting.Instance(_ctx).SMS_ANNOTATION,
								Setting.Instance(_ctx).SMS_ANNOTATION ? smsannot.annotate(smsCur.getString(address)) : "");
	
					}
										

					// we log just the sent and received messages! type: 1 and 2
					// it can happen that this will be called more than once for
					// the same message -> that is why we compare the id with
					// the last logged id.
					if ((smsCur.getInt(type) == 1 || smsCur.getInt(type) == 2)
							&& !smsCur.getString(id).equals(lastId)) {
						lastId = smsCur.getString(id);
						Log.e("-----------SMS-------------",jsonString);
						DataAcquisitor.dataBuff.add(jsonString);
					}
				}

			} catch (Exception e) {
				IOManager errlogger = new IOManager();
				errlogger.logError("[SMSSensor] error:" + e.getMessage()
						+ " Stack:" + Log.getStackTraceString(e));
				e.printStackTrace();
			}

			super.onChange(arg0);
		}

		@Override
		public boolean deliverSelfNotifications() {
			return true;
		}

	}

}