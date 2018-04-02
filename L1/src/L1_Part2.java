// Prints information about the user's current location (zip code, city, state, latitude and longitude)
// Based on a template provided in class, but modified to include the information asked for in the lab
// Requires a user key from the wunderground website
// Makes use of the geolookup feature to automatically find the user's current location

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class L1_Part2 {         
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String key;				// acquired from http://www.wunderground.com/weather/api/
		if(args.length < 1) {
			System.out.println("Enter key: ");
			Scanner in = new Scanner(System.in);
			key = in.nextLine();
		} else {
			key = args[0];
		}
		
		String sURL = "http://api.wunderground.com/api/" + key + "/geolookup/q/autoip.json";		// autoip automatically
																							// gets the user's current location
		// Connect to the URL
		URL url = new URL(sURL);
		HttpURLConnection request = (HttpURLConnection) url.openConnection();
		request.connect();
		
		// Convert to a JSON object to print data
    	JsonParser jp = new JsonParser();
    	JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
    	JsonObject rootobj = root.getAsJsonObject(); // may be Json Array if it's an array, or other type if a primitive
    	
    	// Get some data elements and print them
    	int zip = rootobj.get("location").getAsJsonObject().get("zip").getAsInt();
		System.out.println(zip);
		String city = rootobj.get("location").getAsJsonObject().get("city").getAsString();
		System.out.println(city);
		String state = rootobj.get("location").getAsJsonObject().get("state").getAsString();
		System.out.println(state);
		
		double lat = rootobj.get("location").getAsJsonObject().get("lat").getAsDouble();
		double lon = rootobj.get("location").getAsJsonObject().get("lon").getAsDouble();
		System.out.println(lat + "," + lon);
		
	}

}
