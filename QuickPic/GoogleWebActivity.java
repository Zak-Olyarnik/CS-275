package sam.myapplication;//Replace these two lines with whatever is relevant.

//package com.example.androidtembootwitterlister;
//import com.example.androidtembootwitterlister.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.commons.codec.binary.Base64;
import com.temboo.Library.Google.OAuth.FinalizeOAuth;
import com.temboo.Library.Google.OAuth.FinalizeOAuth.FinalizeOAuthInputSet;
import com.temboo.Library.Google.OAuth.FinalizeOAuth.FinalizeOAuthResultSet;
import com.temboo.Library.Google.OAuth.InitializeOAuth;
import com.temboo.Library.Google.OAuth.InitializeOAuth.InitializeOAuthInputSet;
import com.temboo.Library.Google.OAuth.InitializeOAuth.InitializeOAuthResultSet;
import com.temboo.Library.Google.Drive.Files.Insert;
import com.temboo.Library.Google.Drive.Files.Insert.InsertInputSet;
import com.temboo.Library.Google.Drive.Files.Insert.InsertResultSet;
import com.temboo.core.TembooException;
import com.temboo.core.TembooSession;

import java.io.ByteArrayOutputStream;
import java.io.File;

import sam.myapplication.R;

public class GoogleWebActivity extends Activity {
	// This is our Oauth leg 2 net access thread
	public class OauthFinalizeAsyncTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				continueOAuth();
			} catch (TembooException e) {
				// TODO Auto-generated catch block
				Log.e("exception", e.getMessage());
			}
			
			return null;
		}
	}
        
        public class UploadTask extends AsyncTask<Void, Void, Void> {
            protected Void doInBackground(Void... arg0) {
                try {
                    convertFile("Insert File Path Here");
                    uploadFile();
                } catch (TembooException e) {
                    e.printStackTrace();
                }
                return null;
            }
            
        }
	
	// This is our Oauth leg 1 net access thread
	public class OauthAsyncTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPostExecute(Void arg0) {
			// Have the user hit "allow" which will call continueOAuth to finish
			promptUserToAuthorizeApp(initializeOAuthResults.get_AuthorizationURL());
			
			// TODO: Is this a race condition?  waiting for user to authorize but then calling finalize?
			OauthFinalizeAsyncTask myTask2 = new OauthFinalizeAsyncTask();
			myTask2.execute();			
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				beginOAuth();
			} catch (TembooException e) {
				// TODO Auto-generated catch block
				Log.e("exception", e.getMessage());
			}
			
			return null;			
		}
	}

	protected String CALLBACK_URL;
	protected String tembooAcct;
	protected String tembooAppKey;
	protected String tembooAppValue;

        //Needed to use for Drive's Insert in Temboo.
	protected String googClientID;
	protected String googClientSecret;
        protected String googRefreshToken;
        protected String googContentType = "image/jpeg"; //Since we're focusing on uploading pictures...
        protected String googFileContent; // This needs to be in Base64.
        protected String RequestBody; // This needs to be JSON.
        private String googAccessToken;
        private String googScope = "https://www.googleapis.com/auth/drive";
    
	protected TembooSession ts;
	
	protected InitializeOAuthResultSet initializeOAuthResults;
	protected FinalizeOAuth finalizeOAuthChoreo;
	protected FinalizeOAuthResultSet finalizeOAuthResults;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_webview);
		
		// Pull intent values
		tembooAcct = this.getIntent().getExtras().getString("tembooAcct");
		tembooAppKey = this.getIntent().getExtras().getString("tembooAppKey");
		tembooAppValue = this.getIntent().getExtras().getString("tembooAppValue");
		/*twitterConsumerKey = this.getIntent().getExtras().getString("twitterConsumerKey");
		twitterConsumerSecret = this.getIntent().getExtras().getString("twitterConsumerSecret"); */
		googClientID = this.getIntent().getExtras().getString("googClientID");
		googClientSecret = this.getIntent().getExtras().getString("googClientSecret");
		
		CALLBACK_URL = tembooAcct + ".temboolive.com";
		
		try {
			ts = new TembooSession(tembooAcct, tembooAppKey, tembooAppValue);
		} catch (TembooException e1) {
			// TODO Auto-generated catch block
			Log.e("exception", e1.getMessage());
		}
		
		OauthAsyncTask myTask = new OauthAsyncTask();
		myTask.execute();
	}
	
	private void beginOAuth() throws TembooException {
		// First OAuth Leg
		InitializeOAuth initializeOAuthChoreo = new InitializeOAuth(ts);
		InitializeOAuthInputSet initializeOAuthInputs = initializeOAuthChoreo.newInputSet();
		initializeOAuthInputs.set_ClientID(googClientID);
		initializeOAuthInputs.set_Scope(googScope);
		initializeOAuthResults = initializeOAuthChoreo.execute(initializeOAuthInputs);
	}
	
	private void continueOAuth() throws TembooException {
		// Final OAuth Leg
		finalizeOAuthChoreo = new FinalizeOAuth(ts);
		FinalizeOAuthInputSet finalizeOAuthInputs = finalizeOAuthChoreo.newInputSet();
		finalizeOAuthInputs.set_ClientID(googClientID);
		finalizeOAuthInputs.set_CallbackID(initializeOAuthResults.get_CallbackID());
		finalizeOAuthInputs.set_ClientSecret(googClientSecret);
		finalizeOAuthInputs.set_AccountName(tembooAcct);
		finalizeOAuthInputs.set_AppKeyValue(tembooAppValue);

		finalizeOAuthResults = finalizeOAuthChoreo.execute(finalizeOAuthInputs);
		
		googAccessToken = finalizeOAuthResults.get_AccessToken();
		googRefreshToken = finalizeOAuthResults.get_RefreshToken();
		
		SharedPreferences sharedPref = this.getSharedPreferences(
		        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("googAccessToken", googAccessToken);
		editor.putString("googRefreshToken", googRefreshToken);
		editor.commit();
		
		// Now redirect back to the main activity
		setResult(0);
		finish();
	}
        
        public void convertFile(String filepath) throws TembooException {
            File img = new File(filepath);
            Bitmap bmp = BitmapFactory.decodeFile(img.getAbsolutePath());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100 , os);
            byte[] b = os.toByteArray();

            String googFileContent = Base64.encodeBase64String(b);
        }
        
        public void uploadFile() throws TembooException {
            Insert upload = new Insert(ts);

            InsertInputSet up_in = new InsertInputSet();

            up_in.set_ClientID(googClientID);
            up_in.set_ClientSecret(googClientSecret);
            up_in.set_ContentType(googContentType);
            up_in.set_RequestBody("{title : \"QuickPic.jpg\" }");
            up_in.set_RefreshToken(googRefreshToken);
            up_in.set_FileContent(googFileContent);
            up_in.set_AccessToken(googAccessToken);

            InsertResultSet up_finish = upload.execute(up_in);
            googAccessToken = up_finish.get_NewAccessToken();

        }
	
	// http://blog.doityourselfandroid.com/2011/08/08/improved-twitter-oauth-android/
	@SuppressLint("SetJavaScriptEnabled")
	private void promptUserToAuthorizeApp(String authURL) {
		final WebView webview = new WebView(this);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setVisibility(View.VISIBLE);
		
		webview.setWebViewClient(new WebViewClient() {  
		    @Override  
		    public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon)  
		    {  
		        handleCallback(url, webview);
		    }  
		});  
		
		webview.requestFocus(View.FOCUS_DOWN);
		webview.setOnTouchListener(new View.OnTouchListener()
		{
		    @Override
		    public boolean onTouch(View v, MotionEvent event)
		    {
		        switch (event.getAction())
		        {
		            case MotionEvent.ACTION_DOWN:
		            case MotionEvent.ACTION_UP:
		                if (!v.hasFocus())
		                {
		                    v.requestFocus();
		                }
		                break;
		        }
		        return false;
		    }
		});
		
		setContentView(webview);
		webview.loadUrl(authURL);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		handleCallback(null, null);
	}	
	
	// https://github.com/afollestad/Boid-Twitter-API/wiki/Receiving-OAuth-Callbacks-from-Android
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleCallback(null, null);
    }	
	
	public void handleCallback(String url, WebView webview) {	
		Uri uri = null;
		if(url == null) {
			uri = this.getIntent().getData();
		} else {
			Uri.Builder builder = new Uri.Builder();
			builder.appendPath(url);
			uri = builder.build();
		}
		
		// If the callback contains the callback URL, the user clicked "Allow" and we can proceed
		if ((uri != null && uri.toString().toLowerCase().startsWith(CALLBACK_URL.toLowerCase())) || ((url != null) && (url.contains("oauth_verifier")))) {
			webview.setVisibility(View.INVISIBLE);
		}
	}	
}
