package com.ubiqlog.vis.ui;

import com.ubiqlog.ui.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Main activity
 * 
 * @author Victor Gugonatu
 * @date 10.2010
 * @version 1.0
 */
public class VisMain extends FrameLayout {
	
	public interface OnClosedListener {

		public abstract void onClosed();
	}


	private Context _ctx = null;
	ListView lv = null;
	private OnClosedListener _closed = null;

	public VisMain(Context context,OnClosedListener closed) {
		super(context);
		_ctx = context;
		_closed = closed;

		// initialise values from user preference
		com.ubiqlog.vis.common.Settings.initialise(_ctx);

		LinearLayout viewLayout = new LinearLayout(_ctx);
		viewLayout.setOrientation(LinearLayout.VERTICAL);

		ArrayAdapter<String> mainElems = new ArrayAdapter<String>(_ctx,R.layout.vis_list_view, getResources().getStringArray(R.array.Vis_Applications));

		lv = new ListView(_ctx);
		lv.setCacheColorHint(Color.TRANSPARENT);
		lv.setAdapter(mainElems);
		lv.setOnItemClickListener(onListItemClick);
				
		addView(lv);
	}

	
	ListView.OnItemClickListener onListItemClick =  new ListView.OnItemClickListener() {
		
		//@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			switch (position) 
			{
			case 0:
				Intent i0 = new Intent(_ctx, ApplicationLog.class);
				_ctx.startActivity(i0);
				break;
			case 1:
				Intent i1 = new Intent(_ctx, CallLog.class);
				_ctx.startActivity(i1);
				break;
				
			case 2:
				Intent i2 = new Intent(_ctx, SmsLog.class);
				_ctx.startActivity(i2);
				break;
				
			case 3:
				Intent i3 = new Intent(_ctx, BluetoothLog.class);
				_ctx.startActivity(i3);
				break;
				
			case 4:
				Intent i4 = new Intent(_ctx, LocationLog.class);
				_ctx.startActivity(i4);
				break;
				
			case 5:
				Intent i5 = new Intent(_ctx, MovementLog.class);
				_ctx.startActivity(i5);
				break;
				
			case 6:
				Settings set = new Settings(_ctx, closed);
				removeAllViews();
				set.setPadding(10, 10, 10, 10);
				addView(set);
				break;
		}
	}
	};
	
	Settings.OnClosedListener closed = new Settings.OnClosedListener() {
		
	//	@Override
		public void onClosed() {
			removeAllViews();
			addView(lv);
		}
	};
}