package com.sebhastian.popularmoviesdatabase;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sebhastian.popularmoviesdatabase.adapter.MovieAdapter;
import com.sebhastian.popularmoviesdatabase.db.MovieContract;
import com.sebhastian.popularmoviesdatabase.model.Movie;
import com.sebhastian.popularmoviesdatabase.model.Movies;
import com.sebhastian.popularmoviesdatabase.network.NetworkService;
import com.sebhastian.popularmoviesdatabase.network.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Yonathan Sebhastian on 6/25/2017.
 */

public class MainActivityFragment extends Fragment implements MovieAdapter.Callbacks, LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private static final int MOVIE_LOADER_ID = 1;

    private static final String SAVED_MOVIES = "SAVED_MOVIES";
    private String mSortBy = Constants.SORT_POPULARITY;

    private static final String SAVED_SORT = "SAVED_SORT";

    @BindView(R.id.movie_list)
    RecyclerView mRecyclerViewMovieList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Movie> movies = mMovieAdapter.getMovies();
        if (movies!=null && !movies.isEmpty()){
            outState.putParcelableArrayList(SAVED_MOVIES, movies);
        }
        outState.putString(SAVED_SORT, mSortBy);
    }

    private NetworkService networkService;
    public MovieAdapter mMovieAdapter;

    public MainActivityFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerViewMovieList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }
        else{
            mRecyclerViewMovieList.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        }

        mMovieAdapter = new MovieAdapter(new ArrayList<Movie>(), this);
        mRecyclerViewMovieList.setAdapter(mMovieAdapter);

        networkService = ServiceGenerator.createService(NetworkService.class);

        if (savedInstanceState != null) {
            mSortBy = savedInstanceState.getString(SAVED_SORT);
            if (savedInstanceState.containsKey(SAVED_MOVIES)) {
                List<Movie> movies = savedInstanceState.getParcelableArrayList(SAVED_MOVIES);
                mMovieAdapter.add(movies);
            }
            if (mSortBy.equals(Constants.SORT_FAVES)) {
                getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
            }
        } else {
            if(isConnected()){
                fetchMovies(Constants.SORT_POPULARITY);
            }
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
        switch (mSortBy) {
            case Constants.SORT_POPULARITY:
                menu.findItem(R.id.sort_popularity).setChecked(true);
                break;
            case Constants.SORT_RATING:
                menu.findItem(R.id.sort_top_rated).setChecked(true);
                break;
            case Constants.SORT_FAVES:
                menu.findItem(R.id.sort_favorites).setChecked(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.action_refresh:
                fetchMovies(mSortBy);
                return true;
            case R.id.sort_popularity:
                fetchMovies(Constants.SORT_POPULARITY);
                mSortBy = Constants.SORT_POPULARITY;
                item.setChecked(true);
                return true;
            case R.id.sort_top_rated:
                fetchMovies(Constants.SORT_RATING);
                mSortBy = Constants.SORT_RATING;
                item.setChecked(true);
                return true;
            case R.id.sort_favorites:
                fetchMovies(Constants.SORT_FAVES);
                mSortBy = Constants.SORT_FAVES;
                item.setChecked(true);
                return true;
            default:
                return true;
        }
    }

    public void fetchMovies(String sort_order) {
        if (sort_order.equals(Constants.SORT_FAVES)){

            getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
        }
        else{
            Call<Movies> moviesCall = networkService.getMovies(sort_order);

            moviesCall.enqueue(new Callback<Movies>() {
                @Override
                public void onResponse(Call<Movies> call, Response<Movies> response) {
                    Log.e(LOG_TAG, response.body().toString());
                    List<Movie> movieList = response.body().getMovies();
                    mMovieAdapter.add(movieList);
                }
                @Override
                public void onFailure(Call<Movies> call, Throwable t) {
                    Toast.makeText(getActivity(), getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void detail(Movie movie, int position) {
        Intent detailMovie = new Intent(getActivity(), DetailActivity.class);
        detailMovie.putExtra("MOVIE_OBJ", movie);
        startActivity(detailMovie);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        mMovieAdapter.add(cursor);

    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }

}
