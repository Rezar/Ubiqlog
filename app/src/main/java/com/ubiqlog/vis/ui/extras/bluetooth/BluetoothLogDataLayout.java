package com.ubiqlog.vis.ui.extras.bluetooth;

import com.ubiqlog.vis.extras.bluetooth.BluetoothDetectionContainer;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 
 * @author Dorin Gugonatu
 * 
 */

public class BluetoothLogDataLayout extends LinearLayout
{
	private BluetoothLogHorizontalScrollView bluetoothHorizontalScrollView;
	private BluetoothLogVerticalScrollView bluetoothVerticalScrollView;
	
	private BluetoothLogDataView bluetoothLogDataView;
	private BluetoothLogDevicesView bluetoothLogDevicesView;

	public BluetoothLogDataLayout(Context context) 
	{
		super(context);
		
		this.setOrientation(LinearLayout.HORIZONTAL);
		
		bluetoothHorizontalScrollView = new BluetoothLogHorizontalScrollView(context);
		bluetoothLogDataView = new BluetoothLogDataView(context, 100, 100);
		bluetoothHorizontalScrollView.addView(bluetoothLogDataView);
		bluetoothHorizontalScrollView.addZoomNotificationListener(bluetoothLogDataView);
		
		bluetoothVerticalScrollView = new BluetoothLogVerticalScrollView(context);
		bluetoothLogDevicesView = new BluetoothLogDevicesView(context, bluetoothLogDataView);
		bluetoothVerticalScrollView.addView(bluetoothLogDevicesView);
		bluetoothVerticalScrollView.setPadding(0, 100, 0, 100);
		
		addView(bluetoothVerticalScrollView);
		addView(bluetoothHorizontalScrollView);
	}

	public void setData(BluetoothDetectionContainer bluetoothContainer) 
	{
		bluetoothLogDevicesView.setData(bluetoothContainer);
		bluetoothLogDataView.setData(bluetoothContainer);
	}
	
	@Override
	protected void onSizeChanged(int width, int height, int oldw, int oldh) 
	{		
		int fourth_width = width / 4;
		
		ViewGroup.LayoutParams params = bluetoothVerticalScrollView.getLayoutParams();
		params.width = fourth_width;
		params.height = ViewGroup.LayoutParams.FILL_PARENT;
		bluetoothVerticalScrollView.setLayoutParams(params); 

		params = bluetoothHorizontalScrollView.getLayoutParams(); 
		params.width = 3*fourth_width;
		params.height = ViewGroup.LayoutParams.FILL_PARENT;
		bluetoothHorizontalScrollView.setLayoutParams(params);
	}
}
