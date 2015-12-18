package com.ubiqlog.vis.ui;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;

import com.ubiqlog.ui.R;
import com.ubiqlog.ui.R.drawable;
import com.ubiqlog.vis.common.Settings;
import com.ubiqlog.vis.ui.extras.ControlBar;
import com.ubiqlog.vis.extras.search.Searcher;
import com.ubiqlog.vis.utils.UserFriendlyException;
import com.ubiqlog.vis.utils.Utils;

/**
 * CallLog activity
 * 
 * @author Victor Gugonatu
 * @date 10.2010
 * @version 1.0
 */
public class CallLog extends Activity {

	/**
	 * 
	 * custom view used to display the contact details
	 */
	public class ContactDetailsView extends View {

		private int _width = 0;
		private int _height = 0;

		public ContactDetailsView(Context context) {
			super(context);

		}

		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

			if (_width == 0) {
				_width = _center_X * 2;
				_height = 10;
			}

			setMeasuredDimension(_width, _height);

		}

		protected void onDraw(Canvas canvas) {
			int height = 0;
			if (_detailVisible) {
				String id = "null";
				if (currentStep >= 0)
					id = _calls.get(currentStep).getContactID();

				ContactInfo contact = null;

				for (ContactInfo c : _contacts) {
					if (c.getContactID().equals(id)) {
						contact = c;
						break;
					}

				}

				if (contact != null) {
					// show contact

					Bitmap personIcon = BitmapFactory.decodeResource(this
							.getResources(), drawable.no_photo);

					try {
						personIcon = getPhoto(getContentResolver(), Long
								.parseLong(id));
					} catch (Exception e) {
					}

					if (personIcon == null)
						personIcon = BitmapFactory.decodeResource(this
								.getResources(), drawable.no_photo);

					if (personIcon.getHeight() != 96
							&& personIcon.getWidth() != 96) {
						// resize picture to 96x96
						personIcon = Bitmap.createScaledBitmap(personIcon, 96,
								96, true);
					}

					Paint p = new Paint();
					p.setColor(Color.argb(255, 0, 0, 0));

					canvas.drawBitmap(personIcon, 20, 20, p);

					height += 96 + 20;

					p.setAntiAlias(true);
					p.setColor(Color.WHITE);
					p.setFakeBoldText(true);

					String text = _context.getResources().getString(
							R.string.Vis_Call_ContactDetails);
					int yOffset = 30;
					int yBetween = 20;
					int xOffset = 140;
					Rect bounds = new Rect();

					canvas.drawText(text, xOffset, yOffset, p);

					p.getTextBounds(text, 0, text.length(), bounds);

					yOffset = yOffset + (bounds.bottom - bounds.top) / 2
							+ yBetween;

					text = _context.getResources().getString(
							R.string.Vis_Call_ContactName)
							+ ": " + contact.getDisplayName();
					canvas.drawText(text, xOffset, yOffset, p);

					yBetween = 10;

					p.getTextBounds(text, 0, text.length(), bounds);

					yOffset = yOffset + (bounds.bottom - bounds.top) / 2
							+ yBetween;

					text = _context.getResources().getString(
							R.string.Vis_Call_ContactPhone)
							+ ": " + _calls.get(currentStep).getNumber();

					canvas.drawText(text, xOffset, yOffset, p);

					yBetween = 30;

					p.getTextBounds(text, 0, text.length(), bounds);

					yOffset = yOffset + (bounds.bottom - bounds.top) / 2
							+ yBetween;

					text = _context.getResources().getString(
							R.string.Vis_Call_ContactCallDuration)
							+ ": "
							+ Utils.Instance()
									.getTime(
											_calls.get(currentStep)
													.getDuration(), true);

					canvas.drawText(text, xOffset, yOffset, p);

					yBetween = 10;

					p.getTextBounds(text, 0, text.length(), bounds);

					yOffset = yOffset + (bounds.bottom - bounds.top) / 2
							+ yBetween;

					text = _context.getResources().getString(
							R.string.Vis_Call_ContactCallType)
							+ ": "
							+ Utils.Instance().getCallType(
									_calls.get(currentStep).getType());

					canvas.drawText(text, xOffset, yOffset, p);

					yBetween = 40;

					yOffset = yOffset + (bounds.bottom - bounds.top) / 2
							+ yBetween;

					yOffset = Math.max(yOffset, 96 + 20 + 40);

					xOffset = 30;

					text = _context.getResources().getString(
							R.string.Vis_Call_ContactStatistic);
					canvas.drawText(text, xOffset, yOffset, p);

					yBetween = 30;

					p.getTextBounds(text, 0, text.length(), bounds);

					yOffset = yOffset + (bounds.bottom - bounds.top) / 2
							+ yBetween;

					text = _context.getResources().getString(
							R.string.Vis_Call_ShortestCall)
							+ ": "
							+ Utils.Instance().getTime(
									contact.getShortestCall(), true);
					canvas.drawText(text, xOffset, yOffset, p);

					yBetween = 10;

					p.getTextBounds(text, 0, text.length(), bounds);

					yOffset = yOffset + (bounds.bottom - bounds.top) / 2
							+ yBetween;

					text = _context.getResources().getString(
							R.string.Vis_Call_LongestCall)
							+ ": "
							+ Utils.Instance().getTime(
									contact.getLongestCall(), true);
					canvas.drawText(text, xOffset, yOffset, p);

					yBetween = 10;

					p.getTextBounds(text, 0, text.length(), bounds);

					yOffset = yOffset + (bounds.bottom - bounds.top) / 2
							+ yBetween;

					text = _context.getResources().getString(
							R.string.Vis_Call_TotalTime)
							+ ": "
							+ Utils.Instance().getTime(contact.getTotalTime(),
									true);
					canvas.drawText(text, xOffset, yOffset, p);

					yBetween = 30;

					p.getTextBounds(text, 0, text.length(), bounds);

					yOffset = yOffset + (bounds.bottom - bounds.top) / 2
							+ yBetween;

					int longestDate = 0;
					int longestDuration = 0;
					int longestType = 0;
					// get max number of pixel for each column
					for (CallInfo call : contact.getCallList()) {
						String date = call.getDate().toLocaleString();
						p.getTextBounds(date, 0, date.length(), bounds);
						if ((bounds.right - bounds.left) > longestDate)
							longestDate = (bounds.right - bounds.left);

						String duration = Utils.Instance().getTime(
								call.getDuration(), true);
						p.getTextBounds(duration, 0, duration.length(), bounds);
						if ((bounds.right - bounds.left) > longestDuration)
							longestDuration = (bounds.right - bounds.left);

						String type = Utils.Instance().getCallType(
								call.getType());
						p.getTextBounds(type, 0, type.length(), bounds);
						if ((bounds.right - bounds.left) > longestType)
							longestType = (bounds.right - bounds.left);

					}

					int difference = getWidth() - xOffset - longestDate
							- longestDuration - longestType;

					if (difference >= 0) {

						int toAddDif = difference / 3;

						longestDate += toAddDif;
						longestDuration += toAddDif;
						longestType += toAddDif;

						text = _context.getResources().getString(
								R.string.Vis_Call_Protocol);
						canvas.drawText(text, xOffset, yOffset, p);

						yBetween = 20;

						p.getTextBounds(text, 0, text.length(), bounds);

						yOffset = yOffset + (bounds.bottom - bounds.top) / 2
								+ yBetween;

						text = _context.getResources().getString(
								R.string.Vis_Call_DateTime);

						p.getTextBounds(text, 0, text.length(), bounds);

						int xTempOffset = 0;// (longestDate-(bounds.right-bounds.left))/2;

						canvas
								.drawText(text, xOffset + xTempOffset, yOffset,
										p);

						text = _context.getResources().getString(
								R.string.Vis_Call_Duration);

						p.getTextBounds(text, 0, text.length(), bounds);

						xTempOffset = 0;// (longestDuration-(bounds.right-bounds.left))/2;

						canvas.drawText(text, xOffset + xTempOffset
								+ longestDate, yOffset, p);

						text = _context.getResources().getString(
								R.string.Vis_Call_Type);

						p.getTextBounds(text, 0, text.length(), bounds);

						xTempOffset = 0;// (longestType-(bounds.right-bounds.left))/2;

						canvas.drawText(text, xOffset + xTempOffset
								+ longestDate + longestDuration, yOffset, p);

						yBetween = 15;

						// header

						for (CallInfo call : contact.getCallList()) {

							yOffset = yOffset + (bounds.bottom - bounds.top)/ 2 + yBetween;

							text = call.getDate().toLocaleString();

							p.getTextBounds(text, 0, text.length(), bounds);

							xTempOffset = 0;// (longestDate-(bounds.right-bounds.left))/2;

							canvas.drawText(text, xOffset + xTempOffset,
									yOffset, p);

							text = Utils.Instance().getTime(call.getDuration(),
									true);

							p.getTextBounds(text, 0, text.length(), bounds);

							xTempOffset = 0;// (longestDuration-(bounds.right-bounds.left))/2;

							canvas.drawText(text, xOffset + xTempOffset
									+ longestDate, yOffset, p);

							text = Utils.Instance().getCallType(call.getType());

							p.getTextBounds(text, 0, text.length(), bounds);

							xTempOffset = 0;// (longestType-(bounds.right-bounds.left))/2;

							canvas
									.drawText(text, xOffset + xTempOffset
											+ longestDate + longestDuration,
											yOffset, p);

							yBetween = 10;

						}

					}

					height = yOffset;
				} else {
					// show error
					Paint p = new Paint();
					p.setColor(Color.argb(255, 0, 0, 0));
					p.setAntiAlias(true);
					p.setColor(Color.WHITE);
					p.setFakeBoldText(true);
					String text = "Contact not found!";
					int yOffset = 40;
					canvas.drawText(text, 40, yOffset, p);
					Rect bounds = new Rect();
					p.getTextBounds(text, 0, text.length(), bounds);

					height = yOffset + (bounds.top - bounds.bottom);

				}
			}
			_height = height;
			requestLayout();
		}

