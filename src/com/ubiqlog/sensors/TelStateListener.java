package com.ubiqlog.sensors;

import java.text.DateFormat;
import java.text.Format;

import com.ubiqlog.common.Setting;
import com.ubiqlog.utils.AudioRecorder;
import com.ubiqlog.utils.IOManager;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.sql.*;

public class TelStateListener extends PhoneStateListener {

	AudioRecorder audiorec;

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {

		super.onCallStateChanged(state, incomingNumber);
		try {
			audiorec = new AudioRecorder();
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				if (Setting.telLogging == true) {
					// Log.e("TelStateListener",
					// "---------------Stop Recording the Audio----------------Time:"+
					// Format.getDateTimeInstance().format(System.currentTimeMillis()));
					audiorec.stopRecord();
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				if (Setting.telLogging == true) {
					// Log.e("TelStateListener",
					// "----------------Start Recording the Audio-------------------Time:"+System.currentTimeMillis());
					audiorec.startRecord();
				}
				break;
			default:
				break;
			}
			audiorec = null;
		} catch (Exception ex) {
			IOManager errlogger = new IOManager();
			errlogger.logError("[TellSensor] error:" + ex.getMessage());
			Log.e("TelStateListener", "------Error Recording this call------"+ ex.getLocalizedMessage());
		}
	}

}
