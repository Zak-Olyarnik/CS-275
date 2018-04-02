// Uses a Java BufferedReader with the openStream() command to get the html from the lab webpage
// The java.net library had exactly what we needed to do this

import java.net.*;  // for URL 
import java.io.*;
public class L1 {
	public static void main(String[] args) throws Exception {
        URL laburl = new URL("https://www.cs.drexel.edu/~wmm24/cs275_wi15/labs/wxunderground.html");	// opens URL
        BufferedReader in = new BufferedReader(new InputStreamReader(laburl.openStream()));        // look up the content in URL
        String htmlDisplay;
        while ((htmlDisplay = in.readLine()) != null)		//reads html into string and prints
            System.out.println(htmlDisplay);
        in.close();
    }
}
