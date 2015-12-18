package com.ubiqlog.ui.extras.search;

import java.util.ArrayList;

import com.ubiqlog.ui.R;
import com.ubiqlog.extras.search.Editor;
import com.ubiqlog.extras.search.Searcher;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/*
 * @author      Soheil KHOSRAVIPOUR
 * @date		07.2010
 * @version     0.9
 */
public class SearchResultListUI extends ListActivity {

	Searcher search = new Searcher();

	static final int DIALOG_EDIT_ID = 0;

	String itemTemp = "ERROR!";
	String date1;
	String date2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Receiving Data from the ubiqlogSearch Form
		Intent myIntent = getIntent();
		Bundle b = myIntent.getExtras();
		// Receiving searched sensors
		ArrayList<String> searchedSensors = b.getStringArrayList("sensors");
		// Receiving searched dates
		date1 = b.getString("date1");
		date2 = b.getString("date2");
		// Receiving searched keyword
		String searchedKeyword = b.getString("keyword");

		// Receiving the search results
		ArrayList<String> resultsToShow = search.searchFolder(searchedSensors,
				searchedKeyword, date1, date2);
resultsToShow.add("faf");
resultsToShow.add("faf");
resultsToShow.add("faf");
		setListAdapter(new ArrayAdapter<String>(this,
				R.layout.search_list_items, resultsToShow));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setCacheColorHint(Color.TRANSPARENT);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				itemTemp = ((TextView) view).getText().toString();
				// //If you want to show a toast with the text of the clicked
				// item
				// Toast.makeText(getApplicationContext(), itemTemp,
				// Toast.LENGTH_SHORT).show();
				showDialog(DIALOG_EDIT_ID);
			}
		});
		
		lv.setBackgroundColor(Color.parseColor("#E8E8E8"));
	}

	public void onDestroy(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onBackPressed() {
		SearchMainUI.MPICKSENSOR.setEnabled(true);
		SearchMainUI.MPICKDATE1.setEnabled(true);
		SearchMainUI.MPICKDATE2.setEnabled(true);
		SearchMainUI.MKEYWORDPICKER.setEnabled(true);
		SearchMainUI.SEARCHB.setEnabled(true);
		SearchMainUI.CANCELB.setEnabled(true);
		SearchMainUI.MDATEDISPLAY1.setEnabled(true);
		SearchMainUI.MDATEDISPLAY2.setEnabled(true);
		SearchMainUI.MSENSORDISPLAY.setEnabled(true);
		SearchMainUI.SENSORINFO.setEnabled(true);
		SearchMainUI.DATEDISPLAYINFO1.setEnabled(true);
		SearchMainUI.DATEDISPLAYINFO2.setEnabled(true);
		SearchMainUI.SEARCHLABLE.setEnabled(true);
		SearchMainUI.SEARCHING.setText("");
		SearchResultListUI.this.finish();
	}

	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DIALOG_EDIT_ID:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			Context mContext = getApplicationContext();
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.search_edit_dialog,
					(ViewGroup) findViewById(R.id.layout_root));
			final EditText text = (EditText) layout.findViewById(R.id.text);
			text.setText(itemTemp);

			builder.setMessage("Edit/Delete the record!").setCancelable(false)
					.setPositiveButton("Set",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									String newItem = text.getText().toString();
									if (itemTemp.startsWith("ERROR")
											|| itemTemp.startsWith("NOTE")) {
										Toast
												.makeText(
														getApplicationContext(),
														"You can not edit an error message.",
														Toast.LENGTH_SHORT)
												.show();
										Toast
												.makeText(
														getApplicationContext(),
														"Click the back button and try again.",
														Toast.LENGTH_SHORT)
												.show();
									} else {
										if (newItem.length() == 0) {
											Toast
													.makeText(
															getApplicationContext(),
															"The entered text is empty.",
															Toast.LENGTH_SHORT)
													.show();
											Toast
													.makeText(
															getApplicationContext(),
															"Your selected record will be DELETED.",
															Toast.LENGTH_SHORT)
													.show();

											Editor edit = new Editor();
											if (edit.editFolder(itemTemp,
													date1, date2, false, "")) {
												Toast
														.makeText(
																getApplicationContext(),
																"You will see the changes in the next search query.",
																Toast.LENGTH_SHORT)
														.show();
											} else {
												Toast
														.makeText(
																getApplicationContext(),
																"An error occurred!",
																Toast.LENGTH_SHORT)
														.show();
												Toast
														.makeText(
																getApplicationContext(),
																"Can not delete the record.",
																Toast.LENGTH_SHORT)
														.show();
											}
										} else {
											if (itemTemp.equals(newItem)) {
												Toast
														.makeText(
																getApplicationContext(),
																"You did not change the record.",
																Toast.LENGTH_SHORT)
														.show();
											} else {
												Editor edit = new Editor();
												if (edit.editFolder(itemTemp,
														date1, date2, true,
														newItem)) {
													Toast
															.makeText(
																	getApplicationContext(),
																	"Your changes is saved.",
																	Toast.LENGTH_SHORT)
															.show();
													Toast
															.makeText(
																	getApplicationContext(),
																	"You will see the changes in the next search query.",
																	Toast.LENGTH_SHORT)
															.show();
												} else {
													Toast
															.makeText(
																	getApplicationContext(),
																	"An error occurred!",
																	Toast.LENGTH_SHORT)
															.show();
													Toast
															.makeText(
																	getApplicationContext(),
																	"Can not save the changes.",
																	Toast.LENGTH_SHORT)
															.show();
												}
											}
										}
									}
									removeDialog(DIALOG_EDIT_ID);
								}
							}).setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									removeDialog(DIALOG_EDIT_ID);
									if (itemTemp.startsWith("ERROR")
											|| itemTemp.startsWith("NOTE")) {
										Toast
												.makeText(
														getApplicationContext(),
														"Click the back button and try again.",
														Toast.LENGTH_SHORT)
												.show();
									} else {
										Toast
												.makeText(
														getApplicationContext(),
														"The Record will not be changed.",
														Toast.LENGTH_SHORT)
												.show();
									}
								}
							}).setNeutralButton("Delete",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									if (itemTemp.startsWith("ERROR")
											|| itemTemp.startsWith("NOTE")) {
										Toast
												.makeText(
														getApplicationContext(),
														"You can not delete an error message.",
														Toast.LENGTH_SHORT)
												.show();
										Toast
												.makeText(
														getApplicationContext(),
														"Click the back button and try again.",
														Toast.LENGTH_SHORT)
												.show();
									} else {
										Editor edit = new Editor();
										if (edit.editFolder(itemTemp, date1,
												date2, false, "")) {
											Toast
													.makeText(
															getApplicationContext(),
															"Your selected record is deleted.",
															Toast.LENGTH_SHORT)
													.show();
											Toast
													.makeText(
															getApplicationContext(),
															"You will see the changes in the next search query.",
															Toast.LENGTH_SHORT)
													.show();
										} else {
											Toast.makeText(
													getApplicationContext(),
													"An error occurred!",
													Toast.LENGTH_SHORT).show();
											Toast
													.makeText(
															getApplicationContext(),
															"Can not delete the record.",
															Toast.LENGTH_SHORT)
													.show();
										}
									}
									removeDialog(DIALOG_EDIT_ID);
								}
							});

			builder.setView(layout);
			AlertDialog alert = builder.create();
			return alert;
		default:
			dialog = null;
		}
		return dialog;
	}

}