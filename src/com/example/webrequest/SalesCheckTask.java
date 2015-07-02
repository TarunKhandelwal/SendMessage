package com.example.webrequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.TextView;

import com.example.sendmessage.MainActivity;

public class SalesCheckTask extends AsyncTask<Void, Void, Boolean> {

	private String randPass;
	private Dialog inputDialog;
	private TextView errorText;
	private SharedPreferences salesIdPreference;
	private String salesId;
	private boolean requestForSecurityCheck = false;

	public SalesCheckTask(String unique_pass, Dialog inputDialogCmp,
			TextView errorCmp) {
		super();
		randPass = unique_pass;
		inputDialog = inputDialogCmp;
		errorText = errorCmp;
		if (inputDialog == null || errorText == null) {
			requestForSecurityCheck = true;
		}
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		HttpClient httpClient = new DefaultHttpClient();
		String queryString;
		if (requestForSecurityCheck) {
			queryString = "sales_id=" + randPass;
		} else {
			queryString = "rand_pass=" + randPass;
		}
		HttpGet httpGet = new HttpGet(
				"http://ramtrade.byethost10.com/checkSalesPerson.php?"
						+ queryString);
		ByteArrayOutputStream out = null;
		try {
			HttpResponse response = httpClient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				out = new ByteArrayOutputStream();
				try {
					response.getEntity().writeTo(out);
					String responseString = out.toString();
					if (responseString.equalsIgnoreCase("false")) {
						return false;
					}
					salesId = responseString;
					return true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (requestForSecurityCheck) {
			return true;
		} else {
			return false;
		}
	}

	public void onPostExecute(Boolean result) {
		if (requestForSecurityCheck) {
			if (!result) {
				salesIdPreference = MainActivity.staticContext
						.getSharedPreferences("sales", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = salesIdPreference.edit();
				editor.remove("SalesId");
				editor.commit();
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(1);
			}
		} else {
			if (result) {
				salesIdPreference = MainActivity.staticContext
						.getSharedPreferences("sales", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = salesIdPreference.edit();
				editor.putString("SalesId", salesId);
				editor.commit();
				inputDialog.dismiss();
			} else {
				errorText.setText("Incorrect ID, Please Contact Administrator");
				errorText.setVisibility(1);
			}
		}
	}

}
