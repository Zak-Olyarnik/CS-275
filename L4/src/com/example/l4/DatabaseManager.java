package com.example.l4;


import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


// Database stores the time of the last call and all parsed weather data
// src: Android Programming Unleashed textbook
public class DatabaseManager {
	public static final String DB_NAME = "weather";
	public static final String DB_TABLE = "forecast";
	public static final int DB_VERSION = 2;
	public static final String CREATE_TABLE = "CREATE TABLE " + DB_TABLE + " (time LONG PRIMARY KEY, date TEXT, cond TEXT, temp TEXT, hum TEXT, url TEXT);";
	private SQLHelper helper;
	private SQLiteDatabase db;
	private Context context;
	
	public DatabaseManager(Context c) {
		this.context = c;
		helper = new SQLHelper(c);
		this.db = helper.getWritableDatabase();
	}
	
	public DatabaseManager openReadable() throws android.database.SQLException {
		helper = new SQLHelper(context);
		db = helper.getReadableDatabase();
		return this;
	}
	
	// checks for existence
	// src: http://stackoverflow.com/questions/3386667/query-if-android-database-exists
	public boolean checkDatabase(DatabaseManager myDB) {
	    try {
	        myDB.openReadable();
	        myDB.close();
	        return true;
	    } catch (android.database.SQLException e) {
	        // database doesn't exist yet.
	    	return false;
	    }
	}
	
	public void close() {
		helper.close();
	}
	
	// appends
	public void addRow(long time, String date, String cond, String temp, String hum, String url) {
		ContentValues newForecast = new ContentValues();
		newForecast.put("time", time);
		newForecast.put("date", date);
		newForecast.put("cond", cond);
		newForecast.put("temp", temp);
		newForecast.put("hum", hum);
		newForecast.put("url", url);		
		try{
			db.insertOrThrow(DB_TABLE, null, newForecast);
		}catch(Exception e){
			Log.e("Error in inserting rows ", e.toString());
			e.printStackTrace();
		}
	}
	
	// custom reader stores database info in 2D array for easier population into listView
	public String[][] retrieveData() {
			//String[] columns = new String[]{"time", "date", "cond", "temp", "hum", "url"};
			String[] columns = new String[]{"time", "date", "cond", "temp", "hum", "url"};
			String[][] array = new String[30][6];
			Cursor cursor = db.query(DB_TABLE, columns, null, null, null, null, null);
			cursor.moveToFirst();
			
			int i=0;
			while(cursor.isAfterLast() == false) {
				array[i][0] = String.valueOf(cursor.getLong(0));
				array[i][1] = cursor.getString(1);
				array[i][2] = cursor.getString(2);
				array[i][3] = cursor.getString(3);
				array[i][4] = cursor.getString(4);
				array[i][5] = cursor.getString(5);
				i=i+1;
				cursor.moveToNext();
			}
			if(cursor != null && !cursor.isClosed()){
				cursor.close();
			}
			return array;
	}
	
	// clear
	public void removeAll()
	{
		helper = new SQLHelper(context);
		db = helper.getWritableDatabase();
	    db.delete(DB_TABLE, null, null);
	}
	
	public class SQLHelper extends SQLiteOpenHelper {
		public SQLHelper(Context c){
			super(c, DB_NAME, null, DB_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db){
			db.execSQL(CREATE_TABLE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("Forecast table", "Upgrading database i.e. dropping table and recreating it");
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
			onCreate(db);
		}
	}
}