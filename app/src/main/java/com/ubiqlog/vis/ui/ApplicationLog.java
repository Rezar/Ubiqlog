package com.ubiqlog.vis.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.ubiqlog.ui.R;
import com.ubiqlog.vis.common.Settings;
import com.ubiqlog.vis.ui.extras.ControlBar;
import com.ubiqlog.vis.extras.search.Searcher;
import com.ubiqlog.vis.utils.UserFriendlyException;
import com.ubiqlog.vis.utils.Utils;

/**
 * ApplicationLog activity
 * 
 * @author Victor Gugonatu
 * @date 10.2010
 * @version 1.0
 */
public class ApplicationLog extends Activity {

	/**
	 * custom adapter for displaying the data
	 */
	class ApplicationDataListAdapter extends BaseAdapter {

		/** holds all the application data */
		private List<ApplicationInfo> _data = new ArrayList<ApplicationInfo>();

		/** holds the currently selected position */
		private int _selectedIndex;

		private Context _context;

		/**
		 * create the model-view object that will control the listview
		 * 
		 * @param context
		 *            activity that creates this
		 * @param data
		 *            the data that will be displayed
		 */
		public ApplicationDataListAdapter(final Context context,
				ArrayList<ApplicationInfo> data) {

			// save the activity/context ref
			_context = context;

			// load some data into the model
			_data = data == null ? new ArrayList<ApplicationInfo>() : data;

		}

		/** returns all the items in the _data table */
		public int getCount() {
			return _data.size();
		}

		public Object getItem(int i) {
			Object retval = _data.get(i);
			return retval;
		}

		/** returns the unique id for the given index, which is just the index */
		public long getItemId(int i) {
			return i;
		}

		/**
		 * called when item in listview is selected - fires a model changed
		 * event
		 * 
		 * @param index
		 *            index of item selected in listview. if -1 then it's
		 *            unselected. if the index is already selected it will be
		 *            unselected
		 */
		public void setSelected(int index) {

			if (index == -1) {
				// unselected
			} else {
				// selected index...
			}
			if (_selectedIndex == index)
				_selectedIndex = -1;
			else
				_selectedIndex = index;

			// notify the model that the data has changed, need to update the
			// view
			notifyDataSetChanged();

		}

		public View getView(int index, View cellRenderer, ViewGroup parent) {

			CellRendererView cellRendererView = null;

			if (cellRenderer == null) {
				// create the cell renderer
				cellRendererView = new CellRendererView();
			} else {
				cellRendererView = (CellRendererView) cellRenderer;
			}

			// update the cell renderer, and handle selection state
			cellRendererView.display(index, _selectedIndex == index);

			return cellRendererView;

		}

		/**
		 * this class is responsible for rendering the data in the model, given
		 * the selection state
		 */
		private class CellRendererView extends TableLayout {

			/**
			 * custom progressbar -> displays some text on it
			 */
			class TextProgressBar extends ProgressBar {
				private String text;
				private Paint textPaint;

				public TextProgressBar(Context context) {
					super(context);
					Initialise();
				}

				public TextProgressBar(Context context, AttributeSet attrs) {
					super(context, attrs);
					Initialise();
				}
				private void Initialise() {
					textPaint = new Paint();
					setText("");
					setTextColor(Color.WHITE);
			   }
			   public TextProgressBar(Context context, AttributeSet attrs,int defStyle) {
					super(context, attrs, defStyle);
					Initialise();
				}
				
				@Override
				protected synchronized void onDraw(Canvas canvas) {

					// First draw the regular progress bar, then custom draw the
					// text
					super.onDraw(canvas);
					Rect bounds = new Rect();
					textPaint.getTextBounds(text, 0, text.length(), bounds);
					int x = getWidth() / 2 - bounds.centerX();
					int y = getHeight() / 2 - bounds.centerY();
					canvas.drawText(text, x, y, textPaint);
				}

				public synchronized void setText(String text) {
					this.text = text;
					drawableStateChanged();
				}

				public void setTextColor(int color) {
					textPaint.setColor(color);
					drawableStateChanged();
				}
			}

			// ui stuff
			private TextView _lblName;
			private ImageView _lblIcon;
			private TextProgressBar _progressBar;
			private TextView _accessedTime;
			private TextView _longestTime;
			private TextView _shortestTime;
			private TextView _daynightTime;
			private LinearLayout _table;
			TextView _times_text;

