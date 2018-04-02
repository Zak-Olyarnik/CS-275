package com.example.l4;

import java.util.ArrayList; 

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

// Weather class created to hold the hourly data retrieved and stored in each list row
public class Weather { 
	
	// data
	public String time;
	public String description;
	public String temperature; 
	public String humidity;
	public String icon;

	// constructor
	public Weather(String time, String description, String temperature, String humidity, String icon) { 
 		this.time = time; 
 		this.description = description; 
 		this.temperature = temperature; 
 		this.humidity = humidity;
 		this.icon = icon;
 	} 
 
	// stuffs forecast info into a new array
 	public static ArrayList<Weather> getForecast(JsonArray timearray, DatabaseManager myDB) { 
 		
 		ArrayList<Weather> hours = new ArrayList<Weather>();	// array to hold weather info 
 		// clears database before rebuilding
 		myDB.removeAll();
 		
 		// iterates through Json array
 		for( int i = 0; i < 30; i++) {
			JsonObject time = timearray.get(i).getAsJsonObject();
			String pretty = time.get("FCTTIME").getAsJsonObject().get("pretty").getAsString();
			String temp = time.get("temp").getAsJsonObject().get("english").getAsString();
			String cond = time.get("condition").getAsString();
			String humid = time.get("humidity").getAsString();
			String icon = time.get("icon_url").getAsString();
			hours.add(new Weather(pretty, cond, "Temperature: " + temp + " degrees", "Humidity: " + humid + "%", icon));
			
			// rebuilds database, only adding the time to the first entry (it may throw a non-unique error otherwise)
    		if (i == 0){
    			myDB.addRow(System.currentTimeMillis(), pretty, cond, temp, humid, icon);
    		}
    		else
    		{
    			myDB.addRow(i, pretty, cond, temp, humid, icon);
    		}
 		}
 		return hours; 
 	}
 	
 	// overloaded getForecast for use with the array read out of database
 	public static ArrayList<Weather> getForecast(String[][] array) { 
 		
 		ArrayList<Weather> hours = new ArrayList<Weather>();	// array to hold weather info 
 		
 		// iterates through database array
 		for( int i = 0; i < 30; i++) {
			String pretty = array[i][1];
			String cond = array[i][2];
			String temp = array[i][3];
			String humid = array[i][4];
			String icon = array[i][5];
			hours.add(new Weather(pretty, cond, "Temperature: " + temp + " degrees", "Humidity: " + humid + "%", icon));
 		}
 		return hours; 
 	}
}