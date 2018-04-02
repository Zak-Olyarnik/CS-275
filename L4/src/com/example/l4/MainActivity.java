package com.example.l4;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import android.location.LocationManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;

// retrieves hourly forecast based on GPS coordinates
public class MainActivity extends Activity {

	// global variables to avoid scope problems later
	private LocationManager locationManager;
	double lt;	// latitude
	double lg;	// longitude
	private DatabaseManager myDB;
	JsonArray timearray;
	String[][] tableContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// checks for existence of database before starting.  If one is not found, weather is drawn from the website just like how it updates
		myDB = new DatabaseManager(this);
		if(myDB.checkDatabase(myDB)){
			myDB.openReadable();
			tableContent = myDB.retrieveData();
			myDB.close();
		
			long lastTime = Long.parseLong(tableContent[0][0]);
			long currentTime = System.currentTimeMillis();
			double difference = ((currentTime - lastTime) / 3600000);	// checks if data has been pulled in the last hour
			if (difference < 1){
				// toast
				Context context = getApplicationContext();
				CharSequence text = "Reading weather info from cache";
				int duration = Toast.LENGTH_SHORT;
	
				Toast mytoast = Toast.makeText(context, text, duration);
				mytoast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
				mytoast.show();
				
				populate();
			}
			else{
			
				// GPS location lookup - EXTRA CREDIT
				// src: Android Programming Unleashed textbook
				locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener(){
					public void onProviderDisabled(String provider){}
					public void onProviderEnabled(String provider){}
					public void onStatusChanged(String provider, int status, Bundle extras){}
					public void onLocationChanged(Location loc) {
						if (loc != null){
							lt = (double) (loc.getLatitude());
							lg = (double) (loc.getLongitude());
							WundergroundAsyncTask myTask = new WundergroundAsyncTask();	// new async task to get weather data
							myTask.execute();
						}
					}
				});	// requestLocationUpdates
			} // else
		}else{
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener(){
				public void onProviderDisabled(String provider){}
				public void onProviderEnabled(String provider){}
				public void onStatusChanged(String provider, int status, Bundle extras){}
				public void onLocationChanged(Location loc) {
					if (loc != null){
						lt = (double) (loc.getLatitude());
						lg = (double) (loc.getLongitude());
						WundergroundAsyncTask myTask = new WundergroundAsyncTask();	// new async task to get weather data
						myTask.execute();
					}
				}
			});	// requestLocationUpdates
		} // else
	}	// onCreate
	
	// called once weather data has been retrieved
	private void populate(JsonArray timearray) { 
		// Construct the data source 
		ArrayList<Weather> arrayOfHours = Weather.getForecast(timearray, myDB); 
		// Create the adapter to convert the array to views 
		CustomArrayAdapter adapter = new CustomArrayAdapter(this, arrayOfHours); 
		// Attach the adapter to a ListView 
		ListView listView = (ListView) findViewById(R.id.listView1); 
		listView.setAdapter(adapter);
	}
	
	private void populate() { 
		// Construct the data source 
		ArrayList<Weather> arrayOfHours = Weather.getForecast(tableContent); 
		// Create the adapter to convert the array to views 
		CustomArrayAdapter adapter = new CustomArrayAdapter(this, arrayOfHours); 
		// Attach the adapter to a ListView 
		ListView listView = (ListView) findViewById(R.id.listView1); 
		listView.setAdapter(adapter);
	}
	
	// async task to retrieve weather data
	private class WundergroundAsyncTask extends AsyncTask<Void, Void, Void> {
		
		
		@Override
		protected Void doInBackground(Void... arg0) {
			String key = "49600e365dd5f59d";						// acquired from http://www.wunderground.com/weather/api/

			// connects to URL and retrieves Json weather information
			String sURL = "http://api.wunderground.com/api/" + key + "/hourly/q/" + lt + "," + lg + ".json";
			
			try {		
				URL url = new URL(sURL);
				HttpURLConnection request = (HttpURLConnection) url.openConnection();
				request.connect();
			
				JsonParser jp = new JsonParser();
				JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
				JsonObject rootobj = root.getAsJsonObject();
	    	
				// Creates an array from JSON for hourly forecast
				timearray = rootobj.get("hourly_forecast").getAsJsonArray();
				
	    	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void arg0) {
			populate(timearray);
		}
	}	// async task


	
	// auto-generated
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}	// main activity
