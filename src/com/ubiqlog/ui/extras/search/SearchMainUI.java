package com.ubiqlog.ui.extras.search;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ubiqlog.ui.R;
import com.ubiqlog.core.SensorCatalouge;
import com.ubiqlog.sensors.SensorObj;

/*
 * @author      Soheil KHOSRAVIPOUR
 * @date		07.2010
 * @version     0.9
 */
public class SearchMainUI extends FrameLayout {

	public interface OnClosedListener {

		public abstract void onClosed();
	}
	
	private SensorCatalouge senCat = null;
	private ArrayList<SensorObj> sens;
	private String[] sensorNames;
	private CharSequence[] sensors;
	private ArrayList<String> sensorArrayString = new ArrayList<String>();
	private boolean[] checkedSensors; // = Default:
										// {false,false,false,false,false,false,false,false,false};

	public static TextView MSENSORDISPLAY;
	public static Button MPICKSENSOR;
	private static final int SENSOR_DIALOG_ID = 0;

	public static TextView MDATEDISPLAY1;
	public static Button MPICKDATE1;
	private int mYear1;
	private int mMonth1;
	private int mDay1;
	private static final int DATE_DIALOG_01_ID = 1;

	public static TextView MDATEDISPLAY2;
	public static Button MPICKDATE2;
	private int mYear2;
	private int mMonth2;
	private int mDay2;
	private static final int DATE_DIALOG_02_ID = 2;

	public static EditText MKEYWORDPICKER;
	public static TextView SEARCHING;
	public static TextView SENSORINFO;
	public static TextView DATEDISPLAYINFO1;
	public static TextView DATEDISPLAYINFO2;
	public static TextView SEARCHLABLE;
	public static Button SEARCHB;
	public static Button CANCELB;
	
