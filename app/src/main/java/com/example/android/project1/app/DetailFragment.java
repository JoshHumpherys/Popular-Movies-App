package com.example.android.project1.app;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import app.project1.android.example.com.popularmoviesapp.R;

/**
 * Created by Admin-HHE on 7/13/2015.
 */
public class DetailFragment extends Fragment {
    public static final String DETAIL_URI = "URI";
    private Uri mUri;

    private ImageView poster;
    private TextView title, overview, voteAverage, releaseDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();
        if(args != null) {
            mUri = args.getParcelable(DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        poster = (ImageView)rootView.findViewById(R.id.poster);
        title = (TextView)rootView.findViewById(R.id.title);
        overview = (TextView)rootView.findViewById(R.id.overview);
        voteAverage = (TextView)rootView.findViewById(R.id.vote_average);
        releaseDate = (TextView)rootView.findViewById(R.id.release_date);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
