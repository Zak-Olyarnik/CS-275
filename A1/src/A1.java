// Dropbox file downloader

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;
import org.apache.commons.codec.binary.Base64;

import com.temboo.Library.Dropbox.FilesAndMetadata.GetFile;
import com.temboo.Library.Dropbox.FilesAndMetadata.GetFile.GetFileInputSet;
import com.temboo.Library.Dropbox.FilesAndMetadata.GetFile.GetFileResultSet;
import com.temboo.Library.Dropbox.FilesAndMetadata.UploadFile;
import com.temboo.Library.Dropbox.FilesAndMetadata.UploadFile.UploadFileInputSet;
import com.temboo.Library.Dropbox.FilesAndMetadata.UploadFile.UploadFileResultSet;
import com.temboo.Library.Dropbox.OAuth.FinalizeOAuth;
import com.temboo.Library.Dropbox.OAuth.FinalizeOAuth.FinalizeOAuthInputSet;
import com.temboo.Library.Dropbox.OAuth.FinalizeOAuth.FinalizeOAuthResultSet;
import com.temboo.Library.Dropbox.OAuth.InitializeOAuth;
import com.temboo.Library.Dropbox.OAuth.InitializeOAuth.InitializeOAuthInputSet;
import com.temboo.Library.Dropbox.OAuth.InitializeOAuth.InitializeOAuthResultSet;
import com.temboo.core.TembooSession;

public class A1 {
	/**
    * @param args
    */
	public static void main(String[] args) throws Exception {

		String tAcct, tApp, tKey;	// Temboo
        String dKey, dSecret; 		// Dropbox
        String oToken, oSecret;		// OAuth

        // get these by creating a Temboo account
        Scanner in = new Scanner(System.in);
        System.out.println("Enter Temboo Account Name");
        tAcct = in.nextLine();
        System.out.println("Enter Temboo App Name");
        tApp = in.nextLine();
        System.out.println("Enter Temboo App Key");
        tKey = in.nextLine();

        // get these by creating a new app on Dropbox
        System.out.println("Enter Dropbox App Key");
        dKey = in.nextLine();
        System.out.println("Enter Dropbox App Secret");
        dSecret = in.nextLine();

        // start Temboo to perform OAuth
        TembooSession session = new TembooSession(tAcct, tApp, tKey);
        InitializeOAuth initializeOAuthChoreo = new InitializeOAuth(session);  
        InitializeOAuthInputSet initializeOAuthInputs = initializeOAuthChoreo.newInputSet();
        initializeOAuthInputs.set_AccountName(tAcct);
        initializeOAuthInputs.set_AppKeyName(tApp);
        initializeOAuthInputs.set_AppKeyValue(tKey);
        initializeOAuthInputs.set_DropboxAppKey(dKey);
        initializeOAuthInputs.set_DropboxAppSecret(dSecret);
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
        finalizeOAuthInputs.set_DropboxAppKey(dKey);
        finalizeOAuthInputs.set_DropboxAppSecret(dSecret);
        finalizeOAuthInputs.set_CallbackID(callbackID);
        finalizeOAuthInputs.set_OAuthTokenSecret(oAuthTokenSecret);

        FinalizeOAuthResultSet finalizeOAuthResults = finalizeOAuthChoreo.execute(finalizeOAuthInputs);
        oToken = finalizeOAuthResults.get_AccessToken();
        oSecret = finalizeOAuthResults.get_AccessTokenSecret();

        // confirms that OAuth token was received
        System.out.println("Got oauth token: " + oToken);

        
        // gets the __list file, which describes the rest of the file operations
        GetFile getFileChoreo = new GetFile(session);
        GetFileInputSet getFileInputs = getFileChoreo.newInputSet();
        getFileInputs.set_AccessToken(oToken);
        getFileInputs.set_AccessTokenSecret(oSecret);
        getFileInputs.set_AppKey(dKey);
        getFileInputs.set_AppSecret(dSecret);
        getFileInputs.set_Path("/move/__list");
        GetFileResultSet getFileResults = getFileChoreo.execute(getFileInputs);
        
        // the following code converts the file from the Base64 format stored on Dropbox to ASCII
        // source: http://stackoverflow.com/questions/11544568/decoding-a-base64-string-in-java
        byte[] decoded = Base64.decodeBase64(getFileResults.get_Response().toString());
        String list = new String(decoded, "UTF-8") + "\n";
        
        // checks to see if __list is blank.  The assignment says to take the line out of the file
        // if a successful copy is made (so that it is not repeated), so a second run of the procedure
        // should encounter an empty __list
        if (list.trim().isEmpty()){
        	System.out.print("No files listed to copy.");
        	System.exit(0);
        }
        
        // otherwise, proceed with copy.  Reads the converted string into an array, where even indexes
        // store filenames and odd indexes store the corresponding copy-to path
        String[] listArray = list.split("\\s+");
        
        // iterate through array, setting inputs to get each individual file's data
        for(int i=0; i<listArray.length; i=i+2){
        	 getFileInputs.set_AccessToken(oToken);
             getFileInputs.set_AccessTokenSecret(oSecret);
             getFileInputs.set_AppKey(dKey);
             getFileInputs.set_AppSecret(dSecret);
             getFileInputs.set_Path("/move/" + listArray[i]);
             getFileResults = getFileChoreo.execute(getFileInputs);
             
             // decode the same way as before, and write to a new file in the specified location
             decoded = Base64.decodeBase64(getFileResults.get_Response().toString());
             String fileText = new String(decoded, "UTF-8") + "\n";
             PrintWriter output = new PrintWriter(new FileWriter(listArray[i+1] + "\\" + listArray[i])); 
             output.print(fileText); 
             output.close(); 
        }
        
        // finally, upload a new, blank file for __list, indicating a successful copy
        UploadFile uploadFileChoreo = new UploadFile(session);
        UploadFileInputSet uploadFileInputs = uploadFileChoreo.newInputSet();
        uploadFileInputs.set_AccessToken(oToken);
        uploadFileInputs.set_AccessTokenSecret(oSecret);
        uploadFileInputs.set_AppKey(dKey);
        uploadFileInputs.set_AppSecret(dSecret);
        uploadFileInputs.set_FileName("__list");
        uploadFileInputs.set_Folder("/move/");
        uploadFileInputs.set_FileContents("Cg==");	// Base64 for CRLF
        UploadFileResultSet uploadFileResults = uploadFileChoreo.execute(uploadFileInputs);
        System.out.print("\nCopy Completed!");
	}
}
