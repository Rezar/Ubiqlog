package com.ubiqlog.utils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import android.content.ContentValues;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class AudioRecorder {

	MediaRecorder recorder = new MediaRecorder();
	ContentValues values = new ContentValues(3);
	File audiofile = null;
	Long callDate;

	public void startRecord() {
		try {
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			File audioDir = new File(com.ubiqlog.common.Setting.LOG_FOLDER);
			callDate = System.currentTimeMillis();
			Date now = new Date();
			int thisyear = now.getYear() + 1900;
			String nowStr = thisyear + "-" + now.getMonth() + "-"
					+ now.getDate() + "_" + now.getHours() + "-"
					+ now.getMinutes() + "-" + now.getSeconds();
			audiofile = File.createTempFile("call_" + nowStr, ".3gp", audioDir);

			recorder.setOutputFile(audiofile.getAbsolutePath());
			recorder.prepare();
			recorder.start();

		} catch (IllegalStateException e) {
			Log
					.e("AudioRecorder",
							"----------IllegalStateException-----------");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("AudioRecorder", "----------IOException-----------");
			e.printStackTrace();
		}
	}

	public void stopRecord() {
		recorder.stop();
		recorder.release();
	}
}
