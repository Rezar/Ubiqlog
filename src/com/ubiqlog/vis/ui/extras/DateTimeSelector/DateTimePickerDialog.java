package com.ubiqlog.vis.ui.extras.DateTimeSelector;


import com.ubiqlog.ui.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TimePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TimePicker.OnTimeChangedListener;


/**
 * A simple dialog containing an {@link DateTimeSelector DateTimeSelector}
 * 
 * @author Victor Gugonatu
 * @date 10.2010
 * @version 1.0
 */
public class DateTimePickerDialog extends AlertDialog implements
		Dialog.OnClickListener, OnDateChangedListener, OnTimeChangedListener {

	/**
	 * The callback used to indicate the user is done filling in the data. *
	 * Refers to {@link DateTimePickerDialog DateTimePickerDialog}
	 */
	public interface DateTimePickerDialogOnDataChangedListener {
		public abstract void onDataChanged(DatePicker datePicker,
				TimePicker timePicker, int year, int month, int dayOfMonth,
				int hourOfDay, int minute);
	}

	/**
	 * The type of the {@link DateTimePickerDialog DateTimePickerDialog}.
	 */
	public enum Type {
		DATE, TIME, DATETIME
	}

	private static final String YEAR = "year";
	private static final String MONTH = "month";
	private static final String DAY = "day";
	private static final String HOUR = "hour";
	private static final String MINUTE = "minute";
	private static final String IS_24_HOUR = "is24hour";

	private int mInitialYear;
	private int mInitialMonth;
	private int mInitialDay;
	private int mInitialHourOfDay;
	private int mInitialMinute;
	private boolean mIs24HourView;

	private final DatePicker mDatePicker;
	private final DateTimePickerDialogOnDataChangedListener mCallBack;
	private final TimePicker mTimePicker;

	/**
	 * @param context
	 *            The context the dialog is to run in.
	 * @param callBack
	 *            How the parent is notified that the date and time is set.
	 * @param year
	 *            The initial year of the dialog.
	 * @param monthOfYear
	 *            The initial month of the dialog.
	 * @param dayOfMonth
	 *            The initial day of the dialog.
	 * @param hourOfDay
	 *            The initial hour.
	 * @param minute
	 *            The initial minute.
	 * @param is24HourView
	 *            Whether this is a 24 hour view, or AM/PM.
	 */
	public DateTimePickerDialog(Context context,
			DateTimePickerDialogOnDataChangedListener callBack, int year,
			int monthOfYear, int dayOfMonth, int hourOfDay, int minute,
			boolean is24HourView) {

		this(context, android.R.style.Theme_Translucent_NoTitleBar, callBack,
				year, monthOfYear, dayOfMonth, hourOfDay, minute, is24HourView);
	}

	/**
	 * @param context
	 *            The context the dialog is to run in.
	 * @param theme
	 *            the theme to apply to this dialog
	 * @param callBack
	 *            How the parent is notified that the date and time is set.
	 * @param year
	 *            The initial year of the dialog.
	 * @param monthOfYear
	 *            The initial month of the dialog.
	 * @param dayOfMonth
	 *            The initial day of the dialog.
	 * @param hourOfDay
	 *            The initial hour.
	 * @param minute
	 *            The initial minute.
	 * @param is24HourView
	 *            Whether this is a 24 hour view, or AM/PM.
	 */
	public DateTimePickerDialog(Context context, int theme,
			DateTimePickerDialogOnDataChangedListener callBack, int year,
			int monthOfYear, int dayOfMonth, int hourOfDay, int minute,
			boolean is24HourView) {

		super(context, theme);

		mCallBack = callBack;
		mInitialYear = year;
		mInitialMonth = monthOfYear;
		mInitialDay = dayOfMonth;
		mInitialHourOfDay = hourOfDay;
		mInitialMinute = minute;
		mIs24HourView = is24HourView;

		setTitle(context.getString(R.string.Vis_DateTimePickerTitle));

		setButton(DialogInterface.BUTTON_POSITIVE, context.getText(R.string.Vis_Ok), this);
		setButton(DialogInterface.BUTTON_NEGATIVE, context.getText(R.string.Vis_Cancel), (OnClickListener) null);

		mDatePicker = new DatePicker(this.getContext());
		mDatePicker.init(mInitialYear, mInitialMonth, mInitialDay, this);

		mTimePicker = new TimePicker(this.getContext());
		mTimePicker.setCurrentHour(mInitialHourOfDay);
		mTimePicker.setCurrentMinute(mInitialMinute);
		mTimePicker.setIs24HourView(mIs24HourView);
		mTimePicker.setOnTimeChangedListener(this);

		LinearLayout listLay = new TableLayout(super.getContext());
		listLay.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT));
		listLay.setPadding(0, 0, 0, 0);
		listLay.setVerticalFadingEdgeEnabled(false);
		listLay.setVerticalScrollBarEnabled(false);

		listLay.setOrientation(LinearLayout.VERTICAL);

		listLay.addView(mDatePicker);
		listLay.addView(mTimePicker);

		this.setView(listLay, 0, 0, 0, 0);

	}

	public void updateDateTime(int year, int monthOfYear, int dayOfMonth,
			int hourOfDay, int minutOfHour) {
		mInitialYear = year;
		mInitialMonth = monthOfYear;
		mInitialDay = dayOfMonth;
		mDatePicker.updateDate(year, monthOfYear, dayOfMonth);

		mTimePicker.setCurrentHour(hourOfDay);
		mTimePicker.setCurrentMinute(minutOfHour);
	}

	public void onDateChanged(DatePicker view, int year, int month, int day) {

	}

	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

	}

	public void onClick(DialogInterface dialog, int which) {
		if (mCallBack != null) {
			mDatePicker.clearFocus();
			mTimePicker.clearFocus();

			mCallBack.onDataChanged(mDatePicker, mTimePicker, mDatePicker
					.getYear(), mDatePicker.getMonth(), mDatePicker
					.getDayOfMonth(), mTimePicker.getCurrentHour(), mTimePicker
					.getCurrentMinute());
		}
	}

	/**
	 * save instance
	 */
	@Override
	public Bundle onSaveInstanceState() {
		Bundle state = super.onSaveInstanceState();
		state.putInt(YEAR, mDatePicker.getYear());
		state.putInt(MONTH, mDatePicker.getMonth());
		state.putInt(DAY, mDatePicker.getDayOfMonth());
		state.putInt(HOUR, mTimePicker.getCurrentHour());
		state.putInt(MINUTE, mTimePicker.getCurrentMinute());
		state.putBoolean(IS_24_HOUR, mTimePicker.is24HourView());

		return state;
	}

	/**
	 * load instance
	 */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int year = savedInstanceState.getInt(YEAR);
		int month = savedInstanceState.getInt(MONTH);
		int day = savedInstanceState.getInt(DAY);
		mDatePicker.init(year, month, day, this);

		int hour = savedInstanceState.getInt(HOUR);
		int minute = savedInstanceState.getInt(MINUTE);
		mTimePicker.setCurrentHour(hour);
		mTimePicker.setCurrentMinute(minute);
		mTimePicker.setIs24HourView(savedInstanceState.getBoolean(IS_24_HOUR));
		mTimePicker.setOnTimeChangedListener(this);

	}

}
