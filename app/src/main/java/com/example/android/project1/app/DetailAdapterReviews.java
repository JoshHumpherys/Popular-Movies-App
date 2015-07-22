package com.example.android.project1.app;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.project1.app.data.MoviesContract;

import app.project1.android.example.com.popularmoviesapp.R;

/**
 * Created by Admin-HHE on 7/21/2015.
 */
public class DetailAdapterReviews extends CursorAdapter {
    public DetailAdapterReviews(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_review, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String s = "";
        String content = cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewsEntry.COLUMN_CONTENT));
        ((TextView)view.findViewById(R.id.review_content)).setText(content);
    }
}