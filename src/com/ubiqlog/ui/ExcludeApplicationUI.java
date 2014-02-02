package com.ubiqlog.ui;

import java.util.Vector;

import com.ubiqlog.common.Setting;
import com.ubiqlog.ui.R;


import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class ExcludeApplicationUI extends FrameLayout 
{
	public interface OnClosedListener {

		public abstract void onClosed();
	}
	
	private EditText edtAppName;
	private ListView listViewApps;
	private ArrayAdapter<CheckedTextViewArrayAdapter> listViewAdapter;
	private Context ctx = null;
	private OnClosedListener _closed = null;
	
	public ExcludeApplicationUI(Context context,OnClosedListener closed) {
		super(context);
		ctx = context;
		_closed = closed;
		
		final int ID_TXT_APP_NAME = 1;
		final int ID_TXT_EXCLUDED_APPS = 2;
		final int ID_EDT_APP_NAME = 3;
		final int ID_BTN_ADD = 4;
		final int ID_TABLE_LAYOUT_REMOVE = 5;

		TextView txtAppName = new TextView(ctx);
		txtAppName.setId(ID_TXT_APP_NAME);
		txtAppName.setText("Application to exclude - Please enter full name:");
		txtAppName.setTextColor(Color.BLACK);
		
		TextView txtExcludedApps = new TextView(ctx);
		txtExcludedApps.setId(ID_TXT_EXCLUDED_APPS);
		txtExcludedApps.setText("List of currently excluded applications:");
		txtExcludedApps.setTextColor(Color.BLACK);
		
		edtAppName = new EditText(ctx);
		edtAppName.setId(ID_EDT_APP_NAME);
		edtAppName.setText("");
		edtAppName.clearFocus();
		edtAppName.setSingleLine(true);
		
		Button btnAdd = new Button(ctx);
		btnAdd.setId(ID_BTN_ADD);
		btnAdd.setText("Add");
		btnAdd.setOnClickListener(btnAddListener);
		
		Button btnRemove = new Button(ctx);
		btnRemove.setText("Remove");
		btnRemove.setOnClickListener(btnRemoveListener);
		
		Button btnRemoveAll = new Button(ctx);
		btnRemoveAll.setText("Remove All");
		btnRemoveAll.setOnClickListener(btnRemoveAllListener);
		
		Button btnSave = new Button(ctx);
		btnSave.setText("Save");
		btnSave.setOnClickListener(btnSaveListener);
		
		Button btnCancel = new Button(ctx);
		btnCancel.setText("Cancel");
		btnCancel.setOnClickListener(btnCancelListener);
		
		listViewApps = new ListView(ctx);
		listViewAdapter = new ArrayAdapter<CheckedTextViewArrayAdapter>(ctx, R.layout.exclude_application_list);
		listViewApps.setAdapter(listViewAdapter);
		listViewApps.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listViewApps.setCacheColorHint(Color.TRANSPARENT);
		listViewApps.setTextFilterEnabled(false);
		
		InitListView();
		
		TableLayout tableLayoutRemove = new TableLayout(ctx);
		tableLayoutRemove.setId(ID_TABLE_LAYOUT_REMOVE);
		tableLayoutRemove.setBaselineAligned(true);
		tableLayoutRemove.setStretchAllColumns(true);
		TableRow.LayoutParams rowLayoutSingle = new TableRow.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT);
		TableRow tableRow = new TableRow(ctx);
		tableRow.addView(btnRemove, rowLayoutSingle);
		tableRow.addView(btnRemoveAll, rowLayoutSingle);
		tableLayoutRemove.addView(tableRow, rowLayoutSingle);
		
		tableRow = new TableRow(ctx);
		tableRow.addView(btnSave, rowLayoutSingle);
		tableRow.addView(btnCancel, rowLayoutSingle);
		tableLayoutRemove.addView(tableRow, rowLayoutSingle);
		
		RelativeLayout relativeLayout = new RelativeLayout(ctx);
		
		RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		relativeLayout.addView(txtAppName, relativeLayoutParams);
		
		relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		relativeLayoutParams.addRule(RelativeLayout.BELOW, txtAppName.getId());
		relativeLayout.addView(edtAppName, relativeLayoutParams);
		
		relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		relativeLayoutParams.addRule(RelativeLayout.BELOW, edtAppName.getId());
		relativeLayout.addView(btnAdd, relativeLayoutParams);
		
		relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		relativeLayoutParams.addRule(RelativeLayout.BELOW, btnAdd.getId());
		relativeLayout.addView(txtExcludedApps, relativeLayoutParams);

		relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		relativeLayout.addView(tableLayoutRemove, relativeLayoutParams);
		
		relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		relativeLayoutParams.addRule(RelativeLayout.BELOW, txtExcludedApps.getId());
		relativeLayoutParams.addRule(RelativeLayout.ABOVE, tableLayoutRemove.getId());
		relativeLayout.addView(listViewApps, relativeLayoutParams);

		addView(relativeLayout);
		
	}


	private void InitListView()
	{
		for (int index = 0; index < Setting.Instance(ctx).getCoreProcs().length; ++index)
		{
			CheckedTextViewArrayAdapter checkedTextViewArrayAdapter = new CheckedTextViewArrayAdapter(ctx);
			checkedTextViewArrayAdapter.setText(Setting.Instance(ctx).getCoreProcs()[index]);
			listViewAdapter.add(checkedTextViewArrayAdapter);
		}
	}

	private View.OnClickListener btnAddListener = new View.OnClickListener() 
	{
		public void onClick(View v) 
		{
			CheckedTextViewArrayAdapter newText = new CheckedTextViewArrayAdapter(v.getContext());
			newText.setText(edtAppName.getText().toString().toLowerCase());
			listViewAdapter.add(newText);
			
			edtAppName.setText("");
			edtAppName.clearFocus();
			
			listViewApps.clearChoices();
		}
	};
	
	private View.OnClickListener btnRemoveListener = new View.OnClickListener() 
	{
		public void onClick(View v) 
		{
			SparseBooleanArray sbaPositions = listViewApps.getCheckedItemPositions();
			Vector<CheckedTextViewArrayAdapter> checkedItems = new Vector<CheckedTextViewArrayAdapter>();
			
			for (int index = 0; index < sbaPositions.size(); ++index)
			{
				if (sbaPositions.valueAt(index))
				{
					checkedItems.add(listViewAdapter.getItem(sbaPositions.keyAt(index)));
				}
			}
			
			for (int index = 0; index < checkedItems.size(); ++index)
			{
				listViewAdapter.remove(checkedItems.elementAt(index));
			}
			
			listViewApps.clearChoices();
		}
	};
	
	private View.OnClickListener btnRemoveAllListener = new View.OnClickListener() 
	{
		public void onClick(View v) 
		{
			listViewAdapter.clear();
			listViewApps.clearChoices();
		}
	};
	
	private View.OnClickListener btnSaveListener = new View.OnClickListener() 
	{
		public void onClick(View v) 
		{
			String[] coreProcs = new String[listViewAdapter.getCount()];
		
			for (int index = 0; index < listViewAdapter.getCount(); ++index)
			{
				String strItem = listViewAdapter.getItem(index).toString();
				coreProcs[index] = strItem;
			}
			Setting.Instance(ctx).saveCoreProcs(coreProcs);			
			
			finish();
		}
	};
	
	private View.OnClickListener btnCancelListener = new View.OnClickListener() 
	{
		public void onClick(View v) 
		{
			finish();
		}
	};
	
	private void finish() {
		_closed.onClosed();
	}

}

class CheckedTextViewArrayAdapter extends CheckedTextView
{
	public CheckedTextViewArrayAdapter(Context context)
	{
		super(context);
	}
	
	@Override
	public String toString()
	{
		return this.getText().toString();
	}
}