			public CellRendererView() {

				super(_context);

				_createUI();

			}

			/** create the ui components */
			private void _createUI() {

				// make the 2nd col growable/wrappable
				setColumnShrinkable(1, true);
				setColumnStretchable(1, true);

				// set the padding
				setPadding(10, 10, 10, 10);

				// single row that holds icon & progressbar
				TableRow row = new TableRow(_context);
				row.setLayoutParams(new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

				LinearLayout rowV = new LinearLayout(_context);
				rowV.setOrientation(VERTICAL);
				rowV.setLayoutParams(new TableRow.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

				_lblName = new TextView(_context);

				_lblName.setLayoutParams(new TableRow.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				_lblIcon = Utils.Instance().createImageView(_context, -1, -1,
						-1);

				_lblIcon.setLayoutParams(new TableRow.LayoutParams(60,
						LayoutParams.WRAP_CONTENT));
				_lblIcon.setPadding(0, 0, 10, 0);

				_progressBar = new TextProgressBar(_context, null,
						android.R.attr.progressBarStyleHorizontal);
				_progressBar.setLayoutParams(new TableRow.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				_progressBar.clearAnimation();

				row.addView(_lblIcon);

				rowV.addView(_lblName);
				rowV.addView(_progressBar);

				row.addView(rowV);

				_table = new LinearLayout(_context);
				_table.setOrientation(VERTICAL);

				TableRow tr_accessTime = new TableRow(_context);
				tr_accessTime.setLayoutParams(new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				tr_accessTime.setGravity(Gravity.BOTTOM);

				TextView accessTime_lbl = new TextView(_context);
				accessTime_lbl.setText(_context.getResources().getString(
						R.string.Vis_Application_TimesAccessed));
				accessTime_lbl.setLayoutParams(new TableRow.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				ImageView accessTime_img = new ImageView(_context);
				accessTime_img.setImageDrawable(getResources().getDrawable(com.ubiqlog.ui.R.drawable.line_480px));

				accessTime_img.setPadding(1, 0, 1, 4);
				accessTime_img.setScaleType(ScaleType.CENTER);

				accessTime_img
						.setLayoutParams(new TableRow.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT, 1));

				_accessedTime = new TextView(_context);
				_accessedTime.setLayoutParams(new TableRow.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				tr_accessTime.addView(accessTime_lbl);
				tr_accessTime.addView(accessTime_img);
				tr_accessTime.addView(_accessedTime);

				_table.addView(tr_accessTime);

				TableRow tr_longestTime = new TableRow(_context);
				tr_longestTime.setLayoutParams(new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				tr_longestTime.setGravity(Gravity.BOTTOM);

				TextView longestTime_lbl = new TextView(_context);
				longestTime_lbl.setText(_context.getResources().getString(
						R.string.Vis_Application_LongestTime));
				longestTime_lbl.setLayoutParams(new TableRow.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				ImageView longestTime_img = new ImageView(_context);
				longestTime_img.setImageDrawable(getResources().getDrawable(
						com.ubiqlog.ui.R.drawable.line_480px));

				longestTime_img.setPadding(1, 0, 1, 4);
				longestTime_img.setScaleType(ScaleType.CENTER);

				longestTime_img
						.setLayoutParams(new TableRow.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT, 1));

				_longestTime = new TextView(_context);
				_longestTime.setLayoutParams(new TableRow.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				tr_longestTime.addView(longestTime_lbl);
				tr_longestTime.addView(longestTime_img);
				tr_longestTime.addView(_longestTime);

				_table.addView(tr_longestTime);

				TableRow tr_shortestTime = new TableRow(_context);
				tr_shortestTime.setLayoutParams(new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				tr_shortestTime.setGravity(Gravity.BOTTOM);

				TextView shortestTime_lbl = new TextView(_context);
				shortestTime_lbl.setText(_context.getResources().getString(
						R.string.Vis_Application_ShortestTime));
				shortestTime_lbl.setLayoutParams(new TableRow.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				ImageView shortestTime_img = new ImageView(_context);
				shortestTime_img.setImageDrawable(getResources().getDrawable(
						com.ubiqlog.ui.R.drawable.line_480px));

				shortestTime_img.setPadding(1, 0, 1, 4);
				shortestTime_img.setScaleType(ScaleType.CENTER);

				shortestTime_img
						.setLayoutParams(new TableRow.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT, 1));

				_shortestTime = new TextView(_context);
				_shortestTime.setLayoutParams(new TableRow.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				tr_shortestTime.addView(shortestTime_lbl);
				tr_shortestTime.addView(shortestTime_img);
				tr_shortestTime.addView(_shortestTime);

				_table.addView(tr_shortestTime);

				TableRow tr_daynightTimeTime = new TableRow(_context);
				tr_daynightTimeTime
						.setLayoutParams(new TableLayout.LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT));
				tr_daynightTimeTime.setGravity(Gravity.BOTTOM);

				TextView daynightTimeTime_lbl = new TextView(_context);
				daynightTimeTime_lbl.setText(_context.getResources().getString(
						R.string.Vis_Application_DayNight));
				daynightTimeTime_lbl.setLayoutParams(new TableRow.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				ImageView daynightTimeTime_img = new ImageView(_context);
				daynightTimeTime_img.setImageDrawable(getResources()
						.getDrawable(com.ubiqlog.ui.R.drawable.line_480px));

				daynightTimeTime_img.setPadding(1, 0, 1, 4);
				daynightTimeTime_img.setScaleType(ScaleType.CENTER);

				daynightTimeTime_img
						.setLayoutParams(new TableRow.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT, 1));

				_daynightTime = new TextView(_context);
				_daynightTime.setLayoutParams(new TableRow.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				tr_daynightTimeTime.addView(daynightTimeTime_lbl);
				tr_daynightTimeTime.addView(daynightTimeTime_img);
				tr_daynightTimeTime.addView(_daynightTime);

				_table.addView(tr_daynightTimeTime);

				TableRow tr_times = new TableRow(_context);
				tr_times.setLayoutParams(new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				tr_times.setGravity(Gravity.BOTTOM);

				_times_text = new TextView(_context);
				_times_text.setText("\n"
						+ _context.getResources().getString(
								R.string.Vis_ApplicationUse) + ":");
				_times_text.setLayoutParams(new TableRow.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				tr_times.addView(_times_text);

				_table.addView(tr_times);

				addView(row);
				addView(_table);

			}

			/** update the views with the data corresponding to selection index */
			public void display(int index, boolean selected) {

				ApplicationInfo info = (ApplicationInfo) (getItem(index));

				_lblName.setText(info.getAppName());
				_lblIcon.setImageDrawable(info.getIcon());
				_progressBar.setText(Utils.Instance().getTime(
						info.getUseTime(), true));
				_progressBar.setProgress(info.getPercent());
				_accessedTime.setText(String.valueOf(info.getTimedAccesed()));

				_daynightTime.setText(info.getDayNightUse());
				_longestTime.setText(Utils.Instance().getTime(
						info.getLongestTime(), true));
				_shortestTime.setText(Utils.Instance().getTime(
						info.getShortestTime(), true));
				_times_text.setText("\n"
						+ _context.getResources().getString(
								R.string.Vis_ApplicationUse) + ":"
						+ info.getAppTimes());
				if (selected) {
					_table.setVisibility(View.VISIBLE);
				} else {
					_table.setVisibility(View.GONE);
				}

			}

		}
	}

	ListView listview = null;
	ControlBar cBar = null;
	TextView header_view = null;
	Context _context = null;
	ProgressDialog _progressDialog = null;
	ArrayList<ApplicationInfo> apps = null;
	private int appsTotalTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		_context = this;

		LinearLayout applicationLayout = new LinearLayout(this);
		applicationLayout.setOrientation(LinearLayout.VERTICAL);

		listview = new ListView(this);

		// header_row = new TableRow(this);
		header_view = new TextView(this);
		header_view.setText(getResources().getString(
				R.string.Vis_Application_Choose));

		// bind a selection listener to the view
		listview
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parentView,
							View childView, int position, long id) {

						((ApplicationDataListAdapter) (parentView.getAdapter()))
								.setSelected(position);

					}

					public void onNothingSelected(AdapterView<?> parentView) {

						((ApplicationDataListAdapter) (parentView.getAdapter()))
								.setSelected(-1);

					}
				});
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				((ApplicationDataListAdapter) (arg0.getAdapter()))
						.setSelected(arg2);

			}
		});

		header_view.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		applicationLayout.addView(header_view);

		listview.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1));
		applicationLayout.addView(listview);

		cBar = new ControlBar(this, 1, 1, null, dataChanged, false, false);
		cBar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		applicationLayout.addView(cBar);

		setContentView(applicationLayout);

	}

	View.OnClickListener dataChanged = new View.OnClickListener() {

		public void onClick(View v) {
			refreshApplicationList();

		}

	};

	/*
	 * the search is done in another Thread so that the UI won't be blocked
	 * android closes the application if the UI is not responding for a period
	 * of time
	 */
	private class SearchThread extends Thread {

		@Override
		public void run() {

			try {
				apps = getApplicationLog(cBar.getStartDate(), cBar.getEndDate());
				Collections.sort(apps, new ApplicationInfoComparator());

				_progressDialog.dismiss();

				if (apps.size() <= 0) {

					Message msg = Message.obtain();
					msg.obj = _context.getResources().getString(
							R.string.Vis_noData);
					handler.sendMessage(msg);

				}

				handlerChangeData.sendEmptyMessage(0);

			} catch (UserFriendlyException e) {

				Message msg = Message.obtain();
				msg.obj = e.getMessage();
				handler.sendMessage(msg);
			}

		}

		private Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {

				_progressDialog.dismiss();

				AlertDialog aldlg = new AlertDialog.Builder(_context).create();
				aldlg.setMessage((String) msg.obj);
				aldlg.setButton(AlertDialog.BUTTON_NEUTRAL, _context
						.getResources().getText(R.string.Vis_Ok),
						(OnClickListener) null);
				aldlg.show();

				header_view.setVisibility(View.VISIBLE);

				listview.setAdapter(null);
			}

		};

		private Handler handlerChangeData = new Handler() {

			@Override
			public void handleMessage(Message msg) {

				header_view.setVisibility(View.GONE);

				if (apps.size() == 0) {

					header_view.setVisibility(View.VISIBLE);

					listview.setAdapter(null);

				} else {

					ApplicationDataListAdapter listModelView = new ApplicationDataListAdapter(
							_context, apps);

					listview.setAdapter(listModelView);
					listModelView.setSelected(-1);
				}

			}
		};
	}

