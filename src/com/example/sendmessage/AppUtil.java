package com.example.sendmessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AppUtil {

	public static String getSalesId(Context context) {
		SharedPreferences pref = context.getSharedPreferences("sales",
				Context.MODE_PRIVATE);
		if (pref != null) {
			return pref.getString("SalesId", "");
		}
		return null;
	}

	public static String sendServerPostRequest(String url, String data)
			throws ClientProtocolException, IOException {

		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 20000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		int timeoutSocket = 20000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new StringEntity(data));
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		HttpResponse response = httpclient.execute(httpPost);
		StatusLine statusLine = response.getStatusLine();
		if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			response.getEntity().writeTo(out);
			out.close();
			String responseString = out.toString();
			Log.e("error", responseString);
			return responseString;
		}
		return null;
	}

}
