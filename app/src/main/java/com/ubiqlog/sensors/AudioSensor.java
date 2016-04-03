package com.ubiqlog.sensors;

import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.ubiqlog.common.Setting;
import com.ubiqlog.core.DataAcquisitor;
import com.ubiqlog.utils.JsonEncodeDecode;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;
/**
 * This sensor will use time interval for logging
 * 
 * @author Victor Gugonatu
 * 
 */
public class AudioSensor extends Service implements SensorConnector {

	private static String AUDIO_FOLDER = null;
	private MediaRecorder _recorder = null;

	// in bytes
	// ~ 50MB
	private long max_file_size = 52428800;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.d("[AudioSensor.onCreate]", "--- onCreate");

		_recorder = new MediaRecorder();
		AUDIO_FOLDER = Setting.Instance(this).getAudioFolder();
	}

	
	public void readSensor() {
		initMediaRecorder(_recorder);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.e("[AudioSensor-Logging]", "--- onStart");
		readSensor();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e("[AudioSensor-Logging]", "--- onStartCommand");
		readSensor();
		return START_STICKY;
	}

	@Override
	public void onDestroy() {

		stopAudioRecorder();
		Log.d("[AudioSensor.onDestroy]", "--- onDestroy");
	}


	private MediaRecorder.OnInfoListener mInfoListener = new MediaRecorder.OnInfoListener() {

		public void onInfo(MediaRecorder mr, int what, int extra) {
			switch (what) {
			case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:

				// max file size reached
				// stop recording if not already stopped
				if (mr != null) {
					try {
						mr.stop();
					} catch (IllegalStateException exc) {
						// do nothing - already stopped..
					}
					mr = new MediaRecorder();
					initMediaRecorder(mr);
				}
				break;
			default:
				break;
			}

		}
	};

	private void initMediaRecorder(MediaRecorder recorder) {

		// set media recorder settings -> todo -> move to settings
		recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		recorder.setMaxFileSize(max_file_size);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

		// set the info listener
		// will be called when the maximum file size is reached
		recorder.setOnInfoListener(mInfoListener);

		File sdAudioMainDirectory = new File(AUDIO_FOLDER);
		Boolean pathExists = true;

		if (!sdAudioMainDirectory.exists()) {
			Log.d("[AudioSensor.initMediaRecorder]", "--- create audio folder: " + AUDIO_FOLDER);
			if (sdAudioMainDirectory.mkdirs()) {
				Log.d("[AudioSensor.initMediaRecorder]", "--- create audio folder succeded!");
			} else {
				Log.d("[AudioSensor.initMediaRecorder]", "--- create audio folder failed!");
				pathExists = false;
			}
		}

		if (pathExists) {
			// Generate filename
			SimpleDateFormat sdf = new SimpleDateFormat("Z-yyMMdd-HHmmss.SSS");
			String filename = sdAudioMainDirectory.toString() + "/" + "Audio-"+ sdf.format(new Date());
			String tmp_filename = filename + ".mpeg";
			int i = 0;

			File tmp = new File(tmp_filename);
			while (tmp.exists() && i < 100) {
				i++;
				tmp_filename = filename + "_" + i + ".mpeg";
				tmp = new File(tmp_filename);
			}

			filename = tmp_filename;
			recorder.setOutputFile(filename);
			String jsonString = JsonEncodeDecode.EncodeAudio("Audio", filename, new Date());
			DataAcquisitor.dataBuff.add(jsonString);

			try {
				recorder.prepare();
				recorder.start();
			} catch (IllegalStateException e) {
				Log.d("[AudioSensor.initMediaRecorder]", "--- couldn't start audio logging!"+e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Log.d("[AudioSensor.initMediaRecorder]", "--- couldn't start audio logging 2!"+e.getMessage());
				e.printStackTrace();
			}
		}

	}

	private void stopAudioRecorder() {
		if (_recorder != null) {
			try {
				_recorder.stop();
				_recorder.release();
				_recorder = null;
			} catch (IllegalStateException exc) {
				// do nothing - already stopped..
			}
		}
	}
}