	private void refreshApplicationList() {

		_progressDialog = ProgressDialog.show(_context, _context.getResources()
				.getText(R.string.Vis_loading), _context.getResources()
				.getText(R.string.Vis_Searching), true, false);
		SearchThread searchThread = new SearchThread();
		searchThread.start();

	}

	/*
	 * search the log files for data between from and to Date
	 */
	public ArrayList<ApplicationInfo> getApplicationLog(Date from, Date to)
			throws UserFriendlyException {
		ArrayList<ApplicationInfo> apps = new ArrayList<ApplicationInfo>();
		Searcher searcher = new Searcher();

		SimpleDateFormat dateformat = new SimpleDateFormat("M-d-yyyy");

		String dateFrom = dateformat.format(from);
		String dateTo = dateformat.format(to);

		ArrayList<String> lines = searcher.searchFolder(
				Settings.SensorApplication, dateFrom, dateTo, this);
		SimpleDateFormat dateformatLog = new SimpleDateFormat(
				"M-d-yyyy HH:mm:ss");

		if (lines != null) {
			for (String line : lines) {
				// {"Application":{"ProcessName":"com.android.browser","Time":"Mar 6 2010 6:54:10 PM"}}
				try {

					String[] entities = line.split("\\\"");
					String processName = entities[5];
					// Mar 6 2010 2:49:16 AM -> uknown locale; US locale: Mar 6,
					// 2010 2:49:16 AM

					Date dateTime = null;
					try {
						dateTime = dateformatLog.parse(entities[9]);
					} catch (Exception ex) {
						dateTime = new Date(Date.parse(entities[9].replace(",",
								"")));

					}

					if (dateTime != null && dateTime.compareTo(from) >= 0
							&& dateTime.compareTo(to) <= 0) {
						ApplicationInfo appInfo = new ApplicationInfo();
						appInfo.setProcessName(processName);

						appInfo.setIndividualTime(dateTime);
						apps.add(appInfo);
					}
				} catch (Exception exc) {
					// ignore corrupted lines
				}
			}
		}

		Collections.sort(apps);

		/*
		 * search the results and creates the applications
		 */
		ArrayList<ApplicationInfo> distinctApps = new ArrayList<ApplicationInfo>();

		String currentAppName = "";

		for (ApplicationInfo applicationInfo : apps) {

			if (currentAppName.equals(applicationInfo.getProcessName())) {
				ApplicationInfo currentApp = new ApplicationInfo();

				for (ApplicationInfo applicationInfo2 : distinctApps) {
					if (applicationInfo2.getProcessName().equals(
							applicationInfo.getProcessName())) {
						currentApp = applicationInfo2;
						break;
					}
				}

				if (currentApp.getLastToTime() != null
						&& applicationInfo.getIndividualTime().getTime()
								- currentApp.getLastToTime().getTime() > Settings.application_timeinterval) {
					currentApp.addFromTime(applicationInfo.getIndividualTime());
					currentApp.addToTime(applicationInfo.getIndividualTime());
				} else {
					currentApp.setLastToTime(applicationInfo
							.getIndividualTime());
				}
			} else {
				currentAppName = applicationInfo.getProcessName();
				Boolean found = false;
				ApplicationInfo currentApp = new ApplicationInfo();
				currentApp.setProcessName(applicationInfo.getProcessName());

				for (ApplicationInfo applicationInfo2 : distinctApps) {
					if (applicationInfo2.getProcessName().equals(
							applicationInfo.getProcessName())) {
						found = true;
						currentApp = applicationInfo2;
						break;
					}
				}

				if (found) {
					if (currentApp.getLastToTime() != null
							&& applicationInfo.getIndividualTime().getTime()
									- currentApp.getLastToTime().getTime() > Settings.application_timeinterval) {
						currentApp.addFromTime(applicationInfo
								.getIndividualTime());
						currentApp.addToTime(applicationInfo
								.getIndividualTime());
					} else {
						currentApp.setLastToTime(applicationInfo
								.getIndividualTime());
					}
				} else {
					currentApp.addFromTime(applicationInfo.getIndividualTime());
					currentApp.addToTime(applicationInfo.getIndividualTime());
				}
				if (!found) {
					distinctApps.add(currentApp);
				}

			}
		}

		appsTotalTime = 0;

		for (ApplicationInfo ap : distinctApps) {
			appsTotalTime += ap.getUseTime();
		}

		return distinctApps;
	}

