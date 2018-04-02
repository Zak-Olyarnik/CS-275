// Looks up passwords saved in Cloudmine database to connect with Google calendars and return
// a list of free times between two users

import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.temboo.Library.CloudMine.ObjectStorage.ObjectGet;
import com.temboo.Library.CloudMine.ObjectStorage.ObjectGet.ObjectGetInputSet;
import com.temboo.Library.CloudMine.ObjectStorage.ObjectGet.ObjectGetResultSet;
import com.temboo.Library.CloudMine.ObjectStorage.ObjectSet;
import com.temboo.Library.CloudMine.ObjectStorage.ObjectSet.ObjectSetInputSet;
import com.temboo.Library.CloudMine.ObjectStorage.ObjectSet.ObjectSetResultSet;
import com.temboo.Library.Google.Calendar.GetAllCalendars;
import com.temboo.Library.Google.Calendar.GetAllEvents;
import com.temboo.Library.Google.Calendar.GetAllCalendars.GetAllCalendarsInputSet;
import com.temboo.Library.Google.Calendar.GetAllCalendars.GetAllCalendarsResultSet;
import com.temboo.Library.Google.Calendar.GetAllEvents.GetAllEventsInputSet;
import com.temboo.Library.Google.Calendar.GetAllEvents.GetAllEventsResultSet;
import com.temboo.Library.Google.Calendar.SearchEvents;
import com.temboo.Library.Google.Calendar.SearchEvents.SearchEventsInputSet;
import com.temboo.Library.Google.Calendar.SearchEvents.SearchEventsResultSet;
import com.temboo.Library.Google.OAuth.InitializeOAuth;
import com.temboo.Library.Google.OAuth.InitializeOAuth.InitializeOAuthInputSet;
import com.temboo.Library.Google.OAuth.InitializeOAuth.InitializeOAuthResultSet;
import com.temboo.Library.Google.OAuth.FinalizeOAuth;
import com.temboo.Library.Google.OAuth.FinalizeOAuth.FinalizeOAuthInputSet;
import com.temboo.Library.Google.OAuth.FinalizeOAuth.FinalizeOAuthResultSet;
import com.temboo.core.TembooSession;

