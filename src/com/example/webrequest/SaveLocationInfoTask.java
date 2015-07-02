package com.example.webrequest;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.example.sendmessage.AppUtil;
import com.example.sendmessage.DBHelper;

public class SaveLocationInfoTask extends AsyncTask<Double, Void, Void> {

	private Context locationContext;

	public SaveLocationInfoTask(Context context) {
		locationContext = context;
	}

	@Override
	protected Void doInBackground(Double... arg0) {
		Calendar c = Calendar.getInstance();
		System.out.println("Current time => " + c.getTime());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedDate = df.format(c.getTime());
		DBHelper db = new DBHelper(locationContext);
		db.insertLocation(formattedDate, arg0[0], arg0[1]);
		try {
			sendDataToServer();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void sendDataToServer() throws JSONException {
		// Get All Location Info Which are not Sync
		DBHelper db = new DBHelper(locationContext);
		Cursor locations = db.getAllLocation();
		JSONArray arr = new JSONArray();
		while (locations.moveToNext()) {
			JSONObject ent = new JSONObject();
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			try {
				date = format.parse(locations.getString(0));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			ent.put("L_TIME", date);
			ent.put("LATITUDE", locations.getDouble(1));
			ent.put("LONGITUDE", locations.getDouble(2));
			arr.put(ent);
		}
		locations.close();
		if (arr.length() > 0) {
			send(arr.toString());
		}

	}

	private void send(String data) {
		try {
			String salesid = AppUtil.getSalesId(locationContext);
			if (salesid != null || salesid != "") {
				String response = AppUtil.sendServerPostRequest(
						"http://ramtrade.byethost10.com/insert_location.php?sales_id="
								+ salesid, data);
				DBHelper db = new DBHelper(locationContext);
				if (response != null) {
					db.deleteSyncedLocations();
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
