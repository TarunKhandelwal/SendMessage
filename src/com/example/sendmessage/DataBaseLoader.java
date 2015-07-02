package com.example.sendmessage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.util.Log;

public class DataBaseLoader {

	public static final String DATABASE_NAME="ramtrade.db";
	public Context currentContext;
	public String dbPath;
	public String assetPath;
	
	public DataBaseLoader(Context context){
		currentContext=context;
		dbPath = "/data/data/" + context.getPackageName() + "/databases/";
		Log.d("debug", dbPath+"Value");
	}
	
	public void loadData() throws IOException{
		if(newDBExist()){
			copyDataBase();
		}
	}
	
	private void copyDataBase() throws IOException{
		InputStream io;
		io = currentContext.getAssets().open("database/"+DATABASE_NAME);
		String outputFileName = dbPath+DATABASE_NAME;
		OutputStream out = new FileOutputStream(outputFileName);
		byte[] buffer = new byte[1024];
		int len;
		while((len=io.read(buffer))!=-1){
			out.write(buffer, 0, len);
		}
		Log.d("debug", "Data Written On File");
		out.flush();
		out.close();
		io.close();
	}
	
	private boolean newDBExist() throws IOException{
		String[] files = currentContext.getAssets().list("database");
		if(files.length>0){
			return true;
		}
		return false;
	}
}
