// Lists Google calendars and the events from one of them

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class L2_Part1 {
   /**
    * @param args
    */
      public static void main(String[] args) throws Exception {
    	  
         String clientID, clientSecret, code, redirect, accessToken;

         // get these by creating a new app with your Google account
         Scanner in = new Scanner(System.in);
         System.out.println("Enter Google Client ID");
         clientID = in.nextLine();
         System.out.println("Enter Google Client Secret");
         clientSecret = in.nextLine();
         System.out.println("Enter Google Redirect URI");
         redirect = in.nextLine();
         
         // follow the directions to get the first  access code
         String oauthURL = "https://accounts.google.com/o/oauth2/auth?access_type=offline&client_id=" + clientID + "&scope=https://www.googleapis.com/auth/calendar&response_type=code&redirect_uri=" + redirect + "&state=/profile&approval_prompt=force";
         System.out.println("Go to the following URL and obtain the code that you find there.\n" + oauthURL);
         code = in.nextLine();

         // Google requires a POST for the next step
         String authorizeURL = "https://accounts.google.com/o/oauth2/token";
         String authorizeParams = "code=" + code + "&client_id=" + clientID + "&client_secret=" + clientSecret + "&redirect_uri=" + redirect + "&grant_type=authorization_code";
         String authorizeResponse = executePost(authorizeURL, authorizeParams); 

         // parse the Json response
         JsonParser oauth_jp = new JsonParser();
         JsonElement oauth_root = oauth_jp.parse(authorizeResponse);
         JsonObject oauth_rootobj = oauth_root.getAsJsonObject(); // may be Json Array if it's an array, or other type if a primitive
         accessToken = oauth_rootobj.get("access_token").getAsString();
         System.out.println("Got oauth access token: " + accessToken);

         // concatenates the calendarList URL to the oauth access token
         String sURL = "https://www.googleapis.com/calendar/v3/users/me/calendarList";
         sURL = sURL + "?access_token=" + accessToken;

         // Connect to the URL
         URL url = new URL(sURL);
         HttpURLConnection request = (HttpURLConnection) url.openConnection();
         request.connect();

         // Convert to a Json object to print data
         JsonParser jp = new JsonParser();
         JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
         JsonObject rootobj = root.getAsJsonObject(); // may be Json Array if it's an array, or other type if a primitive

         // the first calendar is found and stored separately so that we can pull the events from it later
         JsonArray items = rootobj.get("items").getAsJsonArray();
         JsonObject item = items.get(0).getAsJsonObject();
         String id1 = item.get("id").getAsString();
         System.out.println("\nFound calendar id: " + id1);
         
         // any other calendars are found with this for loop
         for(int i = 1; i < items.size(); i++) {
                 item = items.get(i).getAsJsonObject();
                 String id = item.get("id").getAsString();
                 System.out.println("Found calendar id: " + id);
         }       
         
         // repeat the steps above with a new URL, requesting the events from the first calendar
         sURL = "https://www.googleapis.com/calendar/v3/calendars/" + id1 + "/events";
         sURL = sURL + "?access_token=" + accessToken;

         // Connect to the URL
         url = new URL(sURL);
         request = (HttpURLConnection) url.openConnection();
         request.connect();

         // Convert to a Json object to print data
         jp = new JsonParser();
         root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
         rootobj = root.getAsJsonObject(); // may be Json Array if it's an array, or other type if a primitive
         
         // events put into Google calendars with "all day" specifications will not return a dateTime field, so first
         // check to see if it exists.  The date field can be returned instead
         items = rootobj.get("items").getAsJsonArray();
         System.out.println("\n");
         for(int i = 0; i < items.size(); i++) {
             item = items.get(i).getAsJsonObject();
             String timex;
             JsonObject start = item.get("start").getAsJsonObject();
             if(start.has("dateTime")){
            	 timex=start.get("dateTime").getAsString();
             }else{
            	 timex=start.get("date").getAsString();
             }
             String title = item.get("summary").getAsString();
             System.out.println(timex + " - " + title);
         }
       }

      
// The following is a routine taken directly fromt the sample code provided, specifically to use the Google authorization interface
public static String executePost(String targetURL, String urlParameters)
{
  URL url;
  HttpURLConnection connection = null;  
  try {
    //Create connection
    url = new URL(targetURL);
    connection = (HttpURLConnection)url.openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", 
         "application/x-www-form-urlencoded");

    connection.setRequestProperty("Content-Length", "" + 
             Integer.toString(urlParameters.getBytes().length));
    connection.setRequestProperty("Content-Language", "en-US");  

    connection.setUseCaches (false);
    connection.setDoInput(true);
    connection.setDoOutput(true);

    //Send request
    DataOutputStream wr = new DataOutputStream (
                connection.getOutputStream ());
    wr.writeBytes (urlParameters);
    wr.flush ();
    wr.close ();

    //Get Response
    InputStream is = connection.getInputStream();
    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
    String line;
    StringBuffer response = new StringBuffer(); 
    while((line = rd.readLine()) != null) {
      response.append(line);
      response.append('\r');
    }
    rd.close();
    return response.toString();

  } catch (Exception e) {

    e.printStackTrace();
    return null;

  } finally {

    if(connection != null) {
      connection.disconnect(); 
    }
  }
}

   }
