package com.ubiqlog.vis.extras.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;

import com.ubiqlog.ui.R;
import com.ubiqlog.vis.common.Settings;
import com.ubiqlog.vis.utils.UserFriendlyException;

/**
 * @author Soheil KHOSRAVIPOUR
 * @modified by Victor Gugonatu
 * @modified on 12.2010
 * @date 07.2010
 * @version 1.0
 */
public class Searcher {

	// This function receives sensor, and address of the file and returns the
	// search results in an array list
	public ArrayList<String> searchFile(String sensor, String fileAddress) {
		FileInputStream in;
		ArrayList<String> result = new ArrayList<String>(); // The Result array list

		try {
			in = new FileInputStream(new File(fileAddress));
		} catch (FileNotFoundException e) {
			// file not found -> ignore
			return new ArrayList<String>();
		}

		InputStreamReader inputreader = new InputStreamReader(in);
		BufferedReader buffreader = new BufferedReader(inputreader);

		String line;

		// read every line of the file into the line-variable, one line at the
		// time
		try {
			while ((line = buffreader.readLine()) != null) {
				if (line.startsWith("{\"" + sensor) || line.startsWith("\"" + sensor)) {
					// Found a record
					result.add(line);
				}

			}
		} catch (IOException e) {
			// ignore
		}

		return result;
	}

	// This function receives a sensor, dates and creates address of the files
	// and returns the search results using searchFile
	public ArrayList<String> searchFolder(String sensor, String date1,String date2, Context _context) 
		throws UserFriendlyException {
		ArrayList<String> result = new ArrayList<String>();

		String folderAddress = Settings.LOG_FOLDER + "/";
		String fileName = "log_";
		String[] firstDateArray = date1.trim().split("-");
		String[] lastDateArray = date2.trim().split("-");

		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Integer.parseInt(firstDateArray[2]), (Integer.parseInt(firstDateArray[0]) - 1), Integer.parseInt(firstDateArray[1]));

		GregorianCalendar lastDay = new GregorianCalendar();
		lastDay.set(Integer.parseInt(lastDateArray[2]), (Integer
				.parseInt(lastDateArray[0]) - 1), Integer
				.parseInt(lastDateArray[1]));

		if (cal.compareTo(lastDay) <= 0) {
			while (!cal.after(lastDay)) {
				String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
				String month = Integer.toString((cal.get(Calendar.MONTH)) + 1);
				String year = Integer.toString(cal.get(Calendar.YEAR));

				// new file naming structure
				fileName = fileName + month + "-" + day + "-" + year + ".txt";

				ArrayList<String> resultTemp = searchFile(sensor,
						(folderAddress + fileName));

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
			return result;
		} else {
			if (_context != null) {
				throw new UserFriendlyException(_context.getResources().getString(R.string.Vis_Searcher_start_date_end_date_exception));
			} else {
				// static -> English
				throw new UserFriendlyException("The to date is before the from date! Please modify the data and try again.");
			}
		}
	}
	
	// this method should be more efficent the the one above
	public String[] searchFolder(String dataFolder, String sensor, Date startDate, Date endDate) 
	{
		ArrayList<String> result = new ArrayList<String>();
		String[] theResult = null;

		String fileNameRoot = "log_";

		if (!dataFolder.endsWith("/")) 
		{
			dataFolder += "/";
		}

		File fileDir = new File(dataFolder);

		boolean bUseFile;
		int cmpStartDate, cmpEndDate;

		if (fileDir.list() != null && fileDir.list().length != 0)
		{
			for (String fileName : fileDir.list()) 
			{
				if (fileName.startsWith(fileNameRoot)) 
				{
					bUseFile = false;
					String[] fileDate = fileName.split(fileNameRoot);
					String[] dateFields = fileDate[1].split("-");

					String year = dateFields[2].split(".txt")[0];
					String month = dateFields[0];
					String day = dateFields[1];

					Date currentDate = new Date(Integer.parseInt(year) - 1900, Integer.parseInt(month) - 1, Integer.parseInt(day));
					
					cmpStartDate = currentDate.compareTo(startDate);
					cmpEndDate = currentDate.compareTo(endDate);

					if (!(cmpStartDate < 0 || cmpEndDate > 0)) 
					{
						bUseFile = true;
					}
					if (bUseFile) 
					{
						ArrayList<String> resultFile = searchFile(sensor, dataFolder + fileName);
						for (String entry : resultFile) 
						{
							result.add(entry);
						}
					}
				}
			}

			theResult = new String[result.size()];
			int index = 0;

			for (String entry : result) 
			{
				theResult[index++] = entry;
			}
		}

		return theResult;
	}

}