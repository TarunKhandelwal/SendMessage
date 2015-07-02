package com.example.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.webrequest.DBSyncTask;

public class InternetCheckReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		try {
			// Toast.makeText(arg0, "Checking Network Availability",
			// Toast.LENGTH_LONG).show();
			if (isNetworkAvailable(arg0)) {
				// Toast.makeText(arg0, "Network Availabile, Syncing Database",
				// Toast.LENGTH_LONG).show();
				DBSyncTask dbSync = new DBSyncTask(arg0);
				dbSync.execute();
				/*
				 * SaveLocationInfoTask task = new SaveLocationInfoTask(arg0);
				 * Toast.makeText(arg0, "Task Created, Executing it",
				 * Toast.LENGTH_LONG).show(); try { task.sendDataToServer(); }
				 * catch (JSONException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); }
				 */
			}
		} catch (Exception e) {
			// Toast.makeText(arg0, "Exception Came", Toast.LENGTH_LONG).show();
			// Toast.makeText(arg0, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	private boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
