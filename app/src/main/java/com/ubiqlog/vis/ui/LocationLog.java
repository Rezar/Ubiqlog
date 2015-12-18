package com.ubiqlog.vis.ui;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.MapActivity;
//import com.google.android.maps.MapController;
//import com.google.android.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ubiqlog.ui.R;
import com.ubiqlog.vis.common.GeoPoint;
import com.ubiqlog.vis.common.Settings;
import com.ubiqlog.vis.ui.extras.ControlBar;
import com.ubiqlog.vis.extras.search.Searcher;
import com.ubiqlog.vis.utils.UserFriendlyException;
import com.ubiqlog.vis.utils.Utils;

/**
 * LocationLog activity
 * 
 * @author Victor Gugonatu
 * @date 10.2010
 * @version 1.0
 */

// MapActivity is deprecated and is now being replaced with
// MapFragment - AP

public class LocationLog extends Activity implements OnMapReadyCallback {

	List<UbiqGeoPoint> _points = null;
	//MapView _mapView = null;
	MapFragment _mapView = null;
	Boolean _needToLoadData = true;
	Date _start = null;
	Date _end = null;
	ControlBar _cBar = null;
	Context _context = null;
	ProgressDialog _progressDialog = null;

	private GoogleMap googleMap;

	private DialogInterface.OnClickListener ic_clicked = new DialogInterface.OnClickListener() {

		public void onClick(DialogInterface arg0, int arg1) {
			// finish application
			LocationLog.this.finish();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_locationlog);

		// check for internet connection; if none that show a dialog to user;
		// else continue application
		if (!Utils.Instance().hasInternetConnection(this)) {

			AlertDialog dlg = new AlertDialog.Builder(this).create();
			dlg.setButton(DialogInterface.BUTTON_NEUTRAL, getResources()
					.getText(R.string.Vis_location_ic_dialog_ok), ic_clicked);
			dlg.setMessage(getResources().getText(
					R.string.Vis_location_ic_dialog_message));
			dlg.show();
		} else {

			_cBar = new ControlBar(this, 0, Settings.location_timeFrame,stateChanged, null, false, true);

			/*
			_mapView = new MapView(this, Settings.googleMapKey);

			// disable user interaction
			_mapView.setClickable(false);
			_mapView.setEnabled(false);

			_context = this;

			// set weight=1 to cover the space left on the screen
			_mapView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
			*/

			// Added by AP
			_mapView = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

			if (_mapView.getView() != null) {
				_mapView.getView().setClickable(false);
				_mapView.getView().setEnabled(false);
			}

			_context = this;

			// set weight=1 to cover the space left on the screen
			_mapView.getView().setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
			_mapView.getMapAsync(this);


			_cBar.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			//LinearLayout locationLayout = new LinearLayout(this);
			LinearLayout locationLayout = (LinearLayout) findViewById(R.id.lin_lay_maps);
			locationLayout.setOrientation(LinearLayout.VERTICAL);

			//locationLayout.addView(_mapView);
			//locationLayout.addView(_mapView.getView());
			locationLayout.addView(_cBar);
			//setContentView(locationLayout);

		}
	}

	/*
	@Override
	protected boolean isRouteDisplayed() {
		// no route information are displayed
		return false;
	}
	*/

	@Override
	public void onMapReady(GoogleMap googleMap) {
		this.googleMap = googleMap;
		this.googleMap.setMyLocationEnabled(true);
	}

