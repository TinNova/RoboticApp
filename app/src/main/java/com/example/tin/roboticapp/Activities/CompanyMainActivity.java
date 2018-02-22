package com.example.tin.roboticapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tin.roboticapp.Adapters.CompanyAdapter;
import com.example.tin.roboticapp.Models.TheCompany;
import com.example.tin.roboticapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyMainActivity extends AppCompatActivity implements CompanyAdapter.ListItemClickListener {

    private static final String TAG = "CompanyMainActivity";

    /** Needed for Intent */
    public static String CURRENT_COMPANY_NAME = "current_company_name";
    public static String CURRENT_COMPANY_TICKER = "current_company_ticker";
    public static String CURRENT_COMPANY_ID = "current_company_id";

    /** Needed for Login & Cookie Authorisation */
    // RequestQueue is for the Volley Authentication
    private RequestQueue mRequestQueue;
    // SharePreferences, the Cookie will be stored here
    public static SharedPreferences mSharedPrefs;

    /** Needed for the RecyclerView */
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private List<TheCompany> theCompanies;

    // int to hold companyId of the company the user clicked on from the RecyclerView
    private int mCompanyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_main);

        // Creating an instance of SharedPreferences & the RequestQueue
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);

        /** Creating The RecyclerView */
        // This will be used to attach the RecyclerView to the MovieAdapter
        mRecyclerView = (RecyclerView) findViewById(R.id.rV_companyList);
        // This will improve performance by stating that changes in the content will not change
        // the child layout size in the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        /*
         * A LayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView as well as determining the policy for when to recycle item views that
         * are no longer visible to the user.
         */
        LinearLayoutManager mLinearLayoutManager =
                new LinearLayoutManager(this);
        // Set the mRecyclerView to the layoutManager so it can handle the positioning of the items
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        // Creating the theCompanies ArrayList<> to avoid a null exception
        theCompanies = new ArrayList<>();

        // Launching the Login Method on App Start
        login();
        Log.i(TAG, "App Launched");

    }

    // Login Method
    private void login() {
        // Handler for the JSON response when server returns ok
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String loginResponse) {
                // We are expecting a String as a response and have named it "loginResponse"
                Log.i("Login Response:", loginResponse);
                try {
                    // We are converting the loginResponse String into a JSONObject in order to parse it
                    JSONObject responseJSON = new JSONObject(loginResponse);
                    // Here we're extracting the access_token from the response and naming it "token"
                    String token = responseJSON.getString("access_token");
                    // Here we are enabling the editing of the SharedPreferences in order to save
                    // the String token within it
                    SharedPreferences.Editor editor = mSharedPrefs.edit();
                    editor.putString("token", token);
                    editor.apply();
                    // Upon Successful LogIn, Launch The Companies Feed
                    RequestCompaniesFeed();
                } catch (JSONException je) {
                    je.printStackTrace();
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

        // This is the body of the Request (What we are sending to the server in order to get an "ok" or "error" Response)
        //  - The Request has been named "request"
        StringRequest request = new StringRequest(Request.Method.POST, "http://10.0.2.2:8000/rest-oauth/token", responseListener, errorListener) {
            // Parameters for the POST Request.
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", "TinNova");
                params.put("password", "RoboTinNovLogin");
                params.put("client_id", "wsytCUq3OF9aK8eEANZXBTJB6RnQq5cQMmZyDAPF");
                params.put("client_secret", "gHdvqyNYjZZ4R5nmOExkI4tEcKHRq82qKyQNmaMYnln9YE4stvh70ZNKWEXoNG6B99tep4IBFF0TgsJZ9IvcnDiP3bKFL6HRnge7yVFkvqf4p5Y75FQNNEMqU6RgT1XZ");
                params.put("grant_type", "password");
                return params;
            }

        };

        mRequestQueue.add(request);
    }


    /** Request on Companies Json w/Cookie attached to request */
    private void RequestCompaniesFeed() {
        // Handler for the JSON response when server returns ok
        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {

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

                        TheCompany theCompany = new TheCompany(
                                companyJsonObject.getInt("id"),
                                companyJsonObject.getString("ticker"),
                                companyJsonObject.getString("name"),
                                companyJsonObject.getInt("sector")
                        );

                        theCompanies.add(theCompany);
                        //Log.d(TAG, "Companies List: " + theCompany);

                    }

                    adapter = new CompanyAdapter(theCompanies, getApplicationContext(), CompanyMainActivity.this);
                    mRecyclerView.setAdapter(adapter);

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
        StringRequest request = new StringRequest(Request.Method.GET, "http://10.0.2.2:8000/rest-api/companies/?limit=500", responseListener, errorListener) {
            // Headers for the POST request (Instead of Parameters as done in the Login Request,
            // here we are are adding adding headers to the request
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                // Extracting the Cookie/Token from SharedPreferences
                String token = mSharedPrefs.getString("token", "");
                // Adding the Cookie/Token to the Header
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        mRequestQueue.add(request);
    }



    /** This Only Works If You Implement: implements CompanyAdapter.ListItemClickListener */
    @Override
    public void onListItemClick(int clickedItemIndex) {

        Intent intent = new Intent(this, CompanyDetailActivity.class);

        // The company ID is not part of the recycler view, so we have to pass it through slightly differently
        mCompanyId = theCompanies.get(clickedItemIndex).getCompanyId();

        // Company Name is needed for the Title of the Activity
        // Ticker is needed for Articles Feed and Title of The Activity
        Bundle companyListBundle = new Bundle();
        companyListBundle.putString(CURRENT_COMPANY_NAME, theCompanies.get(clickedItemIndex).getCompanyName());
        companyListBundle.putString(CURRENT_COMPANY_TICKER, theCompanies.get(clickedItemIndex).getCompanyticker());
        companyListBundle.putInt(CURRENT_COMPANY_ID, mCompanyId);

        intent.putExtras(companyListBundle);

        startActivity(intent);

    }

}
