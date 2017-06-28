package com.sebhastian.popularmoviesdatabase;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yonathan Sebhastian on 6/25/2017.
 */

public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    final static String API_KEY = "you api here";
    final static String BASE_URL = "https://api.themoviedb.org/";
    final static String SORT_TOP_RATED = "3/movie/top_rated";
    final static String SORT_POPULAR = "3/movie/popular";

    final static String PARAM_PAGE = "page";
    final static String PARAM_API = "api_key";
    final static String pageOne = "1";

    public MovieAdapter mMovieAdapter;
    public ProgressDialog mProgressDialog;

    public MainActivityFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final List<Movie> movies = new ArrayList<>();
        mMovieAdapter = new MovieAdapter(getActivity(), movies);
        mProgressDialog = new ProgressDialog(getActivity());
        GridView movieGrid = (GridView) rootView.findViewById(R.id.movies_grid);
        movieGrid.setAdapter(mMovieAdapter);

        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent detailMovie = new Intent(getActivity(), DetailActivity.class);
                detailMovie.putExtra("MOVIE_OBJ", movies.get(i));
                startActivity(detailMovie);
            }
        });

        if(isConnected()){
            new MoviesDBQueryTask().execute(SORT_POPULAR);
        }

        return rootView;
    }

    public boolean isConnected(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected() || !activeNetworkInfo.isAvailable()) {
            Toast.makeText(getActivity(), getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.action_refresh:
                new MoviesDBQueryTask().execute(SORT_POPULAR);
                return true;
            case R.id.action_popularity:
                new MoviesDBQueryTask().execute(SORT_POPULAR);
                return true;
            case R.id.action_top:
                new MoviesDBQueryTask().execute(SORT_TOP_RATED);
                return true;
            default:
                return true;
        }
    }

    public class MoviesDBQueryTask extends AsyncTask<String, Void, List<Movie>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog.setMessage("Please wait");
            mProgressDialog.show();
        }

        @Override
        protected List<Movie> doInBackground(String... strings) {
            HttpURLConnection urlConnection=null;
            String path_sort = strings[0];
            BufferedReader reader = null;
            String JsonResponse = null;
            List<Movie> movieList = new ArrayList<>();

            try {
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .path(path_sort)
                        .appendQueryParameter(PARAM_API, API_KEY)
                        .appendQueryParameter(PARAM_PAGE, pageOne)
                        .build();
                String requestUrl = builtUri.toString();
                URL Url = new URL(requestUrl);
                urlConnection = (HttpURLConnection) Url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder builder = new StringBuilder();

                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Adds '\n' at last line if not already there.
                    // This supposedly makes it easier to debug.
                    builder.append(line).append("\n");
                }

                if (builder.length() == 0) {
                    // No data found. Nothing more to do here.
                    return null;
                }
                JsonResponse = builder.toString();
            }
            catch (Exception e){
                Log.e(LOG_TAG, e.getMessage(), e);
                Toast.makeText(getActivity(), getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                return null;
            }
            finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
                if (reader != null){
                    try {
                        reader.close();
                    }
                    catch (Exception e){
                        Log.e(LOG_TAG, e.getMessage(), e);
                    }
                }
                try{
                    movieList = formatMovieData(JsonResponse);
                }
                catch (JSONException e){
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
            }
            return movieList;
        }

        @Override
        protected void onPostExecute(List<Movie>  movies) {
            super.onPostExecute(movies);
            if(mProgressDialog.isShowing())mProgressDialog.dismiss();
            mMovieAdapter.clear();
            for (Movie movie: movies){
                mMovieAdapter.add(movie);
            }
        }
    }

    private List<Movie>  formatMovieData(String json) throws JSONException{
        List<Movie> movies = new ArrayList<>();
        final String TAG_RESULTS = "results";
        final String TAG_ORIGINAL_TITLE = "original_title";
        final String TAG_POSTER_PATH = "poster_path";
        final String TAG_OVERVIEW = "overview";
        final String TAG_VOTE_AVERAGE = "vote_average";
        final String TAG_RELEASE_DATE = "release_date";

        JSONObject movieJson = new JSONObject(json);
        JSONArray results = movieJson.getJSONArray(TAG_RESULTS);

        for (int i =0; i<results.length(); i++){
            Movie movie = new Movie();
            JSONObject movieInfo = results.getJSONObject(i);

            // Store data in movie object
            movie.setOriginalTitle(movieInfo.getString(TAG_ORIGINAL_TITLE));
            movie.setImageUrl(movieInfo.getString(TAG_POSTER_PATH));
            movie.setOverview(movieInfo.getString(TAG_OVERVIEW));
            movie.setVoteAvg(movieInfo.getString(TAG_VOTE_AVERAGE));
            movie.setReleaseDate(movieInfo.getString(TAG_RELEASE_DATE));
            movies.add(movie);
        }
        return movies;
    }
}
