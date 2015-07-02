package com.example.sendmessage;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.widget.TextView;

import com.example.gps.LocationService;
import com.example.webrequest.SalesCheckTask;
import com.example.webrequest.WebRequest;

public class MainActivity extends Activity {
	private AutoCompleteTextView actv;
	private final Context context = this;
	public static Context staticContext;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivity.staticContext = context;
		setContentView(R.layout.activity_main);
		final DBHelper db = new DBHelper(this);
		db.getWritableDatabase();
		SharedPreferences wmbPreference = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
		if (isFirstRun) {
			DataBaseLoader dbLoader = new DataBaseLoader(this);
			try {
				dbLoader.loadData();
				SharedPreferences.Editor editor = wmbPreference.edit();
				editor.putBoolean("FIRSTRUN", false);
				editor.commit();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		final Intent requestIntent = new Intent(this, TransactionActivity.class);
		Cursor partyList = db.getPartyList();
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, partyList,
				new String[] { "NAME" }, new int[] { android.R.id.text1 }, 0);
		adapter.setCursorToStringConverter(new CursorToStringConverter() {
			public String convertToString(android.database.Cursor cursor) {
				// Get the label for this row out of the "state" column
				final int columnIndex = cursor.getColumnIndexOrThrow("NAME");
				final String str = cursor.getString(columnIndex);
				return str;
			}
		});
		adapter.setFilterQueryProvider(new FilterQueryProvider() {

			@Override
			public Cursor runQuery(CharSequence constraint) {
				String partialValue = constraint.toString();
				return db.getAllSuggestedValues(partialValue);

			}
		});
		actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
		actv.setText("");
		actv.setAdapter(adapter);

		actv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.e("error", "" + arg2 + arg3);
				Cursor selection = (Cursor) arg0.getItemAtPosition(arg2);
				/*
				 * String text = "You Selected Party : " +
				 * selection.getString(selection.getColumnIndex("NAME")) +
				 * " with Contact No : " + selection.getString(selection
				 * .getColumnIndex("CONTACT_NO"));
				 * Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT)
				 * .show();
				 */
				Bundle bundle = new Bundle();
				bundle.putString("PhoneNo", selection.getString(selection
						.getColumnIndex("CONTACT_NO")));
				bundle.putString("Name",
						selection.getString(selection.getColumnIndex("NAME")));

				bundle.putString("ID",
						selection.getString(selection.getColumnIndex("ID")));
				requestIntent.putExtra("Info", bundle);
				startActivity(requestIntent);
			}

		});
		Intent intent = new Intent(this, LocationService.class);
		startService(intent);
	}

	protected void onResume() {
		super.onResume();
		if (actv != null) {
			actv.setText("");
		}
		SharedPreferences pref = getSharedPreferences("sales",
				Context.MODE_PRIVATE);
		String sales_id = pref.getString("SalesId", "");
		if (sales_id == null || sales_id == "") {
			getSalesIDInputDialog().show();
		} else {
			SalesCheckTask salesValidationTask = new SalesCheckTask(sales_id,
					null, null);
			salesValidationTask.execute();
		}
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		for (int i = 0; i < menu.size(); i++) {
			MenuItem mi = menu.getItem(i);
			String title = mi.getTitle().toString();
			Spannable newTitle = new SpannableString(title);
			newTitle.setSpan(
					new ForegroundColorSpan(getResources().getColor(
							R.color.textColor)), 0, newTitle.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			mi.setTitle(newTitle);
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu Synchronize) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, Synchronize);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.Synchronize:
			try {
				syncAll();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public Dialog getSalesIDInputDialog() {

		final Dialog inputDialog = new Dialog(context);
		inputDialog.setContentView(R.layout.dialog_salesid_input);
		inputDialog.setTitle("Sales Id");
		inputDialog.setCancelable(false);
		inputDialog.setCanceledOnTouchOutside(false);
		inputDialog.getWindow().setTitleColor(
				getResources().getColor(R.color.textColor));
		inputDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(R.color.bgColor)));
		final EditText salesIdTxt = (EditText) inputDialog
				.findViewById(R.id.sales_id);
		final TextView errorTxt = (TextView) inputDialog
				.findViewById(R.id.txt_error);

		Button salesIdBtn = (Button) inputDialog.findViewById(R.id.dlg_submit);
		salesIdBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (salesIdTxt.getText() == null
						|| salesIdTxt.getText().toString().equals("")) {
					errorTxt.setText("Id cannot be left blank");
					errorTxt.setVisibility(1);
				} else {
					SalesCheckTask salesValidationTask = new SalesCheckTask(
							salesIdTxt.getText().toString(), inputDialog,
							errorTxt);
					salesValidationTask.execute();
				}
			}
		});
		return inputDialog;
	}

	public void syncAll() throws ClientProtocolException, IOException {
		WebRequest webTask = new WebRequest();
		webTask.execute("");
	}
}
