package com.example.android.project1.app;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.project1.app.data.MoviesContract;
import com.example.android.project1.app.data.MoviesContract.MoviesEntry;
import com.squareup.picasso.Picasso;

import app.project1.android.example.com.popularmoviesapp.R;

/**
 * Created by Admin-HHE on 7/13/2015.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailFragment.class.getName();

    public static final int DETAIL_FRAGMENT_LOADER_ID_TRAILERS = 1;
    public static final int DETAIL_FRAGMENT_LOADER_ID_REVIEWS = 2;

    public static final String MOVIE_DETAILS = "details";
    public static final String FAVORITES_KEY = "favorites";

    private boolean addToFavoritesScheduled = false;
    private boolean removeFromFavoritesScheduled = false;

    public static final String[] DETAIL_COLUMNS = {
            "_ID",
            MoviesEntry.COLUMN_ADULT,
            MoviesEntry.COLUMN_BACKDROP_PATH,
            MoviesEntry.COLUMN_ID,
            MoviesEntry.COLUMN_ORIGINAL_LANGUAGE,
            MoviesEntry.COLUMN_ORIGINAL_TITLE,
            MoviesEntry.COLUMN_OVERVIEW,
            MoviesEntry.COLUMN_RELEASE_DATE,
            MoviesEntry.COLUMN_POSTER_PATH,
            MoviesEntry.COLUMN_POPULARITY,
            MoviesEntry.COLUMN_TITLE,
            MoviesEntry.COLUMN_VIDEO,
            MoviesEntry.COLUMN_VOTE_AVERAGE,
            MoviesEntry.COLUMN_VOTE_COUNT,
            MoviesEntry.COLUMN_FAVORITE
    };

    public static final int COL_ID = 3;
    public static final int COL_POSTER = 8;
    public static final int COL_TITLE = 5;
    public static final int COL_OVERVIEW = 6;
    public static final int COL_VOTE_AVERAGE = 12;
    public static final int COL_RELEASE_DATE = 7;
    public static final int COL_FAVORITE = 14;

    private ImageView poster;
    private TextView title, overview, voteAverage, releaseDate;

    private DetailAdapterTrailers mListAdapterTrailers;
    private DetailAdapterReviews mListAdapterReviews;

    private ListView trailers;
    private ListView reviews;
    private CheckBox favorites;

    private String movieId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        favorites = (CheckBox)rootView.findViewById(R.id.favorites_button);
        favorites.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    addToFavorites(buttonView);
                }
                else {
                    removeFromFavorites(buttonView);
                }
            }
        });

        trailers = (ListView)rootView.findViewById(R.id.trailers_listview);
        reviews = (ListView)rootView.findViewById(R.id.reviews_listview);

        mListAdapterTrailers = new DetailAdapterTrailers(getActivity(), null, 0);
        trailers.setAdapter(mListAdapterTrailers);
        trailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mListAdapterTrailers.getCursor();
                cursor.moveToPosition(position);
                String youtubeId = cursor.getString(cursor.getColumnIndex(MoviesContract.TrailersEntry.COLUMN_KEY));
                Uri uri = Uri.parse("http://www.youtube.com/watch?v=" + youtubeId);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        mListAdapterReviews = new DetailAdapterReviews(getActivity(), null, 0);
        reviews.setAdapter(mListAdapterReviews);

        poster = (ImageView)rootView.findViewById(R.id.poster);
        title = (TextView)rootView.findViewById(R.id.title);
        overview = (TextView)rootView.findViewById(R.id.overview);
        voteAverage = (TextView)rootView.findViewById(R.id.vote_average);
        releaseDate = (TextView)rootView.findViewById(R.id.release_date);

        Bundle args = getArguments();
        if(args != null) {
            String[] details = args.getStringArray(MOVIE_DETAILS);

            movieId = details[COL_ID];

            Picasso.with(getActivity())
                    .load("http://image.tmdb.org/t/p/w342/" + details[COL_POSTER])
                    .into(poster);

            title.setText(details[COL_TITLE]);
            overview.setText(details[COL_OVERVIEW]);
            voteAverage.setText(details[COL_VOTE_AVERAGE] + "/10");
            releaseDate.setText(details[COL_RELEASE_DATE].substring(0, 4));

            String favorited = details[COL_FAVORITE];
            if(favorited != null) {
                if(favorited.equals("1")) {
                    favorites.setChecked(true);
                }
            }

            FetchDetailsTask fdt = new FetchDetailsTask(getActivity());
            fdt.execute(details[COL_ID]);
        } else {
            Log.v(LOG_TAG, "In DetailFragment#onCreate args == null");
        }

        return rootView;
    }

    public void onItemInserted(String movieId, boolean exceptionThrow) {
        this.movieId = movieId;
        if(addToFavoritesScheduled == true) {
            addToFavorites(favorites);
        }
        else if(removeFromFavoritesScheduled == true) {
            removeFromFavorites(favorites);
        }
//        if(exceptionThrow) {
//            Toast.makeText(getActivity(), "Error with network call", Toast.LENGTH_SHORT).show();
//            return;
//        }
        getLoaderManager().initLoader(DETAIL_FRAGMENT_LOADER_ID_TRAILERS, null, this);
        getLoaderManager().initLoader(DETAIL_FRAGMENT_LOADER_ID_REVIEWS, null, this);
    }

    public void addToFavorites(View view) {
        if(movieId == null) {
            addToFavoritesScheduled = true;
            return;
        }
        ContentValues values = new ContentValues();
        values.put(MoviesContract.MoviesEntry.COLUMN_FAVORITE, "1");
        getActivity().getContentResolver().update(MoviesEntry.CONTENT_URI, values, MoviesEntry.COLUMN_ID + " = " + movieId, null);
    }

    public void removeFromFavorites(View view) {
        if(movieId == null) {
            removeFromFavoritesScheduled = true;
            return;
        }
        ContentValues values = new ContentValues();
        values.put(MoviesEntry.COLUMN_FAVORITE, "0");
        getActivity().getContentResolver().update(MoviesEntry.CONTENT_URI, values, MoviesEntry.COLUMN_ID + " = " + movieId, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == DETAIL_FRAGMENT_LOADER_ID_TRAILERS) {
            return new CursorLoader(
                    getActivity(),
                    MoviesContract.TrailersEntry.CONTENT_URI,
                    null,
                    MoviesContract.TrailersEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{movieId},
                    null);
        }
        else if(id == DETAIL_FRAGMENT_LOADER_ID_REVIEWS){
            return new CursorLoader(
                    getActivity(),
                    MoviesContract.ReviewsEntry.CONTENT_URI,
                    null,
                    MoviesContract.ReviewsEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{movieId},
                    null);
        }
        else {
            throw new UnsupportedOperationException("Invalid loader id");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        if(id == DETAIL_FRAGMENT_LOADER_ID_TRAILERS) {
            mListAdapterTrailers.notifyDataSetChanged();
            mListAdapterTrailers.swapCursor(data);

        }
        else if(id == DETAIL_FRAGMENT_LOADER_ID_REVIEWS) {
            mListAdapterReviews.notifyDataSetChanged();
            mListAdapterReviews.swapCursor(data);
        }
        else {
            throw new UnsupportedOperationException("Invalid loader id");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    // Based on a stackoverflow snippet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}