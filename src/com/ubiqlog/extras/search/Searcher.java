package com.ubiqlog.extras.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

import android.util.Log;

import com.ubiqlog.common.Setting;

/**
 * @author Soheil KHOSRAVIPOUR
 * @modified by Victor Gugonatu
 * @modified on 12.2010
 * @date 07.2010
 * @version 1.0
 */
public class Searcher {

	// This function receives sensors, a keyword and address of the file and
	// returns the search results in an array list
	public ArrayList<String> searchFile(ArrayList<String> sensors,String keyword, String fileAddress) {

		ArrayList<String> result = new ArrayList<String>(); // The Result array list
		ArrayList<String> noResult = new ArrayList<String>(); // Used for returning error messages
		FileInputStream in;

		try {
			in = new FileInputStream(new File(fileAddress));
		} catch (FileNotFoundException e) {
			// The Error message for not found files
			noResult.add("ERROR: The file \n\""+ fileAddress + "\" could not be found. Typically this means there is no log file for your specific date.");
			return noResult;
		}
		InputStreamReader inputreader = new InputStreamReader(in);
		BufferedReader buffreader = new BufferedReader(inputreader);
		String line;
		// read every line of the file into the line-variable, one line at the time
		try {
			while ((line = buffreader.readLine()) != null) {
				for (int i = 0; i < sensors.size(); i++) {
					if (line.startsWith("\"" + sensors.get(i)) && line.toLowerCase().contains(keyword.toLowerCase()) ||
						line.startsWith("{\"" + sensors.get(i)) && line.toLowerCase().contains(keyword.toLowerCase()) ) {
						// Found a record
						result.add(line);
					}
				}
			}
		} catch (IOException e) {
			// ignore
		}
		return result;
	}

	// This function receives sensors, a keyword, dates and creates address of
	// the files and returns the search results using searchFile
	// @SuppressWarnings("static-access")
	public ArrayList<String> searchFolder(ArrayList<String> sensors,String keyword, String date1, String date2) {

		ArrayList<String> noResult = new ArrayList<String>();
		if ((sensors == null) || (sensors.size() == 0)) {
			noResult.add("NOTE: Your search did not return any result, because you did not select any sensor. Please modify your search criteria and try again.");
			return noResult;
		}

		ArrayList<String> result = new ArrayList<String>();
		// Setting setting = new Setting();
		String folderAddress = Setting.LOG_FOLDER + "/";
		String fileName = "log_";
		String[] firstDateArray = date1.trim().split("-");
		String[] lastDateArray = date2.trim().split("-");

		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Integer.parseInt(firstDateArray[2]), (Integer.parseInt(firstDateArray[0]) - 1), Integer.parseInt(firstDateArray[1]));

		GregorianCalendar lastDay = new GregorianCalendar();
		lastDay.set(Integer.parseInt(lastDateArray[2]), (Integer.parseInt(lastDateArray[0]) - 1), Integer.parseInt(lastDateArray[1]));

		if (cal.compareTo(lastDay) <= 0) {
			while (!cal.after(lastDay)) {
				String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
				String month = Integer.toString((cal.get(Calendar.MONTH)) + 1);
				String year = Integer.toString(cal.get(Calendar.YEAR));

				// new file naming structure by Victor
				fileName = fileName + month + "-" + day + "-" + year + ".txt";

				ArrayList<String> resultTemp = searchFile(sensors, keyword,(folderAddress + fileName));

				// result.add(fileName); // you can use this line for testing
				// the correctness of fileNames creation
				for (int i = 0; i < resultTemp.size(); i++) {
					result.add(resultTemp.get(i));
				}

				fileName = "log_";
				cal.roll(Calendar.DAY_OF_MONTH, true);
				if ((cal.get(Calendar.DAY_OF_MONTH)) == 1) {
					cal.roll(Calendar.MONTH, true);
					if ((cal.get(Calendar.MONTH)) == 0) {
						cal.roll(Calendar.YEAR, true);
					}
				}
			}
			if (!result.isEmpty()) {
				return result;
			} else // No results found
			{
				// The Error message for not found
				noResult.add("NOTE: Your search did not return any result. Please modify your search criteria and try again.");
				return noResult;
			}
		} else {
			ArrayList<String> wrongDates = new ArrayList<String>();
			wrongDates.add("ERROR: The second date is before the first date! Please modify your search criteria and try again.");
			return wrongDates;
		}
	}
}