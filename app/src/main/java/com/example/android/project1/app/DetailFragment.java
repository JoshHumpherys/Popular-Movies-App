package com.example.android.project1.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
public class DetailFragment extends Fragment {
    public static final String MOVIE_DETAILS = "details";

    public static final String[] DETAIL_COLUMNS = {
            MoviesEntry.COLUMN_POSTER_PATH,
            MoviesEntry.COLUMN_ORIGINAL_TITLE,
            MoviesEntry.COLUMN_OVERVIEW,
            MoviesEntry.COLUMN_VOTE_AVERAGE,
            MoviesEntry.COLUMN_RELEASE_DATE
    };

    public static final int COL_POSTER = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_OVERVIEW = 2;
    public static final int COL_VOTE_AVERAGE = 3;
    public static final int COL_RELEASE_DATE = 4;

    private ImageView poster;
    private TextView title, overview, voteAverage, releaseDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle args = getArguments();
        if(args != null) {
            String[] details = args.getStringArray(MOVIE_DETAILS);

            poster = (ImageView)rootView.findViewById(R.id.poster);
            title = (TextView)rootView.findViewById(R.id.title);
            overview = (TextView)rootView.findViewById(R.id.overview);
            voteAverage = (TextView)rootView.findViewById(R.id.vote_average);
            releaseDate = (TextView)rootView.findViewById(R.id.release_date);

            Picasso.with(getActivity())
                    .load("http://image.tmdb.org/t/p/w342/" + details[0])
                    .into(poster);

            title.setText(details[1]);
            overview.setText(details[2]);
            voteAverage.setText(details[3]);
            releaseDate.setText(details[4]);
        }

        return rootView;
    }
}