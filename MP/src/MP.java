// Retrieves Twitter tweets and analyzes their grade complexity

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.temboo.Library.Twitter.OAuth.FinalizeOAuth;
import com.temboo.Library.Twitter.OAuth.FinalizeOAuth.FinalizeOAuthInputSet;
import com.temboo.Library.Twitter.OAuth.FinalizeOAuth.FinalizeOAuthResultSet;
import com.temboo.Library.Twitter.OAuth.InitializeOAuth;
import com.temboo.Library.Twitter.OAuth.InitializeOAuth.InitializeOAuthInputSet;
import com.temboo.Library.Twitter.OAuth.InitializeOAuth.InitializeOAuthResultSet;
import com.temboo.Library.Twitter.Timelines.UserTimeline;
import com.temboo.Library.Twitter.Timelines.UserTimeline.UserTimelineInputSet;
import com.temboo.Library.Twitter.Timelines.UserTimeline.UserTimelineResultSet;
import com.temboo.core.TembooSession;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class MP {
/**
* @param args
*/

	public static void main(String[] args) throws Exception {
		String tAcct, tApp, tKey;				// Temboo
		String twKey, twSecret, twScreenName; 	// Twitter
		String oToken, oSecret;					// OAuth

		// get these by creating a Temboo account
		Scanner in = new Scanner(System.in);
		System.out.println("Enter Temboo Account Name");
        tAcct = in.nextLine();
        System.out.println("Enter Temboo App Name");
        tApp = in.nextLine();
        System.out.println("Enter Temboo App Key");
        tKey = in.nextLine();

        // get these by creating a new app on Twitter
        System.out.println("Enter Twitter Consumer Key");
        twKey = in.nextLine();
        System.out.println("Enter Twitter Consumer Secret");
        twSecret = in.nextLine();

        // start Temboo to perform OAuth
        TembooSession session = new TembooSession(tAcct, tApp, tKey);
        InitializeOAuth initializeOAuthChoreo = new InitializeOAuth(session);  
        InitializeOAuthInputSet initializeOAuthInputs = initializeOAuthChoreo.newInputSet();
        initializeOAuthInputs.set_AccountName(tAcct);
        initializeOAuthInputs.set_AppKeyName(tApp);
        initializeOAuthInputs.set_AppKeyValue(tKey);
        initializeOAuthInputs.set_ConsumerKey(twKey);
        initializeOAuthInputs.set_ConsumerSecret(twSecret);
        InitializeOAuthResultSet initializeOAuthResults = initializeOAuthChoreo.execute(initializeOAuthInputs);                        
                    
        String userValidateURL = initializeOAuthResults.get_AuthorizationURL();
        System.out.println("Go here and hit allow, and hit enter when you're done...\n" + userValidateURL);
        in.nextLine(); // this waits until the user hits return

        FinalizeOAuth finalizeOAuthChoreo = new FinalizeOAuth(session);
        FinalizeOAuthInputSet finalizeOAuthInputs = finalizeOAuthChoreo.newInputSet();
        String callbackID = initializeOAuthResults.get_CallbackID();
        String oAuthTokenSecret = initializeOAuthResults.get_OAuthTokenSecret();
        finalizeOAuthInputs.set_AccountName(tAcct);
        finalizeOAuthInputs.set_AppKeyName(tApp);
        finalizeOAuthInputs.set_AppKeyValue(tKey);
        finalizeOAuthInputs.set_ConsumerKey(twKey);
        finalizeOAuthInputs.set_ConsumerSecret(twSecret);
        finalizeOAuthInputs.set_CallbackID(callbackID);
        finalizeOAuthInputs.set_OAuthTokenSecret(oAuthTokenSecret);

        FinalizeOAuthResultSet finalizeOAuthResults = finalizeOAuthChoreo.execute(finalizeOAuthInputs);
        oToken = finalizeOAuthResults.get_AccessToken();
        oSecret = finalizeOAuthResults.get_AccessTokenSecret();
        System.out.println("Enter Twitter Handle, without leading @, that you want to retrieve tweets from");
        twScreenName = in.nextLine();

        // Temboo's UserTimeline Choreo lets you pull tweets from a specified handle
        UserTimeline userTimelineChoreo = new UserTimeline(session);
        UserTimelineInputSet userTimelineInputs = userTimelineChoreo.newInputSet();
        userTimelineInputs.set_ConsumerKey(twKey);
        userTimelineInputs.set_ConsumerSecret(twSecret);
        userTimelineInputs.set_AccessToken(oToken);
        userTimelineInputs.set_AccessTokenSecret(oSecret);
        userTimelineInputs.set_ScreenName(twScreenName);
        userTimelineInputs.set_Count("30");			// 30 tweets were requested
        UserTimelineResultSet userTimelineResults = userTimelineChoreo.execute(userTimelineInputs);

        // Convert to a Json Array to continue parsing
        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(userTimelineResults.get_Response());
        JsonArray rootobj = root.getAsJsonArray(); // may be Json Array if it's an array, or other type if a primitive
        
        System.out.println("Enter Wordnik Key");
        String wKey = in.nextLine();
        int polyWords = 0;		// counter for polysyllabic words
        
        // parses the Json response. "i" is the section of the array for each tweet
        for(int i = 0; i < rootobj.size(); i++) {
        	JsonObject item = rootobj.get(i).getAsJsonObject();
        	String tweet = item.get("text").getAsString();
        	System.out.println(tweet);							// prints tweet to check validity
        	tweet = tweet.replaceAll("[^A-Za-z0-9 ]", "");		// replaces any non-alphanumeric characters to avoid bugs in Wordnik
        	String[] words = tweet.split("\\s+");				// splits each tweet by space into an array
        	
        	// now perform operations on each word
        	for (int j=0; j < words.length; j++){
        		String sURL = "http://api.wordnik.com/v4/word.json/" + words[j] + "/hyphenation?useCanonical=false&limit=200&api_key=" + wKey;
        		
        		// Connect to the URL
        		URL url = new URL(sURL);
        		HttpURLConnection request = (HttpURLConnection) url.openConnection();
        		request.connect();
        		
        		// initialize new Json objects to handle the Wordnik response
        		JsonParser jp2 = new JsonParser();
        		JsonElement root2 = jp2.parse(new InputStreamReader((InputStream) request.getContent()));
        		JsonArray rootobj2 = root2.getAsJsonArray();
        		int syllableCount = 0;		// counts the syllables of each word
        		
        		// "k" is the number of fields in the Wordnik response, i.e. the number of syllables
        		for(int k = 0; k < rootobj2.size(); k++) {
                	JsonObject item2 = rootobj2.get(k).getAsJsonObject();
                	if(item2.has("text")){				// this check is necessary because unrecognized words return an empty array
                    	syllableCount = syllableCount + 1;
                	}
        		}
        		// so if the word was determined to be polysyllabic, print it and increase a total counter
        		if (syllableCount > 2){
            		System.out.println("\t" + words[j]);
            		polyWords = polyWords + 1;
            	}
        	}
        }
        System.out.println("\nDone!");		// this code can take a few minutes to run, so the done message is helpful
        // Given formula for grade level = 1.0430 * sqrt(number_of_polysyllables * (30 / number_of_sentences)) + 3.1291
        // Since we know we pulled 30 tweets, assuming one sentence each this simplifies to:
        double grade = 1.0430 * Math.sqrt((double)polyWords) + 3.1291;
        System.out.println("\nGrade level is " + Double.toString(grade));
    }
}
