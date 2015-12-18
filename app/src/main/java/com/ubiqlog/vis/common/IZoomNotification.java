package com.ubiqlog.vis.common;

/**
 * 
 * @author Dorin Gugonatu
 * 
 */

public interface IZoomNotification 
{
	public void viewZoomIn(float left, float right);
	public void viewZoomOut(float left, float right);
	
	public void doZoom(float fScale);
	
	public void viewClearZoom();
}
