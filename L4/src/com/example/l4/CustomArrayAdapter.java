package com.example.l4;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList; 

import android.content.Context; 
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater; 
import android.view.View; 
import android.view.ViewGroup; 
import android.widget.ArrayAdapter; 
import android.widget.ImageView;
import android.widget.TextView; 

// imports the list_row.xml formatting into every row of the List View in activity_main.xml
// src: https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
public class CustomArrayAdapter extends ArrayAdapter<Weather> { 
	public CustomArrayAdapter(Context context, ArrayList<Weather> users) { 
         super(context, 0, users); 
	} 
 
	// global variables to avoid scope problems
	Weather weather;
    View convertView;
    
	@Override
      public View getView(int position, View convertViewIn, ViewGroup parent) { 
         // Get the data item for this position 
    	  weather = getItem(position);
    	  convertView = convertViewIn;
    	  
         // Check if an existing view is being reused, otherwise inflate the view 
         if (convertView == null) { 
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row, parent, false); 
         } 
         
         // Lookup view for data population 
         TextView wTime = (TextView) convertView.findViewById(R.id.time); 
         TextView wDescription = (TextView) convertView.findViewById(R.id.description);
         TextView wTemperature = (TextView) convertView.findViewById(R.id.temperature); 
         TextView wHumidity = (TextView) convertView.findViewById(R.id.humidity);
         
         // Populate the data into the template view using the data object 
         wTime.setText(weather.time); 
         wDescription.setText(weather.description);
         wTemperature.setText(weather.temperature); 
         wHumidity.setText(weather.humidity);
         
         // image must be loaded separately, in an async task, because it requires another http url connection
         ImageLoadAsyncTask myTask = new ImageLoadAsyncTask();
         myTask.execute();
         
         // Return the completed view to render on screen 
         return convertView; 
     } // getView
    
	// actually populates image thumbnails
	private void populate(Bitmap bmp) { 
		ImageView wIcon = (ImageView) convertView.findViewById(R.id.thumbnail);
		wIcon.setImageBitmap(bmp);
	}
	
	// retrieves image thumbnails by converting url location to bitmap
	// src: http://stackoverflow.com/questions/4509912/is-it-possible-to-use-bitmapfactory-decodefile-method-to-decode-a-image-from-htt
	private class ImageLoadAsyncTask extends AsyncTask<Void, Void, Void> {
  		
    	  Bitmap bmp;
  		@Override
  		protected Void doInBackground(Void... arg0) {
  			try{
        	    String url1 = weather.icon;
        	    URL ulrn = new URL(url1);
        	    HttpURLConnection con = (HttpURLConnection)ulrn.openConnection();
        	    InputStream is = con.getInputStream();
        	    bmp = BitmapFactory.decodeStream(is);
        	} catch(Exception e) {
        	}
  			return null;
  		}
  		@Override
		protected void onPostExecute(Void arg0) {
  			populate(bmp);
		}
      } // async task
 }	// custom array adapter
