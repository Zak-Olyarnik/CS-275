// Prints the hourly forecast for the user's current location (about 36 hours)
// Based on a template provided in class, but modified to include an array for storing the data
// Requires a user key from the wunderground website
// Makes use of the geolookup feature to automatically find the user's current location

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class L1_Part3 {
	public static void main(String[] args) throws Exception {
				String key;						// acquired from http://www.wunderground.com/weather/api/
				if(args.length < 1) {
					System.out.println("Enter key: ");
					Scanner in = new Scanner(System.in);
					key = in.nextLine();
				} else {
					key = args[0];
				}
				
				String sURL = "http://api.wunderground.com/api/" + key + "/geolookup/q/autoip.json";	// autoip automatically
																								// gets the user's current location
				// Connect to the URL
				URL url = new URL(sURL);
				HttpURLConnection request = (HttpURLConnection) url.openConnection();
				request.connect();
				
				// Convert to a JSON object to print data
		    	JsonParser jp = new JsonParser();
		    	JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
		    	JsonObject rootobj = root.getAsJsonObject(); // may be Json Array if it's an array, or other type if a primitive
		    	
		    	// Pull location data from geolookup to make the next step easier 
				String city= rootobj.get("location").getAsJsonObject().get("city").getAsString();
				String state = rootobj.get("location").getAsJsonObject().get("state").getAsString();
		
				sURL = "http://api.wunderground.com/api/" + key + "/hourly/q/40.4417,80.0000.json";
				//sURL = "http://api.wunderground.com/api/" + key + "/hourly/q/" + state + "/" + city + ".json";		// key, city,
				url = new URL(sURL);;													// and state were already collected above
				request = (HttpURLConnection) url.openConnection();
				request.connect();
				
				// Convert to a JSON object using the same code as above 
		    	jp = new JsonParser();
		    	root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
		    	rootobj = root.getAsJsonObject(); // may be Json Array if it's an array, or other type if a primitive
		    	
		    	// Creates an array from JSON for hourly forecast
		    	JsonArray timearray = rootobj.get("hourly_forecast").getAsJsonArray();
		    	
		    	// For loop grabs the hourly information using the same format as before
		    	for( int i = 0; i < timearray.size(); i++) {
                      JsonObject time = timearray.get(i).getAsJsonObject();
                      String pretty = time.get("FCTTIME").getAsJsonObject().get("pretty").getAsString();
                      String temp = time.get("temp").getAsJsonObject().get("english").getAsString();
                      String cond = time.get("condition").getAsString();
                      String humid = time.get("humidity").getAsString();
                      
                      // Prints time stamp followed by collected data
                      System.out.println(pretty);
                      System.out.println("\t temp: " + temp);
                      System.out.println("\t condition: " + cond);
                      System.out.println("\t humidity: "+ humid);
                      System.out.println(); 
		    	}
	}

}
