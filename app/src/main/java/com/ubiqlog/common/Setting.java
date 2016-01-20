package com.ubiqlog.common;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public final class Setting {

	public static Vector<String> vectorExcludedApplications = new Vector<String>();
	public static final String googleMapKey = "This has been removed for public access";

	public static final boolean AUTO_START = true;
	// start the app without notfication status bar
	public static final boolean SILENT_MODE = false;
	public static final boolean GROUP_TEST = true;
//	public static String  CURRENT_ACTIVITY = "";

	//	private String previousActv = "";
//	private Date prevDate ;
	//-----------------------------------------------------------
	public static boolean telLogging;
	// preservation
	public static final String ARCH_FILE = "ArchiveAble.txt";
	public static final String[] PRESERVABLE = { "tiff", "tif", "ogg", "mp4","m2s", "m2v", "wav", "aiff" };

	// ------------DEPRECATED---------------------
	public static final String EXCLUDED_APPS_SHARED_PREF_ID = "ExcludedApplicationsSharedPreferencesID";
	public static final String EXCLUDED_APPS_SHARED_PREF_FIRST_TIME_ID = "ExcludedApplicationsSharedPreferencesFirstTimeID";
	public static final String EXCLUDED_APPS_SHARED_PREF_FIRST_TIME_KEY = "FirstTimeKey";
	// Setting
	public static String[] coreProcs = { "com.htc.launcher","com.android.phone", "system",
			"android","android.process.acore", "com.android.email", "com.ubiqlog.ui",
			"com.google.process.gapps", "com.motorola.thumbnailservice",
			"com.arvin.ui", "android.process.media", "com.android.bluetooth",
			"com.nuance.android.vsuite.vsuiteapp", "com.antivirus","com.android.systemui"};
	//----------------------------------------------
	public static String daggSav2File = "10000"; // each 10 seconds
	public static String serverAddress = "http://192.168.0.10:8080/ubiqlogserver/ReceivedData"; // the servlet is not available open source yet.
	public static String username = "";
	public static String password = "";
	public static String maxDataSize = "5MB";
	public static final String FILENAME_STR = "mobilefileName";
	// Sensor Catalouge
	public static final String DATABASE_NAME = "SensorDB";
	public static final String DATABASE_TABLE = "sensors";
	public static final int DATABASE_VERSION = 1;
	public static boolean SMS_ANNOTATION ;
	public static boolean CALL_ANNOTATION;
	// MainUI
	public static final String[] MAINUI_LIST = { "Logging", "Sensors","Settings", "Tools" };
	public static final String LOG_FOLDER = "/sdcard/ubiqlog";// /audio
	public static final String PICTURE_FOLDER = "/sdcard/ubiqlog/picture";
	// public static final String camera = "com.android.camera";
	// public static final String audio = "com.android.music";

	// SensorUI
	public static final String CONFIGDATA = "configdata";
	public static final String SENSORNAME = "sensorName";


	/**
	 *  Added by AP , 2015
	 *  Needed for new sensors:
	 *  	Screen Interaction
	 *  	Battery Usage
	 *  	Accelerometer
	 *  	Raw Audio Signal
	 **/
	public static final int bufferMaxSize = 1;

	public static final String LOG_EX_FOLDER = "ubiqlogex";
	public static final String DEFAULT_FOLDER = "ubiqlog";

	public static final SimpleDateFormat timestampFormat = new SimpleDateFormat("E MMM d HH:mm:ss zzz yyyy"); // e.g. 'Wed Mar 04 00:03:56 GMT+01:00 2015'
	public static final SimpleDateFormat filenameFormat = new SimpleDateFormat("M-d-yyyy");
	public static final String dataFileName_ScreenUsage = "ScreenUsage";
	public static final String dataFileName_Battery = "Battery";
	public static final String dataFileName_Accelerometer = "Accelerometer";
	public static final String dataFileName_Raw_Audio = "RawAudio";
	public static final String dataFileName_AmbientLightSensor = "AmbientLight";

	// Accelerometer delay setting
	public static final Long ACCELEROMETER_SAVE_DELAY = 2000L; // 2000 milliseconds
	// Battery delay setting
	public static long BATTERY_MIN_SAMPLE_INTERVAL = 60000L; // 1 minutes (1000 * 60 * 10)
	// Ambient Light settings
	public static int LIGHT_SAMPLE_AMNT = 3;
	public static long LIGHT_SENSOR_SAMPLE_INTERVAL = 30000L; // 30 Seconds
	// Raw Audio settings
	public static long RAW_AUDIO_DELAY = 5000L; // 30 Seconds



	public static Setting Instance(Context ctx){
		if(_instance == null){
			_instance= new Setting();
			_instance._ctx = ctx;
			_instance.initialise();
		}
		return _instance;
	}

	private Context _ctx = null;
	private static Setting _instance = null;

	private String _archive_file = null;
	private String[] _preservable = null;
	private String _daggSav2File = null;
	private String _remote_serverAddress = null;
	private String _remote_username = null;
	private String _remote_password = null;
	private String _remote_maxDataSize = null;
	private String _remote_filename = null;
	private String[] _coreProcs = null;
	private String _logFolder = null;
	private String _pictureFolder = null;
	private String _audioFolder = null;
	private String _database_name = null;
	private String _database_table_name = null;
	private String _preferences_filename = null;
	private int _application_timeinterval = Integer.MIN_VALUE;
	private int _location_timeFrame = Integer.MIN_VALUE;
	private int _call_timeFrame = Integer.MIN_VALUE;
	private int _sms_timeFrame = Integer.MIN_VALUE;
	private String _EXCLUDED_APPS_SHARED_PREF_ID = null;


	public String getArchiveFile() {
		if(_archive_file==null){
			_archive_file= _ctx.getString(_ctx.getResources().getIdentifier("string/ubiqlog_api_arch_file", null, _ctx.getPackageName()));
		}
		return _archive_file;
	}

	public String[] getPreservable() {
		if(_preservable==null){
			_preservable = _ctx.getResources().getStringArray(_ctx.getResources().getIdentifier("array/ubiqlog_api_preservable", null, _ctx.getPackageName()));
		}
		return _preservable;
	}

	public String getDaggSav2File() {
		if(_daggSav2File==null){
			_daggSav2File= _ctx.getString(_ctx.getResources().getIdentifier("string/ubiqlog_api_daggSav2File", null, _ctx.getPackageName()));
		}
		return _daggSav2File;
	}

	public String getRemoteServerAddress() {
		if(_remote_serverAddress==null){
			_remote_serverAddress= _ctx.getString(_ctx.getResources().getIdentifier("string/ubiqlog_api_remote_serverAddress", null, _ctx.getPackageName()));
		}
		return _remote_serverAddress;
	}

	public String getRemoteUsername() {
		if(_remote_username==null){
			_remote_username= _ctx.getString(_ctx.getResources().getIdentifier("string/ubiqlog_api_remote_username", null, _ctx.getPackageName()));
		}
		return _remote_username;
	}

	public String getRemotePassword() {
		if(_remote_password==null){
			_remote_password= _ctx.getString(_ctx.getResources().getIdentifier("string/ubiqlog_api_remote_password", null, _ctx.getPackageName()));
		}
		return _remote_password;
	}

	public String getRemoteMaxDataSize() {
		if(_remote_maxDataSize==null){
			_remote_maxDataSize= _ctx.getString(_ctx.getResources().getIdentifier("string/ubiqlog_api_remote_maxDataSize", null, _ctx.getPackageName()));
		}
		return _remote_maxDataSize;
	}

	public String getRemoteFilename() {
		if(_remote_filename==null){
			_remote_filename= _ctx.getString(_ctx.getResources().getIdentifier("string/ubiqlog_api_remote_filename", null, _ctx.getPackageName()));
		}
		return _remote_filename;
	}

	public String[] getCoreProcs() {
		if(_coreProcs==null){
			_coreProcs = _ctx.getResources().getStringArray(_ctx.getResources().getIdentifier("array/ubiqlog_api_coreProcs", null, _ctx.getPackageName()));
		}
		return _coreProcs;
	}

	public String getLogFolder() {
		if(_logFolder==null){
			_logFolder= _ctx.getString(_ctx.getResources().getIdentifier("string/ubiqlog_api_log_folder", null, _ctx.getPackageName()));
		}
		return _logFolder;
	}

	public String getPictureFolder() {
		if(_pictureFolder==null){
			_pictureFolder= _ctx.getString(_ctx.getResources().getIdentifier("string/ubiqlog_api_picture_folder", null, _ctx.getPackageName()));
		}
		return _pictureFolder;
	}

	public String getAudioFolder() {
		if(_audioFolder==null){
			_audioFolder= _ctx.getString(_ctx.getResources().getIdentifier("string/ubiqlog_api_audio_folder", null, _ctx.getPackageName()));
		}
		return _audioFolder;
	}

	public String getDatabaseName() {
		if(_database_name==null){
			_database_name= _ctx.getString(_ctx.getResources().getIdentifier("string/ubiqlog_api_database_name", null, _ctx.getPackageName()));
		}
		return _database_name;
	}

	public String getDatabaseTableName() {
		if(_database_table_name==null){
			_database_table_name= _ctx.getString(_ctx.getResources().getIdentifier("string/ubiqlog_api_database_table_name", null, _ctx.getPackageName()));
		}
		return _database_table_name;
	}

	public String getPreferencesFilename() {
		if(_preferences_filename==null){
			_preferences_filename= _ctx.getString(_ctx.getResources().getIdentifier("string/ubiqlog_api_preferences_filename", null, _ctx.getPackageName()));
		}
		return _preferences_filename;
	}

	public int getApplicationTimeInterval() {
		if(_application_timeinterval==Integer.MIN_VALUE){
			_application_timeinterval= Integer.parseInt(_ctx.getString(_ctx.getResources().getIdentifier("string/ubiqlog_api_application_timeinterval", null, _ctx.getPackageName())));
		}
		return _application_timeinterval;
	}

	public int getSmsTimeFrame() {
		if(_sms_timeFrame==Integer.MIN_VALUE){
			_sms_timeFrame= Integer.parseInt(_ctx.getString(_ctx.getResources().getIdentifier("string/ubiqlog_api_sms_time_frame", null, _ctx.getPackageName())));
		}
		return _sms_timeFrame;
	}

	public int getCallTimeFrame() {
		if(_call_timeFrame==Integer.MIN_VALUE){
			_call_timeFrame= Integer.parseInt(_ctx.getString(_ctx.getResources().getIdentifier("string/ubiqlog_api_call_time_frame", null, _ctx.getPackageName())));
		}
		return _call_timeFrame;
	}

	public int getLocationTimeFrame() {
		if(_location_timeFrame==Integer.MIN_VALUE){
			_location_timeFrame= Integer.parseInt(_ctx.getString(_ctx.getResources().getIdentifier("string/ubiqlog_api_location_time_frame", null, _ctx.getPackageName())));
		}
		return _location_timeFrame;
	}

	public String getEXCLUDED_APPS_SHARED_PREF_ID() {
		if(_EXCLUDED_APPS_SHARED_PREF_ID==null){
			_EXCLUDED_APPS_SHARED_PREF_ID= (_ctx.getString(_ctx.getResources().getIdentifier("string/ubiqlog_api_EXCLUDED_APPS_SHARED_PREF_ID", null, _ctx.getPackageName())));
		}
		return _EXCLUDED_APPS_SHARED_PREF_ID;
	}

	// Initialize preferences (from pref file or default)
	public void initialise() {
		SharedPreferences settings = _ctx.getSharedPreferences(getPreferencesFilename(), 0);
		_location_timeFrame = settings.getInt("location_timeFrame",_location_timeFrame);
		_call_timeFrame = settings.getInt("call_timeFrame", _call_timeFrame);
		_sms_timeFrame = settings.getInt("sms_timeFrame", _sms_timeFrame);

		_daggSav2File = settings.getString("daggSav2File", _daggSav2File);
		_remote_maxDataSize = settings.getString("remote_maxDataSize", _remote_maxDataSize);

		_remote_serverAddress = settings.getString("remote_serverAddress", _remote_serverAddress);
		_remote_username = settings.getString("remote_username", _remote_username);
		_remote_password = settings.getString("remote_password", _remote_password);

		getCoreProcsFromPrefs();
	}

	// save preferences in pref file
	public void savePreferences(int location_timeFrame, int call_timeFrame, int sms_timeFrame) {
		SharedPreferences settings = _ctx.getSharedPreferences(
				getPreferencesFilename(), 0);
		SharedPreferences.Editor editor = settings.edit();

		_location_timeFrame = location_timeFrame;
		_call_timeFrame = call_timeFrame;
		_sms_timeFrame = sms_timeFrame;

		editor.putInt("location_timeFrame", _location_timeFrame);
		editor.putInt("call_timeFrame", _call_timeFrame);
		editor.putInt("sms_timeFrame", _sms_timeFrame);

		// Commit the edits!
		editor.commit();

	}

	// save preferences in pref file
	public void savePreferences(String daggSav2File, String remote_maxDataSize) {
		SharedPreferences settings = _ctx.getSharedPreferences(getPreferencesFilename(), 0);
		SharedPreferences.Editor editor = settings.edit();

		_daggSav2File = daggSav2File;
		_remote_maxDataSize = remote_maxDataSize;

		editor.putString("daggSav2File", _daggSav2File);
		editor.putString("remote_maxDataSize", _remote_maxDataSize);

		// Commit the edits!
		editor.commit();
	}


	// save preferences in pref file
	public void savePreferences(String remote_serverAddress, String remote_username, String remote_password) {
		SharedPreferences settings = _ctx.getSharedPreferences(getPreferencesFilename(), 0);
		SharedPreferences.Editor editor = settings.edit();

		_remote_serverAddress = remote_serverAddress;
		_remote_username = remote_username;
		_remote_password = remote_password;

		editor.putString("remote_serverAddress", _remote_serverAddress);
		editor.putString("remote_username", _remote_username);
		editor.putString("remote_password", _remote_password);

		// Commit the edits!
		editor.commit();
	}



	public void saveCoreProcs(String[] coreProcs) {
		SharedPreferences preferences = _ctx.getSharedPreferences(this.getEXCLUDED_APPS_SHARED_PREF_ID(), 0);
		SharedPreferences.Editor prefEditor = preferences.edit();
		prefEditor.clear();
		for (int index = 0; index < coreProcs.length; ++index){
			String strItem = coreProcs[index];
			prefEditor.putString(strItem, strItem);
		}
		prefEditor.commit();
	}

	public void getCoreProcsFromPrefs() {
		SharedPreferences preferences = _ctx.getSharedPreferences(this.getEXCLUDED_APPS_SHARED_PREF_ID(), 0);

		@SuppressWarnings("unchecked")
		Map<String, String> listContent = (Map<String, String>) preferences.getAll();

		if(!listContent.isEmpty())
		{
			this._coreProcs = new String[listContent.size()];
			int index =0;

			Collection<String> listValues = listContent.values();
			for (Iterator<String> iterator = listValues.iterator(); iterator.hasNext(); ){
				this._coreProcs[index] =iterator.next();
				index++;
			}
		}

	}
}
