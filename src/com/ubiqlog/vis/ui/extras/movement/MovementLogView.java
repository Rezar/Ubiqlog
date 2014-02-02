package com.ubiqlog.vis.ui.extras.movement;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Align;
import android.graphics.Path.Direction;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.view.View;
import android.widget.FrameLayout;

import com.ubiqlog.vis.utils.SensorState.Movement;
import com.ubiqlog.vis.common.IZoomNotification;
import com.ubiqlog.vis.common.LevelOfDetail;
import com.ubiqlog.vis.common.LevelOfDetail.LodState;
import com.ubiqlog.vis.extras.movement.MovementInterval;
import com.ubiqlog.vis.extras.movement.MovementStateContainer;

/**
 * 
 * @author Dorin Gugonatu
 * 
 */

public class MovementLogView extends View implements IZoomNotification
{
	private LayerDrawable layerDrawable;
	private Drawable[] drawableLayers;

	private ShapeDrawable shapeDrawableMargins;
	private ShapeDrawable shapeDrawableLinesHours;
	private ShapeDrawable shapeDrawableLinesHalfHours;
	private ShapeDrawable shapeDrawableLinesQuarterHours;
	private ShapeDrawable shapeDrawableZoomInFill;
	private ShapeDrawable shapeDrawableZoomInMargin;
	private ShapeDrawable shapeDrawableZoomOutFill;
	private ShapeDrawable shapeDrawableZoomOutMargin;
	private ShapeDrawable shapeDrawableDataFast;
	private ShapeDrawable shapeDrawableDataSlow;
	private ShapeDrawable shapeDrawableDataSteady;

	private Path pathZoomInFill;
	private Path pathZoomInMargin;
	
	private Path pathZoomOutFill;
	private Path pathZoomOutMargin;
	
	private Paint paintText;
	
	private Path pathDataFast;
	private Path pathDataSlow;
	private Path pathDataSteady;
	
	private ArrayList<MovementInterval> activeDataFast;
	private ArrayList<MovementInterval> activeDataSlow;
	private ArrayList<MovementInterval> activeDataSteady;

	private RectF marginRectPoints;
	
	private PointF[] marginLinesPoints;
	private PointF[] linesPointsHours;
	private PointF[] linesPointsHalfHours;
	private PointF[] linesPointsQuarterHours;
	
	private boolean bDataLoaded;
	
	private long activeTimeDiff;
	
	private LevelOfDetail levelOfDetail;

	private final String[] textLinesHours = {"00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", 
			"13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"};

	private final String[] textLinesHalfHours = {"00:30", "01:30", "02:30", "03:30", "04:30", "05:30", "06:30", "07:30", "08:30", "09:30", "10:30", "11:30", "12:30", 
			"13:30", "14:30", "15:30", "16:30", "17:30", "18:30", "19:30", "20:30", "21:30", "22:30", "23:30"};
	
	private final String[] textLinesQuarterHours = {"00:15", "00:45", "01:15", "01:45", "02:15", "02:45", "03:15", "03:45", "04:15", "04:45", "05:15", "05:45", "06:15", "06:45", 
			"07:15", "07:45", "08:15", "08:45", "09:15", "09:45", "10:15", "10:45", "11:15", "11:45", "12:15", "12:45", 
			"13:15", "13:45", "14:15", "14:45", "15:15", "15:45", "16:15", "16:45", "17:15", "17:45", "18:15", "18:45", 
			"19:15", "19:45", "20:15", "20:45", "21:15", "21:45", "22:15", "22:45", "23:15", "23:45"};
	
	private final float linesTextSize = 28;
	
	private float timelineLengthInitial;
	private float timelineLengthCurrent;
	
	private float numLinesQuarterHours;
	private float startPosQuarterHours;
	private float numLinesHalfHours;
	private float startPosHalfHours;
	private float numLinesHours;
	private float startPosHours;
	
