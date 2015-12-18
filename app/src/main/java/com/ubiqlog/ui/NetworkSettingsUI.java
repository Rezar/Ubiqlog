package com.ubiqlog.ui;

import java.io.File;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ubiqlog.common.Setting;
import com.ubiqlog.core.DataTransmitter;

public class NetworkSettingsUI extends FrameLayout {

	public interface OnClosedListener {

		public abstract void onClosed();
	}
	

	private EditText edtServAdd;
	private EditText edtUserName;
	private EditText edtPassword;
	private Context ctx = null;
	private OnClosedListener _closed = null;
	
	public NetworkSettingsUI(Context context, OnClosedListener closed) {
		super(context);
		ctx = context;
		_closed = closed;
		
		LinearLayout.LayoutParams lineLayParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		LinearLayout listLay = new TableLayout(ctx);
		listLay.setOrientation(LinearLayout.VERTICAL);

		TextView txtServerAdd = new TextView(ctx);
		txtServerAdd.setText("Server Address");
		txtServerAdd.setGravity(Gravity.LEFT);
		txtServerAdd.setTextColor(Color.BLACK);
		edtServAdd = new EditText(ctx);
		edtServAdd.setWidth(310);
		edtServAdd.setText(Setting.serverAddress);
		edtServAdd.setSingleLine(true);

		TextView txtUsername = new TextView(ctx);
		txtUsername.setText("Email");
		txtUsername.setGravity(Gravity.LEFT);
		txtUsername.setTextColor(Color.BLACK);
		edtUserName = new EditText(ctx);
		edtUserName.setWidth(310);
		edtUserName.setText("");
		edtUserName.setSingleLine(true);

		TextView txtPassword = new TextView(ctx);
		txtPassword.setText("Password");
		txtPassword.setGravity(Gravity.LEFT);
		txtPassword.setTextColor(Color.BLACK);
		edtPassword = new EditText(ctx);
		edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		edtPassword.setWidth(310);
		edtPassword.setText("");
		edtPassword.setSingleLine(true);

		Button btnUpload = new Button(ctx);
		btnUpload.setWidth(310);
		btnUpload.setText(" Manually Upload all Data to the Server ");
		btnUpload.setOnClickListener(btnUploadListener);

		listLay.addView(txtServerAdd, lineLayParams);
		listLay.addView(edtServAdd, lineLayParams);
		listLay.addView(txtUsername, lineLayParams);
		listLay.addView(edtUserName, lineLayParams);
		listLay.addView(txtPassword, lineLayParams);
		listLay.addView(edtPassword, lineLayParams);
		listLay.addView(btnUpload, lineLayParams);

		Button btnSave = new Button(ctx);
		btnSave.setText("Save");
		btnSave.setOnClickListener(btnSaveListener);
		btnSave.setWidth(160);

		Button btnCancel = new Button(ctx);
		btnCancel.setText("Cancel");
		btnCancel.setOnClickListener(btnCancelListener);
		btnCancel.setWidth(160);

		TableLayout recordLay = new TableLayout(ctx);
		recordLay.setBaselineAligned(true);
		recordLay.setStretchAllColumns(true);
		TableRow.LayoutParams rowLayoutSingle = new TableRow.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		TableRow trow1 = new TableRow(ctx);
		trow1.addView(btnSave, rowLayoutSingle);
		trow1.addView(btnCancel, rowLayoutSingle);
		recordLay.addView(trow1, rowLayoutSingle);

		listLay.addView(recordLay, lineLayParams);
		addView(listLay);
	}

	private View.OnClickListener btnSaveListener = new View.OnClickListener() {
		public void onClick(View v) {
			Setting.serverAddress = edtServAdd.getText().toString();
			Setting.username = edtUserName.getText().toString();
			Setting.password = edtPassword.getText().toString();
			finish();
		}
	};
	private View.OnClickListener btnCancelListener = new View.OnClickListener() {
		public void onClick(View v) {
			finish();
		}
	};
	private View.OnClickListener btnUploadListener = new View.OnClickListener() {
		public void onClick(View v) {
			uploadAll();
		}
	};

	public void uploadAll() {
		try {
			DataTransmitter datatrans = new DataTransmitter();
			datatrans.serverAdd = edtServAdd.getText().toString();
			if (getFileNames() != null && getFileNames().length > 0) {
				String names[] = getFileNames();
				File tmp;
				// Upload all file to the server

				for (int i = 0; i < getFileNames().length; i++) {
					tmp = new File(names[i]);
					tmp = new File(names[i]);
					datatrans.doUpload(Setting.LOG_FOLDER
							+ tmp.getAbsolutePath(), names[i]);
				}
			} else {
				Log.e("ServerSettingsUI", "--------No file exists at "
						+ Setting.LOG_FOLDER);
			}
		} catch (Exception e) {
			Log.e("ServerSettingsUI", "--------Error-------"
					+ e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

	public String[] getFileNames() {
		File folder = new File(Setting.LOG_FOLDER);
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles != null && listOfFiles.length > 0) {
			String[] fileNames = new String[listOfFiles.length];
			for (int i = 0; i < listOfFiles.length; i++) {
				fileNames[i] = listOfFiles[i].getName();
			}
			return fileNames;
		}
		return null;
	}
	
	private void finish() {
		_closed.onClosed();
	}
}
