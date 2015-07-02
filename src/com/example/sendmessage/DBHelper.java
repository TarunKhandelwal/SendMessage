package com.example.sendmessage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "ramtrade.db";
	private SQLiteStatement statement;
	private SQLiteDatabase db;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("debug", "Inside On Create Method DBHelper");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("debug", "Inside On Update Method DBHelper");
	}

	public Cursor getPartyList() {
		db = this.getReadableDatabase();
		Cursor res = db.rawQuery(
				"select rowid _id,ID,NAME,CONTACT_NO from PARTY", null);
		//db.close();
		return res;
	}

	public Cursor getAllSuggestedValues(String partialValue) {
		db = this.getReadableDatabase();
		Cursor res = db.rawQuery(
				"select rowid _id,ID,NAME,CONTACT_NO from PARTY where NAME LIKE '"
						+ partialValue + "%'", null);
		//db.close();
		return res;
	}

	public void deleteAllParties(String table_name) {
		db = this.getWritableDatabase();
		db.execSQL("delete from " + table_name);
		db.close();
	}

	public void initBulkInsert() {
		db = this.getWritableDatabase();
		String sql = "INSERT INTO PARTY(ID,NAME,CONTACT_NO) VALUES (?,?,?);";
		statement = db.compileStatement(sql);
		db.beginTransaction();
	}

	public void insertParty(Integer ID, String name, Long contact_no) {
		statement.clearBindings();
		statement.bindLong(1, ID);
		statement.bindString(2, name);
		statement.bindLong(3, contact_no);
		statement.execute();
	}

	public void finishBulkInsert() {

		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public void AddTransaction(String date, Float amount, int party_id,
			int sales_person_id, String reciept_No, String bill_no) {
		db = this.getWritableDatabase();
		String sql = "INSERT INTO PARTY_TRANSACTION(T_DATE,T_AMOUNT,SALES_PERSON_ID,PARTY_ID,RECIEPT_NO,BILL_NO) values(\""
				+ date
				+ "\","
				+ amount
				+ ","
				+ sales_person_id
				+ ","
				+ party_id + ", \'" + reciept_No + "\', \'" + bill_no + "\')";
		db.execSQL(sql);
		db.close();
	}

	public Cursor getAllTransactions() {
		db = this.getReadableDatabase();
		Cursor res = db
				.rawQuery(
						"select ID,T_DATE,T_AMOUNT,SALES_PERSON_ID,PARTY_ID,RECIEPT_NO,BILL_NO from PARTY_TRANSACTION where DB_SYNC=0",
						null);
		//db.close();
		return res;
	}

	public void DeleteTransactionSync() {
		db = this.getReadableDatabase();
		Cursor res = db.rawQuery(
				"select id from PARTY_TRANSACTION where DB_SYNC=1", null);
		int noOfSyncedRecords = res.getCount();
		int recordsToDelete = noOfSyncedRecords - 100;
		if (recordsToDelete > 0) {
			db = this.getWritableDatabase();
			db.execSQL("DELETE from PARTY_TRANSACTION WHERE DB_SYNC=1 and ID IN (select id from PARTY_TRANSACTION where DB_SYNC=1 ORDER BY T_DATE ASC LIMIT "
					+ recordsToDelete + ")");
		}
		db.close();
	}

	public void UpdateTransactionSync(String queryString) {
		db = this.getWritableDatabase();

		String query = "UPDATE PARTY_TRANSACTION SET DB_SYNC=1 where DB_SYNC=0 and ID IN "
				+ queryString;
		Log.e("error", query);
		db.execSQL(query);
		db.close();
	}

	public void insertLocation(String currentTime, Double column2,
			Double column3) {
		db = this.getWritableDatabase();
		ContentValues value = new ContentValues();
		value.put("L_TIME", currentTime);
		value.put("LATITUDE", column2);
		value.put("LONGITUDE", column3);
		db.insert("LOCATION", null, value);
		db.close();
	}

	public void deleteSyncedLocations() {
		db = this.getWritableDatabase();
		String query = "DELETE FROM LOCATION";
		Log.e("error", query);
		db.execSQL(query);
		db.close();
	}

	public Cursor getAllLocation() {
		db = this.getReadableDatabase();
		Cursor res = db
				.rawQuery(
						"select L_TIME,LATITUDE,LONGITUDE from LOCATION where DB_SYNC=0",
						null);
		//db.close();
		return res;
	}

}