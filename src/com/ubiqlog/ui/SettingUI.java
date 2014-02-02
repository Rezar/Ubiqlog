package com.ubiqlog.ui;


import com.ubiqlog.core.ArchiveableCheck;


import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class SettingUI extends FrameLayout {

	Context ctx = null;
	LinearLayout listLay = null;
	
	public SettingUI(Context context) {
		super(context);
		ctx = context;

		LoadDefaultContent();
		
	}
	
	private void LoadDefaultContent()
    {
		LinearLayout.LayoutParams lineLayParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		listLay = new TableLayout(ctx);
		listLay.setOrientation(LinearLayout.VERTICAL);

		TextView txtData = new TextView(ctx);
		txtData.setTextColor(Color.BLACK);
		txtData.setText("To Manage the Dataset click this button.");
		txtData.setGravity(Gravity.LEFT);

		Button btnData = new Button(ctx);
		btnData.setText(" Data Management ");
		btnData.setOnClickListener(btnDataListener);

		TextView txtNetwork = new TextView(ctx);
		txtNetwork.setTextColor(Color.BLACK);
		txtNetwork.setText("To configure the network settings click this button.");
		txtNetwork.setGravity(Gravity.LEFT);

		Button btnServer = new Button(ctx);
		btnServer.setText("  Network Settings  ");
		btnServer.setOnClickListener(btnNetworkListener);

		TextView txtArchive = new TextView(ctx);
		txtArchive.setTextColor(Color.BLACK);
		txtArchive.setText("To evaluate if the binary files are longterm preservable click this button.");
		txtArchive.setGravity(Gravity.LEFT);

		Button btnArchive = new Button(ctx);
		btnArchive.setText("Generate Report");
		btnArchive.setOnClickListener(btnArchiveListener);
		
		TextView txtExclude = new TextView(ctx);
		txtExclude.setTextColor(Color.BLACK);
		txtExclude.setText("To exclude applications from being monitored.");
		txtExclude.setGravity(Gravity.LEFT);

		Button btnExclude = new Button(ctx);
		btnExclude.setText(" Exclude Application ");
		btnExclude.setOnClickListener(btnExcludeListener);

		listLay.addView(txtData, lineLayParams);
		listLay.addView(btnData, lineLayParams);
		listLay.addView(txtNetwork, lineLayParams);
		listLay.addView(btnServer, lineLayParams);
		listLay.addView(txtArchive, lineLayParams);
		listLay.addView(btnArchive, lineLayParams);
		listLay.addView(txtExclude, lineLayParams);
		listLay.addView(btnExclude, lineLayParams);
		
		
		listLay.setPadding(10, 10, 10, 10);

		// LinearLayout recordLay = new LinearLayout(this);
		// recordLay.setOrientation(LinearLayout.HORIZONTAL);
		// recordLay.setBaselineAligned(true);
		this.removeAllViews();
		
		addView(listLay);
    
    }


	private View.OnClickListener btnDataListener = new View.OnClickListener() {
		public void onClick(View v) {
			
			DataManagementUI man = new DataManagementUI(ctx, dataManagementClosed);
		    
			removeAllViews();
			man.setPadding(10, 10, 10, 10);
			addView(man);
		}
	};
	
	DataManagementUI.OnClosedListener dataManagementClosed = new DataManagementUI.OnClosedListener() {
		
		//@Override
		public void onClosed() {
			LoadDefaultContent();			
		}
	};

	private View.OnClickListener btnNetworkListener = new View.OnClickListener() {
		public void onClick(View v) {
		NetworkSettingsUI man = new NetworkSettingsUI(ctx, networkSettingsClosed);
		    
			removeAllViews();
			man.setPadding(10, 10, 10, 10);
			addView(man);
		}
	};
	
	NetworkSettingsUI.OnClosedListener networkSettingsClosed = new NetworkSettingsUI.OnClosedListener() {
		public void onClosed() {
			LoadDefaultContent();			
		}
	};

	private View.OnClickListener btnArchiveListener = new View.OnClickListener() {
		public void onClick(View v) {
			ArchiveableCheck arch = new ArchiveableCheck();
			arch.checkAllfile();
		}
	};
	
	private View.OnClickListener btnExcludeListener = new View.OnClickListener() 
	{
		public void onClick(View v) 
		{
			ExcludeApplicationUI excl = new ExcludeApplicationUI(ctx, closed);
			excl.setPadding(10, 10, 10, 10);
			removeAllViews();
			addView(excl);
		}
	};
	
	
	ExcludeApplicationUI.OnClosedListener closed = new ExcludeApplicationUI.OnClosedListener() {
		//@Override
		public void onClosed() {
			removeAllViews();
			addView(listLay);	
		}
	};



}
