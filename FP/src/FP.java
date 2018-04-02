// Uploads pictures to Dropbox and Google Drive

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

import org.apache.commons.codec.binary.Base64;

import com.temboo.Library.Dropbox.FilesAndMetadata.UploadFile;
import com.temboo.Library.Dropbox.FilesAndMetadata.UploadFile.UploadFileInputSet;
import com.temboo.Library.Dropbox.FilesAndMetadata.UploadFile.UploadFileResultSet;
import com.temboo.Library.Dropbox.OAuth.FinalizeOAuth;
import com.temboo.Library.Dropbox.OAuth.FinalizeOAuth.FinalizeOAuthInputSet;
import com.temboo.Library.Dropbox.OAuth.FinalizeOAuth.FinalizeOAuthResultSet;
import com.temboo.Library.Dropbox.OAuth.InitializeOAuth;
import com.temboo.Library.Dropbox.OAuth.InitializeOAuth.InitializeOAuthInputSet;
import com.temboo.Library.Dropbox.OAuth.InitializeOAuth.InitializeOAuthResultSet;
import com.temboo.Library.Google.Drive.Files.Insert;
import com.temboo.Library.Google.Drive.Files.Insert.InsertInputSet;
import com.temboo.Library.Google.Drive.Files.Insert.InsertResultSet;
import com.temboo.core.TembooSession;

