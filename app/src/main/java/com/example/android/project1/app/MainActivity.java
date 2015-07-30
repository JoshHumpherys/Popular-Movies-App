package com.example.android.project1.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import app.project1.android.example.com.popularmoviesapp.R;

public class MainActivity extends AppCompatActivity
        implements GridFragment.Callback, FetchMoviesTask.Callback, FetchDetailsTask.Callback {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    public boolean mTwoPane;
    public boolean toPopulateDetailFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.detail_container) != null) {
            mTwoPane = true;
            if(savedInstanceState == null) {
                toPopulateDetailFragment = true;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }
        else {
            mTwoPane = false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
//        } else if (id == R.id.action_refresh) {
//            refresh();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void refresh() {
        FetchMoviesTask fmt = new FetchMoviesTask(this);
        fmt.execute("3");
    }

    @Override
    public void onItemClick(String[] movieDetails) {
        if(!mTwoPane) {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailFragment.MOVIE_DETAILS, movieDetails);
            startActivity(intent);
        }
        else {
//            ((DetailFragment)getSupportFragmentManager().findFragmentById(R.id.detail_container))
//                    .onItemInserted(movieDetails[DetailFragment.COL_ID]);
            Bundle args = new Bundle();
            args.putStringArray(DetailFragment.MOVIE_DETAILS, movieDetails);

            DetailFragment df = new DetailFragment();
            df.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_container, df, DETAILFRAGMENT_TAG)
                .commit();
        }
    }

    @Override
    public void onInsertComplete(String movieId, boolean exceptionThrown) {
        GridFragment gf = ((GridFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_grid));
        DetailFragment df = ((DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG));
        if(gf != null) {
            gf.onItemInserted(exceptionThrown);
        }
        if(mTwoPane && df != null) {
            df.onItemInserted(movieId, exceptionThrown);
        }
    }

//    @Override
//    public void populateDetailFragment(String[] movieDetails) {
//        if(mTwoPane && toPopulateDetailFragment) {
//            toPopulateDetailFragment = false;
//            Bundle args = new Bundle();
//            args.putStringArray(DetailFragment.MOVIE_DETAILS, movieDetails);
//
//            DetailFragment df = new DetailFragment();
//            df.setArguments(args);
//
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.detail_container, df, DETAILFRAGMENT_TAG)
//                    .commit();
//        }
//    }
//
//    public void addToFavorites(View view) {
//        ((DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG)).addToFavorites(view);
//    }
}
