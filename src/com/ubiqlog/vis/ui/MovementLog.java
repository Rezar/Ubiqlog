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
import com.ubiqlog.vis.extras.movement.MovementDataCollector;
import com.ubiqlog.vis.extras.movement.MovementDataCollectorEvent;
import com.ubiqlog.vis.ui.extras.DateTimeSelector.DateTimeIntervalSelector;
import com.ubiqlog.vis.ui.extras.DateTimeSelector.DateTimePickerDialog.Type;
import com.ubiqlog.vis.ui.extras.movement.MovementLogInfoLayout;
import com.ubiqlog.vis.ui.extras.movement.MovementLogScrollView;
import com.ubiqlog.vis.ui.extras.movement.MovementLogView;

/**
 * 
 * @author Dorin Gugonatu
 * 
 */

public class MovementLog extends Activity implements IDataCollectorListener 
{
	private MovementLogView movementLogDataView;
	private MovementLogInfoLayout infoView;
	private DateTimeIntervalSelector dateSelectorView;
	
	
	private final int ID_INFO_VIEW = 1;
	private final int ID_DATA_VIEW = 2;
	private final int ID_DATE_SELECTOR_VIEW = 3;
	
	private MovementDataCollector movementDataCollector;
	private Handler uiThreadHandler;
	private ProgressDialog progressDialog;

	private final String MAIN_FOLDER = Settings.LOG_FOLDER;
	//private final String TEST_FOLDER = "TestData";
	private final String FOLDER_DELIMITER = "/";
	
	//private final boolean USE_TEST_DATA = false;

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
		
		MovementLogScrollView movementScrollView = new MovementLogScrollView(this);
		movementScrollView.setId(ID_DATA_VIEW);
		
		infoView = new MovementLogInfoLayout(this);
		infoView.setId(ID_INFO_VIEW);
		infoView.setVisibility(View.INVISIBLE);
		
		movementLogDataView = new MovementLogView(this);
		movementScrollView.addView(movementLogDataView);
		movementScrollView.addZoomNotificationListener(movementLogDataView);
		
		
		RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		relativeLayout.addView(dateSelectorView, relativeLayoutParams);
		
		relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		relativeLayout.addView(infoView, relativeLayoutParams);
		
		relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		relativeLayoutParams.addRule(RelativeLayout.ABOVE, dateSelectorView.getId());
		relativeLayoutParams.addRule(RelativeLayout.BELOW, infoView.getId());
		relativeLayout.addView(movementScrollView, relativeLayoutParams);
		
		setContentView(relativeLayout);
	}

	//@Override
	public void completed(DataCollectorEvent dataCollectorEvent) 
	{
		final MovementDataCollectorEvent movementDataCollectorEvent = (MovementDataCollectorEvent)dataCollectorEvent;
		
		uiThreadHandler.post(new Runnable() 
		{
			public void run() 
			{
				progressDialog.dismiss();
				
				if (infoView.getVisibility() == View.INVISIBLE)
				{
					infoView.setVisibility(View.VISIBLE);
				}

				movementLogDataView.setData(movementDataCollectorEvent.getMovementContainer());
			}
		});
	}
	
	private OnClickListener onDataChanged = new OnClickListener() 
	{	
		//@Override
		public void onClick(View v) 
		{
			progressDialog = ProgressDialog.show(MovementLog.this, "Loading ...", "Loading Movement Data", true, false);

			uiThreadHandler = new Handler();

//			if (USE_TEST_DATA)
//			{
//				movementDataCollector = new MovementDataCollector(MovementLog.this, MAIN_FOLDER + FOLDER_DELIMITER + TEST_FOLDER, dateSelectorView.getStartDate(), dateSelectorView.getEndDate());
//			}
//			else
//			{
			movementDataCollector = new MovementDataCollector(MovementLog.this, MAIN_FOLDER + FOLDER_DELIMITER, dateSelectorView.getStartDate(), dateSelectorView.getEndDate());
//			}
			new Thread(movementDataCollector).start();
			
		}
	}; 
}