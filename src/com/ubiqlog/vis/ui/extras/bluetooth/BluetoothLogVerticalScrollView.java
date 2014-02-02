package com.ubiqlog.vis.ui.extras.bluetooth;

import android.content.Context;
import android.widget.ScrollView;

public class BluetoothLogVerticalScrollView extends ScrollView
{
	public BluetoothLogVerticalScrollView(Context context) 
	{
		super(context);
		
		this.setVerticalScrollBarEnabled(false);
	}
	
	@Override
	protected void onScrollChanged(int left, int top, int oldleft, int oldtop)
	{
		super.onScrollChanged(left, top, oldleft, oldtop);
		
		((BluetoothLogDevicesView)getChildAt(0)).DoScroll(top);
	}
}
