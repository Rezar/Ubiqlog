package com.ubiqlog.sensors;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.ubiqlog.core.DataAcquisitor;
import com.ubiqlog.utils.JsonEncodeDecode;

import java.util.Date;

/**
 * Created by Ping_He on 2016/3/30.
 */
public class ApplicationAccessibilityService extends AccessibilityService {

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        //Configure these here for compatibility with API 13 and below.
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

        if (Build.VERSION.SDK_INT >= 16)
            //Just in case this helps
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;

        setServiceInfo(config);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

            String pname = event.getPackageName().toString();

            ApplicationInfo appInfo = tryGetApp(pname);
            boolean isApp = appInfo != null;
            if (isApp)
            {
                //Toast.makeText(getBaseContext(), "CurrentApp:" + getPackageManager().getApplicationLabel(appInfo), Toast.LENGTH_SHORT).show();
                //Log.i("LOGAPP", "CurrentApp:" + getPackageManager().getApplicationLabel(appInfo));
                String encoded = JsonEncodeDecode.EncodeApplication(""+getPackageManager().getApplicationLabel(appInfo),new Date());
                DataAcquisitor.dataBuff.add(encoded);
                Log.e("APPLICATIONLOG", encoded);
            }

        }
    }

    private ApplicationInfo tryGetApp(String  packagename) {
        try {
            return getPackageManager().getApplicationInfo(packagename,0);
            //return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Override
    public void onInterrupt() {}
}



