package com.ubiqlog.vis.ui.extras.DateTimeSelector;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ubiqlog.vis.ui.extras.DateTimeSelector.DateTimePickerDialog.Type;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * A framelayout displaying a button with the a date on it. When the button is
 * pressed a DateTimePicker dialog will be displayed and the user can change the
 * date and/or time
 * 
 * @author Victor Gugonatu
 * @date 10.2010
 * @version 1.0
 */
public class DateTimeSelector extends FrameLayout implements
		View.OnClickListener {

	/**
	 * The callback used to indicate the user is done filling in the data.
	 * Refers to {@link DateTimeSelector DateTimeSelector}
	 */
	public interface OnDataChangedListener {

		public abstract void onDataChanged(Date date, Type type);
	}

	private Date mInitialDate;
	private boolean mIs24HourView;
	private Type mType;

	private final OnDataChangedListener mCallBack;
	private final Button mButton;
	private final TextView mTextView;
	private final TableRow mCustomLayout;
	private final String mTitle;

	/**
	 * @param context
	 *            The context the framelayout belongs to
	 * @param callBack
	 *            How the parent is notified that the date and time is set.
	 * 
	 * @param date
	 *            The initial date to be displayed.
	 * @param type
	 *            The type of the selector. See
	 *            {@link DateTimePickerDialog.Type DateTimePickerDialog.Type}
	 * @param is24HourView
	 *            Whether this is a 24 hour view, or AM/PM.
	 * @param title
	 *            The text that should be shown on the button.
	 */
	public DateTimeSelector(Context context, OnDataChangedListener callback,
			Date date, Type type, boolean is24Hour, String title) {
		super(context);

		mTitle = title;
		mCallBack = callback;
		mIs24HourView = is24Hour;

		mCustomLayout = new TableRow(context);
		mCustomLayout.setLayoutParams(new TableRow.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

		mInitialDate = date;
		mType = type;

		mButton = new Button(context);
		mButton.setOnClickListener(this);

		mTextView = new TextView(context);
		updateText();

		TextView tv = new TextView(context);
		tv.setText(title);

		LinearLayout lay = new LinearLayout(context);
		lay.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT));
		lay.setOrientation(LinearLayout.VERTICAL);

		mButton.setLayoutParams(new TableRow.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT));
		mTextView.setLayoutParams(new TableRow.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		mCustomLayout.setLayoutParams(new TableLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

		lay.setPadding(1, 1, 1, 1);
		tv.setBackgroundColor(Color.LTGRAY);
		mCustomLayout.setGravity(Gravity.CENTER);

		tv.setGravity(Gravity.CENTER);

		lay.addView(mCustomLayout);
		lay.setGravity(Gravity.CENTER);
		addView(mButton);

	}

	public Date getDate() {
		return mInitialDate;
	}

	/**
	 * opens the corresponding dialog (Date, Time or DateTime dialog)
	 */
	public void onClick(View v) {
		Dialog d = null;
		switch (mType) {
		case DATE:
			d = new DatePickerDialog(this.getContext(), dateSetListener,
					mInitialDate.getYear() + 1900, mInitialDate.getMonth(),
					mInitialDate.getDate());

			break;
		case DATETIME:
			d = new DateTimePickerDialog(this.getContext(), saveListener,
					mInitialDate.getYear() + 1900, mInitialDate.getMonth(),
					mInitialDate.getDate(), mInitialDate.getHours(),
					mInitialDate.getMinutes(), mIs24HourView);

			break;
		case TIME:
			d = new TimePickerDialog(this.getContext(), timeSetListener,
					mInitialDate.getHours(), mInitialDate.getMinutes(),
					mIs24HourView);

			break;
		default:
			break;
		}

		// should never be null
		if (d != null) {
			d.show();
		}
	}

	private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

			mInitialDate.setHours(hourOfDay);
			mInitialDate.setMinutes(minute);

			updateText();
			mButton.clearFocus();
			mTextView.clearFocus();
			mCustomLayout.clearFocus();
			if (mCallBack != null) {

				mCallBack.onDataChanged(mInitialDate, mType);
			}

		}
	};
	private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			mInitialDate.setYear(year - 1900);
			mInitialDate.setMonth(monthOfYear);
			mInitialDate.setDate(dayOfMonth);

			updateText();
			mButton.clearFocus();
			mTextView.clearFocus();
			mCustomLayout.clearFocus();
			if (mCallBack != null) {

				mCallBack.onDataChanged(mInitialDate, mType);
			}

		}
	};

	private DateTimePickerDialog.DateTimePickerDialogOnDataChangedListener saveListener = new DateTimePickerDialog.DateTimePickerDialogOnDataChangedListener() {

		public void onDataChanged(DatePicker datePicker, TimePicker timePicker,
				int year, int month, int dayOfMonth, int hourOfDay, int minute) {

			mInitialDate.setYear(year - 1900);
			mInitialDate.setMonth(month);
			mInitialDate.setDate(dayOfMonth);
			mInitialDate.setHours(hourOfDay);
			mInitialDate.setMinutes(minute);

			updateText();
			mButton.clearFocus();
			mTextView.clearFocus();
			mCustomLayout.clearFocus();
			if (mCallBack != null) {

				mCallBack.onDataChanged(mInitialDate, mType);
			}
		}
	};

	private void updateText() {

		String datenow = "";

		SimpleDateFormat dateformatDATE = new SimpleDateFormat("dd.MM.yyyy");
		new SimpleDateFormat("dd.MM.yyyy HH:mm");
		SimpleDateFormat dateformatTIME = new SimpleDateFormat("HH:mm");

		switch (mType) {
		case DATE:

			datenow = java.text.DateFormat.getDateInstance(
					java.text.DateFormat.SHORT).format(mInitialDate);
			datenow = dateformatDATE.format(mInitialDate);

			break;

		case TIME:

			datenow = dateformatTIME.format(mInitialDate);

			break;

		case DATETIME:

			datenow = java.text.DateFormat.getDateInstance(
					java.text.DateFormat.LONG).format(mInitialDate);

			datenow = dateformatDATE.format(mInitialDate) + " "
					+ dateformatTIME.format(mInitialDate);

			break;

		default:
			break;
		}

		mButton.setText(mTitle + "\n" + datenow);
	}

}