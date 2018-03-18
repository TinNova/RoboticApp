package com.example.tin.roboticapp.Fragments;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tin.roboticapp.Adapters.ArticleAdapter;
import com.example.tin.roboticapp.Activities.CompanyMainActivity;
import com.example.tin.roboticapp.Adapters.QaCombinedAdapter;
import com.example.tin.roboticapp.Models.Article;
import com.example.tin.roboticapp.R;
import com.example.tin.roboticapp.SQLite.FavouriteContract;
import com.example.tin.roboticapp.Widget.CompanyWidgetProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tin on 09/01/2018.
 */

public class ArticlesFragment extends Fragment implements ArticleAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String[] FUNDAMENTAL_PROJECTION = {
            FavouriteContract.FavouriteEntry.COLUMN_COMPANY_ARTICLES_LIST
    };

    private static final String TAG = "ArticlesFragment";
    private static final String ARTICLE_ARRAY = "article_array";
    // Used for widget
    public static final String SHARED_PREFERENCES_KEY = "shared_preferences_key";

    /**
     * Needed for Volley Network Connection
     */
    // RequestQueue is for the Volley Network Connection
    private RequestQueue mRequestQueue;

    /**
     * Needed for the RecyclerView
     */
    private RecyclerView mRecyclerView;
    private ArticleAdapter adapter;
    // Public because it is used in CompanyDetailActivity to addToDatabase
    public ArrayList<Article> mArticles = new ArrayList<>();

    String mCompanyTicker;
    int mCompany_id;
    Uri mUri = FavouriteContract.FavouriteEntry.CONTENT_URI;
    // Constant to save state of the loader
    private static final String ROTATION_TAG = "rotation_tag";
    // Constant for logging and referring to a unique loader
    private static final int FUNDAMENTALS_LOADER_ID = 0;
    // 0 = the SQL Loader has never run before, 1 = it has run before, therefore it needs to be reset
    // before running it again
    private int loaderCreated = 0;

    /**
     * Needed to save the state of the Fragment when Fragment enter onDestroyView
     * onSavedInstate state is not good enough as it only saves state when the Activty's View is Destroyed
     */
    private Bundle fragSavedInstanceState;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_articles, container, false);

        Log.d(TAG, ">>>>>>>>>>>>>>>>ON CREATE VIEW<<<<<<<<<<<<<<<<");

        //mArticles = new ArrayList<>();

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
        adapter = new ArticleAdapter(mArticles, getContext(), ArticlesFragment.this);
        mRecyclerView.setAdapter(adapter);

        if (fragSavedInstanceState != null) {

            mArticles = fragSavedInstanceState.getParcelableArrayList(ARTICLE_ARRAY);
            Log.d(TAG, "mySavedInstanceState: " + mArticles);

            adapter = new ArticleAdapter(mArticles, getContext(), ArticlesFragment.this);
            mRecyclerView.setAdapter(adapter);

        } else {

            if (getArguments() != null) {

                // If LIST_TYPE == 0, the Arguments DO NOT contain SQL data
                if (getArguments().getInt(CompanyMainActivity.LIST_TYPE) == 0) {

                    mCompanyTicker = getArguments().getString(CompanyMainActivity.CURRENT_COMPANY_TICKER);
                    requestFeed();

                    // Else LIST_TYPE == 1, the Argument DO contain SQL data
                } else {

                    mCompany_id = getArguments().getInt(CompanyMainActivity.CURRENT_COMPANY__ID);

                    // Here we are building up the uri using the row_id in order to tell the ContentResolver
                    // to delete the item
                    String stringRowId = Integer.toString(mCompany_id);
                    mUri = mUri.buildUpon().appendPath(stringRowId).build();

                    Log.d(TAG, "mCompany_id: " + mCompany_id);
                    Log.d(TAG, "stringRowId: " + stringRowId);
                    Log.d(TAG, "mUri: " + mUri);
                    // Check if there is already an open instance of a Loader
                    if (loaderCreated == 1) {

                        getLoaderManager().restartLoader(FUNDAMENTALS_LOADER_ID, null, this);

                    }

                    // Start the SQL Loader
                    getLoaderManager().initLoader(FUNDAMENTALS_LOADER_ID, null, this);

                }

            } else {

                Toast.makeText(getActivity(), "Error Loading Data, Try Again", Toast.LENGTH_SHORT).show();

            }

        }

        return view;
    }

    public void requestFeed() {

        // Creating a Request Queue for the Volley Network Connection
        mRequestQueue = Volley.newRequestQueue(getActivity());
        RequestArticlesFeed("http://10.0.2.2:8000/rest-api/articles/?format=json&is_useful=yes&mode=company&ticker=EZJ"); //+ mCompanyTicker);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG, ">>>>>>>>>>>>>>>>ON DESTROY VIEW<<<<<<<<<<<<<<<<");

        /** Saving an instance of the Articles List, because as the user navigates through the tabs
         * this Fragment will enter onDestroyView and there for the data will be lost unless saved
         *  HOWEVER: The onSaveInstanceState only saves data when the ACTIVITY is destroyed, therefore
         *  we need a fragSavedInstanceState to save state when the Fragments View is Destroyed. */
        fragSavedInstanceState = new Bundle();
        fragSavedInstanceState.putParcelableArrayList(ARTICLE_ARRAY, mArticles);
    }


    /**
     * What Happens When An Article Is Clicked
     */
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

                    // Now we need to get the individual Company JsonObjects from the companyJsonArray
                    // using a for loop
                    for (int i = 0; i < companyJsonArray.length(); i++) {

                        JSONObject companyJsonObject = companyJsonArray.getJSONObject(i);

                        Article article = new Article(
                                companyJsonObject.getInt("id"),
                                companyJsonObject.getString("publish_date"),
                                companyJsonObject.getString("headline"),
                                companyJsonObject.getString("summary"),
                                companyJsonObject.getString("source_url")

                        );

                        mArticles.add(article);
                        //Log.d(TAG, "Article List: " + article);

                    }

                    adapter.notifyDataSetChanged();

                    makeWidgetData(mArticles);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {

            case FUNDAMENTALS_LOADER_ID:

                return new CursorLoader(getActivity(),
                        mUri,
                        FUNDAMENTAL_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        /** Here We Are Accessing The SQLite Query We Received In The Method getSqlCompanies() Which Is Set To Read All Rows
         * We're Going Through Each Row With A For Loop And Putting Them Into Our FavouriteMovie Model
         *
         * @param cursor
         */

        if (data != null && data.getCount() > 0) {
            data.moveToFirst();

            String stringOfArticles = data.getString(0);
            Log.d(TAG, "stringOfArticles: " + stringOfArticles);

            Gson gson = new Gson();

            Type type = new TypeToken<ArrayList<Article>>() {
            }.getType();

            Log.d(TAG, "mArticles ArrayList Before Gson: " + mArticles);
            mArticles = gson.fromJson(stringOfArticles, type);
            Log.d(TAG, "mArticles ArrayList After Gson: " + mArticles);

            mRecyclerView.setAdapter((new ArticleAdapter(mArticles, getContext(), ArticlesFragment.this)));

            adapter.notifyDataSetChanged();

            loaderCreated = 1;

        } else {
            Log.d(TAG, "cursor is Empty");
        }

        assert data != null;
        data.close();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * OnSavedInstanceState saves the list of Articles, ensuring we don't need to do additional
     * unnecessary Network Connections
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(TAG, ">>>>>>>>>>>>>>>On SAVED INSTANCE STATE<<<<<<<<<<<<<<<<<<<<");

        outState.putParcelableArrayList(ARTICLE_ARRAY, mArticles);

    }

    // Takes the mArticles ArrayList And Converts It To Json
    // Then Passes The Json To The CompanyWidgetService, this will display the article in the Widget
    private void makeWidgetData(ArrayList<Article> articles) {
        // Initialise a Gson
        Gson gson = new Gson();
        // Convert theIngredients to a Json using the Gson Library
        String theArticlesJson = gson.toJson(articles);
        // Initialise SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        // Pass in theIngredients in Json format
        editor.putString(SHARED_PREFERENCES_KEY, theArticlesJson).apply();

        sendBroadcast();

    }

    private void sendBroadcast() {
        Intent intent = new Intent(getActivity(), CompanyWidgetProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE\"");
        getActivity().sendBroadcast(intent);
    }

}