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
 * Created by Admin-HHE on 7/9/2015.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMoviesTask.class.getName();

    private static final String API_KEY = "4be6ba35033283805916b8896c01b040";

    private final Context mContext;

    public FetchMoviesTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        String page;
        try {
            int pageInt = Integer.parseInt(params[0]);
            page = params[0];
        }
        catch(ArrayIndexOutOfBoundsException | NumberFormatException e) {
            // ArrayIndexOutOfBoundsException just means no params were passed, so use page=1
            // NumberFormatException means params[0] was not a number, so again use page=1
            page="1";
        }

        // Declare outside try/catch block to close in finally block
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Stores raw JSON response as string
        String rawJsonStr = null;

        try {
            // Construct the URL for the TheMovieDB query
            final String MOVIES_BASE_URL = "https://api.themoviedb.org/3/discover/movie";
            final String PAGE_PARAM = "page";
            final String API_KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                    .appendQueryParameter(PAGE_PARAM, page)
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            // Create request and open connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Set up a BufferedReader to read the InputStream
            InputStream inputStream = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            // Create raw JSON string by iterating the BufferedReader
            String line;
            rawJsonStr = "";
            while((line = reader.readLine()) != null) {
                rawJsonStr += line;
            }

            Log.v(LOG_TAG, "Raw JSON string pulled with URL " + url + ": " + rawJsonStr);
        } catch(IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }
}
