package com.example.android.project1.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;

import com.example.android.project1.app.data.MoviesContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import app.project1.android.example.com.popularmoviesapp.R;

/**
 * Created by Admin-HHE on 7/9/2015.
 */
public class GridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int GRID_FRAGMENT_LOADER_ID = 0;
    
    private static final String LOG_TAG = GridFragment.class.getName();

    private static final String MOVIE_KEY = "movies";

    private GridView mGridView;
    private GridAdapter mGridAdapter;
    private ArrayList<MovieDetails> mMovies;
    private ArrayList<MovieDetails> mFavorites;
    private String sortOrderPreference;
    private boolean toSortByFavorites = false;

    public interface Callback {
        public void onItemClick(String[] movieDetails);
        public void populateDetailFragment(String[] movieDetails);
    }

    public GridFragment() {}

    @Override
    public void onResume() {
        super.onResume();
        String sortOrder = getSortOrder(); // the current preferred sort order
        if(sortOrderPreference != null) {
            if (!sortOrderPreference.equals(sortOrder)) { // if the sort order has changed
                sortOrderPreference = sortOrder;
                if(!sortByFavorites()) {
                    getLoaderManager().restartLoader(GRID_FRAGMENT_LOADER_ID, null, this);
//                    getLoaderManager().initLoader(GRID_FRAGMENT_LOADER_ID, null, this);
                }
                else {
                    populateWithFavorites();
                }
            }
        }
        else {
            if(!sortByFavorites()) {
                getLoaderManager().restartLoader(GRID_FRAGMENT_LOADER_ID, null, this);
//                getLoaderManager().initLoader(GRID_FRAGMENT_LOADER_ID, null, this);
            }
            else {
                populateWithFavorites();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMovies = null;

        mGridAdapter = new GridAdapter(getActivity(), null, 0);

        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        mGridView.setAdapter(mGridAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                Toast.makeText(getActivity(), Integer.toString(position), Toast.LENGTH_SHORT).show();
                String[] details = new String[MoviesContract.MoviesEntry.DETAIL_COLUMNS.length];
                for (int i = 0; i < details.length; i++) {
//                    details[i] = mCursor.getString(mCursor.getColumnIndex(DetailFragment.DETAIL_COLUMNS[i]));
                    if (!sortByFavorites() || mFavorites == null) {
                        details[i] = mMovies.get(position).mDetails[i];
                    } else{
                        details[i] = mFavorites.get(position).mDetails[i];
                    }
                }
                ((Callback) getActivity()).onItemClick(details);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sortOrderPreference = getSortOrder();
        Context context = getActivity();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String sortKey = context.getString(R.string.pref_sort_key);
        String favorites = context.getString(R.string.pref_sort_favorites);
        if(sp.getString(sortKey, favorites).equals(favorites) && sp.getString(sortKey, null) != null) {
            toSortByFavorites = true;
            if(mMovies == null) {
                if(isNetworkAvailable()) {
                    FetchMoviesTask fmt = new FetchMoviesTask(getActivity());
                    fmt.execute("3");
                }
                else {
                    Toast.makeText(getActivity(), "No network connection", Toast.LENGTH_LONG).show();
                }
            }
            else {
                populateWithFavorites();
            }
        }
        else if(savedInstanceState != null && savedInstanceState.containsKey(MOVIE_KEY)) {
            mMovies = (ArrayList<MovieDetails>)savedInstanceState.get(MOVIE_KEY);

            if(mMovies == null) {
                return;
            }

            MatrixCursor matrixCursor = new MatrixCursor(MoviesContract.MoviesEntry.DETAIL_COLUMNS);
            for (MovieDetails details : mMovies) {
                matrixCursor.addRow(details.mDetails);
            }

            mGridAdapter.notifyDataSetChanged();
            mGridAdapter.swapCursor(matrixCursor);
        }
        else {
            if(isNetworkAvailable()) {
                FetchMoviesTask fmt = new FetchMoviesTask(getActivity());
                fmt.execute("3");
            }
            else {
                Toast.makeText(getActivity(), "No network connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_KEY, mMovies);
    }

    public void onItemInserted(boolean exceptionThrown) {
        if(!exceptionThrown) {
            getLoaderManager().restartLoader(GRID_FRAGMENT_LOADER_ID, null, this);
        }
        else {
            Toast.makeText(getActivity(), "Error with network call", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateWithFavorites() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> set = sp.getStringSet(DetailFragment.FAVORITES_KEY, null);
        List<String> list;
        if(set != null) {
            list = new ArrayList<>(set);
        }
        else {
            Toast.makeText(getActivity(), "No favorites to show", Toast.LENGTH_LONG).show();
            return;
        }

        mFavorites = new ArrayList();
        MatrixCursor matrixCursor = new MatrixCursor(MoviesContract.MoviesEntry.DETAIL_COLUMNS);
        for(int i = 0; i < mMovies.size(); i++) {
            MovieDetails details = mMovies.get(i);
            if(list.contains(details.mDetails[DetailFragment.COL_ID])) {
                matrixCursor.addRow(details.mDetails);
                mFavorites.add(details);
            }
        }

        mGridAdapter.notifyDataSetChanged();
        mGridAdapter.swapCursor(matrixCursor);

        toSortByFavorites = false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = getSortOrder();
        if(sortOrder == null) {
            sortOrder = MoviesContract.MoviesEntry.COLUMN_POPULARITY + " DESC";
        }
        if(sortOrder.equals(getActivity().getString(R.string.pref_sort_favorites))) {
            sortOrder = MoviesContract.MoviesEntry.COLUMN_POPULARITY + " DESC";
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
        if(!sortByFavorites()) {
            mGridAdapter.notifyDataSetChanged();
            mGridAdapter.swapCursor(data);
            mMovies = new ArrayList<MovieDetails>(data.getCount());
            data.moveToPosition(-1);
            while (data.moveToNext()) {
                String[] values = new String[data.getColumnCount()];
                for (int i = 0; i < data.getColumnCount(); i++) {
                    int type = data.getType(i);
                    switch (type) {
                        case Cursor.FIELD_TYPE_STRING:
                            values[i] = data.getString(i);
                            break;
                        case Cursor.FIELD_TYPE_FLOAT:
                            values[i] = Float.toString(data.getFloat(i));
                            break;
                        case Cursor.FIELD_TYPE_INTEGER:
                            values[i] = Integer.toString(data.getInt(i));
                    }
                }
                mMovies.add(new MovieDetails(values));
            }
            MatrixCursor matrixCursor = new MatrixCursor(MoviesContract.MoviesEntry.DETAIL_COLUMNS);
            for (MovieDetails details : mMovies) {
                matrixCursor.addRow(details.mDetails);
            }

            mGridAdapter.notifyDataSetChanged();
            mGridAdapter.swapCursor(matrixCursor);

            mGridView.smoothScrollToPosition(0);
        }
        else {
            if (toSortByFavorites) {
                populateWithFavorites();
            }
        }

        if(((MainActivity)getActivity()).toPopulateDetailFragment && ((MainActivity)getActivity()).mTwoPane) {
            // based on a stackoverflow snippet to handle:
            // java.lang.IllegalStateException: Can not perform this action inside of onLoadFinished
            final int WHAT = 0;
            Handler h = new Handler() {
                @Override
                public void handleMessage(Message m) {
                    if(m.what == WHAT) {
                        String[] details = new String[MoviesContract.MoviesEntry.DETAIL_COLUMNS.length];
                        for (int i = 0; i < details.length; i++) {
                            if (!sortByFavorites() || mFavorites == null) {
                                details[i] = mMovies.get(0).mDetails[i];
                            } else {
                                details[i] = mFavorites.get(0).mDetails[i];
                            }
                        }
                        ((GridFragment.Callback)getActivity()).populateDetailFragment(details);
                    }
                }
            };
            h.sendEmptyMessage(WHAT);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mGridAdapter.swapCursor(null);
    }

    public String getSortOrder() {
        Context context = getActivity();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortKey = context.getString(R.string.pref_sort_key);
        if(sp.getString(sortKey, null) == null) {
            return null;
        }
        String popularity = context.getString(R.string.pref_sort_popularity);
        String rating = context.getString(R.string.pref_sort_rating);
        String favorites = context.getString(R.string.pref_sort_favorites);
        String sortOrder;
        if(sp.getString(sortKey, popularity).equals(popularity)) {
            sortOrder = MoviesContract.MoviesEntry.COLUMN_POPULARITY + " DESC";
        }
        else if(sp.getString(sortKey, rating).equals(rating)) {
            sortOrder = MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " DESC";
        }
        else if(sp.getString(sortKey, favorites).equals(favorites)) {
            sortOrder = favorites; // DO NOT SORT BY THIS SORT ORDER
        }
        else {
            sortOrder = MoviesContract.MoviesEntry.COLUMN_POPULARITY + " DESC";
        }
        return sortOrder;
    }

    public boolean sortByFavorites() {
        Context context = getActivity();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String sortKey = context.getString(R.string.pref_sort_key);
        String favorites = context.getString(R.string.pref_sort_favorites);
        if(sp.getString(sortKey, null) == null) {
            return false;
        }
        return sp.getString(sortKey, favorites).equals(favorites);
    }

    // Based on a stackoverflow snippet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}