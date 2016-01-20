package com.ubiqlog.sensors;

/**
 * Created by Ping_He on 2016/1/19.
 */

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.util.Pair;

import com.ubiqlog.common.Setting;
import com.ubiqlog.core.DataAcquisitor;
import com.ubiqlog.utils.JsonEncodeDecode;

import java.util.Calendar;


public class InteractionSensor extends Service {

    String title = "SCREEN_ON";
    private final String TAG = this.getClass().getSimpleName();
    final int PROCESS_STATE_TOP = 2;

    private Handler mHandler;

    // Added by AP
    Intent ServiceIntent;
    private ActivityManager mActivityManager;
    private DetectAppLaunchRunnable mRunnable;
    ActivityManager.RecentTaskInfo mRecentActivityManager;
    Pair<ActivityManager.RunningAppProcessInfo, String> nextActivePair;

    private long start_timestamp;
    private long end_timestamp;
    Calendar startTime;

    // Buffers
    private DataAcquisitor mDataBuffer;
    private DataAcquisitor mSA_AppUsage;

    HandlerThread appBackThread;
    ScreenBroadcastReceiver m_receiver;
    PowerManager pm;
    PowerManager.WakeLock wl;

    //private static com.insight.insight.utils.AppsGenres AppsGenres = new AppsGenres();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("Interaction-Logging", "--- onCreate");
        // Added by AP
        //Log.d(TAG, "Starting App usage sensor");
        pm = null;
        wl = null;
        ServiceIntent = null;

        appBackThread = new HandlerThread("AppUsage", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        appBackThread.start();
        mHandler = new Handler(appBackThread.getLooper());
        mRunnable = new DetectAppLaunchRunnable();
        mHandler.post(mRunnable);

        nextActivePair = null;
        mDataBuffer = new DataAcquisitor(Setting.DEFAULT_FOLDER, Setting.dataFileName_ScreenUsage);
        //mSA_AppUsage = new DataAcquisitor("SA/" + Setting.dataFolderName_ScreenUsage);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        m_receiver = new ScreenBroadcastReceiver();
        registerReceiver(m_receiver, filter);
        //IOManager ioManager = new IOManager();
        //File dir1 = new File(ioManager.getDataFolderFullPath(mDataBuffer.getFolderName()));
        //File dir2 = new File(ioManager.getDataFolderFullPath(mSA_AppUsage.getFolderName()));

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Interaction-Logging", "--- onStartCommand");
        //mHandler.post(mRunnable);
        if (wl == null && pm == null && ServiceIntent == null) {
            start();
            ServiceIntent = intent;
        }
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }


    /**
     * Added by Author: AP
     * DetectAppLaunchRunnable is used by the AppUsageSensor to get
     * the TOP active application on the watch
     * Upon closing of an application, the value of startTime, endTime, applicationName
     * and minutes elapsed will be saved to a local folder
     */
    @SuppressWarnings("ResourceType")
    public class DetectAppLaunchRunnable implements Runnable {

        @Override
        public void run() {
            //Log.d(TAG, "Screen on");
            start_timestamp = System.currentTimeMillis();

            //mHandler.postDelayed(this, Setting.APP_USAGE_INTERVAL);
        }

    }

    @Override
    public void onDestroy() {
        if (appBackThread != null) {
            Thread moribund = appBackThread;
            appBackThread = null;
            moribund.interrupt();
        }
        mDataBuffer.flush(true);
        //mSA_AppUsage.flush(true);
        super.onDestroy();
        unregisterReceiver(m_receiver);
        //wl.release();

        Intent in = new Intent();
        in.setAction("AppUsage.StartkilledService");
        sendBroadcast(in);
        //Log.d("debug", "Service Killed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressWarnings("deprecation")
    public void start() {
        try {
            /*
            pm = (PowerManager) getApplicationContext().getSystemService(
                    Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP, getClass().getName());
            wl.acquire();
            KeyguardManager mgr = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock lock = mgr.newKeyguardLock(KEYGUARD_SERVICE);
            lock.disableKeyguard();
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class ScreenBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.d("ON SCREEN ON", "might hang here");
                mHandler.removeCallbacks(mRunnable);
                mHandler.post(mRunnable);

            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                start();
                Log.d("SCREEN OFF", "might hang here");
                // stop();
                mHandler.removeCallbacks(mRunnable);

                end_timestamp = System.currentTimeMillis();
                Long totalTime = end_timestamp - start_timestamp;

                //Log.d(TAG, "Start Time: " + Long.toString(start_timestamp));
                //Log.d(TAG,"End Time: " + Long.toString(end_timestamp));

                Double Tminutes = ((end_timestamp - start_timestamp) / (1000.0 * 60.0));
                //Log.d(TAG, "Minutes: " + Tminutes);

                //Log.d(TAG, "Store app usage " + applicationName);
                startTime = Calendar.getInstance();
                startTime.setTimeInMillis(start_timestamp);

                Calendar endTime = Calendar.getInstance();
                endTime.setTimeInMillis(end_timestamp);
                int startHour = startTime.get(Calendar.HOUR_OF_DAY);
                int endHour = endTime.get(Calendar.HOUR_OF_DAY);

                Double MinutesInStartHour = 0.0;
                Double MinutesInEndHour = 0.0;
                if (startHour != endHour) {
                    Calendar atBeginningOfHour = Calendar.getInstance();
                    int nextHour = 0;
                    if (startHour == 23) {
                        nextHour = 0;
                    } else {
                        nextHour = startHour + 1;
                    }
                    atBeginningOfHour.set(Calendar.HOUR_OF_DAY, nextHour);
                    atBeginningOfHour.set(Calendar.MINUTE, 0);
                    atBeginningOfHour.set(Calendar.SECOND, 0);
                    atBeginningOfHour.set(Calendar.MILLISECOND, 0);
                    //Log.d(TAG, "BeginnigOfHour=" + atBeginningOfHour.getTimeInMillis());
                    MinutesInEndHour = ((endTime.getTimeInMillis() - atBeginningOfHour.getTimeInMillis()) / (1000.0 * 60.0));
                    MinutesInStartHour = Tminutes - MinutesInEndHour;
                    //Log.d(TAG, "StartMinutes=" + MinutesInStartHour);
                    //Log.d(TAG, "ExtraMinutes=" + MinutesInEndHour);

                } else {
                    MinutesInStartHour = Tminutes;
                    MinutesInEndHour = 0.0;
                }

                //store in buff / write  file
                String encoded = JsonEncodeDecode.EncodeScreenUsage(Integer.toString(startHour), Integer.toString(endHour),
                        startTime.getTime(), endTime.getTime(), Tminutes, MinutesInStartHour, MinutesInEndHour);

                // mDataBuffer.insert(encoded, true, Setting.bufferMaxSize);
                DataAcquisitor.dataBuff.add(encoded);
                Log.e("Screen_Interaction",encoded);
                /*
                String encoded_SA = SemanticTempCSVUtil.encodedScreenUsage(Integer.toString(startHour),Integer.toString(endHour),
                        startTime.getTime(), endTime.getTime(), Tminutes, MinutesInStartHour, MinutesInEndHour);
                mSA_AppUsage.insert(encoded_SA, true, Setting.bufferMaxSize);
                */

                start_timestamp = 0;
            }
        }
    }
}
