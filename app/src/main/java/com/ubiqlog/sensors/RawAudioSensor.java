package com.ubiqlog.sensors;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.ubiqlog.common.Setting;
import com.ubiqlog.core.DataAcquisitor;

public class RawAudioSensor extends Service {

    private static final int RECORDER_SAMPLERATE = 44100;   // Better quality sound
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    private int count =0;
    private int total=0;

    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format
    int bufferSize = 0;

    private DataAcquisitor mDataBuffer;

    private Handler mHandler;

    public RawAudioSensor() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("RawAudio-Logging", "--- onCreate");
        bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);

        mDataBuffer = new DataAcquisitor(Setting.DEFAULT_FOLDER, Setting.dataFileName_Raw_Audio);
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        Log.d("RawAudio-Logging", "--- onStartCommand");
        mHandler = new Handler();

        // Start the logging of audio in terms of short data
        startRecording();

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }


    private void startRecording() {

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);
        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                mHandler.postDelayed(new LogAudioDataRunnable(), Setting.RAW_AUDIO_DELAY);
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    //convert short to byte
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }

    //convert short to byte
    private short[] byte2short(byte[] bData) {
        int shortArrsize = bData.length;
        short[] shorts = new short[shortArrsize / 2];
        for (int i = 0; i < shortArrsize; i+=2) {
            short lowerNibble =  (short) bData[i];
            short upperNibble =  (short)(bData[i+1] << 8);

            shorts[i] = (short) (upperNibble | lowerNibble);
            //shorts[i * 2] = (byte) (bData[i] & 0x00FF);
            //shorts[(i * 2) + 1] = (byte) (bData[i] >> 8);
            bData[i] = 0;
            bData[i+1] = 0;
        }
        return shorts;
    }

    private void writeAudioDataToFile() {
        // Write the output audio in byte
        if (recorder != null) {
            short sData[] = new short[bufferSize];
            recorder.read(sData, 0, bufferSize);
            StringBuffer sb = new StringBuffer();

            for(int i=0;i<sData.length;i++)
            {
                total = total+Math.abs(sData[i]);
                count++;
            }
            // before writing to the file process the data and store them as the most recent data.
            if(count>=43008){
                SleepSensor.setAudioArray(total/count);
                count=0;
                total=0;
            }
            //SleepSensor.setAudioArray(total/sData.length);
            //Log.e("RAWAUDIO", ""+sData.length);

            // Convert short array into byte array
            //byte [] bData = short2byte(sData);

            // Convert byte array to Base64 string to be stored in JSON
            //String encodedData = android.util.Base64.encodeToString(bData, android.util.Base64.DEFAULT);
            //byte [] tempBytes = android.util.Base64.decode(s, android.util.Base64.DEFAULT);

            // Store the Base64 String in a RawAudio file
            //String encoded = JsonEncodeDecode.EncodeRawAudio(encodedData, new Date());
            //Log.d("Accelerometer-encoded", encoded);
            //mDataBuffer.insert(encoded, true, Setting.bufferMaxSize);

        } else {
            Log.d(getClass().getSimpleName(), "Attempting to write data but recorder is null");
        }
    }

    private void stopRecording() {
        if (mHandler != null) {
            mHandler.removeCallbacks(new LogAudioDataRunnable());
            mHandler = null;
        }

        // stops the recording activity
        if (null != recorder) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("RawAudio-Logging", "--- onDestroy");
        stopRecording();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private class LogAudioDataRunnable implements Runnable {

        @Override
        public void run() {
            writeAudioDataToFile();
            if (mHandler != null) {
                mHandler.postDelayed(this, Setting.RAW_AUDIO_DELAY);
            }
        }
    }

}

