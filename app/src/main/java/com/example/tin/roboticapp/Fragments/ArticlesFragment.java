package com.example.tin.roboticapp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tin.roboticapp.Adapters.ArticleAdapter;
import com.example.tin.roboticapp.Models.Article;
import com.example.tin.roboticapp.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.tin.roboticapp.CompanyDetailActivity.ARTICLES_LIST;
import static com.example.tin.roboticapp.CompanyMainActivity.CURRENT_COMPANY_NAME;
import static com.example.tin.roboticapp.CompanyMainActivity.CURRENT_COMPANY_TICKER;

/**
 * Created by Tin on 09/01/2018.
 */

public class ArticlesFragment extends Fragment implements ArticleAdapter.ListItemClickListener {

    private static final String TAG = "ArticlesFragment";

    private String mCompanyName;
    private String mCompanyTicker;

    /**
     * Needed for the RecyclerView
     */
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private List<Article> mArticleList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_articles, container, false);
        Log.d(TAG, "ARTILCES FRAGMENT onCreate Started");


        Bundle getExtras = getArguments();
        mArticleList = getExtras.getParcelableArrayList(ARTICLES_LIST);


        /** Creating The RecyclerView */
        // This will be used to attach the RecyclerView to the MovieAdapter
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rV_articleList);
        // This will improve performance by stating that changes in the content will not change
        // the child layout size in the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        /*
         * A LayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView as well as determining the policy for when to recycle item views that
         * are no longer visible to the user.
         */
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        // Set the mRecyclerView to the layoutManager so it can handle the positioning of the items
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        adapter = new ArticleAdapter(mArticleList, getContext(), (ArticleAdapter.ListItemClickListener) getActivity());
        mRecyclerView.setAdapter(adapter);




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

    /** What Happens When An Article Is Clicked*/
    @Override
    public void onListItemClick(int clickedItemIndex) {

        //Get the Source_URL and launch an intent to the preferred web-browser

    }
}
