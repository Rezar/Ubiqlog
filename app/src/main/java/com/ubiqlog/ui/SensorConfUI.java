package com.ubiqlog.ui;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ubiqlog.common.Setting;
import com.ubiqlog.core.SensorCatalouge;
import com.ubiqlog.ui.extras.controls.IntervalSelector;

import java.util.List;

public class SensorConfUI extends FrameLayout 
{
	public interface OnClosedListener {

		public abstract void onClosed();
	}
	

	private String[] data;
	private TableLayout recordLay;
	private SensorCatalouge senCat;
	private Context ctx = null;
	private String _sensorName =null;
	private OnClosedListener _closed = null;

	public SensorConfUI(Context context,String[] extras, String sensorName,OnClosedListener closed) {
		super(context);
		ctx = context;
		_sensorName = sensorName;
		_closed = closed;

		if(sensorName.equals("APPLICATION")&&(!isAccessibilityEnabled(getContext())))
		{
			this.addView(initUIApplication(extras));
		}else{
			this.addView(initUI(extras));
		}

	}
	
	private LinearLayout initUI(String[] confdata) 
	{
		
		TableLayout.LayoutParams tbllayParams = new TableLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		recordLay = new TableLayout(ctx);
		recordLay.setStretchAllColumns(true);

		for (int i = 0; i < confdata.length; i++) 
		{
			data = confdata[i].split("=");
			// first item is enable or disable
			data[1] = data[1].trim();
			if (data[1].equalsIgnoreCase("yes")
					|| data[1].equalsIgnoreCase("no")) 
			{
				CheckBox chkvalue = new CheckBox(ctx);
				chkvalue.setTextColor(Color.BLACK);
				chkvalue.setText(data[0]);

				if (data[1].equalsIgnoreCase("yes")) 
				{
					chkvalue.setChecked(true);
				}
				TableRow tr = new TableRow(ctx);
				tr.addView(chkvalue);
				recordLay.addView(tr, tbllayParams);

			} 
			else 
			{
				TextView txtName = new TextView(ctx);
				txtName.setText(data[0]); //+":"
				txtName.setTextColor(Color.BLACK);
				int val = 0;
				try{
					val = Integer.parseInt(data[1]);
				}catch(NumberFormatException f){}
				IntervalSelector sel = new IntervalSelector(ctx,val);
				//EditText edtValue = new EditText(ctx);
				//edtValue.setText(data[1]);
				//edtValue.setSingleLine(true);

				TableRow tr2 = new TableRow(ctx);
				tr2.addView(txtName);
				recordLay.addView(tr2, tbllayParams);

				TableRow tr3 = new TableRow(ctx);
				tr3.addView(sel);
				recordLay.addView(tr3, tbllayParams);
			}
		}

		Button btnSave = new Button(ctx);
		btnSave.setText("Save");
		btnSave.setOnClickListener(btnSaveListener);

		Button btnCancel = new Button(ctx);
		btnCancel.setText("Cancel");
		btnCancel.setOnClickListener(btnCancelListener);

		TableLayout tableLayoutButtons = new TableLayout(ctx);
		tableLayoutButtons.setBaselineAligned(true);
		tableLayoutButtons.setStretchAllColumns(true);
		TableRow.LayoutParams rowLayoutSingle = 
			new TableRow.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
		TableRow tableRow = new TableRow(ctx);
		tableRow.addView(btnSave, rowLayoutSingle);
		tableRow.addView(btnCancel, rowLayoutSingle);
		tableLayoutButtons.addView(tableRow, rowLayoutSingle);

		LinearLayout finalLay = new LinearLayout(ctx);
		finalLay.setOrientation(LinearLayout.VERTICAL);
		finalLay.addView(recordLay);
		finalLay.addView(tableLayoutButtons);
		finalLay.setPadding(10, 10, 10, 10);
		return finalLay;
	}

	private LinearLayout initUIApplication(String[] confdata)
	{

		TableLayout.LayoutParams tbllayParams = new TableLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		recordLay = new TableLayout(ctx);
		recordLay.setStretchAllColumns(true);

		for (int i = 0; i < confdata.length; i++)
		{
			data = confdata[i].split("=");
			// first item is enable or disable
			data[1] = data[1].trim();
			if (data[1].equalsIgnoreCase("yes")
					|| data[1].equalsIgnoreCase("no"))
			{
				CheckBox chkvalue = new CheckBox(ctx);
				chkvalue.setTextColor(Color.BLACK);
				chkvalue.setText(data[0]);


				chkvalue.setChecked(false);
				chkvalue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if(isChecked==true)
						{
							getContext().startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
							Toast.makeText(getContext(), "Please open the service application accessibility service!", Toast.LENGTH_LONG).show();
						}
					}
				});

