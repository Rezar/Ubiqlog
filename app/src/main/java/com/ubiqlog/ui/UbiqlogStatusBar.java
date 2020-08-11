package com.ubiqlog.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

/**
 * Deprecated
 * @author Reza Rawassizadeh
 *
 */
public class UbiqlogStatusBar extends Service {

	private NotificationManager notiman;
	private static final int HELLO_ID = 1;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		notiman = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

	}

	@Override
	public void onStart(Intent intent, int startId) {
		int icon = R.drawable.icon_outline; // icon from resources
		CharSequence tickerText = "Hello"; // ticker-text
		long when = System.currentTimeMillis(); // notification time
		Context context = getApplicationContext(); // application Context
		CharSequence contentTitle = "UbiqLog notification"; // expanded message
															// title
		CharSequence contentText = "Hello !"; // expanded message text

		Intent notificationIntent = new Intent(this, MainUI.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);


		//Notification notification = new Notification(icon, tickerText, when);
		//notification.setLatestEventInfo(context, contentTitle, contentText,contentIntent);
		Notification notification = new NotificationCompat.Builder(this)
				.setSmallIcon(icon)
				.setContentTitle(contentTitle)
				.setContentText(contentText)
				.build();

		notiman.notify(HELLO_ID, notification);
	}

	@Override
	public void onDestroy() {
		notiman.cancel(HELLO_ID);
	}

}