	private float millisPerPixelCurrent = 0.0f;
	
	private int startIndexInTextQuarterHours;
	private int startIndexInTextHalfHours;
	private int startIndexInTextHours;
	
	private Calendar activeDataLowestDate;
	private Calendar activeDataHighestDate;
	
	private float activeDataLowestDateMillis;
	private float activeDataHighestDateMillis;
	
	private Calendar visibleLowestDate;
	private Calendar visibleHighestDate;
	
	private SimpleDateFormat sdfDate;
	private SimpleDateFormat sdfDayName;

	public MovementLogView(Context context) 
	{
		super(context);

		levelOfDetail = new LevelOfDetail();
		
		bDataLoaded = false;

		drawableLayers = new Drawable[11];
		
		shapeDrawableMargins = new ShapeDrawable();
		shapeDrawableMargins.getPaint().setColor(Color.WHITE);
		shapeDrawableMargins.getPaint().setStrokeCap(Paint.Cap.SQUARE);
		shapeDrawableMargins.getPaint().setStyle(Paint.Style.STROKE);
		shapeDrawableMargins.getPaint().setStrokeWidth(6);

		shapeDrawableLinesHours = new ShapeDrawable();
		shapeDrawableLinesHours.getPaint().setColor(Color.WHITE);
		shapeDrawableLinesHours.getPaint().setStrokeCap(Paint.Cap.SQUARE);
		shapeDrawableLinesHours.getPaint().setStyle(Paint.Style.STROKE);
		shapeDrawableLinesHours.getPaint().setStrokeWidth(4);
		
		shapeDrawableLinesHalfHours = new ShapeDrawable();
		shapeDrawableLinesHalfHours.getPaint().setColor(Color.WHITE);
		shapeDrawableLinesHalfHours.getPaint().setStrokeCap(Paint.Cap.SQUARE);
		shapeDrawableLinesHalfHours.getPaint().setStyle(Paint.Style.STROKE);
		shapeDrawableLinesHalfHours.getPaint().setStrokeWidth(2);
		
		shapeDrawableLinesQuarterHours = new ShapeDrawable();
		shapeDrawableLinesQuarterHours.getPaint().setColor(Color.WHITE);
		shapeDrawableLinesQuarterHours.getPaint().setStrokeCap(Paint.Cap.SQUARE);
		shapeDrawableLinesQuarterHours.getPaint().setStyle(Paint.Style.STROKE);
		shapeDrawableLinesQuarterHours.getPaint().setStrokeWidth(1);

		shapeDrawableZoomInFill = new ShapeDrawable();
		shapeDrawableZoomInFill.getPaint().setColor(Color.GREEN);
		shapeDrawableZoomInFill.getPaint().setAlpha(100);
		
		shapeDrawableZoomInMargin = new ShapeDrawable();
		shapeDrawableZoomInMargin.getPaint().setColor(Color.GREEN);
		shapeDrawableZoomInMargin.getPaint().setStrokeCap(Paint.Cap.SQUARE);
		shapeDrawableZoomInMargin.getPaint().setStyle(Paint.Style.STROKE);
		shapeDrawableZoomInMargin.getPaint().setStrokeWidth(4);
		shapeDrawableZoomInMargin.getPaint().setAlpha(200);
		
		shapeDrawableZoomOutFill = new ShapeDrawable();
		shapeDrawableZoomOutFill.getPaint().setColor(Color.MAGENTA);
		shapeDrawableZoomOutFill.getPaint().setAlpha(100);
		
		shapeDrawableZoomOutMargin = new ShapeDrawable();
		shapeDrawableZoomOutMargin.getPaint().setColor(Color.MAGENTA);
		shapeDrawableZoomOutMargin.getPaint().setStrokeCap(Paint.Cap.SQUARE);
		shapeDrawableZoomOutMargin.getPaint().setStyle(Paint.Style.STROKE);
		shapeDrawableZoomOutMargin.getPaint().setStrokeWidth(4);
		shapeDrawableZoomOutMargin.getPaint().setAlpha(200);
		
		shapeDrawableDataFast = new ShapeDrawable();
		shapeDrawableDataFast.getPaint().setColor(MovementUtils.COLOR_FAST);
		shapeDrawableDataFast.getPaint().setStyle(Paint.Style.FILL);
		shapeDrawableDataFast.getPaint().setAlpha(150);
		
		shapeDrawableDataSlow = new ShapeDrawable();
		shapeDrawableDataSlow.getPaint().setColor(MovementUtils.COLOR_SLOW);
		shapeDrawableDataSlow.getPaint().setStyle(Paint.Style.FILL);
		shapeDrawableDataSlow.getPaint().setAlpha(150);
		
		shapeDrawableDataSteady = new ShapeDrawable();
		shapeDrawableDataSteady.getPaint().setColor(MovementUtils.COLOR_STEADY);
		shapeDrawableDataSteady.getPaint().setStyle(Paint.Style.FILL);
		shapeDrawableDataSteady.getPaint().setAlpha(150);

		drawableLayers[0] = shapeDrawableMargins;
		drawableLayers[1] = shapeDrawableLinesHours;
		drawableLayers[2] = shapeDrawableLinesHalfHours;
		drawableLayers[3] = shapeDrawableLinesQuarterHours;
		drawableLayers[4] = shapeDrawableZoomInFill;
		drawableLayers[5] = shapeDrawableZoomInMargin;
		drawableLayers[6] = shapeDrawableZoomOutFill;
		drawableLayers[7] = shapeDrawableZoomOutMargin;
		drawableLayers[8] = shapeDrawableDataFast;
		drawableLayers[9] = shapeDrawableDataSlow;
		drawableLayers[10] = shapeDrawableDataSteady;

		layerDrawable = new LayerDrawable(drawableLayers);

		paintText = new Paint();
		paintText.setTextSize(linesTextSize);
		paintText.setColor(Color.WHITE);
		paintText.setTextAlign(Align.CENTER);
		
		pathZoomInFill = new Path();
		pathZoomInMargin = new Path();
		
		pathZoomOutFill = new Path();
		pathZoomOutMargin = new Path();
		
		pathDataFast = new Path();
		pathDataSlow = new Path();
		pathDataSteady = new Path();
		
		marginRectPoints = new RectF();
		
		visibleLowestDate = Calendar.getInstance();
		visibleHighestDate = Calendar.getInstance();
		
		sdfDate = new SimpleDateFormat("dd.MM.yyyy");
		sdfDayName = new SimpleDateFormat("EEEE");
	}

