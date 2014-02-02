package com.ubiqlog.core;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.ubiqlog.common.Setting;
import com.ubiqlog.utils.IOManager;
import com.ubiqlog.core.DataAcquisitor;

public class DataAggregator extends Service {

	String today;
	Long currDateL;

	IOManager datalogger;
	private Handler objHandler = new Handler();
	private Context _ctx = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private Runnable doDataAggregation = new Runnable() {
		public void run() {
			manageDataAcq(20);
			objHandler.postDelayed(doDataAggregation, Long.parseLong(Setting.Instance(_ctx).getDaggSav2File()));
		}
	};

	@Override
	public void onCreate() {
		Log.d("Data-Aggregator", "--- onCreate");
		_ctx = this;
		datalogger = new IOManager();
	}

	@Override
	public void onDestroy() {
		objHandler.removeCallbacks(doDataAggregation);
		manageDataAcq(0);
		Log.d("Data-Aggregator", "--- onDestroy");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("Data-Aggregator", "--- onStart");
		_ctx = this;
		manageDataAcq(20);
		objHandler.postDelayed(doDataAggregation, Long.parseLong(Setting.Instance(this).getDaggSav2File()));
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("Data-Aggregator", "--- onStartCommand");
		_ctx = this;
		manageDataAcq(20);
		objHandler.postDelayed(doDataAggregation, Long.parseLong(Setting.Instance(this).getDaggSav2File()));
		return START_STICKY;
	}

	
/*
 * added minimumSize as param -> so that on termination every data in buffer to be written in file
 */
	private void manageDataAcq(int minimumSize) {
		if (DataAcquisitor.dataBuff.size() > minimumSize) {
			datalogger.logData(DataAcquisitor.dataBuff);
			DataAcquisitor.dataBuff.clear();
		}

	}

}