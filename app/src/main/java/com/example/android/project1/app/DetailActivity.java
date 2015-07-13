package com.example.android.project1.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import app.project1.android.example.com.popularmoviesapp.R;

/**
 * Created by Admin-HHE on 7/13/2015.
 */
public class DetailActivity extends ActionBarActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle args = new Bundle();
        args.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());

        DetailFragment df = new DetailFragment();
        df.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.detail_container, df)
                .commit();
    }
}
