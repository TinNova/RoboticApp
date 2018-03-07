package com.example.tin.roboticapp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.example.tin.roboticapp.Activities.CompanyMainActivity;
import com.example.tin.roboticapp.Models.Fundamental;
import com.example.tin.roboticapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tin on 09/01/2018.
 */

public class FundamentalsFragment extends Fragment {

    private static final String TAG = "FundamentalsFragment";
    private static final String FUND_DATE = "fund_date";
    private static final String FUND_PRICE = "fund_price";

    /**
     * Needed for Volley Network Connection
     */
    // RequestQueue is for the Volley Network Connection
    private RequestQueue mRequestQueue;

    private TextView tvPriceDate;
    private TextView tvPrice;
    private String mPriceDate;
    // Public because it will be added to the Database in CompanyDetailActivity
    public String mPrice;

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

        tvPriceDate = view.findViewById(R.id.tv_price_date);
        tvPrice = view.findViewById(R.id.tv_price);


        if (fragSavedInstanceState != null) {

            mPriceDate = fragSavedInstanceState.getString(FUND_DATE);
            mPrice = fragSavedInstanceState.getString(FUND_PRICE);

            tvPriceDate.setText(mPriceDate);
            tvPrice.setText(mPrice);

            tvPriceDate = tvPriceDate.findViewById(R.id.tv_price_date);
            tvPrice = tvPrice.findViewById(R.id.tv_price);

        } else {

            requestFeed();

        }

        return view;
    }

    public void requestFeed() {

        // Creating a Request Queue for the Volley Network Connection
        mRequestQueue = Volley.newRequestQueue(getActivity());
        RequestArticlesFeed("http://10.0.2.2:8000/rest-api/fundamentals/?company=31");

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
                //Log.d(TAG, "ArticlesFeed Response: " + response);

                /** Parsing JSON */

                try {
                    // Define the entire response as a JSON Object
                    JSONObject companyResponseJsonObject = new JSONObject(response);
                    // Define the "results" JsonArray as a JSONArray
                    JSONArray companyJsonArray = companyResponseJsonObject.getJSONArray("results");

                    JSONObject companyJsonObject = companyJsonArray.getJSONObject(0);

                    Fundamental fundamental = new Fundamental(
                            companyJsonObject.getString("price_date"),
                            companyJsonObject.getInt("company"),
                            companyJsonObject.getString("price")

                    );

                    mPriceDate = fundamental.getPrice_date();
                    mPrice = fundamental.getPrice();
                    tvPriceDate.setText(mPriceDate);
                    tvPrice.setText(mPrice);


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
}
