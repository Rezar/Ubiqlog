package com.ubiqlog.core;

import com.ubiqlog.utils.IOManager;

import java.util.ArrayList;

public class DataAcquisitor {

	private static final String LOG_TAG = DataAcquisitor.class.getSimpleName();
	private String folderName;
	private String fileName;
	//private Context context;
	private ArrayList<String> dataBuffer;


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ArrayList<String> dataBuff = new ArrayList();


	public String getFolderName() {
		return folderName;
	}

	public DataAcquisitor() {

	}

	public DataAcquisitor(String folderName, String fileName) {
		//this.context = context;
		this.folderName = folderName;
		dataBuffer = new ArrayList<String>();
		this.fileName = fileName;
	}

	public void removeData(Long timeinms) {

	}

	public ArrayList<String> getDataBuffer() {
		return dataBuffer;
	}

	public String getCurrrentFileName() {
		return fileName;
	}


	public void insert(String s, boolean append, int maxBuffSize) {
		//Log.d(LOG_TAG, "Inserting into dBuff");
		dataBuffer.add(s);
		if (dataBuffer.size() >= maxBuffSize) {
			flush(append);
		}
	}

	public void flush(boolean append) {
		//Log.d(LOG_TAG, "Flushing buffer" + this.getFolderName());
		IOManager dataLogger = new IOManager(getCurrrentFileName(), getFolderName());
		dataLogger.logData(this, append);
		getDataBuffer().clear();
	}
}
