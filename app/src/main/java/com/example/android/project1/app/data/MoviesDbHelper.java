package com.example.android.project1.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.project1.app.data.MoviesContract.MoviesEntry;
import com.example.android.project1.app.data.MoviesContract.TrailersEntry;
import com.example.android.project1.app.data.MoviesContract.ReviewsEntry;

/**
 * Created by Admin-HHE on 7/9/2015.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 5;

    static final String DATABASE_NAME = "movie.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " +
                MoviesEntry.TABLE_NAME + " (" +
                MoviesEntry._ID + " INTEGER PRIMARY KEY," +
                MoviesEntry.COLUMN_ADULT + " INTEGER NOT NULL, " +
                MoviesEntry.COLUMN_BACKDROP_PATH + " INTEGER NOT NULL, " +
                MoviesEntry.COLUMN_ID + " INTEGER NOT NULL UNIQUE, " +
                MoviesEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_VIDEO + " INTEGER NOT NULL, " +
                MoviesEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MoviesEntry.COLUMN_VOTE_COUNT + " REAL NOT NULL, " +
                MoviesEntry.COLUMN_FAVORITE + " INTEGER, " +
                " UNIQUE (" + MoviesEntry.COLUMN_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_TRAILER_TABLE =
                "CREATE TABLE " +
                TrailersEntry.TABLE_NAME + " (" +
                TrailersEntry._ID + " INTEGER PRIMARY KEY," +
                TrailersEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                TrailersEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL, " +
                TrailersEntry.COLUMN_LANG + " TEXT NOT NULL, " +
                TrailersEntry.COLUMN_KEY + " TEXT NOT NULL, " +
                TrailersEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                TrailersEntry.COLUMN_SITE + " TEXT NOT NULL, " +
                TrailersEntry.COLUMN_SIZE + " INT NOT NULL, " +
                TrailersEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                " UNIQUE (" + TrailersEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_REVIEW_TABLE =
                "CREATE TABLE " +
                ReviewsEntry.TABLE_NAME + " (" +
                ReviewsEntry._ID + " INTEGER PRIMARY KEY," +
                ReviewsEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                ReviewsEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL, " +
                ReviewsEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                ReviewsEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                ReviewsEntry.COLUMN_URL + " TEXT NOT NULL, " +
                " UNIQUE (" + ReviewsEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_TRAILER_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrailersEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewsEntry.TABLE_NAME);
        onCreate(db);
    }
}