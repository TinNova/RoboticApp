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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tin.roboticapp.Adapters.ArticleAdapter;
import com.example.tin.roboticapp.CompanyMainActivity;
import com.example.tin.roboticapp.Models.Article;
import com.example.tin.roboticapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.tin.roboticapp.CompanyDetailActivity.ARTICLES_LIST;
import static com.example.tin.roboticapp.CompanyMainActivity.CURRENT_COMPANY_NAME;
import static com.example.tin.roboticapp.CompanyMainActivity.CURRENT_COMPANY_TICKER;

/**
 * Created by Tin on 09/01/2018.
 */

public class ArticlesFragment extends Fragment implements ArticleAdapter.ListItemClickListener {

    private static final String TAG = "ArticlesFragment";
    private static final String ARTICLE_ARRAY = "article_array";

    /** Needed for Volley Network Connection */
    // RequestQueue is for the Volley Network Connection
    private RequestQueue mRequestQueue;

    /**
     * Needed for the RecyclerView
     */
    private RecyclerView mRecyclerView;
    private ArticleAdapter adapter;
    private ArrayList<Article> mArticles;

    /**
     * Needed to save the state of the Fragment when Fragment enter onDestroyView
     * onSavedInstate state is not good enough as it only saves state when the Activty's View is Destroyed
     */
    Bundle fragSavedInstanceState;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_articles, container, false);

        Log.v(TAG, ">>>>>>>>>>>>>>>>ON CREATE VIEW<<<<<<<<<<<<<<<<");

        mArticles = new ArrayList<>();

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

        if (fragSavedInstanceState != null) {

            mArticles = fragSavedInstanceState.getParcelableArrayList(ARTICLE_ARRAY);
            Log.v(TAG, "mySavedInstanceState: " + mArticles);

            adapter = new ArticleAdapter(mArticles, getContext(), ArticlesFragment.this);
            mRecyclerView.setAdapter(adapter);

        } else {

            // Creating a Request Queue for the Volley Network Connection
            mRequestQueue = Volley.newRequestQueue(getActivity());
            RequestArticlesFeed("http://10.0.2.2:8000/rest-api/articles/?format=json&is_useful=yes&mode=company&ticker=EZJ"); //+ mCompanyTicker);

        }

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.v(TAG, ">>>>>>>>>>>>>>>>ON DESTROY VIEW<<<<<<<<<<<<<<<<");

        /** Saving an instance of the Articles List, because as the user navigates through the tabs
         * this Fragment will enter onDestroyView and there for the data will be lost unless saved
         *  HOWEVER: The onSaveInstanceState only saves data when the ACTIVITY is destroyed, therefore
         *  we need a fragSavedInstanceState to save state when the Fragments View is Destroyed. */
        fragSavedInstanceState = new Bundle();
        fragSavedInstanceState.putParcelableArrayList(ARTICLE_ARRAY, mArticles);
    }


    /** What Happens When An Article Is Clicked */
    @Override
    public void onListItemClick(int clickedItemIndex) {

        //Get the Source_URL and launch an intent to the preferred web-browser

    }


    /**
     * Request on Articles Json w/Cookie attached to request
     */
    public void RequestArticlesFeed(String url) {

        Log.i(TAG, "RequestArticlesFeed");
        // Handler for the JSON response when server returns ok
        final Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(final String response) {
                //Log.i(TAG, "ArticlesFeed Response: " + response);

                /** Parsing JSON */

                try {
                    // Define the entire response as a JSON Object
                    JSONObject companyResponseJsonObject = new JSONObject(response);
                    // Define the "results" JsonArray as a JSONArray
                    JSONArray companyJsonArray = companyResponseJsonObject.getJSONArray("results");

                    //mArticles = new ArrayList<>();

                    // Now we need to get the individual Company JsonObjects from the companyJsonArray
                    // using a for loop
                    for (int i = 0; i < companyJsonArray.length(); i++) {

                        int j = 0;

                        JSONObject companyJsonObject = companyJsonArray.getJSONObject(i);

                        Article article = new Article(
                                companyJsonObject.getInt("id"),
                                companyJsonObject.getString("publish_date"),
                                companyJsonObject.getString("headline"),
                                companyJsonObject.getString("summary"),
                                companyJsonObject.getString("source_url")

                        );

                        mArticles.add(article);
                        //Log.v(TAG, "Article List: " + article);

                    }

                    adapter = new ArticleAdapter(mArticles, getContext(), ArticlesFragment.this);
                    mRecyclerView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Log.i(TAG, "Articles After Parse: " + mArticles);

            }
        };

        // Handler for when the server returns an error response
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };

        // This is the body of the Request
        //  - The Request has been named "request"
        StringRequest request = new StringRequest(Request.Method.GET, url, responseListener, errorListener) {
            // Headers for the POST request (Instead of Parameters as done in the Login Request,
            // here we are are adding adding headers to the request
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                // Extracting the Cookie/Token from SharedPreferences
                String token = CompanyMainActivity.mSharedPrefs.getString("token", "");
                // Adding the Cookie/Token to the Header
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        mRequestQueue.add(request);

    }

    /** OnSavedInstanceState saves the list of Articles, ensuring we don't need to do additional
     * unnecessary Network Connections */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.v(TAG, ">>>>>>>>>>>>>>>On SAVED INSTANCE STATE<<<<<<<<<<<<<<<<<<<<");

        outState.putParcelableArrayList(ARTICLE_ARRAY, mArticles);

    }

}
