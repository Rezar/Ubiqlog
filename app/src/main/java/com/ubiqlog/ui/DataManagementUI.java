package com.ubiqlog.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ubiqlog.common.Setting;
import com.ubiqlog.utils.SpaceManager;

public class DataManagementUI extends FrameLayout 
{
	public interface OnClosedListener {

		public abstract void onClosed();
	}
	
	private EditText editWritingInterval;
	private EditText editMaxSize;
	private ProgressBar progSize;
	private Context ctx = null;
	private OnClosedListener _closed = null;
	
	public DataManagementUI(Context context,OnClosedListener closed) {
		super(context);
		ctx = context;
		_closed = closed;
		
		final int ID_TEXT_WRITING_INTERVAL = 1;
		final int ID_EDIT_WRITING_INTERVAL = 2;
		final int ID_TEXT_MAX_SIZE = 3;
		final int ID_EDIT_MAX_SIZE = 4;
		final int ID_TEXT_PROGRESS = 5;

		TextView textWritingInterval = new TextView(ctx);
		textWritingInterval.setText("File Writing Intervals in m.s.");
		textWritingInterval.setId(ID_TEXT_WRITING_INTERVAL);
		textWritingInterval.setTextColor(Color.BLACK);
		editWritingInterval = new EditText(ctx);
		
		editWritingInterval.setText(Setting.daggSav2File);
		editWritingInterval.setSingleLine(true);
		editWritingInterval.setId(ID_EDIT_WRITING_INTERVAL);

		TextView textMaxSize = new TextView(ctx);
		textMaxSize.setText("Maximum Threshold to Keep Data Files on the Disk");
		textMaxSize.setTextColor(Color.BLACK);
		textMaxSize.setId(ID_TEXT_MAX_SIZE);
		editMaxSize = new EditText(ctx);
		
		editMaxSize.setText(Setting.maxDataSize);
		editMaxSize.setSingleLine(true);
		editMaxSize.setId(ID_EDIT_MAX_SIZE);

		TextView textProgress = new TextView(ctx);
		textProgress.setText("Disk Space Status");
		textProgress.setTextColor(Color.BLACK);
		textProgress.setId(ID_TEXT_PROGRESS);
		progSize = new ProgressBar(ctx, null,android.R.attr.progressBarStyleHorizontal);
		SetProgressBar(new SpaceManager.CapacityHolder(Setting.maxDataSize));
		progSize.setHorizontalFadingEdgeEnabled(true);

		Button btnSave = new Button(ctx);
		btnSave.setText("Save");
		btnSave.setOnClickListener(btnSaveListener);

		Button btnCancel = new Button(ctx);
		btnCancel.setText("Cancel");
		btnCancel.setOnClickListener(btnCancelListener);

		TableLayout tableLayoutSaveCancel = new TableLayout(ctx);
		tableLayoutSaveCancel.setBaselineAligned(true);
		tableLayoutSaveCancel.setStretchAllColumns(true);
		TableRow.LayoutParams rowLayoutSingle = new TableRow.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT);
		TableRow tableRow = new TableRow(ctx);
		tableRow.addView(btnSave, rowLayoutSingle);
		tableRow.addView(btnCancel, rowLayoutSingle);
		tableLayoutSaveCancel.addView(tableRow, rowLayoutSingle);
		
		RelativeLayout relativeLayout = new RelativeLayout(ctx);
		
		RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		relativeLayout.addView(textWritingInterval, relativeLayoutParams);
		
		relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		relativeLayoutParams.addRule(RelativeLayout.BELOW, textWritingInterval.getId());
		relativeLayout.addView(editWritingInterval, relativeLayoutParams);
		
		relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		relativeLayoutParams.addRule(RelativeLayout.BELOW, editWritingInterval.getId());
		relativeLayout.addView(textMaxSize, relativeLayoutParams);
		
		relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		relativeLayoutParams.addRule(RelativeLayout.BELOW, textMaxSize.getId());
		relativeLayout.addView(editMaxSize, relativeLayoutParams);
		
		relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		relativeLayoutParams.addRule(RelativeLayout.BELOW, editMaxSize.getId());
		relativeLayout.addView(textProgress, relativeLayoutParams);
		
		relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		relativeLayoutParams.addRule(RelativeLayout.BELOW, textProgress.getId());
		relativeLayout.addView(progSize, relativeLayoutParams);

		relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		relativeLayout.addView(tableLayoutSaveCancel, relativeLayoutParams);
		
		addView(relativeLayout);
	}

	
	
	private void SetProgressBar(SpaceManager.CapacityHolder capacityHolder)
	{
		int maxSize = capacityHolder.getCapacity() * capacityHolder.getCapacityFactor();
		
		SpaceManager.DirectorySize directorySize = new SpaceManager.DirectorySize(Setting.LOG_FOLDER);
		int dirSize = (int)directorySize.getSize();
		
		if (dirSize > maxSize)
		{
			dirSize = maxSize;
		}
		
		progSize.setMax(maxSize);
		progSize.setProgress(dirSize);
	}

	private View.OnClickListener btnSaveListener = new View.OnClickListener() 
	{
		public void onClick(View v) 
		{
			Setting.daggSav2File = editWritingInterval.getText().toString();
			Setting.maxDataSize = editMaxSize.getText().toString();

			SetProgressBar(new SpaceManager.CapacityHolder(Setting.maxDataSize));
			
			finish();
		}
	};
	
	private View.OnClickListener btnCancelListener = new View.OnClickListener() 
	{
		public void onClick(View v) 
		{
			finish();
		}

		
	};
	
	private void finish() {
		_closed.onClosed();
	}
}
