package com.example.a2;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;

public class Map extends Activity{
	private GoogleMap mMap;
	private ArrayList<Marker> markers = new ArrayList<Marker>();
	private ArrayList<String> trains;
	private ArrayList<LatLng> stationList = new ArrayList<LatLng>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
		// passes train array list from MainActivity
		Intent myIntent = getIntent();
		trains = myIntent.getStringArrayListExtra("trainArray");
		
		GetLastStationAsyncTask myTask = new GetLastStationAsyncTask();
		myTask.execute(trains);
	}
	
	// async task to retrieve specific train's last known data
	private class GetLastStationAsyncTask extends AsyncTask<ArrayList, Void, Void> {
		
		String lastStop = "";
		
		@Override
		protected Void doInBackground(ArrayList... trainsTask) {
			
			// defines initial map
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			for (int i = 0; i < trainsTask[0].size(); i++){
			
				// connects to URL and retrieves Json train information
				String sURL = "http://www3.septa.org/hackathon/RRSchedules/" + trainsTask[0].get(i) + "";
				lastStop = "";
				
				try {		
					URL url = new URL(sURL);
					HttpURLConnection request = (HttpURLConnection) url.openConnection();
					request.connect();
			
					JsonParser jp = new JsonParser();
					JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
					JsonArray rootobj = root.getAsJsonArray();
				
					// parses the Json response. "i" is the section of the array for each stop
					for(int j = 0; j < rootobj.size(); j++) {
						JsonObject item = rootobj.get(j).getAsJsonObject();
						if(item.get("act_tm").getAsString().equals("na")){
							break;
						}
						else{
							lastStop = item.get("station").getAsString();
						}
					}
					if(lastStop.equals("")){
						// do nothing, train has not left station
					}else{
						LatLng stationLocation = getLocationFromAddress(lastStop + ", Philadelphia, PA");
						stationList.add(stationLocation);
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void arg0) {
			
			// add markers to map
			for(int i = 0; i < stationList.size(); i++){
				Marker station = mMap.addMarker(new MarkerOptions().position(stationList.get(i))
						.title(trains.get(i).toString()));
				markers.add(station);
			}
				// centers single point on map
				if (markers.size() < 2){
					mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markers.get(0).getPosition(), 14));
				}
				// zooms to center of all points
				else{
					// src: http://stackoverflow.com/questions/14828217/android-map-v2-zoom-to-show-all-the-markers
					LatLngBounds.Builder builder = new LatLngBounds.Builder();
					for (Marker marker : markers) {
						builder.include(marker.getPosition());
					}
					LatLngBounds bounds = builder.build();
					int padding = 100; // offset from edges of the map in pixels
					final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
				
					//src: http://stackoverflow.com/questions/13692579/movecamera-with-cameraupdatefactory-newlatlngbounds-crashes
					mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
						@Override
						public void onMapLoaded() {
							mMap.animateCamera(cu);
						}
					});
				}
			}
	}	// async task
	
	public LatLng getLocationFromAddress(String strAddress) {

		// The Geo-Party!  Geo-codes the string station names into LatLng objects to be used as markers
		// This may be the coolest thing I've ever seen
		// src:http://stackoverflow.com/questions/3574644/how-can-i-find-the-latitude-and-longitude-from-address
	    Geocoder coder = new Geocoder(this);
	    List<Address> address;
	    LatLng p1 = null;

	    try {
	        address = coder.getFromLocationName(strAddress, 5);
	        if (address == null) {
	            return null;
	        }
	        Address location = address.get(0);
	        location.getLatitude();
	        location.getLongitude();

	        p1 = new LatLng(location.getLatitude(), location.getLongitude() );

	    } catch (Exception ex) {

	        ex.printStackTrace();
	    }

	    return p1;
	}
}