	public void setData(MovementStateContainer dataContainer) 
	{
		bDataLoaded = false;
		
		activeDataFast = dataContainer.getMovementIntervals(Movement.FAST);
		activeDataSlow = dataContainer.getMovementIntervals(Movement.SLOW);
		activeDataSteady = dataContainer.getMovementIntervals(Movement.STEADY);
		
		activeDataLowestDate = dataContainer.getLowestDate();
		activeDataLowestDateMillis = activeDataLowestDate.getTimeInMillis();
		
		activeDataHighestDate = dataContainer.getHighestDate();
		activeDataHighestDateMillis = activeDataHighestDate.getTimeInMillis();
		
		activeTimeDiff = activeDataHighestDate.getTime().getTime() - activeDataLowestDate.getTime().getTime();
		millisPerPixelCurrent = activeTimeDiff / timelineLengthCurrent;
		
		levelOfDetail.setWholeParamter(activeTimeDiff / timelineLengthInitial);
		
		initHoursData();
		
		refreshPaths(getWidth(), getHeight());
		
		bDataLoaded = true;
		
		invalidate();
	}
	
	private void initHoursData()
	{
		Calendar currentDate =  (Calendar)activeDataLowestDate.clone();
		int minutes = currentDate.get(Calendar.MINUTE);
		int newMinutes;
		
		if (minutes < 15)
		{
			newMinutes = 15 - minutes;
		}
		else if (minutes < 45)
		{
			newMinutes = 45 - minutes;
		}
		else
		{
			newMinutes = 75 - minutes;
		}
		
		currentDate.set(Calendar.MILLISECOND, 0);
		currentDate.set(Calendar.SECOND, 0);
		currentDate.add(Calendar.MINUTE, newMinutes);
		
		startPosQuarterHours = currentDate.getTime().getTime() - activeDataLowestDate.getTime().getTime();
		numLinesQuarterHours = (float)Math.floor((double)(2 * ((activeTimeDiff - startPosQuarterHours) / LevelOfDetail.MILLIS_PER_HOUR))) + 1;
		
		if (currentDate.get(Calendar.MINUTE) == 15)
		{
			startIndexInTextQuarterHours = 2*currentDate.get(Calendar.HOUR_OF_DAY);
		}
		else
		{
			startIndexInTextQuarterHours = 2*currentDate.get(Calendar.HOUR_OF_DAY) + 1;
		}
		
		
		currentDate =  (Calendar)activeDataLowestDate.clone();
		minutes = currentDate.get(Calendar.MINUTE);
		if (minutes < 30)
		{
			newMinutes = 30 - minutes;
		}
		else
		{
			newMinutes = 90 - minutes;
		}
		
		currentDate.set(Calendar.MILLISECOND, 0);
		currentDate.set(Calendar.SECOND, 0);
		currentDate.add(Calendar.MINUTE, newMinutes);
		
		startPosHalfHours = currentDate.getTime().getTime() - activeDataLowestDate.getTime().getTime();
		numLinesHalfHours = (float)Math.floor((double)(((activeTimeDiff - startPosHalfHours) / LevelOfDetail.MILLIS_PER_HOUR))) + 1;
		startIndexInTextHalfHours = currentDate.get(Calendar.HOUR_OF_DAY);
		
		
		currentDate =  (Calendar)activeDataLowestDate.clone();
		minutes = currentDate.get(Calendar.MINUTE);
		if (minutes < 60)
		{
			newMinutes = 60 - minutes;
		}
		else
		{
			newMinutes = 120 - minutes;
		}
		
		currentDate.set(Calendar.MILLISECOND, 0);
		currentDate.set(Calendar.SECOND, 0);
		currentDate.add(Calendar.MINUTE, newMinutes);
		
		startPosHours = currentDate.getTime().getTime() - activeDataLowestDate.getTime().getTime();
		numLinesHours = (float)Math.floor((double)(((activeTimeDiff - startPosHours) / LevelOfDetail.MILLIS_PER_HOUR))) + 1;
		startIndexInTextHours = currentDate.get(Calendar.HOUR_OF_DAY);
	}
	
