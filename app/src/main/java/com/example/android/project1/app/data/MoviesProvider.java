package com.example.android.project1.app.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Admin-HHE on 7/9/2015.
 */
public class MoviesProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    static final int MOVIES_LIST = 100;
//    static final int MOVIE_DETAILS = 101;
//    static final int POSTER = 102;

    private static final SQLiteQueryBuilder sMoviesQueryBuilder = new SQLiteQueryBuilder();

    // movies.post
    private static final String sPosterSelection = null;

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        switch(sUriMatcher.match(uri)) {
            case MOVIES_LIST:
                cursor = db.query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
//            case MOVIE_DETAILS:
//                cursor = db.query(
//                        MoviesContract.MoviesEntry.TABLE_NAME,
//                        new String[]{MoviesContract.MoviesEntry._ID},
//                        null,null,null,null,null
//
//                        );
//                break;
//            case POSTER:
//                cursor = sMoviesQueryBuilder.query(
//                        db,
//                        new String[]{
//                                MoviesContract.MoviesEntry._ID,
//                                MoviesContract.MoviesEntry.COLUMN_POSTER_PATH},
//                        "",
//                        new String[]{
//
//                        },
//                        null,
//                        null,
//                        sortOrder);
//                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch(sUriMatcher.match(uri)) {
            case MOVIES_LIST:
                return MoviesContract.MoviesEntry.CONTENT_TYPE;
//            case MOVIE_DETAILS:
//                return MoviesContract.MoviesEntry.CONTENT_ITEM_TYPE;
//            case POSTER:
//                return "image/jpeg";
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case MOVIES_LIST:
                long _id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, values);
                if(_id > 0) {
                    returnUri = MoviesContract.MoviesEntry.buildMovieUri(_id);
                }
                else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;
        if(selection == null) {
            selection = "1";
        }
        switch(sUriMatcher.match(uri)) {
            case MOVIES_LIST:
                rowsDeleted = db.delete(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;
        if(selection == null) {
            selection = "1";
        }
        switch(sUriMatcher.match(uri)) {
            case MOVIES_LIST:
                rowsUpdated = db.update(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_MOVIE, MOVIES_LIST); // type dir
//        matcher.addURI(authority, MoviesContract.PATH_MOVIE + "/#", MOVIE_DETAILS); // type item
//        matcher.addURI(authority, MoviesContract.PATH_MOVIE + "/*", POSTER); // poster type image/jpeg

        return matcher;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch(sUriMatcher.match(uri)) {
            case MOVIES_LIST:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for(ContentValues value : values) {
                        long _id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, value);
                        if(_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}