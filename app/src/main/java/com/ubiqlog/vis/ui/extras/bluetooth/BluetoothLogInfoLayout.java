package com.ubiqlog.vis.ui.extras.bluetooth;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author Dorin Gugonatu
 * 
 */

public class BluetoothLogInfoLayout extends LinearLayout
{
	private LinearLayout linearLayoutInfoFast;
	private LinearLayout linearLayoutInfoSlow;
	private LinearLayout linearLayoutInfoSteady;

	public BluetoothLogInfoLayout(Context context) 
	{
		super(context);
		
		this.setOrientation(LinearLayout.HORIZONTAL);
		
		linearLayoutInfoFast = new LinearLayout(context);
		linearLayoutInfoSlow = new LinearLayout(context);
		linearLayoutInfoSteady = new LinearLayout(context);
		
		linearLayoutInfoFast.setOrientation(LinearLayout.HORIZONTAL);
		linearLayoutInfoSlow.setOrientation(LinearLayout.HORIZONTAL);
		linearLayoutInfoSteady.setOrientation(LinearLayout.HORIZONTAL);
		
		int widthBitmap = 20;
		int heightBitmap = 20;
		int sizeBitmap = widthBitmap * heightBitmap;
		int [] colorsFast = new int[sizeBitmap];
		int [] colorsSlow = new int[sizeBitmap];
		int [] colorsSteady = new int[sizeBitmap];
		
		for (int index = 0; index < sizeBitmap; ++index)
		{
			colorsFast[index] = BluetoothUtils.COLOR_BONDED;
			colorsSlow[index] = BluetoothUtils.COLOR_BONDING;
			colorsSteady[index] = BluetoothUtils.COLOR_NONE;
		}
		
		ImageView imageViewInfoFast = new ImageView(context);
		imageViewInfoFast.setImageBitmap(Bitmap.createBitmap(colorsFast, widthBitmap, heightBitmap, Bitmap.Config.ARGB_8888));
		
		ImageView imageViewInfoSlow = new ImageView(context);
		imageViewInfoSlow.setImageBitmap(Bitmap.createBitmap(colorsSlow, widthBitmap, heightBitmap, Bitmap.Config.ARGB_8888));
		
		ImageView imageViewInfoSteady = new ImageView(context);
		imageViewInfoSteady.setImageBitmap(Bitmap.createBitmap(colorsSteady, widthBitmap, heightBitmap, Bitmap.Config.ARGB_8888));
		
		TextView textViewInfoFast = new TextView(context);
		textViewInfoFast.setText("  Bonded");
		textViewInfoFast.setTextSize(18);
		linearLayoutInfoFast.setGravity(Gravity.CENTER);
		linearLayoutInfoFast.addView(imageViewInfoFast);
		linearLayoutInfoFast.addView(textViewInfoFast);
		
		TextView textViewInfoSlow = new TextView(context);
		textViewInfoSlow.setText("  Bonding");
		textViewInfoSlow.setTextSize(18);
		linearLayoutInfoSlow.setGravity(Gravity.CENTER);
		linearLayoutInfoSlow.addView(imageViewInfoSlow);
		linearLayoutInfoSlow.addView(textViewInfoSlow);
		
		TextView textViewInfoSteady = new TextView(context);
		textViewInfoSteady.setText("  None");
		textViewInfoSteady.setTextSize(18);
		linearLayoutInfoSteady.setGravity(Gravity.CENTER);
		linearLayoutInfoSteady.addView(imageViewInfoSteady);
		linearLayoutInfoSteady.addView(textViewInfoSteady);
		
		//dateFormat = new SimpleDateFormat("EEE, MMMM dd, yyyy");
		
		this.addView(linearLayoutInfoSteady);
		this.addView(linearLayoutInfoSlow);
		this.addView(linearLayoutInfoFast);
	}
	
	@Override
	protected void onSizeChanged(int width, int height, int oldw, int oldh) 
	{		
		int third_width = width / 3;
		linearLayoutInfoFast.setLayoutParams(new LinearLayout.LayoutParams(third_width, LayoutParams.WRAP_CONTENT));
		linearLayoutInfoSlow.setLayoutParams(new LinearLayout.LayoutParams(third_width, LayoutParams.WRAP_CONTENT));
		linearLayoutInfoSteady.setLayoutParams(new LinearLayout.LayoutParams(third_width, LayoutParams.WRAP_CONTENT));
	}
}