	private void refreshPaths(int width, int height)
	{	
		marginLinesPoints = CalculateMargins(marginRectPoints);
		shapeDrawableMargins.setShape(new PathShape(GeneratePath(marginLinesPoints), width, height));
		shapeDrawableMargins.setBounds(0, 0, width, height);
		
		pathDataFast = GenerateDataPath(pathDataFast, activeDataFast, marginRectPoints.left, marginRectPoints.top, marginRectPoints.bottom);
		shapeDrawableDataFast.setShape(new PathShape(pathDataFast, width, height));
		shapeDrawableDataFast.setBounds(0, 0, width, height);
		
		pathDataSlow = GenerateDataPath(pathDataSlow, activeDataSlow, marginRectPoints.left, marginRectPoints.top, marginRectPoints.bottom);
		shapeDrawableDataSlow.setShape(new PathShape(pathDataSlow, width, height));
		shapeDrawableDataSlow.setBounds(0, 0, width, height);
		
		pathDataSteady = GenerateDataPath(pathDataSteady, activeDataSteady, marginRectPoints.left, marginRectPoints.top, marginRectPoints.bottom);
		shapeDrawableDataSteady.setShape(new PathShape(pathDataSteady, width, height));
		shapeDrawableDataSteady.setBounds(0, 0, width, height);
		
		float pixelsInBetween = (LevelOfDetail.MILLIS_PER_HALF_HOUR * timelineLengthCurrent) / activeTimeDiff;
		
		ClearTextLines();
		
		switch (levelOfDetail.getState())
		{
		case QUARTER_HOUR:
			linesPointsQuarterHours = CalculateLinesPath((int)numLinesQuarterHours, pixelsInBetween, (float)Math.floor(startPosQuarterHours / millisPerPixelCurrent), marginRectPoints.top - 24, marginRectPoints.top);
			shapeDrawableLinesQuarterHours.setShape(new PathShape(GeneratePath(linesPointsQuarterHours), width, height));
			shapeDrawableLinesQuarterHours.setBounds(0, 0, width, height);
			
		case HALF_HOUR:
			linesPointsHalfHours = CalculateLinesPath((int)numLinesHalfHours, 2*pixelsInBetween, (float)Math.floor(startPosHalfHours / millisPerPixelCurrent), marginRectPoints.top - 36, marginRectPoints.top);
			shapeDrawableLinesHalfHours.setShape(new PathShape(GeneratePath(linesPointsHalfHours), width, height));
			shapeDrawableLinesHalfHours.setBounds(0, 0, width, height);
			
		case HOUR:
			linesPointsHours = CalculateLinesPath((int)numLinesHours, 2*pixelsInBetween, (float)Math.floor(startPosHours / millisPerPixelCurrent), marginRectPoints.top - 48, marginRectPoints.top);
			shapeDrawableLinesHours.setShape(new PathShape(GeneratePath(linesPointsHours), width, height));
			shapeDrawableLinesHours.setBounds(0, 0, width, height);
		}
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
		int heightView = View.MeasureSpec.getSize(heightMeasureSpec);
		
		int widthView;
		
		if (levelOfDetail.getState() == LodState.INIT)
		{
			widthView = ((View)getParent()).getWidth();
			
			marginRectPoints.left = 0.05f * widthView;
			marginRectPoints.right = 0.95f * widthView;
			marginRectPoints.top = 0.4f * heightView;
			marginRectPoints.bottom = 0.6f * heightView;
			
			timelineLengthCurrent = marginRectPoints.right - marginRectPoints.left;
			timelineLengthInitial = timelineLengthCurrent;
			
			levelOfDetail.init(timelineLengthInitial);
		}
		else
		{
			marginRectPoints.right = marginRectPoints.left + timelineLengthCurrent;
			widthView = (int)(marginRectPoints.left + marginRectPoints.right);
		}

		setMeasuredDimension(widthView, heightView);
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldw, int oldh) 
	{		
		if (bDataLoaded)
		{
			refreshPaths(width, height);
		}
	}
	

