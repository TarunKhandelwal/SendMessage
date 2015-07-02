package com.example.gps;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.sendmessage.MainActivity;
import com.example.webrequest.SaveLocationInfoTask;

public class LocationBroadcastListener implements LocationListener {

	Context listenerContext;

	public LocationBroadcastListener(Context context) {
		listenerContext = context;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		Log.e("error", "Location Changed");
		/*
		 * Toast.makeText(listenerContext, arg0.getLatitude() + "\n" +
		 * arg0.getLongitude(), Toast.LENGTH_SHORT).show();
		 */SaveLocationInfoTask locationTask = new SaveLocationInfoTask(
				listenerContext);
		Double[] params = new Double[2];
		params[0] = arg0.getLatitude();
		params[1] = arg0.getLongitude();
		locationTask.execute(params);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
