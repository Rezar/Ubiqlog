package com.ubiqlog.ui;


import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ubiqlog.ui.extras.search.SearchMainUI;
import com.ubiqlog.vis.ui.VisMain;

public class ToolsUI extends FrameLayout {

	Context ctx = this.getContext();
	ListView lv = null;
    MainUI mainui = new MainUI();

	public ToolsUI(Context context) {
		super(context);
		ctx = context;
		lv = new ListView(ctx);
		ArrayAdapter<String> ad = new ArrayAdapter<String>(ctx, R.layout.tools_list_view, TOOLS_MAINUI_LIST);
		lv.setCacheColorHint(Color.TRANSPARENT);
		lv.setAdapter(ad);
		lv.setOnItemClickListener(onListItemClick);
		addView(lv);
	}

	private static final int SEARCH = 0;
	private static final int VISUALIZATION = 1;
	private static final int SMS= 2;

	public static final String[] TOOLS_MAINUI_LIST = { "Search","Visualization","Check Permission of SMS" };
	ListView.OnItemClickListener onListItemClick =  new ListView.OnItemClickListener() {

		
		//@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			switch (position) {
                case SEARCH:
    //				SearchMainUI sMain = new SearchMainUI(ctx, closed);
    //				removeAllViews();
    //				addView(sMain);
                    Toast.makeText(ctx, "This item has been disabled for this version", Toast.LENGTH_SHORT).show();
                    break;
                case VISUALIZATION:
    //				VisMain visMain = new VisMain(ctx, visClosed);
    //				removeAllViews();
    //				addView(visMain);
                    Toast.makeText(ctx, "This item has been disabled for this version", Toast.LENGTH_SHORT).show();
                    break;
                case SMS:
                    if (ContextCompat.checkSelfPermission(ctx,
                            "android.permission.READ_SMS")
                            != PackageManager.PERMISSION_GRANTED)
                    {

                        Toast.makeText(ctx, "Read SMS not granted", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                        Toast.makeText(ctx, "Read SMS granted", Toast.LENGTH_SHORT).show();
                    }
			}
			
		}

	};
	
	
	VisMain.OnClosedListener visClosed = new VisMain.OnClosedListener() {
			//@Override
			public void onClosed() {
				removeAllViews();
				addView(lv);
			}
		};
	
	 SearchMainUI.OnClosedListener closed = new SearchMainUI.OnClosedListener() {
			//@Override
			public void onClosed() {
				removeAllViews();
				addView(lv);
				
			}
		};
}

	