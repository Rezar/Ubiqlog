package com.ubiqlog.vis.ui;

import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ubiqlog.ui.R;
import com.ubiqlog.vis.common.DataCollectorEvent;
import com.ubiqlog.vis.common.IDataCollectorListener;
import com.ubiqlog.vis.common.Settings;
import com.ubiqlog.vis.extras.bluetooth.BluetoothDataCollector;
import com.ubiqlog.vis.extras.bluetooth.BluetoothDataCollectorEvent;
import com.ubiqlog.vis.ui.extras.DateTimeSelector.DateTimeIntervalSelector;
import com.ubiqlog.vis.ui.extras.DateTimeSelector.DateTimePickerDialog.Type;
import com.ubiqlog.vis.ui.extras.bluetooth.BluetoothLogDataLayout;
import com.ubiqlog.vis.ui.extras.bluetooth.BluetoothLogInfoLayout;

/**
 * 
 * @author Dorin Gugonatu
 * 
 */

public class BluetoothLog extends Activity implements IDataCollectorListener 
{
	private BluetoothLogInfoLayout infoView;
	private DateTimeIntervalSelector dateSelectorView;
	private BluetoothLogDataLayout bluetoothDataLayout;
	
	private final int ID_INFO_VIEW = 1;
	private final int ID_DATA_VIEW = 2;
	private final int ID_DATE_SELECTOR_VIEW = 3;
	
	private BluetoothDataCollector bluetoothDataCollector;
	private Handler uiThreadHandler;
	private ProgressDialog progressDialog;

	private final String MAIN_FOLDER = Settings.LOG_FOLDER;
	private final String TEST_FOLDER = "TestData";
	private final String FOLDER_DELIMITER = "/";
	
	private final boolean USE_TEST_DATA = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		RelativeLayout relativeLayout = new RelativeLayout(this);
		
		Date nowDate = new Date();
		Date startDate = new Date(nowDate.getYear(), nowDate.getMonth(), nowDate.getDate(), 0, 0);
		Date endDate = new Date(nowDate.getYear(), nowDate.getMonth(), nowDate.getDate(), 23, 59);
		dateSelectorView = new DateTimeIntervalSelector(
				this, startDate, endDate, Type.DATETIME, null, onDataChanged, 
				this.getString(R.string.Vis_StartDateTime), 
				this.getString(R.string.Vis_EndDateTime).toString(), 
				true, false);
		
		dateSelectorView.setId(ID_DATE_SELECTOR_VIEW);
		
		bluetoothDataLayout = new BluetoothLogDataLayout(this);
		bluetoothDataLayout.setId(ID_DATA_VIEW);
		
		infoView = new BluetoothLogInfoLayout(this);
		infoView.setId(ID_INFO_VIEW);
		infoView.setVisibility(View.INVISIBLE);
		
		RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		relativeLayout.addView(dateSelectorView, relativeLayoutParams);
		
		relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		relativeLayout.addView(infoView, relativeLayoutParams);
		
		relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		relativeLayoutParams.addRule(RelativeLayout.ABOVE, dateSelectorView.getId());
		relativeLayoutParams.addRule(RelativeLayout.BELOW, infoView.getId());
		relativeLayout.addView(bluetoothDataLayout, relativeLayoutParams);
		
		setContentView(relativeLayout);
	}

	//@Override
	public void completed(DataCollectorEvent dataCollectorEvent) 
	{
		final BluetoothDataCollectorEvent bluetoothDataCollectorEvent = (BluetoothDataCollectorEvent)dataCollectorEvent;
		
		uiThreadHandler.post(new Runnable() 
		{
			public void run() 
			{
				progressDialog.dismiss();
				
				if (infoView.getVisibility() == View.INVISIBLE)
				{
					infoView.setVisibility(View.VISIBLE);
				}
				
				bluetoothDataLayout.setData(bluetoothDataCollectorEvent.getBluetoothContainer());
			}
		});
	}
	
	private OnClickListener onDataChanged = new OnClickListener() 
	{	
		//@Override
		public void onClick(View v) 
		{
			progressDialog = ProgressDialog.show(BluetoothLog.this, ".. Loading ..", "Loading Bluetooth Data", true, false);

			uiThreadHandler = new Handler();

			if (USE_TEST_DATA)
			{
				bluetoothDataCollector = new BluetoothDataCollector(BluetoothLog.this, MAIN_FOLDER + FOLDER_DELIMITER + TEST_FOLDER, dateSelectorView.getStartDate(), dateSelectorView.getEndDate());
			}
			else
			{
				bluetoothDataCollector = new BluetoothDataCollector(BluetoothLog.this, MAIN_FOLDER + FOLDER_DELIMITER, dateSelectorView.getStartDate(), dateSelectorView.getEndDate());
			}
			new Thread(bluetoothDataCollector).start();
			
		}
	}; 
}