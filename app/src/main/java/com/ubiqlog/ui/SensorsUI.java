package com.ubiqlog.ui;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.ubiqlog.core.SensorCatalouge;

import com.ubiqlog.sensors.SensorObj;


public class SensorsUI extends FrameLayout {

	Context ctx = null;
	ListView lv = null;



	public SensorsUI(Context context) {
		super(context);
		ctx = context;
		
		senCat = new SensorCatalouge(ctx);

		try {
			sens = senCat.getAllSensors();
			String[] sensorNames = new String[sens.size()];
			for (int i = 0; i < sens.size(); i++) {
				sensorNames[i] = sens.get(i).getSensorName();
			}

			// ListAdapter list = new ListView();
			ArrayAdapter<String> mainElems = new ArrayAdapter<String>(ctx,R.layout.tools_list_view, sensorNames);
			lv = new ListView(ctx);
			lv.setCacheColorHint(Color.TRANSPARENT);
			lv.setAdapter(mainElems);
			lv.setOnItemClickListener(onListItemClick);
			
			senCat = new SensorCatalouge(ctx);
			senCat.setUpinitCatalouge();
			senCat.closeDB(ctx);
			
			addView(lv);

		} catch (Exception e) {
			e.printStackTrace();
			Log.e("SensorsUI", "------Can not read Sensor List-------"+ e.getLocalizedMessage());
		}
	}

	private SensorCatalouge senCat;
	private ArrayList<SensorObj> sens;

	
	ListView.OnItemClickListener onListItemClick =  new ListView.OnItemClickListener() {

		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			// v.bringToFront();
			int intID = Integer.parseInt(String.valueOf(id));

			String sensorName = sens.get(intID).getSensorName();
			String[] confsD = null;
			senCat = new SensorCatalouge(ctx);
			try
			{
				
				confsD = senCat.getSensorByName(sensorName).getConfigData();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				Log.e("SensorsUI", "------Can not read sensor configuration-------"+ e.getLocalizedMessage());
				confsD = sens.get(intID).getConfigData();
			}
			senCat.closeDB(ctx);
			SensorConfUI sConf = new SensorConfUI(ctx, confsD, sensorName, closed);
			removeAllViews();
			addView(sConf);
			}
	};
	
	 SensorConfUI.OnClosedListener closed = new SensorConfUI.OnClosedListener() {	
//			@Override
		public void onClosed() {
			removeAllViews();
			addView(lv);
			
		}
	};


}