public class FP {
	/**
    * @param args
    */
	public static void main(String[] args) throws Exception {

		String tAcct, tApp, tKey;	// Temboo
        String dKey, dSecret; 		// Dropbox
        String oToken, oSecret;		// OAuth

// Entering all account information.  This has all been hardcoded here because in the app, these
        // values would be stored in a preferences file so the user would not have to continually
        // re-enter them
        
        // get these by creating a Temboo account
        Scanner in = new Scanner(System.in);
        //System.out.println("Enter Temboo Account Name");
        //tAcct = in.nextLine();
        tAcct = "zwo24";
        //System.out.println("Enter Temboo App Name");
        //tApp = in.nextLine();
        tApp = "myApp";
        //System.out.println("Enter Temboo App Key");
        //tKey = in.nextLine();
        tKey = "b6a2a17eefda41e09e6f4ea910bd9723";

        // get these by creating a new app on Dropbox
        //System.out.println("Enter Dropbox App Key");
        //dKey = in.nextLine();
        dKey = "sajexuhejyx8tb6";
        //System.out.println("Enter Dropbox App Secret");
        //dSecret = in.nextLine();
        dSecret = "gw3jvpsg9j7itye";

// Does OAuth for Dropbox
        
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

// Starts the actual file upload by specifying the file, encoding it as necessary, and running the
        // choreos.  Displays a confirmation message when finished
        
        String uploadFilename = "C:/Users/ZWO/Desktop/Earth Angel.jpg";
        File uploadFile = new File(uploadFilename);
        
        FileInputStream inFile = new FileInputStream(uploadFile);
        byte fileData[] = new byte[(int) uploadFile.length()];
        inFile.read(fileData);

        // Converting Image byte array into Base64 String
        String imageDataString = Base64.encodeBase64URLSafeString(fileData);

        // finally, upload a new, blank file for __list, indicating a successful copy
        UploadFile uploadFileChoreo = new UploadFile(session);
        UploadFileInputSet uploadFileInputs = uploadFileChoreo.newInputSet();
        uploadFileInputs.set_AccessToken(oToken);
        uploadFileInputs.set_AccessTokenSecret(oSecret);
        uploadFileInputs.set_AppKey(dKey);
        uploadFileInputs.set_AppSecret(dSecret);
        uploadFileInputs.set_FileName(uploadFilename);
        uploadFileInputs.set_FileContents(imageDataString);
        UploadFileResultSet uploadFileResults = uploadFileChoreo.execute(uploadFileInputs);
        System.out.print("\nCopy Completed!");
        
// Google file upload.  OAuth is not necessary here because Google provides a refresh token that can
        // be stored in our preferences file and then reused.  All values are inserted into the
        // upload choreo immediately
        
        String gID, gSecret, gRedirect, gCode, gCallbackID, gRefresh;
        
        // get these by creating a new app with your Google account
        //System.out.println("Enter Google Client ID");
        //gID = in.nextLine();
        gID = "447445175444-3vk85tqaorl7idc0q58v4chu7a0cgm91.apps.googleusercontent.com";
        //System.out.println("Enter Google Client Secret");
        //gSecret = in.nextLine();
        gSecret = "xei_ECw9JiY__Ti9QsfCpAKA";
        //System.out.println("Enter Google Redirect URI");
        //gRedirect = in.nextLine();
        gRedirect = "https://zwo24.temboolive.com/callback/google";
        gRefresh = "1/yrFBywkpe1ChjHfYHIBR7YjHgsaoNk0E8HTqY6sTzp0MEudVrK5jSpoR30zcRFq6";
        
        session = new TembooSession(tAcct, tApp, tKey);
       
        // unneeded OAuth code
        /*com.temboo.Library.Google.OAuth.InitializeOAuth gInitializeOAuthChoreo = new com.temboo.Library.Google.OAuth.InitializeOAuth(session);
        com.temboo.Library.Google.OAuth.InitializeOAuth.InitializeOAuthInputSet gInitializeOAuthInputs = gInitializeOAuthChoreo.newInputSet();
        gInitializeOAuthInputs.set_ClientID(gID);
        gInitializeOAuthInputs.set_Scope("https://www.googleapis.com/auth/drive.file");

        // Execute Choreo
        com.temboo.Library.Google.OAuth.InitializeOAuth.InitializeOAuthResultSet gInitializeOAuthResults = gInitializeOAuthChoreo.execute(gInitializeOAuthInputs);
        
        userValidateURL = gInitializeOAuthResults.get_AuthorizationURL();
        System.out.println("Go here and hit allow, and hit enter when you're done...\n" + userValidateURL);
        in.nextLine(); // this waits until the user hits return

        com.temboo.Library.Google.OAuth.FinalizeOAuth gFinalizeOAuthChoreo = new com.temboo.Library.Google.OAuth.FinalizeOAuth(session);
        com.temboo.Library.Google.OAuth.FinalizeOAuth.FinalizeOAuthInputSet gFinalizeOAuthInputs = gFinalizeOAuthChoreo.newInputSet();
        gCallbackID = gInitializeOAuthResults.get_CallbackID();
        System.out.println(gCallbackID);
        gFinalizeOAuthInputs.set_ClientID(gID);
        gFinalizeOAuthInputs.set_ClientSecret(gSecret);
        gFinalizeOAuthInputs.set_CallbackID(gCallbackID);

        com.temboo.Library.Google.OAuth.FinalizeOAuth.FinalizeOAuthResultSet gFinalizeOAuthResults = gFinalizeOAuthChoreo.execute(gFinalizeOAuthInputs);
        oToken = gFinalizeOAuthResults.get_AccessToken();
        */
        
        // confirms that OAuth token was received
        System.out.print("\nGot refresh token: " + gRefresh);

        uploadFilename = "C:/Users/ZWO/Desktop/Earth Angel.jpg";
        uploadFile = new File(uploadFilename);
        
        inFile = new FileInputStream(uploadFile);
        byte fileData2[] = new byte[(int) uploadFile.length()];
        inFile.read(fileData2);

        // Converting Image byte array into Base64 String
        //imageDataString = Base64.encodeBase64URLSafeString(fileData2);
        imageDataString = new String(Base64.encodeBase64(fileData2));
        String safeString = imageDataString.replace('+','-').replace('/','_');
        
        Insert insertChoreo = new Insert(session);
        InsertInputSet insertInputs = insertChoreo.newInputSet();
        String fname = "{\"title\": \"Earth Angel.jpg\"}";
        insertInputs.set_RequestBody(fname);
        insertInputs.set_RefreshToken(gRefresh);
        insertInputs.set_ClientID(gID);
        insertInputs.set_ClientSecret(gSecret);
        insertInputs.set_FileContent(safeString);
        insertInputs.set_ContentType("image/jpg");

        // Execute Choreo
        InsertResultSet insertResults = insertChoreo.execute(insertInputs);
        System.out.print("\nCopy Completed!");
	}  
   
}
