package com.ubiqlog.annotation;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class SMSAnnotation implements Annotation {

	Context ctx;

	public SMSAnnotation(Context con) {
		ctx = con;
	}

	public String annotate(String input) {
		ContentResolver cr = ctx.getContentResolver();
		Uri phoneUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, input);
		Cursor phoneCur = cr.query(phoneUri, new String[] { ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);
		int colindDispName = phoneCur.getColumnIndex(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME);

		String result = new String();
		phoneCur.moveToFirst();

		for (int i = 0; i < phoneCur.getCount(); i++) {
			//Log.e("SMSAnnotation", "--------------------Number:"+phoneCur.getString(colindDispName)+ "---DispName:"+ phoneCur.getString(colindDispName));
			result = phoneCur.getString(colindDispName);
			phoneCur.moveToNext();
		}
		phoneCur.close();
		return result;
	}
}