	@Override
	protected void onDraw(Canvas canvas) 
	{
		layerDrawable.draw(canvas);
		
		if (bDataLoaded)
		{
			Rect visibleRect = new Rect();		
			getLocalVisibleRect(visibleRect);
			
			DrawTextDate(canvas, visibleRect, paintText);
			
			switch (levelOfDetail.getState())
			{
			case QUARTER_HOUR:
				DrawTextArray(canvas, visibleRect, paintText, textLinesQuarterHours, linesPointsQuarterHours, startIndexInTextQuarterHours, 0, -30);
			case HALF_HOUR:
				DrawTextArray(canvas, visibleRect, paintText, textLinesHalfHours, linesPointsHalfHours, startIndexInTextHalfHours, 0, -30);
			case HOUR:
				DrawTextArray(canvas, visibleRect, paintText, textLinesHours, linesPointsHours, startIndexInTextHours, 0, -30);
			}
		}
	}

	//@Override
	public void viewZoomIn(float left, float right) 
	{
		int width = getWidth();
		int height = getHeight();
		
		float diff = marginRectPoints.bottom - marginRectPoints.top;
		int top = (int)(marginRectPoints.top - 0.3 * diff);
		int bottom = (int)(marginRectPoints.bottom + 0.3 *diff);
		
		pathZoomInFill.rewind();
		pathZoomInFill.addRect(left, top, right, bottom, Direction.CCW);
		shapeDrawableZoomInFill.setShape(new PathShape(pathZoomInFill, width, height));
		shapeDrawableZoomInFill.setBounds(0, 0, width, height);
		
		pathZoomInMargin.rewind();
		pathZoomInMargin.moveTo(left, top);
		pathZoomInMargin.lineTo(left, bottom);
		pathZoomInMargin.moveTo(right, top);
		pathZoomInMargin.lineTo(right, bottom);
		pathZoomInMargin.moveTo((left + right)/2, top);
		pathZoomInMargin.lineTo((left + right)/2, bottom);
		
		shapeDrawableZoomInMargin.setShape(new PathShape(pathZoomInMargin, width, height));
		shapeDrawableZoomInMargin.setBounds(0, 0, width, height);
		
		this.invalidate();
	}
	
