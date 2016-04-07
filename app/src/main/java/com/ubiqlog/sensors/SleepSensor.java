package com.ubiqlog.sensors;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.ubiqlog.core.DataAcquisitor;
import com.ubiqlog.utils.JsonEncodeDecode;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ping_He on 2016/3/30.
 */
public class SleepSensor extends Service {

    private final static int AUDIO_SIGNAL= 20; // the noise below this value is silent
    private final static float ACC_FIX=0.5f; // the fluctucations which presents moving
    private final static  int AMB_DARK =5; // the ambient light limit that represents darkness
    private final boolean BATT_CHARGE=true; // the battery is charging

    private final int NoSleepingCheckInterval = 60000; // every minutes check if there is no sleep
    private final int SleepingCheckInterval = 300000; // while user is slept every five minutes it checks if wakeup symptom has happened

    private static boolean IS_WEAR_ACC=false;
    private static boolean IS_WEAR_AMB=false;

    private boolean isSleeping = false;
    private static ArrayList<Float> accArray = new ArrayList<Float>();
    private static boolean isAccSatisfy=false;//condition of accelemeter, if satisfy the sleep, the value will be yes
    private static boolean isAudioSatisfy=false;//condition of audio, if satisfy the sleep, the value will be yes
    private static float ambientData =0;
    private static boolean isCharge = false;
    private Date starttime = null;
    private Date endtime = null;
    private boolean flag = true;



    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Sleep-Logging", "--- onCreate");
    }
    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        Log.e("Sleep-Logging", "--- onStartCommand");

        new Thread(){
            public void run()
            {
                while(flag)
                {
                    Log.e("SleepTAG",isAccSatisfy+","+isAudioSatisfy+","+isCharge+","+ambientData+","+isSleeping);

                    if(readSensor())
                    {
                        if(isSleeping){
                            isSleeping=true;
                        }else{
                            isSleeping=true;
                            starttime = new Date();
                        }

                    }else{
                        if(isSleeping)
                        {
                            isSleeping=false;
                            endtime = new Date();
                            String encoded = JsonEncodeDecode.EncodeSleep(starttime,endtime);
                            DataAcquisitor.dataBuff.add(encoded);
                            Log.e("SleepLOG",encoded);
                            starttime=null;
                            endtime=null;
                        }

                        isSleeping=false;
                    }

                    try{
                        if(isSleeping==false){
                            Thread.sleep(NoSleepingCheckInterval);
                        }else {
                            Thread.sleep(SleepingCheckInterval);
                        }

                    }catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        }.start();
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    public boolean readSensor()
    {
        if(isAudioSatisfy && isAccSatisfy && isCharge &&(ambientData<AMB_DARK))
        {
            return true;//satisfy the sleep condition
        }else{
            return false;
        }
    }
    public static void setAudioArray(int i)
    {
        Log.e("AUdioValue",""+i);
        if(i < AUDIO_SIGNAL)
        {
            isAudioSatisfy=true;// is silent
        }else {
            isAudioSatisfy=false;
        }
    }

    public static void setAccArray(float f)
    {
        if(accArray.size() >= 30)
        {
            float total = 0;
            for(int i=0; i<accArray.size(); i++)
            {
                total= total + accArray.get(i);
            }
            //Log.e("SleepTAG",""+total/accArray.size());
            isAccSatisfy = checkdifference(total/accArray.size(),accArray);

            accArray.clear();
        }
        accArray.add(f);
    }

    public static void setisCharge(boolean b)
    {
        isCharge=b;
    }

    public static void setAmbientData(float lux)
    {
        ambientData = lux;
    }

    /**
     * check the accelerometer if it is moving or not moving
      */
    public static boolean checkdifference(float f,ArrayList<Float> accArray){
        for(int i=0;i<accArray.size();i++)
        {
            if(accArray.get(i)>f+ACC_FIX||accArray.get(i)<f-ACC_FIX){
                return false;
            }
        }

        return true;
    }

    public static void setWEAR_ACC(boolean f)
    {
        IS_WEAR_ACC= f;
    }

    public static void setWEAR_AMB(boolean i)
    {
        IS_WEAR_AMB=i;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("SleepStop-Logging", "--- onDestroy");
        // Unregister accelerometer sensor
        flag=false;
    }
}
