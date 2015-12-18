package com.ubiqlog.sensors;

import java.util.Date;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.ubiqlog.common.Setting;
import com.ubiqlog.core.DataAcquisitor;
import com.ubiqlog.core.SensorCatalouge;
import com.ubiqlog.utils.IOManager;
import com.ubiqlog.utils.JsonEncodeDecode;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceView;

/**
 * This sensor will use time interval for logging
 * 
 * @author Victor Gugonatu, Manuel Bischof
 * 
 */
public class PictureSensor extends Service implements SensorConnector {

	private static final String TAG = "Picture-Logging";
	private static long PICTURE_LOG_INTERVAL = 30000L;
	private static String PICTURE_FOLDER = null;
	private static Camera cam = null;
	private Context _ctx = null;
	private Handler objHandler = new Handler();
	private int autofocusTries = 0;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private Runnable doPictureLogging = new Runnable() {
		public void run() {
			readSensor();
			objHandler.postDelayed(doPictureLogging, PICTURE_LOG_INTERVAL);
		}
	};

	@Override
	public void onCreate() {
		_ctx = this;
		autofocusTries = 0;
		PICTURE_FOLDER = Setting.Instance(this).getPictureFolder();
		Log.d("Picture-Logging", "--- onCreate");
		SensorCatalouge sencat = new SensorCatalouge(getApplicationContext());
		try {
			ArrayList<SensorObj> sens = sencat.getAllSensors();
			for (int i = 0; i < sens.size(); i++) {
				if (sens.get(i).getSensorName().equalsIgnoreCase("PICTURE")) {
					String[] configs = sens.get(i).getConfigData();
					for (int j = 0; j < configs.length; j++) {
						String tmp[] = configs[j].split("=");

						if (tmp[0].trim().equalsIgnoreCase(
								"Record interval in milli second")) {
							PICTURE_LOG_INTERVAL = Long.parseLong(tmp[1]);
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e("Picture Sensor",
					"----------Error reading the log interval from sensor catalogue-----"
							+ e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		objHandler.removeCallbacks(doPictureLogging);
		ReleaseCamera();

		// refresh media - TODO

		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				Uri.parse("file://" + PICTURE_FOLDER)));

		Log.d("Picture-Logging", "--- onDestroy");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("Picture-Logging", "--- onStart");
		_ctx = this;
		readSensor();
		objHandler.postDelayed(doPictureLogging, PICTURE_LOG_INTERVAL);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("Picture-Logging", "--- onStartCommand");
		_ctx = this;
		readSensor();
		objHandler.postDelayed(doPictureLogging, PICTURE_LOG_INTERVAL);
		return START_STICKY;
	}
	
	private void ReleaseCamera() {
		if (cam != null) {
			Log.d("Picture-Logging", "--- release camera");
			try {
				cam.release();
				cam = null;
			} catch (Exception ex) {
			}
		}

	}

	private void InitSurfacePreview() {

		boolean hasPreview = false;

		// try to use superior api version just if supported
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {

				Class<?> classSurfaceTexture = null;
				Constructor<?> constructorSurfaceTexture = null;

				classSurfaceTexture = Class
						.forName("android.graphics.SurfaceTexture");
				// Log.d("Picture-Logging",
				// "--- initialising class successful");

				constructorSurfaceTexture = classSurfaceTexture
						.getConstructor(new Class[] { int.class });
				// Log.d("Picture-Logging",
				// "--- initialising constructor successful");

				Method proSetMethod = cam.getClass().getDeclaredMethod(
						"setPreviewTexture",
						new Class[] { classSurfaceTexture });
				// Log.d("Picture-Logging",
				// "--- initialising method successful");

				proSetMethod.invoke(cam,
						constructorSurfaceTexture.newInstance(1234));
				// Log.d("Picture-Logging", "--- invoking method successful");

				Log.d("Picture-Logging",
						"--- surface texture set as preview texture");
				hasPreview = true;

			} catch (ClassNotFoundException e1) {
				Log.e("Picture-Logging", "--- ERROR initialising camera 1: "
						+ e1.getMessage());
				e1.printStackTrace();

			} catch (SecurityException e1) {
				Log.e("Picture-Logging", "--- ERROR initialising camera 8: "
						+ e1.getMessage());
				e1.printStackTrace();
			} catch (NoSuchMethodException e1) {
				Log.e("Picture-Logging", "--- ERROR initialising camera 9: "
						+ e1.getMessage());
				e1.printStackTrace();
			} catch (IllegalArgumentException e) {
				Log.e("Picture-Logging", "--- ERROR initialising camera 4: "
						+ e.getMessage());
				e.printStackTrace();
			} catch (InstantiationException e) {
				Log.e("Picture-Logging", "--- ERROR initialising camera 5: "
						+ e.getMessage());
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				Log.e("Picture-Logging", "--- ERROR initialising camera 6: "
						+ e.getMessage());
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				Log.e("Picture-Logging", "--- ERROR initialising camera 7: "
						+ e.getMessage());
				e.printStackTrace();
			}

		}

		if (!hasPreview) {
			// register with code for api 7+ using dummy SurfaceView

			SurfaceView surfaceView = new SurfaceView(_ctx);
			try {
				cam.setPreviewDisplay(surfaceView.getHolder());
				Log.d("Picture-Logging",
						"--- surface view set as preview display");
			} catch (IOException e) {
				Log.e("Picture-Logging",
						"--- ERROR CRITICAL - cannot set preview : "
								+ e.getMessage());
				e.printStackTrace();
			}

		}

	}

	private void InitCamera() {
		if (cam == null) {
			Log.d("Picture-Logging", "--- init camera");

			// SurfaceTexture view = new SurfaceTexture(1234);
			cam = Camera.open();

			// register an error callback if something goes wrong
			cam.setErrorCallback(errorCallback);

			// register a preview callback for the next preview frame
			cam.setOneShotPreviewCallback(previewCallback);

			InitSurfacePreview();
			setResolution();
			setFocusMode();
			setSceneMdoe();

		} else {

			// register a preview callback for the next preview frame - must be
			// registered every time
			cam.setOneShotPreviewCallback(previewCallback);

			Log.d("Picture-Logging", "--- camera is already init!");
		}

	}

	private void setSceneMdoe() {
		Camera.Parameters params = cam.getParameters();
		if (params.getSupportedSceneModes() == null) {
			return;
		}
		if (params.getSupportedSceneModes().contains(
				Camera.Parameters.SCENE_MODE_STEADYPHOTO)) {
			params.setSceneMode(Camera.Parameters.SCENE_MODE_STEADYPHOTO);
			cam.setParameters(params);
			return;
		}
		if (params.getSupportedSceneModes().contains(
				Camera.Parameters.SCENE_MODE_SPORTS)) {
			params.setSceneMode(Camera.Parameters.SCENE_MODE_SPORTS);
			cam.setParameters(params);
			return;
		}
	}

	private void setFocusMode() {
		Camera.Parameters params = cam.getParameters();
		if (params.getSupportedFocusModes().contains(
				Camera.Parameters.FOCUS_MODE_INFINITY)) {
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
			cam.setParameters(params);
		}
	}

	public void readSensor() {
		try {

			InitCamera();

			// start preview
			cam.startPreview();

		} catch (Exception e) {
			IOManager errlogger = new IOManager();
			errlogger.logError("[PictureSensor] error:" + e.getMessage());
			Log.e("Picture-Logging", "--- ERROR 3:" + e.getMessage());
			e.printStackTrace();
		}
	}

	private void setResolution() {
		Camera.Parameters params = cam.getParameters();
		Camera.Size preferredSize = params.getPictureSize();
		for (Camera.Size aSize : params.getSupportedPictureSizes()) {
			if (aSize.width == 1600) {
				preferredSize = aSize;
			}
		}
		params.setPictureSize(preferredSize.width, preferredSize.height);
		cam.setParameters(params);

	}

	public Camera.ErrorCallback errorCallback = new Camera.ErrorCallback() {

		public void onError(int error, Camera camera) {
			Log.d("Picture-Logging", "--- ERROR CAMERA:" + error);
			try {
				cam.stopPreview();
			} catch (Exception eee) {
			}
		}
	};

	public Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

		
		public void onPreviewFrame(byte[] data, Camera camera) {
			// save the data on sd and in log
			Log.d("Picture-Logging", "--- preview callback");
			if (wantsAutofocus()) {
				camera.autoFocus(autofocusCallback);
			} else {
				camera.takePicture(null, null, pictureCallback);
			}

		}
	};


	protected boolean wantsAutofocus() {
		// TODO Auto-generated method stub
		// read option from Setting

		return false;
	}

	private AutoFocusCallback autofocusCallback = new AutoFocusCallback() {

		public void onAutoFocus(boolean autofocus, Camera camera) {
			if (autofocus == false) {
				autofocusTries++;
				if (autofocusTries > 5) {
					// take picture anyway
					Log.d(TAG, "Failed to autofocus, trying again");
					camera.takePicture(null, null, pictureCallback);
					autofocusTries = 0;
				}
				return;
			}

			camera.takePicture(null, null, pictureCallback);
			autofocusTries = 0;
		}
	};

	private PictureCallback pictureCallback = new PictureCallback() {

		public void onPictureTaken(byte[] data, Camera camera) {
			Size size = camera.getParameters().getPictureSize();
			saveImageFromPictureCallback(data, size);
			camera.startPreview();
		}
	};

	public void saveImageFromPictureCallback(byte[] data, Size size) {

		// create folder if not already created

		File sdImageMainDirectory = new File(PICTURE_FOLDER);
		
		if (!sdImageMainDirectory.exists()) {
			Log.d("Picture-Logging", "--- create picture folder: "
					+ PICTURE_FOLDER);
			if (sdImageMainDirectory.mkdirs()) {
				Log.d("Picture-Logging", "--- create picture folder succeded!");
			} else {
				Log.d("Picture-Logging", "--- create picture folder failed!");
			}
		}

		// Generate filename
		SimpleDateFormat sdf = new SimpleDateFormat("Z-yyMMdd-HHmmss.SSS");
		String filename = sdImageMainDirectory.toString() + "/" + "Picture-"
				+ sdf.format(new Date());
		String tmp_filename = filename + ".jpg";

		int i = 0;

		File tmp = new File(tmp_filename);
		while (tmp.exists() && i < 100) {
			i++;
			tmp_filename = filename + "_" + i + ".jpg";
			tmp = new File(tmp_filename);

		}
		filename = tmp_filename;

		// Store image to file system
		Log.d("Picture-Logging", "--- try to store picture!");
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			fos.write(data);
			fos.close();
		} catch (FileNotFoundException e) {
			Log.d(TAG, "File not found: " + e.getMessage());
		} catch (IOException e) {
			Log.d(TAG, "Error accessing file: " + e.getMessage());
		}

		// Send broadcast, so that other apps are informed about the new file
		// (e.g Gallery-App)
		Uri fileuri = Uri.fromFile(new File(filename));
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileuri));
		// Log file info into JSON-file
		/*
		 * String jsonString = JsonEncodeDecode.EncodePicture(
		 * getSensorFriendlyName(), PICTURE_FOLDER + "/" + filename + ".jpg",
		 * new Date());
		 */
		String jsonString = JsonEncodeDecode.EncodePicture("Picture", filename, new Date()); // Bug resolved?
		DataAcquisitor.dataBuff.add(jsonString);
	}

	public boolean StoreByteImage(String filename, byte[] imageData, int width, int height, int quality) {

		File sdImageMainDirectory = new File(PICTURE_FOLDER);
		Boolean pathExists = true;
		if (!sdImageMainDirectory.exists()) {
			Log.d("Picture-Logging", "--- create picture folder: "
					+ PICTURE_FOLDER);
			if (sdImageMainDirectory.mkdirs()) {
				Log.d("Picture-Logging", "--- create picture folder succeded!");
			} else {
				Log.d("Picture-Logging", "--- create picture folder failed!");
				pathExists = false;
			}
		}

		if (pathExists) {
			FileOutputStream fileOutputStream = null;
			try {
				// we need to convert the default android format (yuv) to rgb
				// a simpler way is to use YuvImage but it is integrated just in
				// android 2.2+
				int[] rgb = convertYUVtoRGB(imageData, width, height);
				Bitmap bmp = Bitmap.createBitmap(rgb, width, height,Bitmap.Config.ARGB_8888);
				Log.d("Picture-Logging", "--- save picture: "
						+ sdImageMainDirectory.toString() + "/" + filename
						+ ".jpg");
				fileOutputStream = new FileOutputStream(
						sdImageMainDirectory.toString() + "/" + filename
								+ ".jpg");
				bmp.compress(CompressFormat.JPEG, quality, fileOutputStream);
			} catch (FileNotFoundException e) {
				IOManager errlogger = new IOManager();
				errlogger.logError("[PictureSensor] error:" + e.getMessage());
				e.printStackTrace();
				Log.e("Picture-Logging", "--- ERROR 1: " + e.getMessage());
			} catch (Exception e) {
				IOManager errlogger = new IOManager();
				errlogger.logError("[PictureSensor] error:" + e.getMessage());
				e.printStackTrace();
				Log.e("Picture-Logging", "--- ERROR 2: " + e.getMessage());
			}
		}

		return true;
	}

	/*
	 * function to convert from YUV to RGB
	 */
	private int[] convertYUVtoRGB(byte[] yuv420sp, int width, int height) {
		int frameSize = width * height;
		int[] decodedByteArray = new int[width * height];

		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0)
					y = 0;
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}

				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);

				if (r < 0)
					r = 0;
				else if (r > 262143)
					r = 262143;
				if (g < 0)
					g = 0;
				else if (g > 262143)
					g = 262143;
				if (b < 0)
					b = 0;
				else if (b > 262143)
					b = 262143;

				decodedByteArray[yp] = 0xff000000 | ((r << 6) & 0xff0000)
						| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}

		return decodedByteArray;
	}

}