	/*
	// custom map overlay -> display the marker and the location
	class UbiqMapOverlay extends com.google.android.maps.Overlay  {
		private UbiqGeoPoint _point = null;
		private UbiqGeoPoint _pointBefore1 = null;
		private UbiqGeoPoint _pointBefore2 = null;

		public UbiqMapOverlay(UbiqGeoPoint point, UbiqGeoPoint pointBefore1,
				UbiqGeoPoint pointBefore2) {
			super();

			_point = point.initialise();
			_pointBefore1 = pointBefore1 != null ? pointBefore1.initialise(): null;
			_pointBefore2 = pointBefore2 != null ? pointBefore2.initialise(): null;

		}

		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,long when) {
			super.draw(canvas, mapView, shadow);

			// translate the GeoPoint to screen pixels---
			Point screenPts = new Point();
			mapView.getProjection().toPixels(_point.get_geoPoint(), screenPts);

			Point screenPtsBefore1 = new Point();
			if (_pointBefore1 != null)
				mapView.getProjection().toPixels(_pointBefore1.get_geoPoint(),screenPtsBefore1);

			Point screenPtsBefore2 = new Point();
			if (_pointBefore2 != null)
				mapView.getProjection().toPixels(_pointBefore2.get_geoPoint(),screenPtsBefore2);

			// add the marker
			Bitmap bmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.bluepoint);
			canvas.drawBitmap(bmp, screenPts.x - (bmp.getWidth() / 2),
					screenPts.y - (bmp.getHeight() / 2), null);

			Paint paint = new Paint();
			paint.setAlpha(90);
			if (_pointBefore1 != null)
				canvas.drawBitmap(bmp, screenPtsBefore1.x
						- (bmp.getWidth() / 2), screenPtsBefore1.y
						- (bmp.getHeight() / 2), paint);

			paint.setAlpha(40);
			if (_pointBefore2 != null)
				canvas.drawBitmap(bmp, screenPtsBefore2.x
						- (bmp.getWidth() / 2), screenPtsBefore2.y
						- (bmp.getHeight() / 2), paint);

			// configure paint for the location
			Paint p = new Paint();
			p.setAntiAlias(true);
			p.setColor(Color.BLACK);
			p.setFakeBoldText(true);
			p.setTypeface(Typeface.DEFAULT_BOLD);
			p.setTextAlign(Paint.Align.CENTER);
			p.setTextScaleX(1.2f);

			// add the location information
			// draw text cannot draw newline (\n)
			// text is splited and draw separately
			int yInitOffset = 0;
			int yLineOffset = 5;
			Rect bounds = new Rect();
			Rect totalbounds = new Rect();
			String[] phrases = (_point.getAddress()).split("\n");

			for (String phrase : phrases) {
				p.getTextBounds(phrase, 0, phrase.length(), bounds);
				if (totalbounds.bottom == 0) {
					totalbounds.bottom = yInitOffset + yLineOffset+ bounds.bottom - bounds.top;
					totalbounds.top = yInitOffset + yLineOffset;
					totalbounds.left = 0;
					totalbounds.right = bounds.right;
				} else {
					totalbounds.bottom += yLineOffset + bounds.bottom
							- bounds.top;
					totalbounds.right = Math.max(bounds.right,
							totalbounds.right);
				}
			}
			int yTextOffset = 20;
			int xTextOffset = 20;
			int yRectOffset = 10;
			int xRectOffset = 5;

			int yOrigLineOffset = yLineOffset;
			int diff = bounds.bottom - bounds.top;
			int diffOrig = diff;
			for (String phrase : phrases) {
				canvas
						.drawText(phrase, screenPts.x, screenPts.y
								+ bmp.getHeight() + yInitOffset + yLineOffset
								+ diff, p);
				yLineOffset += yOrigLineOffset;
				diff += diffOrig;
			}

			String dateTime = _point.get_dateTime().toLocaleString();

			p.setTextAlign(Paint.Align.RIGHT);
			p.setColor(Color.WHITE);

			ShapeDrawable sp = new ShapeDrawable(new RoundRectShape(
					new float[] { 12, 12, 12, 12, 12, 12, 12, 12 }, null, null));

			bounds = new Rect();
			p.getTextBounds(dateTime, 0, dateTime.length(), bounds);

			sp.setBounds(bounds.left
					+ (mapView.getRight() - bounds.right - yTextOffset)
					- yRectOffset, bounds.top
					+ (mapView.getTop() + xTextOffset) - xRectOffset,
					bounds.right
							+ (mapView.getRight() - bounds.right - yTextOffset)
							+ yRectOffset, bounds.bottom
							+ (mapView.getTop() + xTextOffset) + xRectOffset);
			sp.getPaint().setColor(Color.argb(90, 0, 0, 0));
			sp.getPaint().setStyle(Style.FILL_AND_STROKE);

			sp.draw(canvas);

			canvas.drawText(dateTime, mapView.getRight() - yTextOffset, mapView
					.getTop()
					+ xTextOffset, p);

			return false;
		}
	}
	*/

