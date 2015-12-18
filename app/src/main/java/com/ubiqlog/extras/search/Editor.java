package com.ubiqlog.extras.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

import com.ubiqlog.common.Setting;

/*
 * @author      Soheil KHOSRAVIPOUR
 * @date		07.2010
 * @version     0.9
 */
public class Editor {

	static Scanner in;
	static Writer out;

	// This function receives a record,address of the file, edit(true) or
	// delete(false) flag and the edited record
	// and edit/delete the record. It returning flag will be true if the record
	// is in this file
	// and it is edited/deleted without any error
	public boolean editFile(String record, String fileAddress,boolean editOrDelete, String newRecord) {
		try {
			in = new Scanner(new File(fileAddress));
		} catch (FileNotFoundException e) {
			return false;
		}
		while (in.hasNextLine()) {
			String line = in.nextLine();
			if (line.contains(record)) // "Found the record"
			{
				if (editOrDelete) // must be edited
				{ // If the record edited in this file without error, returns
					// true
					return (editLineFromFile(fileAddress, line, newRecord));
				} else // must be deleted
				{ // If the record deleted in this file without error, returns
					// true
					return (removeLineFromFile(fileAddress, line));

				}
			}
		}
		in.close();

		return false; // The record is not in this file
	}

	public boolean editFolder(String record, String date1, String date2,boolean editOrDelete, String newRecord) 
	{
		String folderAddress = Setting.LOG_FOLDER + "/";
		String fileName = "log_";
		String[] firstDateArray = date1.trim().split("-");
		String[] lastDateArray = date2.trim().split("-");

		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Integer.parseInt(firstDateArray[2]), (Integer.parseInt(firstDateArray[0]) - 1), Integer.parseInt(firstDateArray[1]));

		GregorianCalendar lastDay = new GregorianCalendar();
		lastDay.set(Integer.parseInt(lastDateArray[2]), (Integer.parseInt(lastDateArray[0]) - 1), Integer.parseInt(lastDateArray[1]));

		while (!cal.after(lastDay)) {
			String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
			String month = Integer.toString((cal.get(Calendar.MONTH)) + 1);
			String year;
			if ((cal.get(Calendar.YEAR)) < 2010) {
				year = "0" + Integer.toString((cal.get(Calendar.YEAR)) - 2000);
			} else {
				year = Integer.toString((cal.get(Calendar.YEAR)) - 2000);
			}
			fileName = fileName + month + "-" + day + "-" + year + ".txt";
			boolean resultTemp = editFile(record, (folderAddress + fileName),editOrDelete, newRecord);

			if (resultTemp == true) {
				cal = null;
				lastDay = null;
				firstDateArray = null;
				lastDateArray = null;
				return true;
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
		cal = null;
		lastDay = null;
		firstDateArray = null;
		lastDateArray = null;
		return false;
	}

	public boolean removeLineFromFile(String file, String lineToRemove) {

		try {
			File inFile = new File(file);
			if (!inFile.isFile()) {
				// System.out.println("Parameter is not an existing file");
				return false;
			}
			// Construct the new file that will later be renamed to the original
			// filename.
			File tempFile = new File(inFile.getAbsolutePath() + ".tmp");
			BufferedReader br = new BufferedReader(new FileReader(file));
			PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
			String line = null;

			// Read from the original file and write to the new
			// unless content matches data to be removed.
			while ((line = br.readLine()) != null) {
				if (!line.trim().equals(lineToRemove)) {
					pw.println(line);
					pw.flush();
				}
			}
			pw.close();
			br.close();

			// Delete the original file
			if (!inFile.delete()) {
				// System.out.println("Could not delete file");
				return false;
			}

			// Rename the new file to the filename the original file had.
			if (!tempFile.renameTo(inFile)) {
				// System.out.println("Could not rename file");
				return false; // Soheil
			}
			return true;

		} catch (FileNotFoundException ex) {
			return false;
			// ex.printStackTrace();
		} catch (IOException ex) {
			return false;
			// ex.printStackTrace();
		}
		// return true;
	}

	public boolean editLineFromFile(String file, String lineToEdit,
			String newLine) {

		try {

			File inFile = new File(file);

			if (!inFile.isFile()) {
				// System.out.println("Parameter is not an existing file");
				return false;
			}

			// Construct the new file that will later be renamed to the original
			// filename.
			File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

			BufferedReader br = new BufferedReader(new FileReader(file));
			PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

			String line = null;

			// Read from the original file and write to the new
			// unless content matches data to be removed.
			while ((line = br.readLine()) != null) {

				if (!line.trim().equals(lineToEdit)) {

					pw.println(line);
					pw.flush();
				} else {
					pw.println(newLine);
					pw.flush();
				}
			}
			pw.close();
			br.close();

			// Delete the original file
			if (!inFile.delete()) {
				// System.out.println("Could not delete file");
				return false;
			}

			// Rename the new file to the filename the original file had.
			if (!tempFile.renameTo(inFile)) {
				// System.out.println("Could not rename file");
				return false; // Soheil
			}
			return true;

		} catch (FileNotFoundException ex) {
			return false;
			// ex.printStackTrace();
		} catch (IOException ex) {
			return false;
			// ex.printStackTrace();
		}
		// return true;
	}
}
