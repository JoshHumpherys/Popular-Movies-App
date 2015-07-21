package com.example.android.project1.app;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

            Uri builtUriReviews = Uri.parse(TRAILERS_BASE_URL).buildUpon()
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
//                insertJsonIntoDatabase(rawJsonStr, pageQuery);
        } catch(IOException e) {
            Log.e(LOG_TAG, "Error ", e);
//        } catch(JSONException e) {
//            Log.e(LOG_TAG, e.getMessage(), e);
//            e.printStackTrace();
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