public class L5 {
	/**
    * @param args
    */
	public static void main(String[] args) throws Exception {

		String tAcct, tApp, tKey;	// Temboo
        String cID, cKey;			// Cloudmine
        String name, gID, gSecret, gRefresh, gCalendar;	// values written to cloudmine
        String gCallbackID;	// values to retrieve calendar
        String friendName, friendID, friendSecret, friendRefresh, friendCalendar;	// values written to cloudmine

        // get these by creating a Temboo account
        Scanner in = new Scanner(System.in);
        System.out.println("Enter Temboo Account Name");
        tAcct = in.nextLine();
        System.out.println("Enter Temboo App Name");
        tApp = in.nextLine();
        System.out.println("Enter Temboo App Key");
        tKey = in.nextLine();
        
        // get these by creating a Cloudmine app
        System.out.println("Enter Cloudmine Account ID");
        cID = in.nextLine();
        System.out.println("Enter Cloudmine App Key");
        cKey = in.nextLine();
        
        
        
//*************** PART ONE: Check for users in cloudmine database, and enter their information there ***************\\
        
        System.out.println("Enter your name");
        name = in.nextLine();
        
        // start Temboo for cloudmine access
        TembooSession session = new TembooSession(tAcct, tApp, tKey);
        ObjectGet objectGetChoreo = new ObjectGet(session);
        ObjectGetInputSet objectGetInputs = objectGetChoreo.newInputSet();
        objectGetInputs.set_ApplicationIdentifier(cID);
        objectGetInputs.set_APIKey(cKey);
        ObjectGetResultSet objectGetResults = objectGetChoreo.execute(objectGetInputs);
        
        // parse the Json response
        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(objectGetResults.get_Response());
        JsonObject rootobj = root.getAsJsonObject();
        JsonObject users = rootobj.get("success").getAsJsonObject();
        
        // look for user's name already in cloudmine database
        if (users.has(name)){
        	JsonObject item = users.get(name).getAsJsonObject();
       		gID = item.get("ID").getAsString();
    		gSecret = item.get("Secret").getAsString();
    		gRefresh = item.get("Refresh").getAsString();
    		gCalendar = item.get("Calendar").getAsString();
    		System.out.println("Calendar " + gCalendar + " found!");
    	
    	// write to cloudmine database if user is not found
        }else{
        	System.out.println("No calendar found\nEnter Google Client ID");
        	gID = in.nextLine();
        	System.out.println("Enter Google Client Secret");
            gSecret = in.nextLine();
            
            // OAuth to get permission for Google
            InitializeOAuth InitializeOAuthChoreo = new InitializeOAuth(session);
            InitializeOAuthInputSet InitializeOAuthInputs = InitializeOAuthChoreo.newInputSet();
            InitializeOAuthInputs.set_ClientID(gID);
            InitializeOAuthInputs.set_Scope("https://www.googleapis.com/auth/calendar");
            InitializeOAuthResultSet InitializeOAuthResults = InitializeOAuthChoreo.execute(InitializeOAuthInputs);
            
            String userValidateURL = InitializeOAuthResults.get_AuthorizationURL();
            System.out.println("Go here and hit allow, and hit enter when you're done...\n" + userValidateURL);
            in.nextLine(); // this waits until the user hits return

            FinalizeOAuth FinalizeOAuthChoreo = new FinalizeOAuth(session);
            FinalizeOAuthInputSet FinalizeOAuthInputs = FinalizeOAuthChoreo.newInputSet();
            gCallbackID = InitializeOAuthResults.get_CallbackID();
            FinalizeOAuthInputs.set_ClientID(gID);
            FinalizeOAuthInputs.set_ClientSecret(gSecret);
            FinalizeOAuthInputs.set_CallbackID(gCallbackID);
            FinalizeOAuthResultSet gFinalizeOAuthResults = FinalizeOAuthChoreo.execute(FinalizeOAuthInputs);
            gRefresh = gFinalizeOAuthResults.get_RefreshToken();	// refresh token is more important than OAuth token for longtime use
  
            // next, get calendar
            GetAllCalendars getAllCalendarsChoreo = new GetAllCalendars(session);
            GetAllCalendarsInputSet getAllCalendarsInputs = getAllCalendarsChoreo.newInputSet();
            getAllCalendarsInputs.set_ClientSecret(gSecret);
            getAllCalendarsInputs.set_RefreshToken(gRefresh);
            getAllCalendarsInputs.set_ClientID(gID);
            GetAllCalendarsResultSet getAllCalendarsResults = getAllCalendarsChoreo.execute(getAllCalendarsInputs);

            // parse the Json like normal
            jp = new JsonParser();
            root = jp.parse(getAllCalendarsResults.get_Response());
            rootobj = root.getAsJsonObject();
            JsonArray items = rootobj.get("items").getAsJsonArray();
            JsonObject item = items.get(0).getAsJsonObject();	// we only want the first calendar
            gCalendar = item.get("id").getAsString();
            
            // finally, write user to cloudmine database
            ObjectSet objectSetChoreo = new ObjectSet(session);
            ObjectSetInputSet objectSetInputs = objectSetChoreo.newInputSet();
            objectSetInputs.set_ApplicationIdentifier(cID);
            objectSetInputs.set_APIKey(cKey);
            objectSetInputs.set_Data("{\"" + name + "\":{\"ID\":\"" + gID + "\", \"Secret\":\"" + gSecret + "\", \"Refresh\":\"" + gRefresh + "\", \"Calendar\":\"" + gCalendar + "\"}}");
            ObjectSetResultSet objectSetResults = objectSetChoreo.execute(objectSetInputs);
            System.out.println("User " + name + " entered!");   
        }
        
        System.out.println("Enter friend's name");
        friendName = in.nextLine();
            
        // look for user's name already in cloudmine database
        if (users.has(friendName)){
        	JsonObject item = users.get(friendName).getAsJsonObject();
        	friendID = item.get("ID").getAsString();
        	friendSecret = item.get("Secret").getAsString();
        	friendRefresh = item.get("Refresh").getAsString();
        	friendCalendar = item.get("Calendar").getAsString();
    		System.out.println("Calendar " + friendCalendar + " found!");
    	
    	// write to cloudmine database if user is not found
        }else{
        	System.out.println("No calendar found\nEnter friend's Google Client ID");
        	friendID = in.nextLine();
        	System.out.println("Enter friend's Google Client Secret");
        	friendSecret = in.nextLine();
            
            // OAuth to get permission for Google
        	//TembooSession friendSession = new TembooSession(friendtAcct, friendtApp, friendtKey);
            InitializeOAuth InitializeOAuthChoreo = new InitializeOAuth(session);
            InitializeOAuthInputSet InitializeOAuthInputs = InitializeOAuthChoreo.newInputSet();
            InitializeOAuthInputs.set_ClientID(gID);
            InitializeOAuthInputs.set_Scope("https://www.googleapis.com/auth/calendar");
            InitializeOAuthResultSet InitializeOAuthResults = InitializeOAuthChoreo.execute(InitializeOAuthInputs);
            
            String userValidateURL = InitializeOAuthResults.get_AuthorizationURL();
            System.out.println("Go here and hit allow, and hit enter when you're done...\n" + userValidateURL);
            in.nextLine(); // this waits until the user hits return

            FinalizeOAuth FinalizeOAuthChoreo = new FinalizeOAuth(session);
            FinalizeOAuthInputSet FinalizeOAuthInputs = FinalizeOAuthChoreo.newInputSet();
            gCallbackID = InitializeOAuthResults.get_CallbackID();
            FinalizeOAuthInputs.set_ClientID(gID);
            FinalizeOAuthInputs.set_ClientSecret(gSecret);
            FinalizeOAuthInputs.set_CallbackID(gCallbackID);
            FinalizeOAuthResultSet FinalizeOAuthResults = FinalizeOAuthChoreo.execute(FinalizeOAuthInputs);
            friendRefresh = FinalizeOAuthResults.get_RefreshToken();	// refresh token is more important than OAuth token for longtime use

            // next, get calendar
            GetAllCalendars getAllCalendarsChoreo = new GetAllCalendars(session);
            GetAllCalendarsInputSet getAllCalendarsInputs = getAllCalendarsChoreo.newInputSet();
            getAllCalendarsInputs.set_ClientSecret(gSecret);
            getAllCalendarsInputs.set_RefreshToken(friendRefresh);
            getAllCalendarsInputs.set_ClientID(gID);
            GetAllCalendarsResultSet getAllCalendarsResults = getAllCalendarsChoreo.execute(getAllCalendarsInputs);

            // parse the Json like normal
            jp = new JsonParser();
            root = jp.parse(getAllCalendarsResults.get_Response());
            rootobj = root.getAsJsonObject();
            JsonArray items = rootobj.get("items").getAsJsonArray();
            JsonObject item = items.get(0).getAsJsonObject();	// we only want the first calendar
            friendCalendar = item.get("id").getAsString();
            
            // finally, write user to cloudmine database
            ObjectSet objectSetChoreo = new ObjectSet(session);
            ObjectSetInputSet objectSetInputs = objectSetChoreo.newInputSet();
            objectSetInputs.set_ApplicationIdentifier(cID);
            objectSetInputs.set_APIKey(cKey);
            objectSetInputs.set_Data("{\"" + friendName + "\":{\"ID\":\"" + friendID + "\", \"Secret\":\"" + friendSecret + "\", \"Refresh\":\"" + friendRefresh + "\", \"Calendar\":\"" + friendCalendar + "\"}}");
            ObjectSetResultSet objectSetResults = objectSetChoreo.execute(objectSetInputs);
            System.out.println("User " + friendName + " entered!");
        }
        
        
        
//*************** PART TWO: Get all events from both calendars ***************\\
        
        // Get events from first user's calendar
        GetAllEvents getAllEventsChoreo = new GetAllEvents(session);
        GetAllEventsInputSet getAllEventsInputs = getAllEventsChoreo.newInputSet();
        getAllEventsInputs.set_ClientID(gID);
        getAllEventsInputs.set_ClientSecret(gSecret);
        getAllEventsInputs.set_CalendarID(gCalendar);
        getAllEventsInputs.set_RefreshToken(gRefresh);
        GetAllEventsResultSet getAllEventsResults = getAllEventsChoreo.execute(getAllEventsInputs);       
      
        // Now parse the Json
        jp = new JsonParser();
        root = jp.parse(getAllEventsResults.get_Response());
        rootobj = root.getAsJsonObject();

        // events put into Google calendars with "all day" specifications will not return a dateTime field, so first
        // check to see if it exists.  The date field can be returned instead
        JsonArray calendars = rootobj.get("items").getAsJsonArray();
        System.out.println();
        for(int i = 0; i < calendars.size(); i++) {
        	JsonObject itemx = calendars.get(i).getAsJsonObject();
        	String timex;
        	JsonObject start = itemx.get("start").getAsJsonObject();
        	if(start.has("dateTime")){
        		timex=start.get("dateTime").getAsString();
        	}else{
        		timex=start.get("date").getAsString();
        	}
        	String title = itemx.get("summary").getAsString();
        	System.out.println(timex + " - " + title);
        }
        
        // repeat with second user's calendar
        getAllEventsChoreo = new GetAllEvents(session);
        getAllEventsInputs = getAllEventsChoreo.newInputSet();
        getAllEventsInputs.set_ClientID(gID);
        getAllEventsInputs.set_ClientSecret(gSecret);
        getAllEventsInputs.set_CalendarID(friendCalendar);
        getAllEventsInputs.set_RefreshToken(friendRefresh);
        getAllEventsResults = getAllEventsChoreo.execute(getAllEventsInputs);       
      
        // Now parse the Json
        jp = new JsonParser();
        root = jp.parse(getAllEventsResults.get_Response());
        rootobj = root.getAsJsonObject();
        
        // events put into Google calendars with "all day" specifications will not return a dateTime field, so first
        // check to see if it exists.  The date field can be returned instead
        calendars = rootobj.get("items").getAsJsonArray();
        System.out.println();
        for(int i = 0; i < calendars.size(); i++) {
        	JsonObject itemx = calendars.get(i).getAsJsonObject();
        	String timex;
        	JsonObject start = itemx.get("start").getAsJsonObject();
        	if(start.has("dateTime")){
        		timex=start.get("dateTime").getAsString();
        	}else{
        		timex=start.get("date").getAsString();
        	}
        	String title = itemx.get("summary").getAsString();
        	System.out.println(timex + " - " + title);
        }
        
        
        
//*************** PART THREE: Compare free times from both calendars ***************\\
        
        String compareDate, startTime, endTime;
        
        // reads in date in a normal format, then converts to the Google calendar representation
        System.out.println("\nEnter date to compare (dd/mm/yyyy)");
        compareDate = in.nextLine();
        String compareArray[] = compareDate.split("/");
        compareDate = compareArray[2] + "-" + compareArray[1] + "-" + compareArray[0];
        
        // arrays for each user's free times
        String meFree[] = new String[24];
        String friendFree[] = new String[24];
        for (int i = 0; i < 24; i++){
        	meFree[i] = "free";
        	friendFree[i] = "free";
        }
        
        // checking first user's free times, broken up into sections to handle leading zeroes
        // and the last hour
        int k = 0;
        while (k < 9){
        	startTime = compareDate + "T0" + Integer.toString(k) + ":00:00-04:00";
        	endTime = compareDate + "T0" + Integer.toString(k+1) + ":00:00-04:00";
        	SearchEvents searchEventsChoreo = new SearchEvents(session);
            SearchEventsInputSet searchEventsInputs = searchEventsChoreo.newInputSet();
            searchEventsInputs.set_ClientSecret(gSecret);
            searchEventsInputs.set_CalendarID(gCalendar);
            searchEventsInputs.set_MinTime(startTime);
            searchEventsInputs.set_RefreshToken(gRefresh);
            searchEventsInputs.set_MaxTime(endTime);
            searchEventsInputs.set_ClientID(gID);
            SearchEventsResultSet searchEventsResults = searchEventsChoreo.execute(searchEventsInputs);
        	
            // Now parse the Json
            jp = new JsonParser();
            root = jp.parse(searchEventsResults.get_Response());
            rootobj = root.getAsJsonObject();
            
            JsonArray events = rootobj.get("items").getAsJsonArray();
            if (!(events.size() == 0)){
            	meFree[k] = "busy";
            }
            k++;
        }
        
        k = 9;
        while (k < 10){
	        startTime = compareDate + "T09:00:00-04:00";
	    	endTime = compareDate + "T10:00:00-04:00";
	    	SearchEvents searchEventsChoreo = new SearchEvents(session);
	        SearchEventsInputSet searchEventsInputs = searchEventsChoreo.newInputSet();
	        searchEventsInputs.set_ClientSecret(gSecret);
	        searchEventsInputs.set_CalendarID(gCalendar);
	        searchEventsInputs.set_MinTime(startTime);
	        searchEventsInputs.set_RefreshToken(gRefresh);
	        searchEventsInputs.set_MaxTime(endTime);
	        searchEventsInputs.set_ClientID(gID);
	        SearchEventsResultSet searchEventsResults = searchEventsChoreo.execute(searchEventsInputs);
	    	
	        // Now parse the Json
	        jp = new JsonParser();
	        root = jp.parse(searchEventsResults.get_Response());
	        rootobj = root.getAsJsonObject();
	        
	        JsonArray events = rootobj.get("items").getAsJsonArray();
	        if (!(events.size() == 0)){
            	meFree[k] = "busy";
            }
            k++;
        }
        
        k = 10;
        while (k < 23){
        	startTime = compareDate + "T" + Integer.toString(k) + ":00:00-04:00";
        	endTime = compareDate + "T" + Integer.toString(k+1) + ":00:00-04:00";
        	SearchEvents searchEventsChoreo = new SearchEvents(session);
            SearchEventsInputSet searchEventsInputs = searchEventsChoreo.newInputSet();
            searchEventsInputs.set_ClientSecret(gSecret);
            searchEventsInputs.set_CalendarID(gCalendar);
            searchEventsInputs.set_MinTime(startTime);
            searchEventsInputs.set_RefreshToken(gRefresh);
            searchEventsInputs.set_MaxTime(endTime);
            searchEventsInputs.set_ClientID(gID);
            SearchEventsResultSet searchEventsResults = searchEventsChoreo.execute(searchEventsInputs);
        	
            // Now parse the Json
            jp = new JsonParser();
            root = jp.parse(searchEventsResults.get_Response());
            rootobj = root.getAsJsonObject();
            
            JsonArray events = rootobj.get("items").getAsJsonArray();
            if (!(events.size() == 0)){
            	meFree[k] = "busy";
            }
            k++;
        }
        
        k = 23;
        while (k < 24){
        	startTime = compareDate + "T23:00:00-04:00";
        	endTime = compareDate + "T23:30:00-04:00";
        	SearchEvents searchEventsChoreo = new SearchEvents(session);
            SearchEventsInputSet searchEventsInputs = searchEventsChoreo.newInputSet();
            searchEventsInputs.set_ClientSecret(gSecret);
            searchEventsInputs.set_CalendarID(gCalendar);
            searchEventsInputs.set_MinTime(startTime);
            searchEventsInputs.set_RefreshToken(gRefresh);
            searchEventsInputs.set_MaxTime(endTime);
            searchEventsInputs.set_ClientID(gID);
            SearchEventsResultSet searchEventsResults = searchEventsChoreo.execute(searchEventsInputs);
        	
            // Now parse the Json
            jp = new JsonParser();
            root = jp.parse(searchEventsResults.get_Response());
            rootobj = root.getAsJsonObject();
            
            JsonArray events = rootobj.get("items").getAsJsonArray();
            if (!(events.size() == 0)){
            	meFree[k] = "busy";
            }
            k++;
        }
        
        // same code repeated for friend's calendar
        k = 0;
        while (k < 9){
        	startTime = compareDate + "T0" + Integer.toString(k) + ":00:00-04:00";
        	endTime = compareDate + "T0" + Integer.toString(k+1) + ":00:00-04:00";
        	SearchEvents searchEventsChoreo = new SearchEvents(session);
            SearchEventsInputSet searchEventsInputs = searchEventsChoreo.newInputSet();
            searchEventsInputs.set_ClientSecret(gSecret);
            searchEventsInputs.set_CalendarID(friendCalendar);
            searchEventsInputs.set_MinTime(startTime);
            searchEventsInputs.set_RefreshToken(friendRefresh);
            searchEventsInputs.set_MaxTime(endTime);
            searchEventsInputs.set_ClientID(gID);
            SearchEventsResultSet searchEventsResults = searchEventsChoreo.execute(searchEventsInputs);
        	
            // Now parse the Json
            jp = new JsonParser();
            root = jp.parse(searchEventsResults.get_Response());
            rootobj = root.getAsJsonObject();
            
            JsonArray events = rootobj.get("items").getAsJsonArray();
            if (!(events.size() == 0)){
            	friendFree[k] = "busy";
            }
            k++;
        }
        
        k = 9;
        while (k < 10){
	        startTime = compareDate + "T09:00:00-04:00";
	    	endTime = compareDate + "T10:00:00-04:00";
	    	SearchEvents searchEventsChoreo = new SearchEvents(session);
	        SearchEventsInputSet searchEventsInputs = searchEventsChoreo.newInputSet();
	        searchEventsInputs.set_ClientSecret(gSecret);
	        searchEventsInputs.set_CalendarID(friendCalendar);
	        searchEventsInputs.set_MinTime(startTime);
	        searchEventsInputs.set_RefreshToken(friendRefresh);
	        searchEventsInputs.set_MaxTime(endTime);
	        searchEventsInputs.set_ClientID(gID);
	        SearchEventsResultSet searchEventsResults = searchEventsChoreo.execute(searchEventsInputs);
	    	
	        // Now parse the Json
	        jp = new JsonParser();
	        root = jp.parse(searchEventsResults.get_Response());
	        rootobj = root.getAsJsonObject();
	        
	        JsonArray events = rootobj.get("items").getAsJsonArray();
	        if (!(events.size() == 0)){
            	friendFree[k] = "busy";
            }
            k++;
        }
        
        k = 10;
        while (k < 23){
        	startTime = compareDate + "T" + Integer.toString(k) + ":00:00-04:00";
        	endTime = compareDate + "T" + Integer.toString(k+1) + ":00:00-04:00";
        	SearchEvents searchEventsChoreo = new SearchEvents(session);
            SearchEventsInputSet searchEventsInputs = searchEventsChoreo.newInputSet();
            searchEventsInputs.set_ClientSecret(gSecret);
            searchEventsInputs.set_CalendarID(friendCalendar);
            searchEventsInputs.set_MinTime(startTime);
            searchEventsInputs.set_RefreshToken(friendRefresh);
            searchEventsInputs.set_MaxTime(endTime);
            searchEventsInputs.set_ClientID(gID);
            SearchEventsResultSet searchEventsResults = searchEventsChoreo.execute(searchEventsInputs);
        	
            // Now parse the Json
            jp = new JsonParser();
            root = jp.parse(searchEventsResults.get_Response());
            rootobj = root.getAsJsonObject();
            
            JsonArray events = rootobj.get("items").getAsJsonArray();
            if (!(events.size() == 0)){
            	friendFree[k] = "busy";
            }
            k++;
        }
        
        k = 23;
        while (k < 24){
        	startTime = compareDate + "T23:00:00-04:00";
        	endTime = compareDate + "T23:30:00-04:00";
        	SearchEvents searchEventsChoreo = new SearchEvents(session);
            SearchEventsInputSet searchEventsInputs = searchEventsChoreo.newInputSet();
            searchEventsInputs.set_ClientSecret(gSecret);
            searchEventsInputs.set_CalendarID(friendCalendar);
            searchEventsInputs.set_MinTime(startTime);
            searchEventsInputs.set_RefreshToken(friendRefresh);
            searchEventsInputs.set_MaxTime(endTime);
            searchEventsInputs.set_ClientID(gID);
            SearchEventsResultSet searchEventsResults = searchEventsChoreo.execute(searchEventsInputs);
        	
            // Now parse the Json
            jp = new JsonParser();
            root = jp.parse(searchEventsResults.get_Response());
            rootobj = root.getAsJsonObject();
            
            JsonArray events = rootobj.get("items").getAsJsonArray();
            if (!(events.size() == 0)){
            	friendFree[k] = "busy";
            }
            k++;
        }
        
        // prints a list of times that are still free for both users
        for (int i = 0; i < 24; i++){
        	if(meFree[i].equals("free") && friendFree[i].equals("free")){
        		System.out.println(i + ":00:00 - free");
        	}
        }   
	}
}
