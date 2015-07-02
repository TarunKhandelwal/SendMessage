package com.example.webrequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.sendmessage.DBHelper;

public class DBSyncTask extends AsyncTask<Void, Void, Void> {

	Context transactionContext;

	public DBSyncTask(Context context) {
		transactionContext = context;
	}

	@Override
	protected Void doInBackground(Void... params) {

		try {
			// Get All Transactions which are not synced.
			DBHelper helper = new DBHelper(transactionContext);
			Cursor transactions = helper.getAllTransactions();
			JSONArray arr = new JSONArray();
			while (transactions.moveToNext()) {
				JSONObject ent = new JSONObject();
				ent.put("ID", transactions.getInt(0));
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date date = new Date();
				try {
					date = format.parse(transactions.getString(1));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ent.put("T_DATE", date);
				ent.put("T_AMOUNT", transactions.getDouble(2));
				ent.put("SALES_PERSON_ID", transactions.getInt(3));
				ent.put("PARTY_ID", transactions.getInt(4));
				ent.put("RECIEPT_NO", transactions.getString(5));
				ent.put("BILL_NO", transactions.getString(6));
				arr.put(ent);
			}
			transactions.close();

			// StringEntity entity = null;
			/*
			 * try { entity = new StringEntity(array.toString()); } catch
			 * (UnsupportedEncodingException e) { // TODO Auto-generated catch
			 * block e.printStackTrace(); }
			 */

			// Send Data to Web
			String idToUpdate = doSync(arr.toString());
			if (idToUpdate != null) {
				// Delete All Transactions Where Sync Status = 0 since they are
				// synced now;
				// convert string from php to sqlite readable array i.e from
				// [123,3242] to (12,12)
				JSONArray idArray = new JSONArray(idToUpdate);
				StringBuffer queryString = new StringBuffer("(");
				for (int i = 0; i < idArray.length(); i++) {
					int id = idArray.getInt(i);
					queryString.append(id);
					if (i < (idArray.length() - 1)) {
						queryString.append(",");
					}
				}
				queryString.append(")");
				Log.e("error", queryString.toString());
				helper.UpdateTransactionSync(queryString.toString());
				helper.DeleteTransactionSync();
			}
		} catch (Exception jSOnException) {
			jSOnException.printStackTrace();
		}
		return null;
	}

	public String doSync(String data) throws UnsupportedEncodingException {
		Log.e("error", data);
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 20000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		int timeoutSocket = 20000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		HttpPost httpPost = new HttpPost(
				"http://ramtrade.byethost10.com/insert_trans.php");
		httpPost.setEntity(new StringEntity(data));
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		try {
			HttpResponse response = httpclient.execute(httpPost);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				try {
					response.getEntity().writeTo(out);
					out.close();
					String responseString = out.toString();
					Log.e("error", responseString);
					return responseString;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