	class ApplicationInfo implements Comparable<ApplicationInfo> {

		private String _pname = "";
		private String _appName = null;
		private Drawable _icon = null;
		// useTime in seconds
		private int useTime = -1;
		private Date _individualTime = null;
		private ArrayList<Date> _dateFrom = null;
		private ArrayList<Date> _dateTo = null;
		private int _longestTime = 0;
		private int _shortestTime = Integer.MAX_VALUE;
		private int _dayUse = 0;
		private int _nightUse = 0;

		public int getUseTime() {
			if (useTime < 0) {
				calculateUsedTime();
			}

			return useTime;
		}

		/*
		 * calculate total time, shortest time, longest time
		 */
		private void calculateUsedTime() {
			useTime = 0;
			for (int i = 0; i < _dateFrom.size(); i++) {
				int intervall = (int) ((_dateTo.get(i).getTime() - _dateFrom
						.get(i).getTime()) / 1000);
				if (intervall < 1)
					intervall = 1;
				if (intervall > _longestTime)
					_longestTime = intervall;
				if (intervall < _shortestTime)
					_shortestTime = intervall;
				// day use -> 07:00 -> 19:00 ; night use: 19:00 -> 07:00
				GregorianCalendar calFrom = new GregorianCalendar();
				calFrom.setTimeInMillis((_dateFrom.get(i).getTime()));

				GregorianCalendar calTo = new GregorianCalendar();
				calTo.setTimeInMillis(_dateTo.get(i).getTime());

				if (calFrom.compareTo(calTo) == 0) {
					calTo.roll(Calendar.SECOND, 1);
				}
				if (calFrom.compareTo(calTo) <= 0) {
					while (!calFrom.after(calTo)) {
						long startMilis = calFrom.getTimeInMillis();
						if (calFrom.get(Calendar.HOUR_OF_DAY) < 7) {
							calFrom.set(Calendar.HOUR_OF_DAY, 7);
							_nightUse += Math.min(calFrom.getTimeInMillis(),
									calTo.getTimeInMillis())
									- startMilis;
						} else if (calFrom.get(Calendar.HOUR_OF_DAY) < 19) {
							calFrom.set(Calendar.HOUR_OF_DAY, 19);
							_dayUse += Math.min(calFrom.getTimeInMillis(),
									calTo.getTimeInMillis())
									- startMilis;
						} else {
							calFrom.set(Calendar.HOUR_OF_DAY, 0);
							calFrom.roll(Calendar.DAY_OF_MONTH, true);
							if ((calFrom.get(Calendar.DAY_OF_MONTH)) == 1) {
								calFrom.roll(Calendar.MONTH, true);
								if ((calFrom.get(Calendar.MONTH)) == 0) {
									calFrom.roll(Calendar.YEAR, true);
								}
							}
							_nightUse += Math.min(calFrom.getTimeInMillis(),
									calTo.getTimeInMillis())
									- startMilis;
						}
					}
				}

				useTime += intervall;
			}
		}

