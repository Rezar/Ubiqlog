package com.ubiqlog.core;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.util.Log;
import android.*;

import com.ubiqlog.common.Setting;
import com.ubiqlog.utils.IOManager;

public class ArchiveableCheck {
	private IOManager ioman;
	private FileWriter writer;
	private String TAG = "ArchiveableCheck";

	public void checkAllfile() {
		try {
			File dir = new File(Setting.LOG_FOLDER);
			if (dir.isDirectory()) {
				Log.e(TAG, "---------YES YES IT IS DIRECTORY");
				File archFile = new File(Setting.LOG_FOLDER + "/"+ Setting.ARCH_FILE);
				writer = new FileWriter(archFile, true);
				if (!archFile.exists()) {
					archFile.createNewFile();
				}

				// writer.append(inArr.get(i) +
				// System.getProperty("line.separator"));
				File folder = new File(Setting.LOG_FOLDER);
				File[] listOfFiles = folder.listFiles();
				if (listOfFiles != null && listOfFiles.length > 0) {
					String[] fileNames = new String[listOfFiles.length];
					for (int i = 0; i < listOfFiles.length; i++) {
						String tmpName = listOfFiles[i].getName();
						if (!(tmpName.substring(tmpName.length() - 3,tmpName.length()).equalsIgnoreCase("txt") || 
								tmpName.substring(tmpName.length() - 3,tmpName.length()).equalsIgnoreCase("log"))) {
							String post = tmpName.substring(tmpName.length() - 3, tmpName.length());
							boolean ispreservable = false;
							for (int j = 0; j < Setting.PRESERVABLE.length; j++) {
								if (post.equalsIgnoreCase(Setting.PRESERVABLE[j])) {
									writer.append("file_name:"+ tmpName+ ", preservable:yes"+ System.getProperty("line.separator"));
									ispreservable = true;
									break;
								} else if (!(ispreservable)) {
									writer.append("file_name:"+ tmpName+ ", preservable:no"+ System.getProperty("line.separator"));
									break;
								}
							}
						}
					}
				}
				writer.flush();
				writer.close();
				writer = null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
