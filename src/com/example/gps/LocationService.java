package com.example.gps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class LocationService extends Service {

	private WakeLock wakeLock;
	private LocationManager manager;
	private LocationBroadcastListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.e("error", "Inside Service onCreate");
		super.onCreate();
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");

		manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		listener = new LocationBroadcastListener(this);
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600000, 0,
				listener);
		Log.e("error", "Listener Added");
		Log.e("error", "Service Created");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.e("error", "Inside Destroy Service");
		super.onDestroy();
		wakeLock.release();
		if (manager != null && listener != null) {
			manager.removeUpdates(listener);
		}
		Log.e("error", "Service Destoryed");
	}
}