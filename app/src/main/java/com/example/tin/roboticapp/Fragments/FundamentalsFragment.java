package com.example.tin.roboticapp.Fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.support.v4.content.CursorLoader;

import com.example.tin.roboticapp.Activities.CompanyMainActivity;
import com.example.tin.roboticapp.Models.Fundamental;
import com.example.tin.roboticapp.R;
import com.example.tin.roboticapp.SQLite.FavouriteContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tin on 09/01/2018.
 */

public class FundamentalsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String[] FUNDAMENTAL_PROJECTION = {
            FavouriteContract.FavouriteEntry.COLUMN_COMPANY_PRICE
    };
    int mCompanyPrice;

    private static final String TAG = "FundamentalsFragment";
    private static final String FUND_DATE = "fund_date";
    private static final String FUND_PRICE = "fund_price";

    /**
     * Needed for Volley Network Connection
     */
    // RequestQueue is for the Volley Network Connection
    private RequestQueue mRequestQueue;

    private TextView tvTitle;
    private TextView tvPriceDate;
    private TextView tvPrice;
    private String mPriceDate;
    // Public because it will be added to the Database in CompanyDetailActivity
    public String mPrice;

    // TextViews for when Json results array is empty
    private TextView tvNoDataTitle;
    private TextView tvNoDataBody;

    // For Extracting Arguments Passed into Fragment
    private int mCompanyId;
    private int mCompany_id;
    private Uri mUri = FavouriteContract.FavouriteEntry.CONTENT_URI;

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
    Bundle fragSavedInstanceState;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fundamentals, container, false);

        Log.d(TAG, "FundFrag OnCreateView");

        tvTitle = view.findViewById(R.id.tv_fund_title);
        tvPriceDate = view.findViewById(R.id.tv_price_date);
        tvPrice = view.findViewById(R.id.tv_price);
        tvNoDataTitle = view.findViewById(R.id.tv_fund_no_data_title);
        tvNoDataBody = view.findViewById(R.id.tv_fund_no_data_body);


        if (fragSavedInstanceState != null) {

            mPriceDate = fragSavedInstanceState.getString(FUND_DATE);
            mPrice = fragSavedInstanceState.getString(FUND_PRICE);

            tvPriceDate.setText(mPriceDate);
            tvPrice.setText(mPrice);

            tvPriceDate = tvPriceDate.findViewById(R.id.tv_price_date);
            tvPrice = tvPrice.findViewById(R.id.tv_price);

        } else {
            // First safety checking that Arguments passed from CompanyDetailActivity are not null
            if (getArguments() != null) {

                // If LIST_TYPE == 0, the Arguments DO NOT contain SQL data
                if (getArguments().getInt(CompanyMainActivity.LIST_TYPE) == 0) {

                    mCompanyId = getArguments().getInt(CompanyMainActivity.CURRENT_COMPANY_ID);
                    requestFeed(mCompanyId);

                    // Else LIST_TYPE == 1, the Argument DO contain SQL data, therefore user navigated here via Saved list
                } else {

                    mCompany_id = getArguments().getInt(CompanyMainActivity.CURRENT_COMPANY__ID);

                    // Here we are building up the uri using the row_id in order to tell the ContentResolver
                    // to delete the item
                    String stringRowId = Integer.toString(mCompany_id);
                    mUri = mUri.buildUpon().appendPath(stringRowId).build();

                    Log.d(TAG, "mCompany_id: " + mCompany_id);
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

    public void requestFeed(int companyId) {

        // Creating a Request Queue for the Volley Network Connection
        mRequestQueue = Volley.newRequestQueue(getActivity());
        // Original: http://10.0.2.2:8000/rest-api/fundamentals/?company=31
        RequestArticlesFeed("https://robotic-site.herokuapp.com/rest-api/fundamentals/?company=" + companyId);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        fragSavedInstanceState = new Bundle();
        fragSavedInstanceState.putString(FUND_DATE, mPriceDate);
        fragSavedInstanceState.putString(FUND_PRICE, mPrice);

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
                Log.d(TAG, "Fundamentals Response: " + response);

                /** Parsing JSON */

                try {
                    // Define the entire response as a JSON Object
                    JSONObject companyResponseJsonObject = new JSONObject(response);
                    // Define the "results" JsonArray as a JSONArray
                    JSONArray companyJsonArray = companyResponseJsonObject.getJSONArray("results");

                    // if the JsonArray within "results" is NOT 0, it has data, else it is empty, so show the no data screen
                    if (companyJsonArray.length() != 0) {

                        // Define each item in the companyJsonArray as an individual companyJsonObject
                        JSONObject companyJsonObject = companyJsonArray.getJSONObject(0);

                        Fundamental fundamental = new Fundamental(
                                companyJsonObject.getString("price_date"),
                                companyJsonObject.getInt("company"),
                                companyJsonObject.getString("price")

                        );

                        // If the data is NOT empty, display it, else display the no data message
                        //if (fundamental.getPrice() != null) {
                        mPriceDate = fundamental.getPrice_date();
                        mPrice = fundamental.getPrice();
                        tvPriceDate.setText(mPriceDate);
                        tvPrice.setText(mPrice);

                    } else {

                        tvTitle.setVisibility(View.GONE);
                        tvPriceDate.setVisibility(View.GONE);
                        tvPrice.setVisibility(View.GONE);

                        tvNoDataTitle.setVisibility(View.VISIBLE);
                        tvNoDataBody.setVisibility(View.VISIBLE);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            //Log.i(TAG, "Articles After Parse: " + mArticles);

        };

        // Handler for when the server returns an error response
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse: " + error);
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
            mCompanyPrice = data.getInt(0);

            if (mCompanyPrice != 0) {
                tvPrice.setText(null);
                tvPrice.setText(String.valueOf(mCompanyPrice));

            } else {

                tvTitle.setVisibility(View.GONE);
                tvPriceDate.setVisibility(View.GONE);
                tvPrice.setVisibility(View.GONE);

                tvNoDataTitle.setVisibility(View.VISIBLE);
                tvNoDataBody.setVisibility(View.VISIBLE);

            }

            loaderCreated = 1;



        } else {
            Log.v(TAG, "cursor is Empty");
        }

        assert data != null;
        data.close();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
