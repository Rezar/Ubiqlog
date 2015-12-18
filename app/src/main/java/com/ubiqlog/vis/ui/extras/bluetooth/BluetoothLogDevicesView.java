package com.ubiqlog.vis.ui.extras.bluetooth;

import java.util.ArrayList;

import com.ubiqlog.vis.extras.bluetooth.BluetoothDetection;
import com.ubiqlog.vis.extras.bluetooth.BluetoothDetectionContainer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class BluetoothLogDevicesView extends View
{
	private String[] devicesNames;
	private String[] devicesAddresses;
	
	private ArrayList<Integer> indexesVisibleDevices;
	
	private LayerDrawable layerDrawable;
	private Drawable[] drawableLayers;
	
	private ShapeDrawable shapeDrawableAxis;
	private ShapeDrawable shapeDrawableTicks;
	
	private Paint paintText;
	
	private Path pathTicks;
	private Path pathAxis;
	
	private int numDevices;
	private int spaceInBetweenTicks = 100;
	private int spaceInBetweenTicksOneThird = spaceInBetweenTicks / 3;
	private int choosenIndex = -1;
	
	private boolean bDataLoaded;
	
	private PointF[] tickTextPositions;
	private Float[] tickPositions;
	
	private Toast viewToast;
	
	private BluetoothLogDataView bluetoothLogDataView;
	
	private int iPaddingTop, iPaddingBottom;

	public BluetoothLogDevicesView(Context context, BluetoothLogDataView bluetoothLogDataView) 
	{
		super(context);
		
		this.bluetoothLogDataView = bluetoothLogDataView;
		
		bDataLoaded = false;
		
		drawableLayers = new Drawable[2];
		
		shapeDrawableAxis = new ShapeDrawable();
		shapeDrawableAxis.getPaint().setColor(Color.WHITE);
		shapeDrawableAxis.getPaint().setStrokeCap(Paint.Cap.SQUARE);
		shapeDrawableAxis.getPaint().setStyle(Paint.Style.STROKE);
		shapeDrawableAxis.getPaint().setStrokeWidth(3);
		
		shapeDrawableTicks = new ShapeDrawable();
		shapeDrawableTicks.getPaint().setColor(Color.WHITE);
		shapeDrawableTicks.getPaint().setStrokeCap(Paint.Cap.SQUARE);
		shapeDrawableTicks.getPaint().setStyle(Paint.Style.STROKE);
		shapeDrawableTicks.getPaint().setStrokeWidth(2);
		
		drawableLayers[0] = shapeDrawableAxis;
		drawableLayers[1] = shapeDrawableTicks;
		
		layerDrawable = new LayerDrawable(drawableLayers);
		
		paintText = new Paint();
		paintText.setTextSize(18);
		paintText.setColor(Color.WHITE);
		paintText.setTextAlign(Align.RIGHT);
		
		pathTicks = new Path();
		pathAxis = new Path();
		
		indexesVisibleDevices = new ArrayList<Integer>();
	}

	
	public void setData(BluetoothDetectionContainer bluetoothContainer)
	{
		bDataLoaded = false;
		
		numDevices = bluetoothContainer.getNumberOfDevices();
		devicesNames = new String[numDevices];
		devicesAddresses = new String[numDevices];
		ArrayList<BluetoothDetection> bluetoothDetections = bluetoothContainer.getAllDevicesDetections();
		int index = 0;
		
		for (BluetoothDetection bluetoothDetection : bluetoothDetections)
		{
			devicesNames[index] = bluetoothDetection.getDeviceName();
			devicesAddresses[index] = bluetoothDetection.getDeviceAddress();
			++index;
		}
		
		tickTextPositions = new PointF[numDevices];
		tickPositions = new Float[numDevices];
		
		iPaddingTop = this.getPaddingTop();
		iPaddingBottom = this.getPaddingBottom();
		
		bDataLoaded = true;
		
		ViewGroup.LayoutParams params = getLayoutParams(); 
		params.height = ViewGroup.LayoutParams.WRAP_CONTENT; 
		setLayoutParams(params);
		
		invalidate();
	}
	
	public void DoScroll(int top)
	{	
		UpdateDeviceData(top);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
		int width = View.MeasureSpec.getSize(widthMeasureSpec);
		
		int height = 0;
		
		if (bDataLoaded)
		{
			height = spaceInBetweenTicks * (numDevices+1);
			
			RefreshTicksPath(0.9f*width, width, spaceInBetweenTicks);
			shapeDrawableTicks.setShape(new PathShape(pathTicks, width, height));
			shapeDrawableTicks.setBounds(0, 0, width, height);
			
			pathAxis.rewind();
			pathAxis.moveTo(0.95f*width, 0.01f*height);
			pathAxis.lineTo(0.95f*width, 0.99f*height);
			shapeDrawableAxis.setShape(new PathShape(pathAxis, width, height));
			shapeDrawableAxis.setBounds(0, 0, width, height);
			
			RefreshTickTextPositions(0.8f*width, spaceInBetweenTicks);
		}

		setMeasuredDimension(width, height);
	}
	
	@Override
	public void onLayout (boolean changed, int left, int top, int right, int bottom)
	{
		UpdateDeviceData(0);
	}

	@Override
	protected void onDraw(Canvas canvas) 
	{
		layerDrawable.draw(canvas);
		
		if (bDataLoaded)
		{
			for (int indexText = 0, indexPos = 0; indexPos < tickTextPositions.length && indexText < devicesNames.length; indexPos++, indexText++) 
			{
				canvas.drawText(devicesNames[indexText], tickTextPositions[indexPos].x, tickTextPositions[indexPos].y, paintText);
			}
		}
	}
	
	@Override 
	public boolean onTouchEvent(MotionEvent event) 
	{
		if (bDataLoaded)
		{
			switch (event.getAction()) 
			{ 
			case MotionEvent.ACTION_DOWN:
				int y = (int)event.getY();
				
				for (int index = 0; index < tickPositions.length; ++ index)
				{
					if ((tickPositions[index] >= (y - spaceInBetweenTicksOneThird)) && (tickPositions[index] <= (y + spaceInBetweenTicksOneThird)))
					{
						choosenIndex = index;
						break;
					}
				}
				return true;
	        	
	        case MotionEvent.ACTION_UP:
	    		String strintToShow = new String("Device name: " + devicesNames[choosenIndex] + "\nDevice address: " + devicesAddresses[choosenIndex]);
		    	viewToast = Toast.makeText(BluetoothLogDevicesView.this.getContext(), strintToShow, Toast.LENGTH_SHORT); 
		    	viewToast.show();
		    	
	        	break;
			}
		}
		
		return false;
	}
	
	private void RefreshTickTextPositions(float right, float top) 
	{		
		float fCurrentY = top;
		
		for (int index = 0; index < numDevices; index++)
		{
			tickTextPositions[index] = new PointF(right, fCurrentY);
			fCurrentY += spaceInBetweenTicks;
		}
	}

	private void RefreshTicksPath(float left, float right, float top) 
	{
		pathTicks.rewind();
		float fCurrentY = top;
		
		for (int index = 0; index < numDevices; index++)
		{
			tickPositions[index] = new Float(fCurrentY);
			
			pathTicks.moveTo(left, fCurrentY);
			pathTicks.lineTo(right, fCurrentY);
			
			fCurrentY += spaceInBetweenTicks;
		}
	}
	
	private void UpdateDeviceData(int top)
	{
		Rect rect = new Rect();
		
		getLocalVisibleRect(rect);
		
		indexesVisibleDevices.clear();
		
		for (int index = 0; index < numDevices; ++index)
		{
			if ((rect.top + iPaddingTop + spaceInBetweenTicks) <= tickPositions[index].intValue() && tickPositions[index].intValue() <= (rect.bottom - iPaddingBottom - spaceInBetweenTicks))
			{
				indexesVisibleDevices.add(index);
			}
		}
		
		int numIndexes = indexesVisibleDevices.size();
		
		String[] stringVisibleDeviceAddresses = new String[numIndexes];
		float[] floatVisibleDevicesPositions = new float[numIndexes];
		
		int index = 0;
		
		for (int visibleIndex : indexesVisibleDevices)
		{
			stringVisibleDeviceAddresses[index] = devicesAddresses[visibleIndex];
			
			floatVisibleDevicesPositions[index] = tickPositions[visibleIndex] - (top - iPaddingTop - spaceInBetweenTicks);
			index++;
		}
		
		bluetoothLogDataView.updateDeviceData(stringVisibleDeviceAddresses, floatVisibleDevicesPositions);
	}
}