		public Bitmap getPhoto(ContentResolver contentResolver, Long contactId) {
			Uri contactPhotoUri = ContentUris.withAppendedId(
					Contacts.CONTENT_URI, contactId);

			InputStream photoDataStream = Contacts.openContactPhotoInputStream(
					contentResolver, contactPhotoUri);
			Bitmap photo = BitmapFactory.decodeStream(photoDataStream);
			return photo;
		}

	}

	/**
	 * 
	 * custom view used to display the circle and the person in the middle
	 */
	public class MainView extends View {

		public MainView(Context context) {
			super(context);

		}

		protected void onDraw(Canvas canvas) {
			// height -> 283
			// width -> 320

			Paint p = new Paint();
			Paint p_stroke = new Paint();
			// int sizeOfCBar = 200;
			int borderSize = 20;

			_center_X = getWidth() / 2;
			_center_Y = getHeight() / 2;

			_bigRadius = (Math.min(getHeight(), getWidth())) / 2;
			_bigRadius = _bigRadius - borderSize;
			_smallRadius = _bigRadius / 5;
			// _bigRadius = _smallRadius * 4;

			canvas.drawColor(Color.BLACK);

			p.setStyle(Paint.Style.FILL_AND_STROKE);

			p_stroke.setStyle(Paint.Style.STROKE);

			p.setColor(Color.rgb(164, 169, 243));
			canvas.drawCircle(_center_X, _center_Y, _bigRadius, p);
			canvas.drawCircle(_center_X, _center_Y, _bigRadius, p_stroke);

			p.setColor(Color.rgb(117, 122, 196));
			canvas.drawCircle(_center_X, _center_Y, _smallRadius * 4, p);
			canvas.drawCircle(_center_X, _center_Y, _smallRadius * 4, p_stroke);

			p.setColor(Color.rgb(95, 102, 161));
			canvas.drawCircle(_center_X, _center_Y, _smallRadius * 3, p);
			canvas.drawCircle(_center_X, _center_Y, _smallRadius * 3, p_stroke);

			p.setColor(Color.rgb(69, 73, 117));
			canvas.drawCircle(_center_X, _center_Y, _smallRadius * 2, p);
			canvas.drawCircle(_center_X, _center_Y, _smallRadius * 2, p_stroke);

			p.setColor(Color.rgb(48, 50, 81));
			canvas.drawCircle(_center_X, _center_Y, _smallRadius, p);
			canvas.drawCircle(_center_X, _center_Y, _smallRadius, p_stroke);

			p.setColor(Color.rgb(255, 187, 5));
			Bitmap personIcon = BitmapFactory.decodeResource(this
					.getResources(), drawable.person);

			canvas.drawBitmap(personIcon, _center_X
					- (personIcon.getWidth() / 2), _center_Y
					- (personIcon.getHeight() / 2), p);

		}

	}

	/**
	 * 
	 * custom view used to display the call type icon
	 */
	public class CallIconView extends View {

		private int _x;
		private int _y;
		private int _type;

		public CallIconView(Context context, int x, int y, int type) {
			super(context);
			_x = x;
			_y = y;
			_type = type;

		}

		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			Paint p = new Paint();
			int iconId = drawable.call_out_no_s1;
			switch (_type) {
			case Calls.INCOMING_TYPE:
				iconId = drawable.call_in_yes_s1;
				break;
			case Calls.OUTGOING_TYPE:
				iconId = drawable.call_out_yes_s1;
				break;
			case Calls.MISSED_TYPE:
				iconId = drawable.call_in_no_s1;
				break;
			default:
				iconId = drawable.call_out_no_s1;
				break;
			}
			Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
					iconId);

			p.setStyle(Paint.Style.FILL_AND_STROKE);
			p.setAntiAlias(true);

			p.setColor(Color.BLACK);
			p.setTypeface(Typeface.DEFAULT_BOLD);

			canvas.drawBitmap(icon, _x - (icon.getWidth() / 2), _y
					- (icon.getHeight() / 2), p);

		}

	}

	/**
	 * 
	 * custom view used to display the info icon
	 */
	public class InfoIconView extends View {

		public InfoIconView(Context context) {
			super(context);

		}

		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			Paint p = new Paint();
			int iconId = drawable.male_user_info;

			Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
					iconId);

			p.setStyle(Paint.Style.FILL_AND_STROKE);
			p.setAntiAlias(true);

			p.setColor(Color.BLACK);
			p.setTypeface(Typeface.DEFAULT_BOLD);

			canvas.drawBitmap(icon, 0, 0, p);

		}

	}

	/**
	 * 
	 * custom view used to display the date and time of the current displayed
	 * call
	 */
	public class DateTimeView extends View {

		private int _x;
		private int _y;
		private Date _date;

		public DateTimeView(Context context, int x, int y, Date date) {
			super(context);
			_x = x;
			_y = y;
			_date = date;

		}

		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			String dateTime = _date.toLocaleString();

			Paint p = new Paint();

			int yTextOffset = 20;
			int xTextOffset = 10;
			int yRectOffset = 10;
			int xRectOffset = 10;

			p.setAntiAlias(true);
			p.setFakeBoldText(true);
			p.setTypeface(Typeface.DEFAULT_BOLD);
			p.setTextScaleX(1.2f);
			p.setTextAlign(Paint.Align.RIGHT);
			p.setColor(Color.WHITE);

			ShapeDrawable sp = new ShapeDrawable(new RoundRectShape(
					new float[] { 12, 12, 12, 12, 12, 12, 12, 12 }, null, null));

			Rect bounds = new Rect();
			p.getTextBounds(dateTime, 0, dateTime.length(), bounds);

			sp.setBounds(bounds.left + (_x - bounds.right - yTextOffset)
					- yRectOffset, bounds.top + (_y + xTextOffset)
					- xRectOffset, bounds.right
					+ (_x - bounds.right - yTextOffset) + yRectOffset,
					bounds.bottom + (_y + xTextOffset) + xRectOffset);
			sp.getPaint().setColor(Color.argb(90, 0, 0, 0));
			sp.getPaint().setStyle(Style.FILL_AND_STROKE);

			// sp.draw(canvas);

			canvas.drawText(dateTime, _x - xTextOffset, _y + yTextOffset, p);

		}

	}

	/**
	 * 
	 * custom view used to display the contact icon and name
	 */
	public class ContactView extends View {

		private int _x;
		private int _y;
		private String _name;

		public ContactView(Context context, int x, int y, String name) {
			super(context);
			_x = x;
			_y = y;
			_name = name;

		}

		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			Paint p = new Paint();

			p.setStyle(Paint.Style.FILL_AND_STROKE);
			p.setAntiAlias(true);

			p.setColor(Color.WHITE);
			p.setTypeface(Typeface.DEFAULT_BOLD);
			Bitmap personIcon = BitmapFactory.decodeResource(this
					.getResources(), drawable.person16x16);

			canvas.drawBitmap(personIcon, _x - (personIcon.getWidth() / 2), _y
					- (personIcon.getHeight() / 2), p);

			p.setTextAlign(Paint.Align.CENTER);
			p.setTextScaleX(1.2f);

			canvas.drawText(_name, _x, _y + (personIcon.getHeight() / 2) + 15,
					p);
		}
	}

	private int totalTime = 0;

	private int _bigRadius;
	private int _smallRadius;
	private int _center_X;
	private int _center_Y;
	private HashMap<String, View> _callersGroup;
	private ArrayList<CallInfo> _calls;
	private ArrayList<ContactInfo> _contacts;
	private ControlBar _cBar;
	private CallIconView _cd_incoming = null;
	private CallIconView _cd_outgoing = null;
	private CallIconView _cd_missed = null;
	private InfoIconView _info_icon = null;
	private Context _context;
	private DateTimeView _dt = null;
	ProgressDialog _progressDialog = null;
	Date _start = null;
	Date _end = null;
	private Activity _activity;
	private int currentStep = -1;
	private Boolean _detailVisible = false;
	private ScrollView _scrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		_activity = this;
		_context = this;
		_callersGroup = new HashMap<String, View>();
		_contacts = new ArrayList<ContactInfo>();

		LinearLayout callLayout = new LinearLayout(this);
		callLayout.setOrientation(LinearLayout.VERTICAL);

		MainView mainView = new MainView(this);

		callLayout.addView(mainView, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1));

		_cBar = new ControlBar(this, 0, Settings.call_timeFrame, stateC, null,
				false, true);

		callLayout.addView(_cBar, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		setContentView(callLayout);

		ContactDetailsView contactDetailsView = new ContactDetailsView(this);
		_scrollView = new ScrollView(this);
		_scrollView.addView(contactDetailsView, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));

	}

	/*
	 * handles the click on the info icon
	 */
	View.OnClickListener infoClickListener = new View.OnClickListener() {

		public void onClick(View v) {
			_cBar.setPause();
			_detailVisible = true;
			_scrollView.setVisibility(View.VISIBLE);
			_scrollView.bringToFront();

		}
	};

	ControlBar.OnStateChangedListener stateC = new ControlBar.OnStateChangedListener() {

		public void onStateChanged(SeekBar seekBar, int step) {
			_detailVisible = false;

			if (_cd_incoming == null) {
				// initialise the views -> must be done here because the main
				// view sets the center coordinates

				_cd_incoming = new CallIconView(_context, 20,
						_center_Y * 2 - 20, Calls.INCOMING_TYPE);
				_cd_outgoing = new CallIconView(_context, 20,
						_center_Y * 2 - 20, Calls.OUTGOING_TYPE);
				_cd_missed = new CallIconView(_context, 20, _center_Y * 2 - 20,
						Calls.MISSED_TYPE);
				_info_icon = new InfoIconView(_context);
				_info_icon.setClickable(true);
				_info_icon.setOnClickListener(infoClickListener);

				RelativeLayout rl = new RelativeLayout(_context);

				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						64, 64);
				params.leftMargin = _center_X * 2 - 70;
				params.topMargin = _center_Y * 2 - 70;
				rl.addView(_info_icon, params);

				addContentView(_scrollView, new LayoutParams(_center_X * 2,
						_center_Y * 2));

				ShapeDrawable sp = new ShapeDrawable(new RectShape());
				sp.getPaint().setColor(Color.argb(255, 0, 0, 0));
				sp.setPadding(10, 10, 10, 10);
				sp.getPaint().setStyle(Style.FILL_AND_STROKE);

				ShapeDrawable sp3 = new ShapeDrawable(new RoundRectShape(
						new float[] { 12, 12, 12, 12, 12, 12, 12, 12 }, null,
						null));
				sp3.setPadding(10, 10, 10, 10);
				sp3.getPaint().setAntiAlias(true);
				sp3.getPaint().setStrokeWidth(2);
				sp3.getPaint().setColor(Color.argb(255, 255, 0, 0));
				sp3.getPaint().setStyle(Style.STROKE);

				LayerDrawable dr = new LayerDrawable(new Drawable[] { sp, sp3 });

				_scrollView.setBackgroundDrawable(dr);
				_scrollView.setPadding(10, 10, 10, 10);
				_scrollView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
				_scrollView.setVisibility(View.GONE);

				addContentView(_cd_incoming, new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				addContentView(_cd_outgoing, new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				addContentView(_cd_missed, new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				addContentView(rl, new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));

			}
			currentStep = step;
			if (step == -1) {
				_scrollView.setVisibility(View.GONE);
				_progressDialog = ProgressDialog.show(_context, _context
						.getResources().getString(R.string.Vis_loading),
						_context.getResources().getString(
								R.string.Vis_Searching), true, false);

				SearchThread searchThread = new SearchThread();
				searchThread.start();

				_cd_incoming.setVisibility(View.GONE);
				_cd_outgoing.setVisibility(View.GONE);
				_cd_missed.setVisibility(View.GONE);
				_info_icon.setVisibility(View.GONE);

			} else {
				_scrollView.setVisibility(View.GONE);
				_info_icon.setVisibility(View.VISIBLE);
				goToNextCaller(step);

			}

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

			// getData
			_start = _cBar.getStartDate();
			_end = _cBar.getEndDate();
			Boolean play = false;
			try {
				_calls = getCallLog(_start, _end);

				_progressDialog.dismiss();

				if (_calls.size() > 0) {

					play = true;

				} else {
					play = false;

					Message msg = Message.obtain();
					msg.obj = _context.getResources().getString(
							R.string.Vis_noData);
					handler.sendMessage(msg);

				}

				if (play) {
					// player is valid
					handlerChangeData.sendEmptyMessage(0);
				}
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
						.getResources().getString(R.string.Vis_Ok),
						(OnClickListener) null);
				aldlg.show();

				_cBar.setProgress(0);
				_cBar.setMax(0);
			}
		};

		private Handler handlerChangeData = new Handler() {

			@Override
			public void handleMessage(Message msg) {

				if (_calls.size() > 0) {
					addCallers(_contacts);
					_cBar.setProgress(0);
					_cBar.setMax(_calls.size() - 1);

					_cBar.setValidAndPlay();
				}
			}
		};
	}

	/*
	 * search the log files for data between from and to Date
	 */
	public ArrayList<CallInfo> getCallLog(Date from, Date to)
			throws UserFriendlyException {
		ArrayList<CallInfo> calls = new ArrayList<CallInfo>();
		Searcher searcher = new Searcher();
		totalTime = 0;
		SimpleDateFormat dateformat = new SimpleDateFormat("M-d-yyyy");

		String dateFrom = dateformat.format(from);
		String dateTo = dateformat.format(to);
		ArrayList<String> lines = searcher.searchFolder(Settings.SensorCall,
				dateFrom, dateTo, this);
		int unknownID = 0;

		SimpleDateFormat dateformatLog = new SimpleDateFormat(
				"M-d-yyyy HH:mm:ss");

		if (lines != null) {
			for (String line : lines) {
				// {"Call":{"Number":"-2","Duration":"0","Time":"Mar 6 2010 4:14:47 PM","Type":"3","metadata":{"name":""}}}
				try {
					String[] entities = line.split("\\\"");
					String number = entities[5];
					int duration = Integer.parseInt(entities[9]);
					// //Mar 6 2010 2:49:16 AM -> uknown locale; US locale: Mar
					// 6, 2010 2:49:16 AM
					//
					Date dateTime = null;
					try {
						dateTime = dateformatLog.parse(entities[13]);
					} catch (Exception ex) {
						dateTime = new Date(Date.parse(entities[13].replace(
								",", "")));

					}

					int type = Integer.parseInt(entities[17]);
					if (dateTime != null && dateTime.compareTo(from) >= 0
							&& dateTime.compareTo(to) <= 0) {
						CallInfo call = new CallInfo();
						call.setDate(dateTime);
						call.setDuration(duration);
						totalTime += duration;
						call.setNumber(android.telephony.PhoneNumberUtils
								.formatNumber(number));
						call.setType(type);
						Boolean found = false;
						for (ContactInfo contact : _contacts) {
							if (contact.containsPhoneNumber(call.getNumber())) {
								found = true;
								call.setContactID(contact.getContactID());
								call.setDisplayName(contact.getDisplayName());
								contact.addCallInfo(call);
							}
						}
						if (!found) {
							ContactInfo contact = new ContactInfo();
							contact.setContactID("null");
							contact.setDisplayName(_context.getResources()
									.getString(R.string.Vis_Call_Unknown));

							ContentResolver cr = getContentResolver();
							Uri uri = Uri.withAppendedPath(
									PhoneLookup.CONTENT_FILTER_URI, Uri
											.encode(call.getNumber()));
							Cursor cur = cr.query(uri, new String[] {
									PhoneLookup.DISPLAY_NAME,
									PhoneLookup.PHOTO_ID, PhoneLookup._ID },
									null, null, null);
							while (cur != null && cur.moveToNext()) {
								contact
										.setDisplayName((cur
												.getString(cur
														.getColumnIndexOrThrow(PhoneLookup.DISPLAY_NAME))));
								contact
										.setContactID((cur
												.getString(cur
														.getColumnIndexOrThrow(PhoneLookup._ID))));
							}
							if (cur != null)
								cur.close();

							Boolean foundContact = false;
							if (contact.getContactID() != "null") {
								for (ContactInfo c : _contacts) {
									if (c.getContactID().equals(
											contact.getContactID())) {
										foundContact = true;
										contact = c;
										break;
									}
								}
								if (!foundContact) {
									cur = cr
											.query(
													ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
													null,
													ContactsContract.CommonDataKinds.Phone.CONTACT_ID
															+ " = ?",
													new String[] { contact
															.getContactID() },
													null);
									if (cur.getCount() > 0) {
										while (cur.moveToNext()) {
											contact
													.addNumber(android.telephony.PhoneNumberUtils
															.formatNumber(cur
																	.getString(cur
																			.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))));
										}
									}
									if (cur != null)
										cur.close();
								}
							} else {
								unknownID++;
								contact.setContactID("unknownID_" + unknownID);
							}

							call.setContactID(contact.getContactID());
							call.setDisplayName(contact.getDisplayName());
							contact.addCallInfo(call);
							if (!foundContact)
								_contacts.add(contact);
						}
						calls.add(call);
					}
				} catch (Exception exc) {
					// ignore corrupted lines
				}
			}
		}

		if (calls != null && calls.size() > 0)
			Collections.sort(calls);

		return calls;
	}

	/**
	 * animate the next contact
	 */
	private void goToNextCaller(int index) {
		Message msg = Message.obtain();
		msg.obj = "INVISIBLE";
		handler.sendMessage(msg);

		String k = _calls.get(index).getContactID();

		for (String key : _callersGroup.keySet()) {
			if (key != k) {
				if (((ContactView) _callersGroup.get(key)).getAnimation() != null) {
					((ContactView) _callersGroup.get(key)).clearAnimation();

				}

			}
		}
		ContactView dv = ((ContactView) _callersGroup.get(k));
		ScaleAnimation rAnim = new ScaleAnimation(1, (float) 1.4, 1,
				(float) 1.4, dv._x, dv._y);

		dv.bringToFront();

		rAnim.setDuration(2000);
		rAnim.setFillAfter(true);
		dv.setAnimation(rAnim);

		Message msg2 = Message.obtain();
		msg2.arg1 = index;
		msg2.obj = String.valueOf(_calls.get(index).getType());
		handler.sendMessage(msg2);

	}

	/**
	 * used to display the proper icon accordingly to call type
	 */
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (msg.obj == "INVISIBLE") {
				_cd_incoming.setVisibility(View.GONE);
				_cd_outgoing.setVisibility(View.GONE);
				_cd_missed.setVisibility(View.GONE);
				if (_dt != null)
					((ViewGroup) _dt.getParent()).removeView(_dt);
			} else {

				switch (Integer.parseInt((String) msg.obj)) {
				case Calls.INCOMING_TYPE:
					_cd_incoming.setVisibility(View.VISIBLE);
					break;
				case Calls.OUTGOING_TYPE:
					_cd_outgoing.setVisibility(View.VISIBLE);
					break;
				case Calls.MISSED_TYPE:
					_cd_missed.setVisibility(View.VISIBLE);
					break;

				default:
					break;
				}
				_dt = new DateTimeView(_context, _center_X * 2, 0, _calls.get(
						msg.arg1).getDate());
				addContentView(_dt, new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));

			}
		}
	};

	/**
	 * adds the contacts on the "circle" randomly but taking in account total
	 * time spent
	 */
	public void addCallers(ArrayList<ContactInfo> contacts) {

		for (View v : _callersGroup.values()) {
			((ViewGroup) v.getParent()).removeView(v);
		}

		_callersGroup = new HashMap<String, View>();

		Random r = new Random();

		int intervall = Math.max(totalTime, (_bigRadius - _smallRadius))
				/ (_bigRadius - _smallRadius);

		HashMap<Integer, ArrayList<Integer>> points = new HashMap<Integer, ArrayList<Integer>>();

		for (ContactInfo contact : contacts) {

			{

				int angle = r.nextInt(360);

				int position = _bigRadius - contact.getTotalTime() / intervall;

				Boolean searchNext = false;
				int p = 0;

				for (int i = -50; i <= 50; i++) {
					if (points.containsKey(position + i)) {
						for (int d = -50; d <= 50; d++) {

							if (points.get(position + i).contains(angle + d)) {
								searchNext = true;
								break;
							}
						}
						if (searchNext)
							break;
					}
				}

				while (searchNext && p < 100) {
					p++;
					angle = r.nextInt(360);

					searchNext = false;

					for (int i = -50; i <= 50; i++) {
						if (points.containsKey(position + i)) {
							for (int d = -50; d <= 50; d++) {

								if (points.get(position + i)
										.contains(angle + d)) {
									searchNext = true;
									break;
								}
							}
							if (searchNext)
								break;
						}
					}
				}

				if (points.containsKey(position)) {
					points.get(position).add(angle);
				} else {
					points.put(position, new ArrayList<Integer>());
					points.get(position).add(angle);
				}

				int x_pos = (int) (_center_X + position * Math.cos(angle));
				int y_pos = (int) (_center_Y + position * Math.sin(angle));

				ContactView cd = new ContactView(this, x_pos, y_pos, contact
						.getDisplayName());

				_callersGroup.put(contact.getContactID(), cd);

				_activity.addContentView(cd, new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			}
		}

	}

	/**
	 * object used to hold the call details -> datetime, number, type
	 * 
	 */
	class CallInfo implements Comparable<CallInfo> {
		private String mContactID;
		private String mDisplayName;
		private String mNumber;
		private int mDuration;
		private int mType;
		private Date mDate;

		public CallInfo() {
			mContactID = "";
			mDisplayName = "Unknown";
			mNumber = "";
			mDuration = 0;
			mType = -1;
			mDate = null;
		}

		public String getContactID() {
			return mContactID;
		}

		public void setContactID(String mContactID) {
			this.mContactID = mContactID;
		}

		public String getDisplayName() {
			return mDisplayName;
		}

		public void setDisplayName(String mDisplayName) {
			this.mDisplayName = mDisplayName;
		}

		public String getNumber() {
			return mNumber;
		}

		public void setNumber(String mNumber) {
			this.mNumber = mNumber;
		}

		public int getDuration() {
			return mDuration;
		}

		public void setDuration(int mDuration) {
			this.mDuration = mDuration;
		}

		public int getType() {
			return mType;
		}

		public void setType(int mType) {
			this.mType = mType;
		}

		public Date getDate() {
			return mDate;
		}

		public void setDate(Date mDate) {
			this.mDate = mDate;
		}

		public int compareTo(CallInfo another) {

			return this.mDate.compareTo(another.getDate());
		}

	}

	/**
	 * object used to hold the contact data (name, numbers, call list)
	 * 
	 */
	public class ContactInfo {

		private String mContactID;
		private String mDisplayName;
		private ArrayList<String> mPhoneNumbers;
		private InputStream mPhotoStream;
		private ArrayList<CallInfo> mCalls;
		int _positionOnMap_x;
		int _positionOnMap_y;

		public ContactInfo() {
			mContactID = "";
			mDisplayName = getResources().getString(R.string.Vis_Call_Unknown);
			mPhoneNumbers = new ArrayList<String>();
			mPhotoStream = null;
			mCalls = new ArrayList<CallLog.CallInfo>();
		}

		public int getTotalTime() {
			int nr = 0;
			for (CallInfo call : mCalls) {
				nr += call.getDuration();
			}
			return nr;
		}

		public void addNumber(String number) {
			this.mPhoneNumbers.add(number);
		}

		public void addCallInfo(CallInfo call) {
			this.mCalls.add(call);
		}

		public Boolean containsPhoneNumber(String number) {
			for (String num : mPhoneNumbers) {
				if (num.equals(number))
					return true;
			}
			return false;
		}

		public int getNumberOfCalls() {
			return mCalls.size();
		}

		public int getNumberOfCallsByType(int type) {
			int nr = 0;
			for (CallInfo call : mCalls) {
				if (call.getType() == type)
					nr++;
			}
			return nr;
		}

		public int getLongestCall() {
			int maxTime = 0;
			for (CallInfo call : mCalls) {
				if (call.getDuration() > maxTime)
					maxTime = call.getDuration();
			}
			return maxTime;
		}

		public int getShortestCall() {
			int minTime = Integer.MAX_VALUE;
			for (CallInfo call : mCalls) {
				if (call.getDuration() < minTime && call.getDuration() > 0)
					minTime = call.getDuration();
			}
			if (minTime == Integer.MAX_VALUE)
				minTime = 0;
			return minTime;
		}

		public ArrayList<CallInfo> getCallList() {
			return mCalls;
		}

		public void setDisplayName(String displayName) {
			this.mDisplayName = displayName;
		}

		public String getDisplayName() {
			return mDisplayName;
		}

		public void setPhotoStream(InputStream mPhotoStream) {
			this.mPhotoStream = mPhotoStream;
		}

		public InputStream getPhotoStream() {
			return mPhotoStream;
		}

		public void setContactID(String mContactID) {
			this.mContactID = mContactID;
		}

		public String getContactID() {
			return mContactID;
		}
	}
}