				TableRow tr = new TableRow(ctx);
				tr.addView(chkvalue);
				recordLay.addView(tr, tbllayParams);

			}
			else
			{
				TextView txtName = new TextView(ctx);
				txtName.setText(data[0]); //+":"
				txtName.setTextColor(Color.BLACK);
				int val = 0;
				try{
					val = Integer.parseInt(data[1]);
				}catch(NumberFormatException f){}
				IntervalSelector sel = new IntervalSelector(ctx,val);
				//EditText edtValue = new EditText(ctx);
				//edtValue.setText(data[1]);
				//edtValue.setSingleLine(true);

				TableRow tr2 = new TableRow(ctx);
				tr2.addView(txtName);
				recordLay.addView(tr2, tbllayParams);

				TableRow tr3 = new TableRow(ctx);
				tr3.addView(sel);
				recordLay.addView(tr3, tbllayParams);
			}
		}

		Button btnSave = new Button(ctx);
		btnSave.setText("Save");
		btnSave.setOnClickListener(btnSaveListener);

		Button btnCancel = new Button(ctx);
		btnCancel.setText("Cancel");
		btnCancel.setOnClickListener(btnCancelListener);

		TableLayout tableLayoutButtons = new TableLayout(ctx);
		tableLayoutButtons.setBaselineAligned(true);
		tableLayoutButtons.setStretchAllColumns(true);
		TableRow.LayoutParams rowLayoutSingle =
				new TableRow.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
		TableRow tableRow = new TableRow(ctx);
		tableRow.addView(btnSave, rowLayoutSingle);
		tableRow.addView(btnCancel, rowLayoutSingle);
		tableLayoutButtons.addView(tableRow, rowLayoutSingle);

		LinearLayout finalLay = new LinearLayout(ctx);
		finalLay.setOrientation(LinearLayout.VERTICAL);
		finalLay.addView(recordLay);
		finalLay.addView(tableLayoutButtons);
		finalLay.setPadding(10, 10, 10, 10);
		return finalLay;
	}

	private View.OnClickListener btnCancelListener = new View.OnClickListener() 
	{
		public void onClick(View v) 
		{
			finish();
		}
	};

	private View.OnClickListener btnSaveListener = new View.OnClickListener() 
	{
		public void onClick(View v) 
		{
			StringBuilder newConfigData = new StringBuilder();
			int iTableChildren = recordLay.getChildCount();
			for (int i = 0; i < iTableChildren; i++) 
			{
				TableRow tr = (TableRow) recordLay.getChildAt(i);
				for (int j = 0; j < tr.getChildCount(); j++) 
				{
					if (tr.getChildAt(j).getClass().getCanonicalName()
							.equalsIgnoreCase("android.widget.CheckBox")) 
					{	// it's a CheckBox

						CheckBox chktmp = (CheckBox) tr.getChildAt(j);
						StringBuilder yesno = new StringBuilder();
						if (chktmp.isChecked() == false) 
						{
							yesno.append("no");
							if (chktmp.getText().toString().trim()
									.equalsIgnoreCase("Record Communication")) 
							{
								Setting.Instance(ctx).telLogging = false;
							}
						} else 
						{
							yesno.append("yes");
							if (chktmp.getText().toString().trim()
									.equalsIgnoreCase("Record Communication")) 
							{
								Setting.Instance(ctx).telLogging = true;
							}
						}
						newConfigData.append(chktmp.getText() + "=" + yesno);
						newConfigData.append(",");
					} 
					else if (tr.getChildAt(j).getClass().getCanonicalName().
							equalsIgnoreCase("android.widget.TextView")) 
					{ // or a TextBox
						TextView txtTmp = (TextView) tr.getChildAt(j);						

						// the value is in the next row
						int iNext = i+1;

						if(iNext < iTableChildren)
						{
							TableRow tr2 = (TableRow) recordLay.getChildAt(i+1);
							IntervalSelector edtTmp = (IntervalSelector) tr2.getChildAt(j);
							newConfigData.append(txtTmp.getText().toString().trim()
									+ "=" + edtTmp.getInterval());
							newConfigData.append(",");
						}
					}
				}
			}
			
			senCat = new SensorCatalouge(ctx);
			senCat.updateSensor(_sensorName, newConfigData.toString());
			senCat.closeDB(ctx);
			Toast.makeText(v.getContext(), "Settings have been saved!", Toast.LENGTH_SHORT).show();
			
			finish();
		}
	};
	
	private void finish() {
		_closed.onClosed();
	}

	public static boolean isAccessibilityEnabled(Context context) {
		String id ="com.ubiqlog.sensors/.ApplicationAccessibilityService";
		AccessibilityManager am = (AccessibilityManager) context
				.getSystemService(Context.ACCESSIBILITY_SERVICE);

		List<AccessibilityServiceInfo> runningServices = am
				.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
		for (AccessibilityServiceInfo service : runningServices) {
			if (id.equals(service.getId())) {
				return true;
			}
		}

		return false;
	}

}