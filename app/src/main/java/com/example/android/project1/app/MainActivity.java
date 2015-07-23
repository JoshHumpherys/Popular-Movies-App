package com.example.android.project1.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import app.project1.android.example.com.popularmoviesapp.R;

public class MainActivity extends AppCompatActivity
        implements GridFragment.Callback, FetchMoviesTask.Callback {

    private static final String GRID_FRAGMENT_TAG = "GFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new GridFragment(), GRID_FRAGMENT_TAG)
                    .commit();
            getSupportFragmentManager().executePendingTransactions();
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
        } else if (id == R.id.action_refresh) {
            refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void refresh() {
        FetchMoviesTask fmt = new FetchMoviesTask(this);
        fmt.execute("3");
    }

    @Override
    public void onItemClick(String[] movieDetails) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailFragment.MOVIE_DETAILS, movieDetails);
        startActivity(intent);
    }

    @Override
    public void onInsertComplete() {
        ((GridFragment)getSupportFragmentManager().findFragmentByTag(GRID_FRAGMENT_TAG)).onItemInserted();
    }
}
