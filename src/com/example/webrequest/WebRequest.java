package com.example.webrequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.sendmessage.DBHelper;
import com.example.sendmessage.MainActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class WebRequest extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... arg0) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = null;
		String responseString = null;
		try {
			SharedPreferences pref = MainActivity.staticContext
					.getSharedPreferences("sales", Context.MODE_PRIVATE);

			String sales_id = pref.getString("SalesId", "");
			response = httpclient.execute(new HttpGet(
					"http://ramtrade.byethost10.com/updatesyncsts.php?request=SYNCALL&salesid="
							+ sales_id));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (response == null)
			return null;
		StatusLine statusLine = response.getStatusLine();
		if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				response.getEntity().writeTo(out);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			responseString = out.toString();
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				JSONArray partyArray = new JSONArray(responseString);
				DBHelper dbHelper = new DBHelper(MainActivity.staticContext);
				dbHelper.deleteAllParties("PARTY");
				dbHelper.initBulkInsert();
				for (int i = 0; i < partyArray.length(); i++) {
					JSONObject partyObject = partyArray.getJSONObject(i);
					dbHelper.insertParty(partyObject.getInt("id"),
							partyObject.getString("name"),
							partyObject.getLong("contact_no"));
				}
				dbHelper.finishBulkInsert();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Log.e("error", responseString);
		}
		return responseString;
	}
}
