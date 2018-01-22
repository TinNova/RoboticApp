package com.example.tin.roboticapp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tin.roboticapp.CompListActivity;
import com.example.tin.roboticapp.CompanyDetailActivity;
import com.example.tin.roboticapp.R;

import java.util.ArrayList;

import static com.example.tin.roboticapp.CompListActivity.CURRENT_COMPANY_NAME;
import static com.example.tin.roboticapp.CompListActivity.CURRENT_COMPANY_TICKER;

/**
 * Created by Tin on 09/01/2018.
 */

public class ArticlesFragment extends Fragment {

    private static final String TAG = "ArticlesFragment";

    private String mCompanyName;
    private String mCompanyTicker;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_articles, container, false);
        Log.d(TAG, "ARTILCES FRAGMENT onCreate Started");

            Bundle getExtras = getArguments();
            mCompanyName = getExtras.getString(CURRENT_COMPANY_NAME);
            mCompanyTicker = getExtras.getString(CURRENT_COMPANY_TICKER);

        TextView tvCompanyName = (TextView) view.findViewById(R.id.tvCompanyName);
        tvCompanyName.setText(mCompanyName);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d(TAG, "onStop");
    }
}
