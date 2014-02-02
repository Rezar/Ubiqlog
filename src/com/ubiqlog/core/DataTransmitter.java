package com.ubiqlog.core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.FileEntity; //import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.HttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.*;

import android.net.Uri;
import android.util.Log;

import com.ubiqlog.common.Setting;

public class DataTransmitter {
	private HttpGet get;
	private HttpPost post;
	public String serverAdd;
	private final String TAG = "DataTransmitter";

	/**
	 * Upload file content via HttpPost method (We use Android built-in
	 * HttpClient modules)
	 * 
	 * @param filepath
	 */
	public void doUpload(String filepath, String filename) {
		HttpClient httpClient = new DefaultHttpClient();
		try {
			httpClient.getParams().setParameter("http.socket.timeout",new Integer(90000)); // 90 second
			post = new HttpPost(new URI(Setting.serverAddress));
			File file = new File(filepath);
			FileEntity entity;
			if (filepath.substring(filepath.length() - 3, filepath.length()).equalsIgnoreCase("txt") || 
					filepath.substring(filepath.length() - 3,filepath.length()).equalsIgnoreCase("log")) {
				entity = new FileEntity(file, "text/plain; charset=\"UTF-8\"");
				entity.setChunked(true);
			} else {
				entity = new FileEntity(file, "binary/octet-stream");
				entity.setChunked(true);
			}
			post.setEntity(entity);
			post.addHeader(Setting.FILENAME_STR, filename);

			HttpResponse response = httpClient.execute(post);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				Log.e(TAG, "--------Error--------Response Status line code:"+ response.getStatusLine());
			} else {
				SimpleDateFormat dateformat = new SimpleDateFormat("M-d-yyyy");
				//String datenow = DateFormat.getDateInstance(DateFormat.SHORT).format(System.currentTimeMillis());
				String datenow = dateformat.format(new Date());
//				datenow = datenow.replace("/", "-");
				if (!filename.equalsIgnoreCase("log_" + datenow + ".txt")) {
					removeFile(filename);
				}
			}
			HttpEntity resEntity = response.getEntity();
			if (resEntity == null) {
				Log.e(TAG, "---------Error No Response !!!-----");
			}
		} catch (Exception ex) {
			Log.e(TAG, "---------Error-----" + ex.getMessage());
			ex.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	public void removeFile(String filename) {
		File file4die = new File(Setting.LOG_FOLDER + "/" + filename);
		file4die.delete();
	}

	/**
	 * @deprecated This is just for test Get method do not use it.
	 */
	public void doUploadFileInfo(String filename) {
		HttpClient httpClient = new DefaultHttpClient();
		try {
			httpClient.getParams().setParameter("http.socket.timeout",
					new Integer(90000)); // 90 second
			get = new HttpGet(new URI(Setting.serverAddress));
			httpClient.getParams().setParameter("filename", filename);
			httpClient.execute(get);
			HttpResponse response = httpClient.execute(post);
			Log.e("DataTrasmitor", "----------------Response Status line code:"
					+ response.getStatusLine());
		} catch (Exception ex) {
			Log.e("DataTransmitor", "---------Error-----" + ex.getMessage());
			ex.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

	}

}
