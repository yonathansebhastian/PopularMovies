package com.sebhastian.popularmoviesdatabase;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.sebhastian.popularmoviesdatabase.adapter.MovieAdapter;
import com.sebhastian.popularmoviesdatabase.model.Movie;
import com.sebhastian.popularmoviesdatabase.model.Movies;
import com.sebhastian.popularmoviesdatabase.network.ServiceGenerator;
import com.sebhastian.popularmoviesdatabase.network.NetworkService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private NetworkService networkService;
    private List<Movie> movies;
    public MovieAdapter mMovieAdapter;
    public ProgressDialog mProgressDialog;

    public MainActivityFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        movies = new ArrayList<>();

        mMovieAdapter = new MovieAdapter(getActivity(), movies);
        mProgressDialog = new ProgressDialog(getActivity());
        GridView movieGrid = (GridView) rootView.findViewById(R.id.movies_grid);
        movieGrid.setAdapter(mMovieAdapter);
        networkService = ServiceGenerator.createService(NetworkService.class);

        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent detailMovie = new Intent(getActivity(), DetailActivity.class);
                detailMovie.putExtra("MOVIE_OBJ", movies.get(i));
                startActivity(detailMovie);
            }
        });

        if(isConnected()){
            fetchMovies(Constants.SORT_POPULARITY);
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
                fetchMovies(Constants.SORT_POPULARITY);
                return true;
            case R.id.action_popularity:
                fetchMovies(Constants.SORT_POPULARITY);
                return true;
            case R.id.action_top:
                fetchMovies(Constants.SORT_RATING);
                return true;
            default:
                return true;
        }
    }

    public void fetchMovies(String sort_order) {
        Call<Movies> moviesCall = networkService.getMovies(sort_order);

        moviesCall.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                Log.e(LOG_TAG, response.body().toString());
                List<Movie> movieList = response.body().getMovies();

                mMovieAdapter.clear();
                for (Movie movie : movieList) {
                    mMovieAdapter.add(movie);
                }
            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t) {
                Toast.makeText(getActivity(), getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
