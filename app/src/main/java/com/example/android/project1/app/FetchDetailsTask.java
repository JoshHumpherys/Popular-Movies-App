package com.example.android.project1.app;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.project1.app.data.MoviesContract;
import com.example.android.project1.app.data.MoviesContract.TrailersEntry;
import com.example.android.project1.app.data.MoviesContract.ReviewsEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Admin-HHE on 7/21/2015.
 */
public class FetchDetailsTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMoviesTask.class.getName();

    private static final String API_KEY = "4be6ba35033283805916b8896c01b040";

    private final Context mContext;

    public interface Callback {
        public void onInsertComplete();
    }

    public FetchDetailsTask(Context context) {
        mContext = context;
    }

    public void insertJsonIntoDatabase(String rawJsonStrTrailers, String rawJsonStrReviews)
            throws JSONException {

        // Outermost JSON objects
        final String MOVIE_ID = "id";
        final String RESULTS = "results";
        final String REVIEWS_PAGE = "page"; // only for reviews json

        // JSON objects contained in RESULTS object of trailers request
        final String TRAILER_ID = "id";
        final String LANG = "iso_639_1";
        final String KEY = "key"; // to be used with trailer intent
        final String NAME = "name";
        final String SITE = "site";
        final String SIZE = "size"; // int
        final String TYPE = "type";

        // JSON objects contained in RESULTS object of reviews request
        final String REVIEW_ID = "id";
        final String AUTHOR = "author";
        final String CONTENT = "content";
        final String URL = "url";

        try {
            JSONObject jsonObjectTrailers = new JSONObject(rawJsonStrTrailers);
            JSONArray jsonArrayTrailers = jsonObjectTrailers.getJSONArray(RESULTS);
            JSONObject jsonObjectReviews = new JSONObject(rawJsonStrReviews);
            JSONArray jsonArrayReviews = jsonObjectReviews.getJSONArray(RESULTS);

            Vector<ContentValues> vectorTrailers = new Vector<ContentValues>(jsonArrayTrailers.length());
            Vector<ContentValues> vectorReviews = new Vector<ContentValues>(jsonArrayReviews.length());

            for(int i = 0; i < jsonArrayTrailers.length(); i++) {
                JSONObject movieObject = jsonArrayTrailers.getJSONObject(i);

                String id = movieObject.getString(TRAILER_ID);
                String lang = movieObject.getString(LANG);
                String key = movieObject.getString(KEY);
                String name = movieObject.getString(NAME);
                String site = movieObject.getString(SITE);
                String size = movieObject.getString(SIZE);
                String type = movieObject.getString(TYPE);

                ContentValues values = new ContentValues();

                values.put(TrailersEntry.COLUMN_TRAILER_ID, id);
                values.put(TrailersEntry.COLUMN_LANG, lang);
                values.put(TrailersEntry.COLUMN_KEY, key);
                values.put(TrailersEntry.COLUMN_NAME, name);
                values.put(TrailersEntry.COLUMN_SITE, site);
                values.put(TrailersEntry.COLUMN_SIZE, size);
                values.put(TrailersEntry.COLUMN_TYPE, type);

                vectorTrailers.add(values);
            }

            for(int i = 0; i < jsonArrayReviews.length(); i++) {
                JSONObject movieObject = jsonArrayReviews.getJSONObject(i);

                String id = movieObject.getString(REVIEW_ID);
                String author = movieObject.getString(AUTHOR);
                String content = movieObject.getString(CONTENT);
                String url = movieObject.getString(URL);

                ContentValues values = new ContentValues();

                values.put(ReviewsEntry.COLUMN_REVIEW_ID, id);
                values.put(ReviewsEntry.COLUMN_AUTHOR, author);
                values.put(ReviewsEntry.COLUMN_CONTENT, content);
                values.put(ReviewsEntry.COLUMN_URL, url);

                vectorReviews.add(values);
            }

            if(vectorTrailers.size() > 0) {
                ContentValues[] array = new ContentValues[vectorTrailers.size()];
                vectorTrailers.toArray(array);
                mContext.getContentResolver().bulkInsert(MoviesContract.TrailersEntry.CONTENT_URI, array);
            }
            if(vectorReviews.size() > 0) {
                ContentValues[] array = new ContentValues[vectorReviews.size()];
                vectorReviews.toArray(array);
                mContext.getContentResolver().bulkInsert(MoviesContract.ReviewsEntry.CONTENT_URI, array);
            }
        }
        catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        String id = params[0];

        // Declare outside try/catch block to close in finally block
        HttpURLConnection urlConnectionTrailers = null;
        HttpURLConnection urlConnectionReviews = null;
        BufferedReader readerTrailers = null;
        BufferedReader readerReviews = null;

        // Stores raw JSON response as string
        String rawJsonStrTrailers = null;
        String rawJsonStrReviews = null;

        try {
            // Construct the URL for the TheMovieDB query
            final String TRAILERS_BASE_URL = "https://api.themoviedb.org/3/movie/" + id + "/videos";
            final String REVIEWS_BASE_URL = "https://api.themoviedb.org/3/movie/" + id + "/reviews";
            final String API_KEY_PARAM = "api_key";

            Uri builtUriTrailers = Uri.parse(TRAILERS_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .build();

            Uri builtUriReviews = Uri.parse(REVIEWS_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .build();

            URL urlTrailers = new URL(builtUriTrailers.toString());
            URL urlReviews = new URL(builtUriReviews.toString());

            // Create request and open connection
            urlConnectionTrailers = (HttpURLConnection) urlTrailers.openConnection();
            urlConnectionTrailers.setRequestMethod("GET");
            urlConnectionTrailers.connect();
            urlConnectionReviews = (HttpURLConnection) urlReviews.openConnection();
            urlConnectionReviews.setRequestMethod("GET");
            urlConnectionReviews.connect();

            // Set up a BufferedReader to read the InputStream
            InputStream inputStreamTrailers = urlConnectionTrailers.getInputStream();
            InputStream inputStreamReviews = urlConnectionReviews.getInputStream();
            readerTrailers = new BufferedReader(new InputStreamReader(inputStreamTrailers));
            readerReviews = new BufferedReader(new InputStreamReader(inputStreamReviews));

            // Create raw JSON string by iterating the BufferedReader
            String line;
            rawJsonStrTrailers = "";
            while ((line = readerTrailers.readLine()) != null) {
                rawJsonStrTrailers += line;
            }
            rawJsonStrReviews = "";
            while ((line = readerReviews.readLine()) != null) {
                rawJsonStrReviews += line;
            }

            Log.v(LOG_TAG, "Raw JSON string pulled with URL " + urlTrailers + ": " + rawJsonStrTrailers);
            Log.v(LOG_TAG, "Raw JSON string pulled with URL " + urlReviews + ": " + rawJsonStrReviews);
            insertJsonIntoDatabase(rawJsonStrTrailers, rawJsonStrReviews);
        } catch(IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } catch(JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } catch(NumberFormatException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if(urlConnectionTrailers != null) {
                urlConnectionTrailers.disconnect();
            }
            if(readerTrailers != null) {
                try {
                    readerTrailers.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
            if(urlConnectionReviews != null) {
                urlConnectionReviews.disconnect();
            }
            if(readerReviews != null) {
                try {
                    readerReviews.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        ((Callback)mContext).onInsertComplete();
    }
}