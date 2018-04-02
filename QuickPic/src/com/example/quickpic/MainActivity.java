package com.example.quickpic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


//import org.apache.commons.codec.binary.Base64;
//import org.json.JSONObject;


import com.temboo.Library.Google.Drive.Files.Insert;
import com.temboo.Library.Google.Drive.Files.Insert.InsertInputSet;
import com.temboo.Library.Google.Drive.Files.Insert.InsertResultSet;
import com.temboo.core.TembooException;
import com.temboo.core.TembooSession;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	
	// label our logs "QuickPic"
    private static String logtag = "QuickPic";
    // tells us which camera to take a picture from
    private static int TAKE_PICTURE = 1;
    // empty variable to hold our image Uri once we store it
    private Uri imageUri;
    File photo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("TembooAccount", "zwo24");
		editor.putString("TembooApp", "myApp");
		editor.putString("TembooKey", "b6a2a17eefda41e09e6f4ea910bd9723");
		editor.putString("DropboxKey", "sajexuhejyx8tb6");
		editor.putString("DropboxSecret", "gw3jvpsg9j7itye");
		editor.putString("DropboxRefresh", "");
		editor.putString("GoogleID", "447445175444-3vk85tqaorl7idc0q58v4chu7a0cgm91.apps.googleusercontent.com");
		editor.putString("GoogleSecret", "xei_ECw9JiY__Ti9QsfCpAKA");
		editor.putString("GoogleRefresh", "1/yrFBywkpe1ChjHfYHIBR7YjHgsaoNk0E8HTqY6sTzp0MEudVrK5jSpoR30zcRFq6");
		editor.commit();
		
		
		 // look for the button we set in the view
        Button cameraButton = (Button)findViewById(R.id.button_camera);
        // set a listener on the button
        cameraButton.setOnClickListener(cameraListener);
        Button googleButton = (Button)findViewById(R.id.button1);
        googleButton.setOnClickListener(gUploadListener);
    }
	
	// set a new listener
    private OnClickListener cameraListener = new OnClickListener() {
        public void onClick(View v) {
            // open the camera and pass in the current view
            takePhoto(v);
        }
    };
 // set a new google upload listener
    private OnClickListener gUploadListener = new OnClickListener() {
        public void onClick(View v) {
            // open the camera and pass in the current view
            //takePhoto(v);
        	GoogleUploadAsyncTask myTask = new GoogleUploadAsyncTask();
        	myTask.execute();
        	
        }
    };
    
private class GoogleUploadAsyncTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected Void doInBackground(Void... arg0) {
			
			EditText textField = (EditText)findViewById(R.id.editText1);
			
			SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
			String tAcct = sharedPref.getString("TembooAccount", null);
			String tKey = sharedPref.getString("TembooKey", null);
			String tApp = sharedPref.getString("TembooApp", null);
			String gID = sharedPref.getString("GoogleID", null);
			String gSecret = sharedPref.getString("GoogleSecret", null);
			String gRefresh = sharedPref.getString("GoogleRefresh", null);
			String filename = textField.getText().toString();
			
			try{
			TembooSession session = new TembooSession(tAcct, tApp, tKey);

	        //String uploadFilename = "C:/Users/ZWO/Desktop/Earth Angel.jpg";
	        //File uploadFile = new File(uploadFilename);
			//File uploadFile = new File(photo);
	        
			File pic = new File("/storage/emulated/0/Pictures/pic.jpg");
			
	        FileInputStream inFile = new FileInputStream(pic);
	        byte fileData[] = new byte[(int) pic.length()];
	        inFile.read(fileData);

	        // Converting Image byte array into Base64 String
	        //String safeString = Base64.encodeBase64URLSafeString(fileData);
	        String imageDataString = new String(Base64.encodeToString(fileData, Base64.DEFAULT));
	        String safeString = imageDataString.replace('+','-').replace('/','_');
	        
	        Insert insertChoreo = new Insert(session);
	        InsertInputSet insertInputs = insertChoreo.newInputSet();
	        insertInputs.set_RefreshToken(gRefresh);
	        insertInputs.set_ClientID(gID);
	        insertInputs.set_ClientSecret(gSecret);
	        insertInputs.set_FileContent(safeString);
	        insertInputs.set_ContentType("image/jpg");
	        
	        if(filename.equals("")){
	        	filename = "New Upload";
	        }
	        String fname = "{\"title\": \"" + filename + ".jpg\"}";
	        insertInputs.set_RequestBody(fname);
	        
	        // Execute Choreo
	        InsertResultSet insertResults = insertChoreo.execute(insertInputs);
	        //System.out.print("\nCopy Completed!");
			}catch(IOException e){
				e.printStackTrace();
			} catch (TembooException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		} 

		@Override
		protected void onPostExecute(Void arg0) {
			Toast.makeText(getBaseContext(), "Copy Completed!", Toast.LENGTH_SHORT).show();
		}
	}	// async task
    
    
    
    
    
    
    public void takePhoto(View v) {
        // tell the phone we want to use the camera
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // create a new temp file called pic.jpg in the "pictures" storage area of the phone
        photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "pic.jpg");
        // take the return data and store it in the temp file "pic.jpg"
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        // stor the temp photo uri so we can find it later
        imageUri = Uri.fromFile(photo);
        // start the camera
        startActivityForResult(intent, TAKE_PICTURE);
    }
    
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	// override the original activity result function
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // call the parent
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
        // if the requestCode was equal to our camera code (1) then...
        case 1:
            // if the user took a photo and selected the photo to use
            if(resultCode == Activity.RESULT_OK) {
                // get the image uri from earlier
                Uri selectedImage = imageUri;
                // notify any apps of any changes we make
                getContentResolver().notifyChange(selectedImage, null);
                // get the imageView we set in our view earlier
                ImageView imageView = (ImageView)findViewById(R.id.image_view_camera);
                //imageView.setRotation(90);
                // create a content resolver object which will allow us to access the image file at the uri above
                ContentResolver cr = getContentResolver();
                // create an empty bitmap object
                Bitmap bitmap;
                try {
                    // get the bitmap from the image uri using the content resolver api to get the image
                    bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
                    // set the bitmap to the image view
                    imageView.setImageBitmap(bitmap);
                    // notify the user
                    Toast.makeText(MainActivity.this, selectedImage.toString(), Toast.LENGTH_LONG).show();
                } catch(Exception e) {
                    // notify the user
                    Toast.makeText(MainActivity.this, "failed to load", Toast.LENGTH_LONG).show();
                    Log.e(logtag, e.toString());
                }
            }
        }
    }
}
