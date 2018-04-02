package com.example.a2;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.support.v4.app.FragmentActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	ArrayList<String> trains = new ArrayList<String>();
	ArrayList<String> times = new ArrayList<String>();
	String sourceStation;
	String destStation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// search button to look for trains matching the station criteria
		Button buttonSearch = (Button) findViewById(R.id.button1);
		final Button buttonMap = (Button) findViewById(R.id.button2);
		final ListView listView = (ListView) findViewById(R.id.listView1);
		buttonSearch.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				// get stations from spinner.  If source and destination are the same, do nothing
				// but set the map button and list invisible in case a query was already run
				Spinner sourceSpinner = (Spinner) findViewById(R.id.spinner1);
				Spinner destSpinner = (Spinner) findViewById(R.id.spinner2);
				sourceStation = sourceSpinner.getSelectedItem().toString();
				destStation = destSpinner.getSelectedItem().toString();
				if (sourceStation.equals(destStation)){
					buttonMap.setVisibility(View.INVISIBLE);
					listView.setVisibility(View.INVISIBLE);
				}else{
				
				GetTrainsAsyncTask myTask = new GetTrainsAsyncTask();
				myTask.execute();
				}
			}
		});
		
		// button to generate map of trains' last known locations
		final Context context = this;
		buttonMap.setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					Intent intent = new Intent(context, com.example.a2.Map.class);
					intent.putExtra("trainArray", trains);	// pass parameter into new intent
					startActivity(intent);
					}
				});
	}
	
	
		
	// async task to retrieve train data
	private class GetTrainsAsyncTask extends AsyncTask<Void, Void, Void> {
			
		@Override
		protected Void doInBackground(Void... arg0) {
			
			// URL encodes info from spinner
			sourceStation = sourceStation.replace(" ", "%20");
			destStation = destStation.replace(" ", "%20");
			
			// connects to URL and retrieves Json train information
			String sURL = "http://www3.septa.org/hackathon/NextToArrive/" + sourceStation + "/" + destStation + "/20";
			
			try {		
				URL url = new URL(sURL);
				HttpURLConnection request = (HttpURLConnection) url.openConnection();
				request.connect();
			
				JsonParser jp = new JsonParser();
				JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
				JsonArray rootobj = root.getAsJsonArray();
	    	
				// clears array lists before repopulating
				trains.clear();
				times.clear();
				
				// parses the Json response. "i" is the section of the array for each train
		        for(int i = 0; i < rootobj.size(); i++) {
		        	JsonObject item = rootobj.get(i).getAsJsonObject();
		        	String trainNum = item.get("orig_train").getAsString();
		        	String departTime = item.get("orig_departure_time").getAsString();
		        	String arrivalTime = item.get("orig_arrival_time").getAsString();
		        	trains.add(trainNum);
		        	times.add("Departure: " + departTime + "       Arrival: " + arrivalTime);
		        }
				
	    	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void arg0) {
			populate();
		}
	}	// async task
	
	// populates the list of matching trains, utilizing the listview's subtext option and a hash map
	// src: http://stackoverflow.com/questions/7916834/android-adding-listview-sub-item-text
	private void populate() { 
		ListView listView = (ListView) findViewById(R.id.listView1);
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		Button buttonMap = (Button) findViewById(R.id.button2);
		
		for (int i=0; i < trains.size(); i++) {
		    Map<String, String> datum = new HashMap<String, String>(2);
		    datum.put("train", trains.get(i));
		    datum.put("time", times.get(i));
		    data.add(datum);
		}
		
		SimpleAdapter ListAdapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2,
                new String[] {"train", "time"},
                new int[] {android.R.id.text1,
                           android.R.id.text2});
		
		listView.setAdapter(null);
		listView.setAdapter(ListAdapter);
		buttonMap.setVisibility(View.VISIBLE);
		listView.setVisibility(View.VISIBLE);
		
		// displays additional information on long click
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
               long id) {
                
            	String lastTrain = ((TextView)view.findViewById(android.R.id.text1)).getText().toString();
            	GetLastTrainAsyncTask myTask2 = new GetLastTrainAsyncTask();
				myTask2.execute(lastTrain);
                
                return true;
            }
        });
	}
	
	// async task to retrieve specific train's last known data
	private class GetLastTrainAsyncTask extends AsyncTask<String, Void, Void> {
		
		String lastStop = "";
		String schedTime = "";
		String actTime = "";
		
		@Override
		protected Void doInBackground(String... lastTrain) {
			
			// connects to URL and retrieves Json train information
			String sURL = "http://www3.septa.org/hackathon/RRSchedules/" + lastTrain[0] + "";
			
			try {		
				URL url = new URL(sURL);
				HttpURLConnection request = (HttpURLConnection) url.openConnection();
				request.connect();
			
				JsonParser jp = new JsonParser();
				JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
				JsonArray rootobj = root.getAsJsonArray();
				
				// parses the Json response. "i" is the section of the array for each stop
		        for(int i = 0; i < rootobj.size(); i++) {
		        	JsonObject item = rootobj.get(i).getAsJsonObject();
		        	if(item.get("act_tm").getAsString().equals("na")){
		        		break;
		        	}
		        	else{
		        		lastStop = item.get("station").getAsString();
		        		schedTime = item.get("sched_tm").getAsString();
		        		actTime = item.get("act_tm").getAsString();
		        	}
		        }
	    	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void arg0) {
			trainToast(lastStop, schedTime, actTime);
		}
	}	// async task
	
	// Toasts info about train's last stop
	private void trainToast(String lastStop, String schedTime, String actTime ) { 
		Toast.makeText(getBaseContext(), "Last stop: " + lastStop + "\nSched. time: " + schedTime + "\nAct. time: " + actTime, Toast.LENGTH_SHORT).show();
	}
		
	
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
}