	public Context ctx = null;
	private OnClosedListener _closed = null;
	

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener1 = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear1 = year;
			mMonth1 = monthOfYear;
			mDay1 = dayOfMonth;
			updateDateDisplay1();
		}
	};

	private DatePickerDialog.OnDateSetListener mDateSetListener2 = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear2 = year;
			mMonth2 = monthOfYear;
			mDay2 = dayOfMonth;
			updateDateDisplay2();
		}
	};
	
	public SearchMainUI(Context context, OnClosedListener closed) {
		super(context);
		ctx = context;	
		_closed = closed;
		senCat = new SensorCatalouge(ctx);
		
		try {
			sens = senCat.getAllSensors();
			sensorNames = new String[sens.size()];
			for (int i = 0; i < sens.size(); i++) {
				sensorNames[i] = sens.get(i).getSensorName();
				sensors = new CharSequence[sensorNames.length];
				checkedSensors = new boolean[sensorNames.length];
			}
			
			LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View v  = inflater.inflate(R.layout.search_main_ui, null);
			addView(v);

			// Search Button
			SEARCHB = (Button) findViewById(R.id.searchButton);

			// Cancel Button
			CANCELB = (Button) findViewById(R.id.cancelButton);

			// Searching Message
			SEARCHING = (TextView) findViewById(R.id.searching);
			SEARCHING.setText("");

			// Info TextViews
			SENSORINFO = (TextView) findViewById(R.id.sensorInfo);
			DATEDISPLAYINFO1 = (TextView) findViewById(R.id.dateDisplayInfo1);
			DATEDISPLAYINFO2 = (TextView) findViewById(R.id.dateDisplayInfo2);
			SEARCHLABLE = (TextView) findViewById(R.id.searchLable);

			// capture our View elements
			MSENSORDISPLAY = (TextView) findViewById(R.id.sensorDisplay);
			MPICKSENSOR = (Button) findViewById(R.id.pickSensor);

			// capture our View elements
			MDATEDISPLAY1 = (TextView) findViewById(R.id.dateDisplay1);
			MPICKDATE1 = (Button) findViewById(R.id.pickDate1);

			// capture our View elements
			MDATEDISPLAY2 = (TextView) findViewById(R.id.dateDisplay2);
			MPICKDATE2 = (Button) findViewById(R.id.pickDate2);

			// capture our View elements
			MKEYWORDPICKER = (EditText) findViewById(R.id.pickKeyword);

			/*
			 * initialize the sensors //4 search You can use these lines in
			 * order to change the default searched Sensors You must care to
			 * other side effects (checkedSensors).
			 */
			// for(int i=0; i<sensorNames.length;i++)
			// {
			// sensorArrayString.add(sensorNames[i]);
			// }
			// initialize the sensors //Dialog
			for (int i = 0; i < sensorNames.length; i++) {
				checkedSensors[i] = false;
			}

			// initialize the sensors //Dialog
			for (int i = 0; i < sensorNames.length; i++) {
				sensors[i] = sensorNames[i];
			}

			// initialize the sensors // display
			String sensorDisplayUpdateTemp = "";
			for (int i = 0; i < checkedSensors.length; i++) {
				if (checkedSensors[i]) {
					if (sensorDisplayUpdateTemp.length() == 0) {
						sensorDisplayUpdateTemp += sensorNames[i]; // example: "SMS" Without:","
					} else {
						sensorDisplayUpdateTemp = sensorDisplayUpdateTemp+ ", " + sensorNames[i]; // example:"Application, SMS"
					}
				}
			}
			if (sensorDisplayUpdateTemp.equals("")) {
				sensorDisplayUpdateTemp = "None! Please select at least one sensor.";
			}
			
			
			MSENSORDISPLAY.setText(sensorDisplayUpdateTemp);


			// add a click listener to the button
			MPICKSENSOR.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					showDialog(SENSOR_DIALOG_ID);
				}
			});

			// add a click listener to the button
			MPICKDATE1.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					showDialog(DATE_DIALOG_01_ID);
				}
			});

			// add a click listener to the button
			MPICKDATE2.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					showDialog(DATE_DIALOG_02_ID);
				}
			});

			SEARCHB.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {

					// Passing data to the searchResultlist view
					Intent myIntent = new Intent(view.getContext(),SearchResultListUI.class);
					myIntent.putExtra("sensors", sensorArrayString);
					myIntent.putExtra("date1", MDATEDISPLAY1.getText().toString());
					myIntent.putExtra("date2", MDATEDISPLAY2.getText().toString());
					myIntent.putExtra("keyword", MKEYWORDPICKER.getText().toString());

					// Disabling the view elements
					MPICKSENSOR.setEnabled(false);
					MPICKDATE1.setEnabled(false);
					MPICKDATE2.setEnabled(false);
					SEARCHB.setEnabled(false);
					CANCELB.setEnabled(false);
					MKEYWORDPICKER.setEnabled(false);
					MDATEDISPLAY1.setEnabled(false);
					MDATEDISPLAY2.setEnabled(false);
					MSENSORDISPLAY.setEnabled(false);
					SENSORINFO.setEnabled(false);
					DATEDISPLAYINFO1.setEnabled(false);
					DATEDISPLAYINFO2.setEnabled(false);
					SEARCHLABLE.setEnabled(false);
					SEARCHING.setText("Searching...");
					
					ctx.startActivity(myIntent);
				}

			});

			CANCELB.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					finish();
				}

			});

			// get the current date
			final Calendar c1 = Calendar.getInstance();
			mYear1 = c1.get(Calendar.YEAR);
			mMonth1 = c1.get(Calendar.MONTH);
			mDay1 = c1.get(Calendar.DAY_OF_MONTH);

			final Calendar c2 = Calendar.getInstance();
			mYear2 = c2.get(Calendar.YEAR);
			mMonth2 = c2.get(Calendar.MONTH);
			mDay2 = c2.get(Calendar.DAY_OF_MONTH);

			// display the current date (this method is below)
			updateDateDisplay1();
			updateDateDisplay2();

		} catch (Exception e) {
			e.printStackTrace();
			Log.e("SensorsUI", "------Can not read Sensor List-------"
					+ e.getLocalizedMessage());
		}
	}



	// updates the date in the TextView
	private void updateDateDisplay1() {
		MDATEDISPLAY1.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(mMonth1 + 1).append("-").append(mDay1).append("-")
				.append(mYear1).append(" "));
	}

	// updates the date in the TextView
	private void updateDateDisplay2() {
		MDATEDISPLAY2.setText(new StringBuilder().append(mMonth2 + 1).append("-").append(mDay2).append("-").append(mYear2).append(" "));
	}
	
	
	private void finish() {
		_closed.onClosed();
	}


	protected void showDialog(int id) {
		switch (id) {
		case DATE_DIALOG_01_ID:
			DatePickerDialog d =  new DatePickerDialog(ctx, mDateSetListener1, mYear1,mMonth1, mDay1);
			d.show();
			break;
		case DATE_DIALOG_02_ID:
			DatePickerDialog dd = new DatePickerDialog(ctx, mDateSetListener2, mYear2,mMonth2, mDay2);
			dd.show();
			break;
		case SENSOR_DIALOG_ID:

			AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
			builder.setTitle("Select Sensors:");
			builder.setMultiChoiceItems(sensors, checkedSensors,
					new DialogInterface.OnMultiChoiceClickListener() {
						public void onClick(DialogInterface dialog,int whichButton, boolean isChecked) {
							/* Something on click of the check box */
						}
					}).setCancelable(false).setPositiveButton(R.string.alert_dialog_set,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int whichButton) {
							/* User clicked set */
							ArrayList<String> sensorCheck = new ArrayList<String>();
							String sensorDisplayUpdate = "";

							for (int i = 0; i < checkedSensors.length; i++) {
								if (checkedSensors[i]) {
									sensorCheck.add(sensorNames[i]);
									if (sensorDisplayUpdate.length() == 0) {
										sensorDisplayUpdate += sensorNames[i]; // example: "SMS" Without: ","
									} else {
										sensorDisplayUpdate = sensorDisplayUpdate+ ", " + sensorNames[i]; // example: "Application, SMS"
									}
								}
							}
							if (sensorDisplayUpdate.length() == 0)// No sensor is  selected!
							{
								sensorDisplayUpdate = "None! Please select at least one sensor.";  
							}
							sensorArrayString = sensorCheck;
							MSENSORDISPLAY.setText(sensorDisplayUpdate);

						}
					});
			AlertDialog alert = builder.create();
			alert.show();
			break;

		}
	}
}