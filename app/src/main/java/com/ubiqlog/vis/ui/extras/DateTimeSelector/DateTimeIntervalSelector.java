package com.ubiqlog.vis.ui.extras.DateTimeSelector;

import java.util.Date;

import android.R.drawable;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.ImageView.ScaleType;

import com.ubiqlog.vis.ui.extras.DateTimeSelector.DateTimePickerDialog.Type;

/**
 * A simple framelayout that lets the user select a datetime interval.
 * 
 * @author Victor Gugonatu
 * @date 10.2010
 * @version 1.0
 */
public class DateTimeIntervalSelector extends FrameLayout {

	/**
	 * The callback used to indicate that the data has been changed.
	 */
	public interface OnDataChangedListener {

		public abstract void onDataChanged(Date startDate, Date endDate,
				Type type);
	}

	public final DateTimeSelector startSel;
	public final DateTimeSelector endSel;
	public Date mStartDate;
	public Date mEndDate;
	public final Type mType;
	public final boolean is24HourDay;
	public final OnDataChangedListener mcallBack;
	public final ImageButton img_Refresh;
	private View.OnClickListener _toChanged;

	/**
	 * @param context
	 *            The context the dialog is to run in.
	 * @param startDate
	 *            The initial start date to be displayed.
	 * @param endDate
	 *            The initial end date to be displayed.
	 * @param type
	 *            The type of the selector. See
	 *            {@link DateTimePickerDialog.Type DateTimePickerDialog.Type}
	 * @param callBack
	 *            How the parent is notified that the date is changed
	 * @param toChanged
	 *            How the parent is notified that the end date is changed
	 * @param startDateBtn
	 *            The text to be displayed on the start button
	 * @param endDateBtn
	 *            The text to be displayed on the end button
	 * @param is24HourView
	 *            Whether this is a 24 hour view, or AM/PM.
	 * @param showRefresh
	 *            Should refresh button be shown.
	 */
	public DateTimeIntervalSelector(Context context, Date startDate,
			Date endDate, Type type, OnDataChangedListener callBack,
			View.OnClickListener toChanged, String startDateBtn,
			String endDateBtn, boolean is24Hour, boolean showRefresh) {

		super(context);

		_toChanged = toChanged;
		mType = type;

		img_Refresh = new ImageButton(context);

		// add refresh button if needed
		if (showRefresh) {

			img_Refresh.setImageResource(drawable.ic_popup_sync);
			img_Refresh.setScaleType(ScaleType.FIT_XY);
			img_Refresh.setLayoutParams(new TableRow.LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.FILL_PARENT));
		}

		is24HourDay = is24Hour;
		mStartDate = startDate;
		mEndDate = endDate;
		mcallBack = callBack;

		startSel = new DateTimeSelector(context,
				startDateTimeSelectorDataChanged, startDate, mType,
				is24HourDay, startDateBtn);
		endSel = new DateTimeSelector(context, endDateTimeSelectorDataChanged,
				endDate, mType, is24HourDay, endDateBtn);

		TableRow layout = new TableRow(context);

		layout.setLayoutParams(new TableLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

		layout.addView(startSel);
		layout.addView(endSel);
		if (showRefresh) {
			layout.addView(img_Refresh);
		}

		TableLayout ll = new TableLayout(context);

		ll.setLayoutParams(new TableLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		ll.setColumnStretchable(0, true);
		ll.setColumnStretchable(1, true);
		ll.setColumnStretchable(2, true);
		ll.setGravity(Gravity.CENTER);
		ll.addView(layout);

		addView(ll);

	}

	private DateTimeSelector.OnDataChangedListener startDateTimeSelectorDataChanged = new DateTimeSelector.OnDataChangedListener() {

		public void onDataChanged(Date date, Type type) {
			mStartDate = date;
			if (mcallBack != null) {
				mcallBack.onDataChanged(mStartDate, mEndDate, mType);
			}

		}
	};

	private DateTimeSelector.OnDataChangedListener endDateTimeSelectorDataChanged = new DateTimeSelector.OnDataChangedListener() {

		public void onDataChanged(Date date, Type type) {

			mEndDate = date;

			if (mcallBack != null) {
				mcallBack.onDataChanged(mStartDate, mEndDate, mType);
			}
			if (_toChanged != null) {
				_toChanged.onClick(null);
			}

		}
	};

	public Date getStartDate() {
		return mStartDate;
	}

	public Date getEndDate() {
		return mEndDate;
	}

	public Type getType() {
		return mType;
	}

}
