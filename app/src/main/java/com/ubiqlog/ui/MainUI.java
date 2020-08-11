package com.ubiqlog.ui;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.ubiqlog.common.Setting;
import com.ubiqlog.core.Engine;
import com.ubiqlog.core.SensorCatalouge;
import com.ubiqlog.sensors.AccelerometerSensor;
import com.ubiqlog.sensors.ApplicationSensor;
import com.ubiqlog.sensors.AudioSensor;
import com.ubiqlog.sensors.BatterySensor;
import com.ubiqlog.sensors.BluetoothSensor;
import com.ubiqlog.sensors.CallSensor;
import com.ubiqlog.sensors.HardwareSensor_OLD;
import com.ubiqlog.sensors.InteractionSensor;
import com.ubiqlog.sensors.LocationSensor;
import com.ubiqlog.sensors.PictureSensor;
import com.ubiqlog.sensors.SMSSensor;
import com.ubiqlog.sensors.SensorObj;
import com.ubiqlog.sensors.SleepSensor;
import com.ubiqlog.sensors.WearSensor;
import com.ubiqlog.sensors.WiFiSensor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;


public class MainUI extends Activity {
	TextView tv_status = null;
	Context ctx = null;
	ScrollView sv_info = null;
	LinearLayout contentLayout = null;
	ImageView img_settings = null;
	TextView tv_sensors = null;
	TextView tv_tools = null;
	ToggleButton status_btn = null;


	private static final int MENU_HOME = 1;
	private static final int MENU_SETTINGS = 2;
	private static final int MENU_TOOLS = 3;
	private static final int MENU_ABOUT = 6;
	private static final int MENU_SENSORS = 4;
	private static final int MENU_EXIT = 5;

	private SensorCatalouge senCat;

	/** Called when the activity is first created. */
	@SuppressLint("ResourceType")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// init the setting with context
		com.ubiqlog.common.Setting.Instance(this);

		senCat = new SensorCatalouge(this);
		senCat.setUpinitCatalouge();

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		ctx = this;
		LinearLayout mainLayout = new LinearLayout(this);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.setBackgroundColor(Color.WHITE);

		TableLayout tbl_header = new TableLayout(this);
		tbl_header.setColumnShrinkable(0, true);
		tbl_header.setColumnStretchable(0, true);

		TableRow tr_header = new TableRow(this);

		RelativeLayout lin_lay_header = new RelativeLayout(this);

		ImageView img_icon = new ImageView(this);
		img_icon.setImageDrawable(getResources().getDrawable(R.drawable.logo));
		img_icon.setScaleType(ScaleType.CENTER);
		RelativeLayout.LayoutParams lp_img_icon = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		img_icon.setLayoutParams(lp_img_icon);
		img_icon.setId(44141);
		img_icon.setOnClickListener(logo_Clicked);