	// ubiqlog GeoPoint
	// contains the needed data
	public class UbiqGeoPoint implements Comparable<UbiqGeoPoint> {

		private String _address = null;
		private Date _dateTime = null;
		private String _rowData = null;
		private Boolean isCorrupt = false;
		//private GeoPoint _geoPoint = null;
		private LatLng _geoPoint = null;

		public UbiqGeoPoint(String rowData) {
			_rowData = rowData;
		}

		public UbiqGeoPoint initialise() {
			if (!isCorrupt && _rowData != null && _geoPoint == null) {
				try {
					String[] entities = _rowData.split("\\\"");
					//int latitude = (int) (Double.parseDouble(entities[5]) * 1E6);
					double latitude = (Double.parseDouble(entities[5]));
					//int longitude = (int) (Double.parseDouble(entities[9]) * 1E6);
					double longitude = (Double.parseDouble(entities[9]));
					this._geoPoint = new LatLng(latitude, longitude);
					// Mar 6 2010 2:49:16 AM -> uknown locale; US locale: Mar 6,
					// 2010 2:49:16 AM
					_dateTime = new Date(Date.parse(entities[17]));

					Geocoder geoCoder = new Geocoder(getBaseContext(), Locale
							.getDefault());
					List<Address> addresses;
					try {
						/*
						addresses = geoCoder.getFromLocation(this._geoPoint
								.getLatitudeE6() / 1E6, this._geoPoint
								.getLongitudeE6() / 1E6, 1);
						*/

						addresses = geoCoder.getFromLocation(this._geoPoint.latitude, this._geoPoint
								.longitude , 1);

						_address = "";
						if (addresses.size() > 0) {
							for (int i = 0; i < addresses.get(0)
									.getMaxAddressLineIndex(); i++) {
								_address += addresses.get(0).getAddressLine(i)
										+ "\n";
							}
						}
					} catch (IOException e) {
						// nothing
					}

				} catch (Exception ex) {
					isCorrupt = true;
				}
			}
			return this;
		}

		public UbiqGeoPoint(double latitudeE6, double longitudeE6, Date dateTime) {
			_geoPoint = new LatLng(latitudeE6, longitudeE6);
			_dateTime = dateTime;
		}

		/*
		 * try to get the address from gps coordinates
		 */
		public String getAddress() {
			if (_address == null) {
				_address = "";
				Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
				List<Address> addresses;
				try {
					/*
					addresses = geoCoder.getFromLocation(this._geoPoint
							.getLatitudeE6() / 1E6, this._geoPoint
							.getLongitudeE6() / 1E6, 1);
					*/

					addresses = geoCoder.getFromLocation(this._geoPoint
							.latitude , this._geoPoint
							.longitude , 1);

					if (addresses.size() > 0) {
						for (int i = 0; i < addresses.get(0)
								.getMaxAddressLineIndex(); i++) {
							_address += addresses.get(0).getAddressLine(i)
									+ "\n";
						}
					}
				} catch (IOException e) {
					// nothing
				}

			}
			return _address;
		}

		public Date get_dateTime() {
			return _dateTime;
		}

		public String get_rowData() {
			return _rowData;
		}

		public LatLng get_geoPoint() {
			this.initialise();
			return _geoPoint;
		}

		public int compareTo(UbiqGeoPoint another) {

			return this._dateTime.compareTo(another._dateTime);
		}
	}

