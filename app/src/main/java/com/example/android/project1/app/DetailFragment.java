package com.example.android.project1.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.project1.app.data.MoviesContract.MoviesEntry;
import com.squareup.picasso.Picasso;

import app.project1.android.example.com.popularmoviesapp.R;

/**
 * Created by Admin-HHE on 7/13/2015.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String DETAIL_URI = "URI";
    private Uri mUri;

    private static final int DETAIL_FRAGMENT_LOADER_ID = 0;

    private static final String[] DETAIL_COLUMNS = {
            MoviesEntry.TABLE_NAME + "." + MoviesEntry._ID,
            MoviesEntry.COLUMN_POSTER_PATH,
            MoviesEntry.COLUMN_ORIGINAL_TITLE,
            MoviesEntry.COLUMN_OVERVIEW,
            MoviesEntry.COLUMN_VOTE_AVERAGE,
            MoviesEntry.COLUMN_RELEASE_DATE
    };

    public static final int COL_ID = 0;
    public static final int COL_POSTER = 1;
    public static final int COL_TITLE = 2;
    public static final int COL_OVERVIEW = 3;
    public static final int COL_VOTE_AVERAGE = 4;
    public static final int COL_RELEASE_DATE = 5;

    private ImageView poster;
    private TextView title, overview, voteAverage, releaseDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();
        if(args != null) {
            mUri = args.getParcelable(DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        poster = (ImageView)rootView.findViewById(R.id.poster);
        title = (TextView)rootView.findViewById(R.id.title);
        overview = (TextView)rootView.findViewById(R.id.overview);
        voteAverage = (TextView)rootView.findViewById(R.id.vote_average);
        releaseDate = (TextView)rootView.findViewById(R.id.release_date);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_FRAGMENT_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(mUri != null) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    null,
                    null,
                    null,
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.moveToFirst()) {
//            String posterPath = data.getString(COL_POSTER);
            String posterPath = data.getString(data.getColumnIndex(MoviesEntry.COLUMN_POSTER_PATH));
            Picasso.with(getActivity())
                    .load("http://image.tmdb.org/t/p/w185/" + posterPath)
                    .into(poster);

//            title.setText("title = " + data.getString(COL_TITLE));
//            overview.setText("overview = " + data.getString(COL_OVERVIEW));
//            voteAverage.setText("vote average = " + Float.toString(data.getFloat(COL_VOTE_AVERAGE)));
//            releaseDate.setText("release date = " + data.getString(COL_RELEASE_DATE));

            title.setText(data.getString(data.getColumnIndex(MoviesEntry.COLUMN_TITLE)));
            overview.setText(data.getString(data.getColumnIndex(MoviesEntry.COLUMN_OVERVIEW)));
            voteAverage.setText(Float.toString(data.getFloat(data.getColumnIndex(MoviesEntry.COLUMN_VOTE_AVERAGE))));
            releaseDate.setText(data.getString(data.getColumnIndex(MoviesEntry.COLUMN_RELEASE_DATE)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}