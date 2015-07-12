package com.example.android.project1.app;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.project1.app.data.MoviesContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.project1.android.example.com.popularmoviesapp.R;

/**
 * Created by Admin-HHE on 7/11/2015.
 */
public class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridViewHolder> {
    private List<String> mDataSet;
    private Context mContext;
    private Cursor mCursor;
//    private ContentObserver mChangeObserver;
    private DataSetObserver mDataSetObserver;
//    private int mRowIDColumn;
    private boolean mDataValid;

    public GridAdapter(Context context, Cursor cursor) {
//        mDataSetObservable = new DataSetObservable();
        mDataSet = new ArrayList<String>();
        mContext = context;
        mCursor = cursor;

        mDataSet = new ArrayList<String>(cursor.getCount());
        if(mCursor.moveToFirst() && cursor.getCount() >= 1) {
            do {
                String posterPath = mCursor.getString(mCursor.getColumnIndex(
                        MoviesContract.MoviesEntry.COLUMN_POSTER_PATH));
                mDataSet.add(posterPath);
            } while(mCursor.moveToNext());
        }

        mDataSetObserver = new DataSetObserver() {
            @Override public void onChanged() { super.onChanged();
                mDataValid = true;
                notifyDataSetChanged();
            }
            @Override public void onInvalidated() { super.onInvalidated();
                mDataValid = false;
                notifyDataSetChanged();
            }
        };
        mDataValid = (cursor != null);
        if(mDataValid) {
//            mRowIDColumn = mCursor.getColumnIndex(MoviesContract.MoviesEntry._ID);
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
//        else mRowIDColumn = -1;
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.grid_imageview, viewGroup, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GridViewHolder gridViewHolder, int i) {
        String posterPath = mDataSet.get(i);
        Picasso.with(mContext)
                .load("http://image.tmdb.org/t/p/w780/" + posterPath)
                .into(gridViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    /**
     * This code is taken from the android.widget.CursorAdapter source code but is slightly modified
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        if (oldCursor != null) {
//            if (mChangeObserver != null) oldCursor.unregisterContentObserver(mChangeObserver);
            if (mDataSetObserver != null) oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (newCursor != null) {
//            if (mChangeObserver != null) newCursor.registerContentObserver(mChangeObserver);
            if (mDataSetObserver != null) newCursor.registerDataSetObserver(mDataSetObserver);
//            mRowIDColumn = newCursor.getColumnIndexOrThrow(MoviesContract.MoviesEntry._ID);
            mDataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
//            mRowIDColumn = -1;
            mDataValid = false;
            // notify the observers about the lack of a data set
            //notifyDataSetInvalidated();
            notifyDataSetChanged(); // hopefully this works just as well as notifyDataSetInvalidated()
        }
        return oldCursor;
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        public GridViewHolder(View view) {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.imageview);
        }
    }
}