	//@Override
	public void viewZoomOut(float left, float right) 
	{
		int width = getWidth();
		int height = getHeight();
		
		float diff = marginRectPoints.bottom - marginRectPoints.top;
		int top = (int)(marginRectPoints.top - 0.3 * diff);
		int bottom = (int)(marginRectPoints.bottom + 0.3 *diff);
		
		pathZoomOutFill.rewind();
		pathZoomOutFill.addRect(left, top, right, bottom, Direction.CCW);
		shapeDrawableZoomOutFill.setShape(new PathShape(pathZoomOutFill, width, height));
		shapeDrawableZoomOutFill.setBounds(0, 0, width, height);
		
		pathZoomOutMargin.rewind();
		pathZoomOutMargin.moveTo(left, top);
		pathZoomOutMargin.lineTo(left, bottom);
		pathZoomOutMargin.moveTo(right, top);
		pathZoomOutMargin.lineTo(right, bottom);
		pathZoomOutMargin.moveTo((left + right)/2, top);
		pathZoomOutMargin.lineTo((left + right)/2, bottom);
		
		shapeDrawableZoomOutMargin.setShape(new PathShape(pathZoomOutMargin, width, height));
		shapeDrawableZoomOutMargin.setBounds(0, 0, width, height);
		
		this.invalidate();
		
	}

	//@Override
	public void doZoom(float fScale) 
	{
		boolean skipZoom = false;
		
		LevelOfDetail tempLod = new LevelOfDetail();
		
		float tempLineLength = timelineLengthCurrent * fScale;
		float tempMillisPerPixel = activeTimeDiff / tempLineLength;
		
		tempLod.init(timelineLengthInitial);
		tempLod.setWholeParamter(activeTimeDiff / timelineLengthInitial);
		tempLod.submitChange(tempMillisPerPixel);
		
		if (fScale > 1.0f && tempLod.getState() == LodState.QUARTER_HOUR)
		{
			if (levelOfDetail.getState() == LodState.QUARTER_HOUR)
			{
				skipZoom = true;
			}
		}
		
		if (fScale < 1.0f && tempLod.getState() == LodState.WHOLE)
		{
			if (levelOfDetail.getState() == LodState.WHOLE)
			{
				skipZoom = true;
			}
			else
			{
				tempLineLength = timelineLengthInitial;
			}
		}
		
		ClearZoom();
		
		if (!skipZoom)
		{
			timelineLengthCurrent = tempLineLength;
			millisPerPixelCurrent = activeTimeDiff / timelineLengthCurrent;
			levelOfDetail.submitChange(millisPerPixelCurrent);
			this.setLayoutParams(new FrameLayout.LayoutParams((int)(timelineLengthCurrent), FrameLayout.LayoutParams.FILL_PARENT));
		}
		else
		{
			invalidate();
		}
	}
	

