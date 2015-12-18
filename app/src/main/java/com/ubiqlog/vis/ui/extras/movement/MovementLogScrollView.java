package com.ubiqlog.vis.ui.extras.movement;

import com.ubiqlog.vis.common.IZoomNotification;

import android.content.Context;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * 
 * @author Dorin Gugonatu
 * 
 */

public class MovementLogScrollView extends HorizontalScrollView 
{
	private boolean bIsZooming;
	private float fZoomInOldDistance;
	private float fZoomScale;
	private IZoomNotification zoomNotification;
	

	public MovementLogScrollView(Context context) 
	{
		super(context);
	}
	
	public void addZoomNotificationListener(IZoomNotification zoomNotification)
	{
		this.zoomNotification = zoomNotification;
	}
	
	private float spacing(MotionEvent event) 
	{
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float)Math.sqrt(x * x + y * y);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent motionEvent) 
	{	
		int action = motionEvent.getAction();
		
		switch (action & MotionEvent.ACTION_MASK)
		{
		case MotionEvent.ACTION_POINTER_DOWN:
			fZoomInOldDistance = spacing(motionEvent);
			if (fZoomInOldDistance > 10f) 
			{
				bIsZooming = true;
				fZoomScale = 1.0f;
				
				zoomNotification.viewZoomIn(motionEvent.getX(0) + getScrollX(), motionEvent.getX(1) + getScrollX());
			}
			break;

		case MotionEvent.ACTION_MOVE:		   
		   if (bIsZooming)
		   {
		      float newDist = spacing(motionEvent);
		      if (newDist > 10f) 
		      {
		         fZoomScale = newDist / fZoomInOldDistance;
		         
		         zoomNotification.viewClearZoom();
		         
		         if (fZoomScale > 1.0f)
		         {
		        	 zoomNotification.viewZoomIn(motionEvent.getX(0) + getScrollX(), motionEvent.getX(1) + getScrollX());
		         }
		         else if (fZoomScale < 1.0f)
		         {
		        	 zoomNotification.viewZoomOut(motionEvent.getX(0) + getScrollX(), motionEvent.getX(1) + getScrollX());
		         }
		      }
		   }
		   break;
		   
		case MotionEvent.ACTION_POINTER_UP:
			if (bIsZooming) 
			{
				bIsZooming = false;
				if (fZoomScale > 1.0f)
				{
					zoomNotification.doZoom(fZoomScale);
				}
				else if (fZoomScale < 1.0f)
				{
					zoomNotification.doZoom(1-fZoomScale);
				}
			}
			break;
		}
		
		if (bIsZooming)
		{
			return true;
		}
		else
		{
			return super.onTouchEvent(motionEvent);
		}
	}

}
