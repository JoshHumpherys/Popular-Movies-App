package com.example.android.project1.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.project1.app.data.MoviesContract;

import app.project1.android.example.com.popularmoviesapp.R;

/**
 * Created by Admin-HHE on 7/9/2015.
 */
public class GridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int GRID_FRAGMENT_LOADER_ID = 0;

    private GridView mGridView;
    private GridAdapter mGridAdapter;
    private Cursor mCursor;

    public interface Callback {
        public void onItemClick(String[] movieDetails);
    }

    public GridFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridAdapter = new GridAdapter(getActivity(), null, 0);

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridview.setAdapter(mGridAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String[] details = new String[DetailFragment.DETAIL_COLUMNS.length];
                for(int i = 0; i < details.length; i++) {
                    details[i] = mCursor.getString(mCursor.getColumnIndex(DetailFragment.DETAIL_COLUMNS[i]));
                }
                ((Callback)getActivity()).onItemClick(details);
            }
        });

        return rootView;
    }

    public void onItemInserted() {
        getLoaderManager().initLoader(GRID_FRAGMENT_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder;

        Context context = getActivity();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortKey = context.getString(R.string.pref_sort_key);
        String popularity = context.getString(R.string.pref_sort_popularity);
        String rating = context.getString(R.string.pref_sort_rating);

        if(sp.getString(sortKey, popularity).equals(popularity)) {
            sortOrder = MoviesContract.MoviesEntry.COLUMN_POPULARITY + " DESC";
        }
        else if(sp.getString(sortKey, rating).equals(rating)) {
            sortOrder = MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " DESC";
        }
        else {
            sortOrder = sp.getString(sortKey, popularity);
        }
        return new CursorLoader(
                getActivity(),
                MoviesContract.MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        mGridAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mGridAdapter.swapCursor(null);
    }
}