package com.javacodegeeks.android.apps.moviesearchapp;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.javacodegeeks.android.apps.moviesearchapp.model.Movie;
import com.javacodegeeks.android.apps.moviesearchapp.ui.MoviesAdapter;

public class MoviesListActivity extends ListActivity {
	
	private static final String IMDB_BASE_URL = "http://m.imdb.com/title/";
	
	private ArrayList<Movie> moviesList = new ArrayList<Movie>();
	private MoviesAdapter moviesAdapter;
	
	@SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_layout);

        moviesAdapter = new MoviesAdapter(this, R.layout.movie_data_row, moviesList);
        moviesList = (ArrayList<Movie>) getIntent().getSerializableExtra("movies");
        
        setListAdapter(moviesAdapter);
        
        if (moviesList!=null && !moviesList.isEmpty()) {
        	
        	moviesAdapter.notifyDataSetChanged();
        	moviesAdapter.clear();
    		for (int i = 0; i < moviesList.size(); i++) {
    			moviesAdapter.add(moviesList.get(i));
    		}
        }
        
        moviesAdapter.notifyDataSetChanged();
        
    }
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		super.onListItemClick(l, v, position, id);
		Movie movie = moviesAdapter.getItem(position);
		
		String imdbId = movie.imdbId;
		if (imdbId==null || imdbId.length()==0) {
			longToast(getString(R.string.no_imdb_id_found));
			return;
		}
		
		String imdbUrl = IMDB_BASE_URL + movie.imdbId;
		
		Intent imdbIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(imdbUrl));				
		startActivity(imdbIntent);
		
	}
	
	public void longToast(CharSequence message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
}