	//@Override
	public void viewClearZoom() 
	{
		ClearZoom();
	}

	private void ClearZoom()
	{
		int width = getWidth();
		int height = getHeight();
		
		if (!pathZoomOutFill.isEmpty())
		{
			pathZoomOutFill.rewind();
			shapeDrawableZoomOutFill.setShape(new PathShape(pathZoomOutFill, width, height));
			shapeDrawableZoomOutFill.setBounds(0, 0, width, height);
		}
		
		if (!pathZoomOutMargin.isEmpty())
		{
			pathZoomOutMargin.rewind();
			shapeDrawableZoomOutMargin.setShape(new PathShape(pathZoomOutMargin, width, height));
			shapeDrawableZoomOutMargin.setBounds(0, 0, width, height);
		}
		
		if (!pathZoomInFill.isEmpty())
		{
			pathZoomInFill.rewind();
			shapeDrawableZoomInFill.setShape(new PathShape(pathZoomInFill, width, height));
			shapeDrawableZoomInFill.setBounds(0, 0, width, height);
		}
		
		if (!pathZoomInMargin.isEmpty())
		{
			pathZoomInMargin.rewind();
			shapeDrawableZoomInMargin.setShape(new PathShape(pathZoomInMargin, width, height));
			shapeDrawableZoomInMargin.setBounds(0, 0, width, height);
		}
	}
	
	private void ClearTextLines()
	{
		int width = getWidth();
		int height = getHeight();
		
		shapeDrawableLinesQuarterHours.setShape(new PathShape(new Path(), width, height));
		shapeDrawableLinesQuarterHours.setBounds(0, 0, width, height);
		
		shapeDrawableLinesHalfHours.setShape(new PathShape(new Path(), width, height));
		shapeDrawableLinesHalfHours.setBounds(0, 0, width, height);
		
		shapeDrawableLinesHours.setShape(new PathShape(new Path(), width, height));
		shapeDrawableLinesHours.setBounds(0, 0, width, height);
	}

	private void DrawTextArray(Canvas canvas, Rect visibleRect, Paint paint, String[] arrayText, PointF[] arrayPosition, int textStartIndex, float offsetXPos, float offsetYPos) 
	{
		for (int indexText = textStartIndex, indexPos = 0; indexPos < arrayPosition.length; indexText = ((indexText + 1) % arrayText.length), indexPos += 2) 
		{
			if (visibleRect.left <= arrayPosition[indexPos].x && visibleRect.right >= arrayPosition[indexPos].x)
			{
				canvas.drawText(arrayText[indexText], arrayPosition[indexPos].x + offsetXPos, arrayPosition[indexPos].y + offsetYPos, paint);
			}
		}
	}
	
