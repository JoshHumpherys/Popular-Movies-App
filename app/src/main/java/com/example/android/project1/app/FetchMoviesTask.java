package com.example.android.project1.app;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.project1.app.data.MoviesContract;

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
 * Created by Admin-HHE on 7/9/2015.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMoviesTask.class.getName();

    private static final String API_KEY = "4be6ba35033283805916b8896c01b040";

    private final Context mContext;

    private String lastParam;

    public interface Callback {
        public void onInsertComplete();
    }

    public FetchMoviesTask(Context context) {
        mContext = context;
    }

    public void insertJsonIntoDatabase(String rawJsonStr, String pageQuery) throws JSONException{
        // Outermost JSON objects
        final String PAGE = "page";
        final String RESULTS = "results";
        final String TOTAL_PAGES = "total_pages";
        final String TOTAL_RESULTS = "total_results";

        // JSON objects contained in RESULTS object
        final String ADULT = "adult";
        final String BACKDROP_PATH = "backdrop_path";
        final String GENRE_IDS = "genre_ids";
        final String ID = "id";
        final String ORIGINAL_LANGUAGE = "original_language";
        final String ORIGINAL_TITLE = "original_title";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String POSTER_PATH = "poster_path";
        final String POPULARITY= "popularity";
        final String TITLE = "title";
        final String VIDEO = "video";
        final String VOTE_AVERAGE = "vote_average";
        final String VOTE_COUNT = "vote_count";

        try {
            JSONObject jsonObject = new JSONObject(rawJsonStr);
            JSONArray jsonArray = jsonObject.getJSONArray(RESULTS);

            Vector<ContentValues> vector = new Vector<ContentValues>(jsonArray.length());

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject movieObject = jsonArray.getJSONObject(i);

                boolean adult = movieObject.getBoolean(ADULT);
                String backdropPath = movieObject.getString(BACKDROP_PATH);
                int[] genreIds;
                int id = movieObject.getInt(ID);
                String originalLanguage = movieObject.getString(ORIGINAL_LANGUAGE);
                String originalTitle = movieObject.getString(ORIGINAL_TITLE);
                String overview = movieObject.getString(OVERVIEW);
                String releaseDate = movieObject.getString(RELEASE_DATE);
                String posterPath = movieObject.getString(POSTER_PATH);
                double popularity = movieObject.getDouble(POPULARITY);
                String title = movieObject.getString(TITLE);
                boolean video = movieObject.getBoolean(VIDEO);
                double voteAverage = movieObject.getDouble(VOTE_AVERAGE);
                int voteCount = movieObject.getInt(VOTE_COUNT);

                ContentValues values = new ContentValues();

                values.put(MoviesContract.MoviesEntry.COLUMN_ADULT, adult);
                values.put(MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH, backdropPath);
                values.put(MoviesContract.MoviesEntry.COLUMN_ID, id);
                values.put(MoviesContract.MoviesEntry.COLUMN_ORIGINAL_LANGUAGE, originalLanguage);
                values.put(MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
                values.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, overview);
                values.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, releaseDate);
                values.put(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH, posterPath);
                values.put(MoviesContract.MoviesEntry.COLUMN_POPULARITY, popularity);
                values.put(MoviesContract.MoviesEntry.COLUMN_TITLE, title);
                values.put(MoviesContract.MoviesEntry.COLUMN_VIDEO, video);
                values.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, voteAverage);
                values.put(MoviesContract.MoviesEntry.COLUMN_VOTE_COUNT, voteCount);

                vector.add(values);
            }

            if(vector.size() > 0) {
                ContentValues[] array = new ContentValues[vector.size()];
                vector.toArray(array);
                mContext.getContentResolver().bulkInsert(MoviesContract.MoviesEntry.CONTENT_URI, array);
            }
        }
        catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        String pageQuery;
        try {
            int pageInt = Integer.parseInt(params[0]);
            pageQuery = params[0];
        }
        catch(ArrayIndexOutOfBoundsException | NumberFormatException e) {
            // ArrayIndexOutOfBoundsException just means no params were passed, so use page=1
            // NumberFormatException means params[0] was not a number, so again use page=1
            pageQuery="1";
        }

        lastParam = pageQuery;

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

            int maxPage = Integer.parseInt(pageQuery);
            for(int i = 1; i < maxPage; i++) {
                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(PAGE_PARAM, Integer.toString(i))
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
                while ((line = reader.readLine()) != null) {
                    rawJsonStr += line;
                }

                Log.v(LOG_TAG, "Raw JSON string pulled with URL " + url + ": " + rawJsonStr);
                insertJsonIntoDatabase(rawJsonStr, pageQuery);
            }
        } catch(IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } catch(JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } catch(NumberFormatException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
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

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        ((Callback)mContext).onInsertComplete();
    }
}