		img_settings = new ImageView(this);
		img_settings.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.settings_background));
		img_settings.setScaleType(ScaleType.CENTER);

		RelativeLayout.LayoutParams lp_img_settings = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
		lp_img_settings.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		img_settings.setLayoutParams(lp_img_settings);
		img_settings.setId(735353);
		img_settings.setOnClickListener(settings_Clicked);

		ImageView img_line = new ImageView(this);
		img_line.setImageDrawable(getResources()
				.getDrawable(R.drawable.line1px));
		img_line.setScaleType(ScaleType.CENTER_CROP);
		RelativeLayout.LayoutParams lp_img_line = new RelativeLayout.LayoutParams(
				1, LayoutParams.WRAP_CONTENT);
		lp_img_line.addRule(RelativeLayout.LEFT_OF, img_settings.getId());
		img_line.setLayoutParams(lp_img_line);

		lin_lay_header.addView(img_icon);
		lin_lay_header.addView(img_settings);
		lin_lay_header.addView(img_line);

		tr_header.addView(lin_lay_header);

		TableLayout tbl_subHeader = new TableLayout(this);

		tbl_subHeader.setColumnStretchable(0, true);
		tbl_subHeader.setColumnShrinkable(0, true);
		tbl_subHeader.setColumnStretchable(1, true);
		tbl_subHeader.setColumnShrinkable(1, true);

		TableRow tr_subHeader = new TableRow(this);
		tr_subHeader.setBackgroundColor(Color.parseColor("#C8C8C8"));
		tr_subHeader.setPadding(0, 1, 0, 1); // Border between rows

		TableRow.LayoutParams llp = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		llp.setMargins(0, 0, 1, 0);// 2px right-margin

		tv_sensors = new TextView(this);
		tv_sensors.setText("Sensors");
		tv_sensors.setTextSize(20);
		tv_sensors.setGravity(Gravity.CENTER);
		tv_sensors.setTypeface(Typeface.DEFAULT_BOLD);
		tv_sensors.setTextColor(Color.BLACK);
		tv_sensors.setBackgroundDrawable(getResources().getDrawable(R.drawable.textview_background));
		tv_sensors.setLayoutParams(llp);
		tv_sensors.setOnClickListener(sensors_Clicked);

		tv_tools = new TextView(this);
		tv_tools.setText("Tools");
		tv_tools.setTextSize(20);
		tv_tools.setGravity(Gravity.CENTER);
		tv_tools.setTypeface(Typeface.DEFAULT_BOLD);
		tv_tools.setTextColor(Color.BLACK);
		tv_tools.setBackgroundColor(Color.WHITE);
		tv_tools.setBackgroundDrawable(getResources().getDrawable(R.drawable.textview_background));
		tv_tools.setOnClickListener(tools_Clicked);

		tr_subHeader.addView(tv_sensors);
		tr_subHeader.addView(tv_tools);

		tbl_header.addView(tr_header, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		tbl_subHeader.addView(tr_subHeader, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		mainLayout.addView(tbl_header, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mainLayout.addView(tbl_subHeader, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		contentLayout = new LinearLayout(this);
		sv_info = new ScrollView(this);
		contentLayout.setBackgroundColor(Color.parseColor("#E8E8E8"));

		mainLayout.addView(contentLayout, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1));

		tv_status = new TextView(this);
		tv_status.setText("UbiqLog is running now.");
		tv_status.setPadding(0, 10, 0, 0);
		tv_status.setTypeface(Typeface.DEFAULT_BOLD);
		tv_status.setTextColor(Color.BLACK);
		tv_status.setBackgroundColor(Color.WHITE);
		tv_status.setGravity(Gravity.CENTER);

		status_btn = new ToggleButton(this);

		status_btn.setTextOn("Click here to stop Ubiqlog.");
		status_btn.setTextOff("Click here to start Ubiqlog.");
		status_btn.toggle();
		status_btn.setChecked(isUbiqlogRunning());

		status_btn.setOnClickListener(btnStartStopListener);
		LoadDefaultContent();
		mainLayout.addView(status_btn);

		setContentView(mainLayout);
	}

	private boolean isUbiqlogRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (UbiqlogStatusBar.class.getName().equals(service.service.getClassName())
					|| CallSensor.class.getName().equals(service.service.getClassName())
					|| SMSSensor.class.getName().equals(service.service.getClassName())
					|| ApplicationSensor.class.getName().equals(service.service.getClassName())
					|| LocationSensor.class.getName().equals(service.service.getClassName())
					|| WiFiSensor.class.getName().equals(service.service.getClassName())
					|| BluetoothSensor.class.getName().equals(service.service.getClassName())
					|| HardwareSensor_OLD.class.getName().equals(service.service.getClassName())
					|| PictureSensor.class.getName().equals(service.service.getClassName())
					|| AudioSensor.class.getName().equals(service.service.getClassName())
					|| AccelerometerSensor.class.getName().equals(service.service.getClassName())
					|| BatterySensor.class.getName().equals(service.service.getClassName())
					|| InteractionSensor.class.getName().equals(service.service.getClassName())
					) { //|| ActivitySensor.class.getName().equals(service.service.getClassName())
				return true;
			}
		}

		return false;
	}

	private void ResetStates() {
		tv_sensors.setSelected(false);
		tv_tools.setSelected(false);
		img_settings.setSelected(false);
	}

	private void LoadDefaultContent() {
		ResetStates();
		sv_info.removeAllViews();

		TextView tv_info = new TextView(this);
		tv_info.setTextColor(Color.BLACK);

		tv_info.setPadding(20, 20, 20, 20);
		tv_info.setTextSize(20);

		String enabledSensors = "";

		try {
			SensorCatalouge senCat = new SensorCatalouge(this);
			ArrayList<SensorObj> allsens;
			allsens = senCat.getAllSensors();
			for (int i = 0; i < allsens.size(); i++) {
				String[] configdata = allsens.get(i).getConfigData();
				String[] isenable = configdata[0].split("=");
				isenable[1] = isenable[1].trim();
				if (isenable[1].equalsIgnoreCase("yes")) {
					enabledSensors += " - " + allsens.get(i).getSensorName()+ "\n";
				}
			}
		} catch (Exception e) {
			tv_info.setText("There is a problem retrieving the data!\n");
			e.printStackTrace();
		}

		//
		if (enabledSensors != "") {
			tv_info.setText("Following sensors are active:\n" + enabledSensors);
		} else {
			tv_info.setText("No sensor is active!");
		}

		status_btn.setVisibility(View.VISIBLE);
		sv_info.addView(tv_info);
		contentLayout.removeAllViews();
		contentLayout.addView(sv_info, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}

	
	public void onResume() {
		super.onResume();
		if (status_btn != null) {
			status_btn.setChecked(isUbiqlogRunning());
		}

		senCat.closeDB(getApplicationContext());
	}

	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, MENU_HOME, Menu.NONE, "HOME");
		menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, "SETTINGS");
		menu.add(Menu.NONE, MENU_ABOUT, Menu.NONE, "ABOUT");
		menu.add(Menu.NONE, MENU_SENSORS, Menu.NONE, "SENSORS");
		menu.add(Menu.NONE, MENU_TOOLS, Menu.NONE, "TOOLS");
		menu.add(Menu.NONE, MENU_EXIT, Menu.NONE, "EXIT");

		return true;
	}

	private View.OnClickListener btnStartStopListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			if(Build.VERSION.SDK_INT >= 23)
			{
				if (((ToggleButton) v).isChecked()) {
					Log.e("MainUI", "----Start Logging Sensors");
					requestPermissions(new String[]{"android.permission.READ_SMS",
							"android.permission.READ_CALL_LOG",
							"android.permission.READ_CONTACTS",
							"android.permission.READ_PHONE_STATE",
							"android.permission.RECORD_AUDIO",
							"android.permission.WRITE_EXTERNAL_STORAGE",
							"android.permission.INTERNET",
							"android.permission.LOCATION",
							"android.permission.ACCESS_FINE_LOCATION",
							"android.permission.ACCESS_COARSE_LOCATION",
							"android.permission.BLUETOOTH",
							"android.permission.BLUETOOTH_ADMIN","android.permission.CAMERA",
							"android.permission.ACCESS_NETWORK_STATE",
							"android.permission.GET_TASKS",
							"android.permission.ACCESS_WIFI_STATE",
							"android.permission.CHANGE_WIFI_STATE",
							"com.google.android.gms.permission.ACTIVITY_RECOGNITION",
							"android.permission.RECEIVE_BOOT_COMPLETED",
							"android.permission.READ_EXTERNAL_STORAGE"},0);
				} else {
					Log.e("MainUI", "----Stop Logging Sensors");
					stopAllService();
					tv_status.setText("UbiqLog is not running now.");
				}

				((ToggleButton) v).setChecked(isUbiqlogRunning());
			}else{
				if (((ToggleButton) v).isChecked()) {
					Log.e("MainUI", "----Start Logging Sensors");
					startAllService();
					tv_status.setText("UbiqLog is running now.");
				} else {
					Log.e("MainUI", "----Stop Logging Sensors");
					stopAllService();
					tv_status.setText("UbiqLog is not running now.");
				}

				((ToggleButton) v).setChecked(isUbiqlogRunning());
			}

		}
	};

	private void stopAllService() {
		
		Engine.stopRecording(getApplicationContext(), this.getApplication());
		Intent stopSleep = new Intent(this, SleepSensor.class);
		stopService(stopSleep);

		Intent stopwear = new Intent(this, WearSensor.class);
		stopService(stopwear);

		Intent notiI = new Intent();
		notiI.setClass(getApplication(), UbiqlogStatusBar.class);
		stopService(notiI);

		String msgToast = "Stop the Logging Process.";
		Toast toast = Toast.makeText(getApplicationContext(), msgToast, Toast.LENGTH_SHORT);
		toast.show();
	}

	private void startAllService() {
		try {
			Engine.startRecording(getApplicationContext(), getApplication());
			if (!Setting.SILENT_MODE){ // silent mode is on thus no status bar will be shown
				Intent notiI = new Intent();
				notiI.setClass(getApplication(), UbiqlogStatusBar.class);
				startService(notiI);
			}

			String msgToast = "Start the Logging Process.";
			Toast toast = Toast.makeText(getApplicationContext(), msgToast, Toast.LENGTH_SHORT);
			toast.show();

		} catch (Exception e) {
			Log.e("MainUI", "-----------ERROR:" + e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		super.onMenuItemSelected(featureId, item);
		switch (item.getItemId()) {
		case MENU_HOME:
			LoadDefaultContent();
			break;
		case MENU_SETTINGS:
			LoadSettingsUI();
			break;
		case MENU_SENSORS:
			LoadSensorUI();
			break;
		case MENU_TOOLS:
			LoadToolsUI();
			break;
		case MENU_ABOUT:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			try {
				PackageInfo pinfo = getPackageManager().getPackageInfo(
						getPackageName(), 0);
				String dateTimeString = "unknown";
				Properties props = new Properties();
				try {
					props.load(getAssets().open("config.properties"));
					dateTimeString = props.getProperty("projectbuildDate");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				builder.setMessage(
						"Version Number: " + pinfo.versionName
								+ "\nVersion Code: " + pinfo.versionCode
								+ "\nDate: " + dateTimeString)
						.setCancelable(false).setNeutralButton("OK", null);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			AlertDialog alert = builder.create();
			alert.show();
			break;
		case MENU_EXIT:
			setResult(RESULT_CANCELED);
			finish();
			break;

		}

		return true;
	}

	View.OnClickListener logo_Clicked = new View.OnClickListener() {
		public void onClick(View v) {
			LoadDefaultContent();
		}
	};

	View.OnClickListener tools_Clicked = new View.OnClickListener() {
		public void onClick(View v) {
			LoadToolsUI();
		}
	};

	View.OnClickListener settings_Clicked = new View.OnClickListener() {
		public void onClick(View v) {
			LoadSettingsUI();
		}
	};

	View.OnClickListener sensors_Clicked = new View.OnClickListener() {
		public void onClick(View v) {
			LoadSensorUI();
		}
	};

	private void LoadToolsUI() {
		status_btn.setVisibility(View.GONE);

		ToolsUI s = new ToolsUI(ctx);

		contentLayout.removeAllViews();
		contentLayout.addView(s, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));

		ResetStates();
		tv_tools.setSelected(true);
	}

	private void LoadSettingsUI() {
		status_btn.setVisibility(View.GONE);

		SettingUI s = new SettingUI(ctx);
		contentLayout.removeAllViews();
		contentLayout.addView(s, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		ResetStates();
		img_settings.setSelected(true);
	}

	private void LoadSensorUI() {
		status_btn.setVisibility(View.GONE);

		SensorsUI s = new SensorsUI(ctx);
		contentLayout.removeAllViews();
		contentLayout.addView(s, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		ResetStates();
		tv_sensors.setSelected(true);
	}
	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case 0: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startAllService();
                    tv_status.setText("UbiqLog is running now.");
					// permission was granted, yay! Do the
					// contacts-related task you need to do.
					Log.e("grant", "user granted the permission!");

				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
					Log.e("grant", "user denied the permission!");
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}

}