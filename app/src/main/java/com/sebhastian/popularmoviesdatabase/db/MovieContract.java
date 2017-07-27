package com.sebhastian.popularmoviesdatabase.db;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Yonathan Sebhastian on 7/22/2017.
 */

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.sebhastian.popularmoviesdatabase";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movies";

    private MovieContract() {
    }

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "original_title";
        public static final String COLUMN_MOVIE_POSTER_PATH = "poster_path";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_MOVIE_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";

        public static final int COLUMN_MOVIE_ID_INDEX = 0;
        public static final int COLUMN_MOVIE_TITLE_INDEX = 1;
        public static final int COLUMN_MOVIE_POSTER_PATH_INDEX = 2;
        public static final int COLUMN_MOVIE_OVERVIEW_INDEX = 3;
        public static final int COLUMN_MOVIE_VOTE_AVERAGE_INDEX = 4;
        public static final int COLUMN_MOVIE_RELEASE_DATE_INDEX = 5;

        public static final String[] MOVIE_COLUMNS = {
                COLUMN_MOVIE_ID,
                COLUMN_MOVIE_TITLE,
                COLUMN_MOVIE_POSTER_PATH,
                COLUMN_MOVIE_OVERVIEW,
                COLUMN_MOVIE_VOTE_AVERAGE,
                COLUMN_MOVIE_RELEASE_DATE
        };

    }
}
