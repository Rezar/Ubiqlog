package com.ubiqlog.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ubiqlog.common.Setting;
import com.ubiqlog.sensors.SensorObj;

import java.util.ArrayList;


public class SensorCatalouge {

	private SQLiteDatabase db;
	private Context con;
	private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS [TABLE_NAME] (sensorname VARCHAR(30) PRIMARY KEY, "
			+ "classname TEXT NOT NULL, "+ "configdata TEXT NOT NULL,"+ "annotationClass TEXT);";

	public SensorCatalouge(Context ctx) {
		try {
			con = ctx;
			db = ctx.openOrCreateDatabase(Setting.Instance(ctx).getDatabaseName(),Context.MODE_WORLD_WRITEABLE, null);
			// db.execSQL("DROP TABLE sensors;");
			db.execSQL(DATABASE_CREATE.replace("[TABLE_NAME]", Setting.Instance(ctx).getDatabaseTableName()));
			db.close();
		} catch (Exception e) {
			Log.e("SensorCatalouge", "------Error creating or opening DB-----"+ e.getLocalizedMessage());
		}
	}

	public void closeDB(Context ctx) {
		if (db != null) {
			db.close();
		}
	}

	public void addSensor(String sensorname, String classname,String configdata, String annotation) {
		db = con.openOrCreateDatabase(Setting.Instance(null).getDatabaseName(),Context.MODE_WORLD_WRITEABLE, null);
		ContentValues initialValues = new ContentValues();


		initialValues.put("sensorname", sensorname);
		initialValues.put("classname", classname.toString());
		initialValues.put("configdata", configdata);
		initialValues.put("annotationClass", annotation);
		db.insert(Setting.Instance(null).getDatabaseTableName(), null, initialValues);
		db.close();
	}

	public void removeSensor(String sensorname) throws Exception {
		db = con.openOrCreateDatabase(Setting.Instance(null).getDatabaseName(),Context.MODE_WORLD_WRITEABLE, null);
		db.delete(Setting.Instance(null).getDatabaseTableName(), "sensorname='" + sensorname + "'",null);
		db.close();
	}

	// Change this according to my requirement
	public void updateSensor(String sensorName, String configdata) {
		db = con.openOrCreateDatabase(Setting.Instance(null).getDatabaseName(),Context.MODE_WORLD_WRITEABLE, null);
		db.execSQL("UPDATE "+ Setting.Instance(null).getDatabaseTableName()+" SET configdata='" + configdata+ "' WHERE sensorname='" + sensorName + "' ;");
		db.close();
	}
	
	public SensorObj getSensorByName(String sensorName) throws Exception
	{
		db = con.openOrCreateDatabase(Setting.Instance(null).getDatabaseName(),Context.MODE_WORLD_WRITEABLE, null);
		Cursor c = null;
		SensorObj sensor = null;
		try {
			c = db.query(Setting.Instance(null).getDatabaseTableName(), new String[] { "sensorname","classname", "configdata", "annotationClass" }, "sensorname='" + sensorName + "'", null,null, null, null);
			c.moveToFirst();
			
			sensor = new SensorObj();
			sensor.setSensorName(c.getString(0));
			sensor.setClassName(c.getString(1));
			sensor.setConfigData(c.getString(2).split(","));
			sensor.setAnnotationCalss(c.getString(3));
		} 
		catch (SQLException e) {
			Log.e("SensorCatalouge","-------------------Error listing Sensors:"+ e.getMessage());
		} 
		finally {
			c.close();
			db.close();
		}
		return sensor;
	}

	public ArrayList<SensorObj> getAllSensors() throws Exception {
		db = con.openOrCreateDatabase(Setting.Instance(null).getDatabaseName(),Context.MODE_WORLD_WRITEABLE, null);
		ArrayList<SensorObj> allSensors = new ArrayList<SensorObj>();
		Cursor c = null;
		try {
			c = db.query(Setting.Instance(null).getDatabaseTableName(), new String[] { "sensorname","classname", "configdata", "annotationClass" }, null, null,null, null, null);
			int numRows = c.getCount();
			c.moveToFirst();
			for (int i = 0; i < numRows; ++i) {
				SensorObj sensor = new SensorObj();
				sensor.setSensorName(c.getString(0));
				sensor.setClassName(c.getString(1));
				sensor.setConfigData(c.getString(2).split(","));
				sensor.setAnnotationCalss(c.getString(3));
				//Log.e("GETALLSENSOR", c.getString(0)+"%%%"+c.getString(1)+"%%%"+c.getString(2).split(",")+"%%%"+c.getString(3));
				allSensors.add(sensor);
				c.moveToNext();
			}
			String con = "Enable = yes";

			SensorObj sensorrawaudio = new SensorObj();
			sensorrawaudio.setSensorName("RAW_AUDIO");
			sensorrawaudio.setClassName("com.ubiqlog.sensors.RawAudioSensor");
			sensorrawaudio.setConfigData(con.split(","));
			sensorrawaudio.setAnnotationCalss(null);
			allSensors.add(sensorrawaudio);

			SensorObj sensorsleep = new SensorObj();
			sensorsleep.setSensorName("SLEEP");
			sensorsleep.setClassName("com.ubiqlog.sensors.SleepSensor");
			sensorsleep.setConfigData(con.split(","));
			sensorsleep.setAnnotationCalss(null);
			allSensors.add(sensorsleep);

			SensorObj sensorwear = new SensorObj();
			sensorwear.setSensorName("WEAR");
			sensorwear.setClassName("com.ubiqlog.sensors.WearSensor");
			sensorwear.setConfigData(con.split(","));
			sensorwear.setAnnotationCalss(null);
			allSensors.add(sensorwear);

		} catch (SQLException e) {
			Log.e("SensorCatalouge","-------------------Error listing Sensors:"+ e.getMessage());
		} finally {
			c.close();
			db.close();
		}
		return allSensors;
	}

	
	public void setUpinitCatalouge() {
		try {
			//SensorController ctrl = new SensorController(con);
			if (getAllSensors().size() == 0) { // There is no data there
				// Newly added 2015 by AP
				addSensor("BATTERY", com.ubiqlog.sensors.BatterySensor.class.getName(), "Enable = yes", null);				   // 10 seconds
				addSensor("SCREEN_INTERACTION", com.ubiqlog.sensors.InteractionSensor.class.getName(), "Enable = yes", null);  // 1 second
				addSensor("ACCELEROMETER", com.ubiqlog.sensors.AccelerometerSensor.class.getName(), "Enable = yes", null);
				addSensor("RAW_AUDIO", com.ubiqlog.sensors.RawAudioSensor.class.getName(), "Enable = yes", null);
				addSensor("AMBIENT_LIGHT", com.ubiqlog.sensors.AmbientLightSensor.class.getName(), "Enable = yes", null);

				addSensor("APPLICATION",  com.ubiqlog.sensors.ApplicationSensor.class.getName(),"Enable = yes, Record interval=10000", null);
				addSensor("CALL", com.ubiqlog.sensors.CallSensor.class.getName(),"Enable = yes, Record communication = no","CallAnnotation");
				addSensor("SMS", com.ubiqlog.sensors.SMSSensor.class.getName(), "Enable = yes", "SMSAnnotation");
				addSensor("BLUETOOTH", com.ubiqlog.sensors.BluetoothSensor.class.getName(),"Enable = no, Scan interval=600000", null); // each 10 minutes it scans for changes
				addSensor("WIFI", com.ubiqlog.sensors.WiFiSensor.class.getName(), "Enable = yes, Scan interval=600000",null);
				addSensor("LOCATION", com.ubiqlog.sensors.LocationSensor.class.getName(), "Enable = yes", null);
				// addSensor("ACCELEROMETER", com.ubiqlog.sensors.Hardware.class.getName(), "Enable = no",null);
				addSensor("PICTURE", com.ubiqlog.sensors.PictureSensor.class.getName(), "Enable = no, Record interval=30000", null);
				addSensor("AUDIO", com.ubiqlog.sensors.AudioSensor.class.getName(), "Enable = yes", null);
				addSensor("ACTIVITY", com.ubiqlog.sensors.ActivitySensor.class.getName(), "Enable = yes, Scan interval=30000",null);
				addSensor("LOCATION_FUSE", com.ubiqlog.sensors.LocationGSSensor.class.getName(), "Enable = yes, Scan interval=60000", null);

			}
			// else if (getAllSensors().size()<8) {
			//     addSensor(SensorName.AUDIO, ctrl.getSensorClassName(SensorName.AUDIO), "Enable = no", null);
			// }
		} catch (Exception e) {
			Log.e("SensorCatalouge","---------Can not initiate Sensor Catalouge------"+ e.getMessage());
			e.printStackTrace();
		}
	}
}