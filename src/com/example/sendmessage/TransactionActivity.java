package com.example.sendmessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.webrequest.DBSyncTask;

public class TransactionActivity extends Activity {

	EditText phoneNo;
	EditText amount;
	EditText billNo;
	Button submit;
	Button cancel;
	String partyName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transaction);
		phoneNo = (EditText) findViewById(R.id.phone_no);
		amount = (EditText) findViewById(R.id.amount_paid);
		billNo = (EditText) findViewById(R.id.bill_no);
		submit = (Button) findViewById(R.id.btn_submit);
		cancel = (Button) findViewById(R.id.btn_cancel);
		final DBHelper db = new DBHelper(this);
		final int p_id;
		final int s_id;
		Bundle bundle = getIntent().getBundleExtra("Info");

		phoneNo.setText(bundle.getString("PhoneNo"));

		partyName = bundle.getString("Name");
		final String partyId = bundle.getString("ID");

		SharedPreferences pref = getSharedPreferences("sales",
				Context.MODE_PRIVATE);

		final String sales_id = pref.getString("SalesId", "");

		p_id = Integer.parseInt(partyId);
		s_id = Integer.parseInt(sales_id);

		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView errorTextAmmount = (TextView) findViewById(R.id.error_string_amnt);
				errorTextAmmount.setVisibility(0);
				TextView errorTextBill = (TextView) findViewById(R.id.error_string_bill);
				errorTextBill.setVisibility(0);
				if (amount.getText() == null
						|| amount.getText().toString().equals("")) {
					errorTextAmmount.setText("Please Enter Amount");
					errorTextAmmount.setVisibility(1);
					return;
				} else if (billNo.getText() == null
						|| billNo.getText().toString().equals("")) {
					errorTextBill.setText("Please Enter Bill No");
					errorTextBill.setVisibility(1);
					return;
				}
				float amt = Float.parseFloat(amount.getText().toString());
				Calendar c = Calendar.getInstance();
				System.out.println("Current time => " + c.getTime());
				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String formattedDate = df.format(c.getTime());
				SimpleDateFormat uniqueReciept = new SimpleDateFormat(
						"yyMMddHHmm");
				String longUnique = uniqueReciept.format(c.getTime());
				Long recieptUniqueValue = Long.valueOf(longUnique);
				String reciept_No = String.valueOf(recieptUniqueValue * 2);
				String bill_No = billNo.getText().toString();
				String message = "Party : " + partyName + " paid amount of Rs "
						+ amount.getText() + " for " + bill_No + ".\n"
						+ "Your Reciept No is : " + reciept_No;
				Log.e("error", message);
				sendSMSMessage(phoneNo.getText().toString(), message);
				db.AddTransaction(formattedDate, amt, p_id, s_id, reciept_No,
						bill_No);
				DBSyncTask dbSync = new DBSyncTask(MainActivity.staticContext);
				dbSync.execute();
				finish();
			}
		});

		cancel.setOnClickListener(new OnClickListener() {
			;
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void sendSMSMessage(String phoneNo, String message) {
		Log.d("Send SMS", "Sending Message");
		try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(phoneNo, null, message, null, null);
			Toast.makeText(getApplicationContext(), "SMS sent.",
					Toast.LENGTH_LONG).show();

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(),
					"SMS faild, please try again.", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

}
