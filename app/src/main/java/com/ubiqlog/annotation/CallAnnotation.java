package com.ubiqlog.annotation;

import android.database.Cursor;
import android.content.*;

import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

public class CallAnnotation implements Annotation {
	Context ctx;

	public CallAnnotation(Context con) {
		ctx = con;
	}

	public String annotate(String input) {
		ContentResolver cr = ctx.getContentResolver();
		Uri phoneUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, input);
		Cursor phoneCur = cr.query(phoneUri, new String[] { PhoneLookup.NUMBER,PhoneLookup.DISPLAY_NAME }, null, null, null);
		int colindDispName = phoneCur.getColumnIndex(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME);
		int colindNum = phoneCur.getColumnIndex(android.provider.ContactsContract.PhoneLookup.NUMBER);

		String result = new String();
		phoneCur.moveToFirst();

		for (int i = 0; i < phoneCur.getCount(); i++) {
			 //Log.e("CallAnnotation", "--------------------Number:"+phoneCur.getString(colindDispName) + "---DispName:"+ phoneCur.getString(colindDispName));
			result = phoneCur.getString(colindDispName);
			phoneCur.moveToNext();
		}
		phoneCur.close();

		return result;
	}

}