	private void DrawTextDate(Canvas canvas, Rect visibleRect, Paint paintText) 
	{	
		float lowestVisibleTime = activeDataLowestDateMillis + millisPerPixelCurrent * visibleRect.left;
		float highestVisibleTime = activeDataLowestDateMillis + millisPerPixelCurrent * visibleRect.right;
		
		if (visibleRect.left <= marginRectPoints.left)
		{
			lowestVisibleTime = activeDataLowestDateMillis;
		}
		
		if (visibleRect.right >= marginRectPoints.right)
		{
			highestVisibleTime = activeDataHighestDateMillis;
		}
		
		visibleLowestDate.setTimeInMillis((long)lowestVisibleTime);
		visibleHighestDate.setTimeInMillis((long)highestVisibleTime);
		
		boolean bTwoDays = true;
		
		if (highestVisibleTime - lowestVisibleTime <= LevelOfDetail.MILLIS_PER_DAY)
		{
			if (visibleLowestDate.get(Calendar.DAY_OF_YEAR) == visibleHighestDate.get(Calendar.DAY_OF_YEAR))
			{
				bTwoDays = false;
			}
		}
		
		if (bTwoDays)
		{
			String strLowestDate = sdfDate.format(visibleLowestDate.getTime());
			String strLowestDay = sdfDayName.format(visibleLowestDate.getTime());
			
			String strHighestDate = sdfDate.format(visibleHighestDate.getTime());
			String strHighestDay = sdfDayName.format(visibleHighestDate.getTime());
			
			canvas.drawText(strLowestDate, visibleRect.left + (visibleRect.right - visibleRect.left)/4.0f, visibleRect.bottom * 0.75f, paintText);
			canvas.drawText(strLowestDay, visibleRect.left + (visibleRect.right - visibleRect.left)/4.0f, visibleRect.bottom * 0.8f, paintText);
			
			canvas.drawText(strHighestDate, visibleRect.left + 3*(visibleRect.right - visibleRect.left)/4.0f, visibleRect.bottom * 0.75f, paintText);
			canvas.drawText(strHighestDay, visibleRect.left + 3*(visibleRect.right - visibleRect.left)/4.0f, visibleRect.bottom * 0.8f, paintText);
		}
		else
		{
			String strDate = sdfDate.format(visibleLowestDate.getTime());
			String strDay = sdfDayName.format(visibleLowestDate.getTime());
			
			canvas.drawText(strDate, visibleRect.left + (visibleRect.right - visibleRect.left)/2.0f, visibleRect.bottom * 0.75f, paintText);
			canvas.drawText(strDay, visibleRect.left + (visibleRect.right - visibleRect.left)/2.0f, visibleRect.bottom * 0.8f, paintText);
		}
	}
	
	private Path GenerateDataPath(Path oldPath, ArrayList<MovementInterval> movementList, float left, float top, float bottom) 
	{
		oldPath.rewind();
		
		float fStartInterval;
		float fEndInterval;
		long lStartInterval;
		long lEndInterval;
		
		for (MovementInterval movementInterval:movementList)
		{
			fStartInterval = movementInterval.getStart();
			fEndInterval = movementInterval.getEnd();
			
			fStartInterval /= millisPerPixelCurrent;
			fEndInterval /= millisPerPixelCurrent;
			
			lStartInterval = (long)(fStartInterval + left);
			lEndInterval = (long)(fEndInterval + left);
			
			if (lEndInterval - lStartInterval >= 1)
			{
				oldPath.addRect(lStartInterval, top, lEndInterval, bottom, Direction.CCW);
			}
		}

		return oldPath;
	}
	
	private PointF[] CalculateMargins(RectF rectPoints)
	{
		PointF[] points = new PointF[4];
		
		points[0] = new PointF(rectPoints.left, rectPoints.top);
		points[1] = new PointF(rectPoints.right, rectPoints.top);
		points[2] = new PointF(rectPoints.left, rectPoints.bottom);
		points[3] = new PointF(rectPoints.right, rectPoints.bottom);
		
		return points;
	}

	private PointF[] CalculateLinesPath(int numLines, float spaceInBetween, float left, float top, float bottom)
	{		
		PointF[] points = new PointF[2*numLines];
		
		float fCurrentX = left;
		
		for (int index = 0; index < 2*numLines; index += 2)
		{
			points[index] = new PointF(fCurrentX, top);
			points[index+1] = new PointF(fCurrentX, bottom);
			
			fCurrentX += spaceInBetween;
		}

		return points;
	}

	private Path GeneratePath(PointF[] points) 
	{
		Path generatedPath = new Path();

		for (int index = 0; index < points.length; index += 2) 
		{
			generatedPath.moveTo(points[index].x, points[index].y);
			generatedPath.lineTo(points[index + 1].x, points[index + 1].y);
		}

		return generatedPath;
	}
}