	ControlBar.OnStateChangedListener stateChanged = new ControlBar.OnStateChangedListener() {
		public void onStateChanged(SeekBar seekBar, int step) {
			//MapController mc = _mapView.getController();
			//_mapView.getOverlays().clear();

			googleMap.clear();
			if (step == -1) {
				_progressDialog = ProgressDialog.show(_context, getResources()
						.getText(R.string.Vis_loading), getResources().getText(
						R.string.Vis_Searching), true, false);
				SearchThread searchThread = new SearchThread();
				searchThread.start();
			} else {
				/*
				_mapView.getgetOverlays().add(
						new UbiqMapOverlay(_points.get(step),
								(step > 0 ? _points.get(step - 1) : null),
								(step > 1 ? _points.get(step - 2) : null)));
				mc.animateTo(_points.get(step).get_geoPoint());
				mc.setZoom(_mapView.getMaxZoomLevel());
				*/

				// Added by AP
				// Get Current GeoPoint
				UbiqGeoPoint _point = _points.get(step).initialise();

				// Get Text ready for marker
				String[] phrases = (_points.get(step).getAddress()).split("\n");
				String dateTime = _points.get(step).get_dateTime().toLocaleString();
				StringBuilder markerString = new StringBuilder();
				for (String phrase : phrases) {
					markerString.append(phrase);
					markerString.append('\n');
				}
				markerString.append(dateTime);

				// Add a Marker to the Map
				googleMap.addMarker(new MarkerOptions()
						.position(_point.get_geoPoint())			// latlng
						.title(markerString.toString()));			// marker text

				// Move camera
				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
							_points.get(step).get_geoPoint(), googleMap.getMaxZoomLevel());
				googleMap.animateCamera(cameraUpdate);
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
				_points = getLocationLog(_start, _end);
				_progressDialog.dismiss();
				if (_points.size() > 0) {
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
				if (_points.size() > 0) {
					_cBar.setProgress(0);
					_cBar.setMax(_points.size() - 1);

					_cBar.setValidAndPlay();
				}
			}
		};
	}

	/*
	 * search the log files for data between from and to Date
	 */
	public ArrayList<UbiqGeoPoint> getLocationLog(Date from, Date to)
			throws UserFriendlyException {
		ArrayList<UbiqGeoPoint> points = new ArrayList<UbiqGeoPoint>();
		Searcher searcher = new Searcher();

		SimpleDateFormat dateformat = new SimpleDateFormat("M-d-yyyy");

		String dateFrom = dateformat.format(from);
		String dateTo = dateformat.format(to);
		ArrayList<String> lines = searcher.searchFolder(
				Settings.SensorLocation, dateFrom, dateTo, this);

		SimpleDateFormat dateformatLog = new SimpleDateFormat(
				"M-d-yyyy HH:mm:ss");

		if (lines != null) {
			for (String line : lines) {
				// {"Location":{"Latitude":"48.23316693305829","Longtitude":"16.377139091491223","Altitude":"220.0","time":"Mar 6 2010 2:49:16 AM","Accuracy":"9.487171","Provider":"gps"}}
				try {
					String[] entities = line.split("\\\"");
					//int latitude = (int) (Double.parseDouble(entities[5]) * 1E6);
					double latitude = (Double.parseDouble(entities[5]));
					//int longitude = (int) (Double.parseDouble(entities[9]) * 1E6);
					double longitude = (Double.parseDouble(entities[9]));
					// Mar 6 2010 2:49:16 AM -> uknown locale; US locale: Mar 6,
					// 2010 2:49:16 AM

					Date dateTime = null;
					try {
						dateTime = dateformatLog.parse(entities[17]);
					} catch (Exception ex) {
						dateTime = new Date(Date.parse(entities[17].replace(
								",", "")));

					}

					if (dateTime != null && dateTime.compareTo(from) >= 0
							&& dateTime.compareTo(to) <= 0) {
						UbiqGeoPoint point = new UbiqGeoPoint(latitude,
								longitude, dateTime);
						points.add(point);
					}
				} catch (Exception exc) {
					// ignore corrupted lines
				}
			}
		}

		Collections.sort(points);

		return points;
	}

}
