package com.example.android.project1.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Admin-HHE on 7/9/2015.
 */
public class MoviesContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.project1.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final class MoviesEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        //public static final String COLUMN_GENRE_IDS = "genre_ids";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_POPULARITY= "popularity";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_VIDEO = "video";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";

        public static final String[] DETAIL_COLUMNS = {
                "_ID",
                COLUMN_ADULT,
                COLUMN_BACKDROP_PATH,
                COLUMN_ID,
                COLUMN_ORIGINAL_LANGUAGE,
                COLUMN_ORIGINAL_TITLE,
                COLUMN_OVERVIEW,
                COLUMN_RELEASE_DATE,
                COLUMN_POSTER_PATH,
                COLUMN_POPULARITY,
                COLUMN_TITLE,
                COLUMN_VIDEO,
                COLUMN_VOTE_AVERAGE,
                COLUMN_VOTE_COUNT
        };

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}