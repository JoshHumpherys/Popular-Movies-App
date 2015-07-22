package com.example.android.project1.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import app.project1.android.example.com.popularmoviesapp.R;

/**
 * Created by Admin-HHE on 7/13/2015.
 */
public class DetailActivity extends AppCompatActivity implements FetchDetailsTask.Callback {

    private static final String DETAIL_FRAGMENT_TAG = "DFTAG";

    @Override
    public void onInsertComplete(String movieId) {
        ((DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG)).onItemInserted(movieId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();

        String[] details = intent.getStringArrayExtra(DetailFragment.MOVIE_DETAILS);

        Bundle args = new Bundle();
        args.putStringArray(DetailFragment.MOVIE_DETAILS, details);

        DetailFragment df = new DetailFragment();
        df.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_container, df, DETAIL_FRAGMENT_TAG)
                .commit();
    }
}