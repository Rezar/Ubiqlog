package com.ubiqlog.ui.extras.controls;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A simple framelayout that lets the user select an interval in minutes and
 * seconds.
 * 
 * @author Victor Gugonatu
 * @date 08.2012
 * @version 1.0
 */
public class IntervalSelector extends FrameLayout {

	private EditText editText_min = null;
	private EditText editText_sec = null;

	public IntervalSelector(Context context, int milliseconds) {

		super(context);
//		Log.i("DDDDD", "Ms: " + milliseconds);
		int seconds = milliseconds / 1000;

		int minutes = seconds / 60;
		seconds = seconds % 60;

		LinearLayout layout = new LinearLayout(context);
		layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		editText_min = new EditText(context);
		TextView textView_min = new TextView(context);

		editText_min.setInputType(InputType.TYPE_CLASS_NUMBER);

		editText_min.setLines(1);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 2);
		params.setMargins(10, 0, 10, 0);

		editText_min.setLayoutParams(params);

		textView_min.setText("min");

		editText_sec = new EditText(context);
		TextView textView_sec = new TextView(context);

		editText_sec.setLayoutParams(params);

		editText_sec.setInputType(InputType.TYPE_CLASS_NUMBER);
		editText_sec.setLines(1);

		textView_sec.setText("sec");

		layout.addView(editText_min);
		layout.addView(textView_min);

		layout.addView(editText_sec);
		layout.addView(textView_sec);

		layout.setPadding(0, 10, 10, 10);
		addView(layout);

		editText_min.setText(Integer.toString(minutes));
		editText_sec.setText(Integer.toString(seconds));

	}

	public int getInterval() {

		int minutes = 0;
		try {
			minutes = Integer.parseInt(editText_min.getText().toString());
		} catch (NumberFormatException f) {
		}

		int seconds = 0;

		try {
			seconds = Integer.parseInt(editText_sec.getText().toString());
		} catch (NumberFormatException f) {
		}

		return minutes * 60 * 1000 + seconds * 1000;
	}

}