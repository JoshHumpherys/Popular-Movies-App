package com.example.android.project1.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.project1.app.data.MoviesContract;
import com.squareup.picasso.Picasso;

import app.project1.android.example.com.popularmoviesapp.R;

/**
 * Created by Admin-HHE on 7/9/2015.
 */
public class GridAdapter extends CursorAdapter {
    public GridAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.grid_imageview, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String posterPath = cursor.getString(cursor.getColumnIndex(
                MoviesContract.MoviesEntry.COLUMN_POSTER_PATH));
        Picasso.with(context)
                .load("http://image.tmdb.org/t/p/w500/" + posterPath)
                .into((ImageView)view);
    }
}