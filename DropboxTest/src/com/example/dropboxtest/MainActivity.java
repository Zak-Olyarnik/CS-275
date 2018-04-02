package com.example.dropboxtest;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.temboo.Library.Google.Drive.Files.Insert;
import com.temboo.Library.Google.Drive.Files.Insert.InsertInputSet;
import com.temboo.Library.Google.Drive.Files.Insert.InsertResultSet;
import com.temboo.Library.Utilities.Encoding.Base64Encode;
import com.temboo.Library.Utilities.Encoding.Base64Encode.Base64EncodeInputSet;
import com.temboo.Library.Utilities.Encoding.Base64Encode.Base64EncodeResultSet;
import com.temboo.core.TembooSession;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An AsyncTask that will be used to encode a string and upload it as a file to DropBox
 */
class DropboxTask extends AsyncTask<Void, Void, String> {

    private TextView textView;

    // The name of your Temboo Dropbox credential.
    private static final String DROPBOX_CREDENTIAL = "YOUR_DROPBOX_CREDENTIAL_NAME";

    // Set file name and contents.
    // Note the date and file extension are being added to the file name in the Dropbox input set.
    private static final String FILE_NAME = "hello"; 
    private static final String FILE_CONTENTS = "You just made this file with Java and Temboo! ";

    public DropboxTask(TextView textView){
        this.textView = textView;
    }

    @Override
    protected String doInBackground(Void... arg0) {
        try {
            // Instantiate the Choreo, using a previously instantiated TembooSession object, eg:
            TembooSession session = new TembooSession("zwo24", "myApp", "b6a2a17eefda41e09e6f4ea910bd9723");

            // Instantiate the Utilities.Encoding.Base64Encode Choreo, using the session object.
            Base64Encode base64EncodeChoreo = new Base64Encode(session);

            // Get an InputSet object for Utilities.Encoding.Base64Encode Choreo.
            Base64EncodeInputSet base64EncodeInputs = base64EncodeChoreo.newInputSet();

            // Set inputs for Utilities.Encoding.Base64Encode Choreo.
            base64EncodeInputs.set_Text(FILE_CONTENTS);

            // Execute Utilities.Encoding.Base64Encode Choreo.
            Base64EncodeResultSet base64EncodeResults = base64EncodeChoreo.execute(base64EncodeInputs);

            // Generate and format a timestamp that we can put at the end of the file name.
           // Date date = new Date();
           // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
           // String formattedDate = sdf.format(date);

            Insert insertChoreo = new Insert(session);
	        InsertInputSet insertInputs = insertChoreo.newInputSet();
	        insertInputs.set_RequestBody("{\"title\": \"myTitle.jpg\"}");
	        insertInputs.set_RefreshToken(gRefresh);
	        insertInputs.set_ClientID("447445175444-3vk85tqaorl7idc0q58v4chu7a0cgm91.apps.googleusercontent.com");
	        insertInputs.set_ClientSecret("xei_ECw9JiY__Ti9QsfCpAKA");
	        insertInputs.set_FileContent(base64EncodeResults.get_Base64EncodedText());
	        insertInputs.set_ContentType("image/jpg");

	        // Execute Choreo
	        InsertResultSet insertResults = insertChoreo.execute(insertInputs);
            
            
           /* // Instantiate the Dropbox.FilesAndMetadata.UploadFile Choreo, using the session object.
            UploadFile uploadFileChoreo = new UploadFile(session);

            // Get an InputSet object for Dropbox.FilesAndMetadata.UploadFile Choreo.
            UploadFileInputSet uploadFileInputs = uploadFileChoreo.newInputSet();

            // Set Dropbox credentials.
            uploadFileInputs.setCredential(DROPBOX_CREDENTIAL);

            // Set inputs for Dropbox.FilesAndMetadata.UploadFile Choreo.
            uploadFileInputs.set_FileName(FILE_NAME + "_" + formattedDate + ".txt");
            uploadFileInputs.set_Root("sandbox");
            uploadFileInputs.set_FileContents(base64EncodeResults.get_Base64EncodedText());

            // Execute Dropbox.FilesAndMetadata.UploadFile Choreo.
            UploadFileResultSet uploadFileResults = uploadFileChoreo.execute(uploadFileInputs);
*/
            return "File upload success: " + insertResults.get_Response();
        } catch(Exception e) {
            // if an exception occurred, log it
            Log.e(this.getClass().toString(), e.getMessage());
        }
        return null;
    }

    protected void onPostExecute(String result) {
        try {
            // Update UI
            textView.setText(result);
        } catch(Exception e) {
            // if an exception occurred, show an error message
            Log.e(this.getClass().toString(), e.getMessage());
        }
    }
}