package com.ubiqlog.vis.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.CallLog;
import android.widget.ImageView;

/**
 * Utils
 * 
 * @author Victor Gugonatu
 * @date 10.2010
 * @version 1.0
 */
public final class Utils {

	private static Utils _utils;

	public static Utils Instance() {
		_utils = new Utils();
		return _utils;
	}

	// check if the phone is connected to the internet
	public Boolean hasInternetConnection(Context _context) {
		ConnectivityManager conMgr = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = conMgr.getActiveNetworkInfo();

		return info != null && info.isAvailable() && info.isConnected();

	}

	// returns the time in user friendly format
	public String getTime(long _time, Boolean withZero) {

		String sTime = "";
		if (_time == 0) {
			if (withZero)
				sTime = "0 sec";
			else
				sTime = "";
		} else if (_time < 60) {
			sTime = _time + " sec";
		} else if (_time < 3600) {
			sTime = _time / 60 + " min " + getTime(_time % 60, false);
		} else if (_time < 86400) {
			sTime = _time / 3600 + " h " + getTime(_time % 3600, false);
		} else {
			sTime = _time / 86400 + " days " + getTime(_time % 86400, false);
		}
		return sTime;
	}

	// returns the call type in user friendly format
	public String getCallType(int callType) {
		String toReturn = "";

		switch (callType) {
		case CallLog.Calls.INCOMING_TYPE:
			toReturn = "INCOMING CALL";
			break;
		case CallLog.Calls.MISSED_TYPE:
			toReturn = "MISSED CALL";
			break;
		case CallLog.Calls.OUTGOING_TYPE:
			toReturn = "OUTGOING CALL";
			break;

		default:
			break;
		}

		return toReturn;
	}

	// returns the sms type in user friendly format
	public String getSmsType(int callType) {
		String toReturn = "";

		switch (callType) {
		case 1:
			toReturn = "RECEIVED";
			break;
		case 2:
			toReturn = "SENT";
			break;
		default:
			break;
		}

		return toReturn;
	}

	public ImageView createImageView(Context activity, int iconWidth,
			int iconHeight, int imageRes) {
		ImageView icon = new ImageView(activity);
		icon.setAdjustViewBounds(true);
		icon.setScaleType(ImageView.ScaleType.FIT_CENTER);

		if (iconHeight != -1) {
			icon.setMaxHeight(iconHeight);
		}
		if (iconWidth != -1) {
			icon.setMaxWidth(iconWidth);
		}

		if (imageRes != -1) {
			icon.setImageResource(imageRes);
		}
		return icon;
	}

}
