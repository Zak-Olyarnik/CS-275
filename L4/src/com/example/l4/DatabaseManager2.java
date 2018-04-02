package com.example.l4;


import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



public class DatabaseManager2 {
	public static final String DB_NAME = "weather";
	public static final String DB_TABLE = "forecast";
	public static final int DB_VERSION = 1;
	public static final String CREATE_TABLE = "CREATE TABLE " + DB_TABLE + " (time LONG PRIMARY KEY, date TEXT, cond TEXT, temp TEXT, hum TEXT, url TEXT);";
	private SQLHelper helper;
	private SQLiteDatabase db;
	private Context context;
	
	public DatabaseManager2(Context c) {
		this.context = c;
		helper = new SQLHelper(c);
		this.db = helper.getWritableDatabase();
	}
	
	public DatabaseManager2 openReadable() throws android.database.SQLException {
		helper = new SQLHelper(context);
		db = helper.getReadableDatabase();
		return this;
	}
	
	public void close() {
		helper.close();
	}
	
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
	
	public String retrieveRows() {
		String[] columns = new String[]{"time", "date", "cond", "temp", "hum", "url"};
		Cursor cursor = db.query(DB_TABLE, columns, null, null, null, null, null);
		String tablerows = "";
		cursor.moveToFirst();
		while(cursor.isAfterLast() == false) {
			tablerows = tablerows + cursor.getLong(0) + ", " + cursor.getString(1) + "\n";
			cursor.moveToNext();
		}
		if(cursor != null && !cursor.isClosed()){
			cursor.close();
		}
		return tablerows;
	}
	
	public String[][] retrieveData() {
			//String[] columns = new String[]{"time", "date", "cond", "temp", "hum", "url"};
			String[] columns = new String[]{"time", "date", "cond", "temp", "hum", "url"};
			String[][] array = new String[30][5];
			Cursor cursor = db.query(DB_TABLE, columns, null, null, null, null, null);
			cursor.moveToFirst();
			
			for(int i = 0; i < 30; i++){
				array[i][0] = String.valueOf(cursor.getLong(0));
				for(int j = 1; j < 7; j++){
					array[i][j] = cursor.getString(j);
				}
				cursor.moveToNext();
			}
			
			//while(cursor.isAfterLast() == false) {
				//tablerows = tablerows + cursor.getLong(0) + ", " + cursor.getString(1) + "\n";
				//cursor.moveToNext();
			//}
			//if(cursor != null && !cursor.isClosed()){
			//	cursor.close();
			//}
			return array;
		
		//String[] array = new String[2];
		//Cursor cursor = db.query(DB_TABLE, columns, null, null, null, null, null);
		//String tablerows = "";
		//cursor.moveToFirst();
		//array[0] = String.valueOf(cursor.getLong(0));
		//array[1] = cursor.getString(1);

		//return array;
	}
	
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