		public int getLongestTime() {
			if (useTime < 0) {
				calculateUsedTime();
			}

			return _longestTime;

		}

		public int getShortestTime() {
			if (useTime < 0) {
				calculateUsedTime();
			}

			return _shortestTime;

		}

		public String getAppTimes() {
			String returnValue = "";
			java.text.DateFormat df = java.text.DateFormat.getDateTimeInstance(
					java.text.DateFormat.SHORT, java.text.DateFormat.MEDIUM,
					Locale.getDefault());
			for (int i = 0; i < _dateFrom.size(); i++) {
				returnValue += "\n" + df.format(_dateFrom.get(i)) + " - "
						+ df.format(_dateTo.get(i));
			}
			return returnValue;
		}

		/*
		 * calculates day/night application use
		 */
		public String getDayNightUse() {
			if (useTime < 0) {
				calculateUsedTime();
			}
			String returnValue = "";
			long daynight = _dayUse + _nightUse;
			double dayP = (((double) _dayUse / (double) daynight) * 100);
			double nightP = (((double) _nightUse / (double) daynight) * 100);
			if (dayP > nightP) {
				dayP = Math.floor(dayP);
				nightP = Math.ceil(nightP);
			} else if (dayP < nightP) {
				dayP = Math.ceil(dayP);
				nightP = Math.floor(nightP);
			} else {
				dayP = Math.floor(dayP);
				nightP = Math.floor(nightP);
			}
			returnValue = (int) dayP + "/" + (int) nightP;

			return returnValue;
		}

