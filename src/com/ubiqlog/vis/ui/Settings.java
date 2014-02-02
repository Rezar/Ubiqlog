package com.ubiqlog.vis.ui;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ubiqlog.ui.R;

/**
 * Settings
 * 
 * @author Victor Gugonatu
 * @date 12.2010
 * @version 1.0
 */
public class Settings extends FrameLayout {

	public interface OnClosedListener {

		public abstract void onClosed();
	}
	
	private EditText locationTimeFrame_EditText;
	private EditText callTimeFrame_EditText;
	private EditText smsTimeFrame_EditText;

	private Context _context = null;
	private OnClosedListener _closed = null;
	
	public Settings(Context context,OnClosedListener closed) {
		super(context);
		_context = context;
		_closed = closed;
		
		LinearLayout.LayoutParams lineLayParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		LinearLayout listLay = new TableLayout(_context);
		listLay.setOrientation(LinearLayout.VERTICAL);

		TextView locationTimeFrame_TextView = new TextView(_context);
		locationTimeFrame_TextView
				.setText(R.string.Vis_settings_location_timeframe);
		locationTimeFrame_TextView.setTextColor(Color.BLACK);
		locationTimeFrame_TextView.setGravity(Gravity.LEFT);
		locationTimeFrame_EditText = new EditText(_context);
		locationTimeFrame_EditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		locationTimeFrame_EditText.setText(String
				.valueOf(com.ubiqlog.vis.common.Settings.location_timeFrame));
		locationTimeFrame_EditText.setSingleLine(true);

		listLay.addView(locationTimeFrame_TextView, lineLayParams);
		listLay.addView(locationTimeFrame_EditText, lineLayParams);

		TextView callTimeFrame_TextView = new TextView(_context);
		callTimeFrame_TextView.setText(R.string.Vis_settings_call_timeframe);
		callTimeFrame_TextView.setTextColor(Color.BLACK);
		callTimeFrame_TextView.setGravity(Gravity.LEFT);
		callTimeFrame_EditText = new EditText(_context);
		callTimeFrame_EditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		callTimeFrame_EditText.setText(String
				.valueOf(com.ubiqlog.vis.common.Settings.call_timeFrame));
		callTimeFrame_EditText.setSingleLine(true);

		listLay.addView(callTimeFrame_TextView, lineLayParams);
		listLay.addView(callTimeFrame_EditText, lineLayParams);

		TextView smsTimeFrame_TextView = new TextView(_context);
		smsTimeFrame_TextView.setText(R.string.Vis_settings_sms_timeframe);
		smsTimeFrame_TextView.setTextColor(Color.BLACK);
		smsTimeFrame_TextView.setGravity(Gravity.LEFT);
		smsTimeFrame_EditText = new EditText(_context);
		smsTimeFrame_EditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		smsTimeFrame_EditText.setText(String
				.valueOf(com.ubiqlog.vis.common.Settings.sms_timeFrame));
		smsTimeFrame_EditText.setSingleLine(true);

		listLay.addView(smsTimeFrame_TextView, lineLayParams);
		listLay.addView(smsTimeFrame_EditText, lineLayParams);

		Button btnSave = new Button(_context);
		btnSave.setText(R.string.Vis_settings_save);
		btnSave.setOnClickListener(btnSaveListener);

		Button btnCancel = new Button(_context);
		btnCancel.setText(R.string.Vis_settings_cancel);
		btnCancel.setOnClickListener(btnCancelListener);

		TableLayout recordLay = new TableLayout(_context);
		recordLay.setBaselineAligned(true);
		recordLay.setStretchAllColumns(true);
		TableRow.LayoutParams rowLayoutSingle = new TableRow.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		TableRow trow1 = new TableRow(_context);
		trow1.addView(btnSave, rowLayoutSingle);
		trow1.addView(btnCancel, rowLayoutSingle);
		recordLay.addView(trow1, rowLayoutSingle);

		listLay.addView(recordLay, lineLayParams);

		ScrollView sView = new ScrollView(_context);

		sView.addView(listLay);
		sView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_INSET);

		addView(sView);
	}

	

	private View.OnClickListener btnSaveListener = new View.OnClickListener() {
		public void onClick(View v) {
			com.ubiqlog.vis.common.Settings.savePreferences(_context, Integer
					.parseInt(locationTimeFrame_EditText.getText().toString()),
					Integer.parseInt(callTimeFrame_EditText.getText()
							.toString()), Integer
							.parseInt(smsTimeFrame_EditText.getText()
									.toString()));
			finish();
		}
	};
	private View.OnClickListener btnCancelListener = new View.OnClickListener() {
		public void onClick(View v) {
			finish();
		}
	};
	

	private void finish() {
		_closed.onClosed();
	}


}
