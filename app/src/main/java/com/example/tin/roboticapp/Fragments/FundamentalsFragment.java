package com.example.tin.roboticapp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tin.roboticapp.R;

/**
 * Created by Tin on 09/01/2018.
 */

public class FundamentalsFragment extends Fragment {

    private static final String TAG = "FundamentalsFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fundamentals, container, false);

        return view;
    }
}