		public int getTimedAccesed() {
			return _dateFrom.size();
		}

		public Date getLastToTime() {
			if (_dateTo.size() > 0)
				return _dateTo.get(_dateTo.size() - 1);

			return null;
		}

		public ApplicationInfo() {
			_dateFrom = new ArrayList<Date>();
			_dateTo = new ArrayList<Date>();
		}

		public void setLastToTime(Date individualTime) {
			_dateTo.set(_dateTo.size() - 1, individualTime);

		}

		public void addToTime(Date individualTime) {
			_dateTo.add(individualTime);

		}

		public void addFromTime(Date individualTime) {
			_dateFrom.add(individualTime);

		}

		public void setIndividualTime(Date individualTime) {
			_individualTime = individualTime;
		}

		public Date getIndividualTime() {
			return _individualTime;
		}

		public void setProcessName(String pname) {
			_pname = pname;
		}

		public String getProcessName() {
			return _pname;
		}

		/*
		 * get application icon from the phone if installed
		 */
		public Drawable getIcon() {

			if (_icon == null) {
				try {
					_icon = getPackageManager().getApplicationIcon(_pname);
				} catch (NameNotFoundException e) {

					PackageManager pm = getPackageManager();
					_icon = pm.getDefaultActivityIcon();

				}
			}
			return _icon;
		}

		/*
		 * get application name from the phone if installed
		 */
		public String getAppName() {
			if (_appName == null)

			{
				try {
					_appName = getPackageManager().getApplicationInfo(
							_pname,
							PackageManager.GET_META_DATA
									| PackageManager.GET_UNINSTALLED_PACKAGES)
							.loadLabel(getPackageManager()).toString();
				} catch (NameNotFoundException e) {
					_appName = _pname;
				}
			}
			return _appName;

		}

		public int getPercent() {
			return ((getUseTime() * 100) / appsTotalTime);
		}

		public int compareTo(ApplicationInfo another) {

			return this._individualTime.compareTo(another.getIndividualTime());
		}

	}

	/*
	 * compares total application use of two ApplicationInfo objects
	 */
	class ApplicationInfoComparator implements Comparator<ApplicationInfo> {

		public int compare(ApplicationInfo ap1, ApplicationInfo ap2) {

			if (ap1.getUseTime() > ap2.getUseTime())
				return -1;
			else if (ap1.getUseTime() < ap2.getUseTime())
				return 1;
			else
				return 0;

		}

	}

}
