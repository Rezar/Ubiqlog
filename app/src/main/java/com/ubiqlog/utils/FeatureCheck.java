package com.ubiqlog.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by AP on 12/22/2015.
 */
public class FeatureCheck {
    public static boolean hasBluetoothFeature(Context context) {
        boolean status = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
        //Log.d("Features > Bluetooth: ", String.valueOf(status));
        return status;
    }

    public static boolean hasLightFeature(Context context) {
        boolean status = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_LIGHT);
        //Log.d("Features > Ambient Light Sensor: ", String.valueOf(status));
        return status;
    }

    public static boolean hasHeartrateFeature(Context context) {
        boolean status = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_HEART_RATE);
        //Log.d("Features > Heartrate Sensor: ", String.valueOf(status));
        return status;
    }
}
