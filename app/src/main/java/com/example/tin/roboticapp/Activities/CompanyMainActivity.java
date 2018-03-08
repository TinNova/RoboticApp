package com.example.tin.roboticapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
import com.example.tin.roboticapp.SQLite.FavouriteContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyMainActivity extends AppCompatActivity implements CompanyAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "CompanyMainActivity";

    /**
     * Needed for Intent
     */
    public static String CURRENT_COMPANY_NAME = "current_company_name";
    public static String CURRENT_COMPANY_TICKER = "current_company_ticker";
    public static String CURRENT_COMPANY_ID = "current_company_id";
    public static String CURRENT_COMPANY_SECTOR = "current_company_sector";

    /**
     * Needed for Login & Cookie Authorisation
     */
    // RequestQueue is for the Volley Authentication
    private RequestQueue mRequestQueue;
    // SharePreferences, the Cookie will be stored here
    public static SharedPreferences mSharedPrefs;

    /**
     * Needed for the RecyclerView
     */
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private List<TheCompany> theCompanies;

    /**
     * Needed for saving Companies List To Bundle
     */
    private static String COMPANIES_LIST = "companies_list";
    private Bundle savedEntireList;
    private Bundle savedSqlList;

    /**
     * Needed for loading data from SQL
     */
    // Constant to save state of the loader
    private static final String ROTATION_TAG = "rotation_tag";
    // Constant for logging and referring to a unique loader
    private static final int FAVOURITEMOVIES_LOADER_ID = 0;


    // This Is For The Save Button In The Menu Item
    public static MenuItem savedMenu;

    // int to hold companyId of the company the user clicked on from the RecyclerView
    private int mCompanyId;
    private int mCompanySector;


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
        Log.d(TAG, "App Launched");

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


    /**
     * Request on Companies Json w/Cookie attached to request
     */
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
                                // This first 'id' is just a default value, it is actually used as a
                                // '_id' for the SQLite table. this default value should be ignored.
                                companyJsonObject.getInt("id"),
                                companyJsonObject.getInt("id"),
                                companyJsonObject.getString("ticker"),
                                companyJsonObject.getString("name"),
                                companyJsonObject.getInt("sector")
                        );

                        theCompanies.add(theCompany);
                        Log.d(TAG, "Companies List: " + theCompany);

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


    /**
     * This Only Works If You Implement: implements CompanyAdapter.ListItemClickListener
     */
    @Override
    public void onListItemClick(int clickedItemIndex) {

        Intent intent = new Intent(this, CompanyDetailActivity.class);

        // The company ID is not part of the recycler view, so we have to pass it through slightly differently
        mCompanyId = theCompanies.get(clickedItemIndex).getCompanyId();
        mCompanySector = theCompanies.get(clickedItemIndex).getCompanySector();

        // Company Name is needed for the Title of the Activity
        // Ticker is needed for Articles Feed and Title of The Activity
        Bundle companyListBundle = new Bundle();
        companyListBundle.putString(CURRENT_COMPANY_NAME, theCompanies.get(clickedItemIndex).getCompanyName());
        companyListBundle.putString(CURRENT_COMPANY_TICKER, theCompanies.get(clickedItemIndex).getCompanyticker());
        companyListBundle.putInt(CURRENT_COMPANY_ID, mCompanyId);
        companyListBundle.putInt(CURRENT_COMPANY_SECTOR, mCompanySector);

        intent.putExtras(companyListBundle);

        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_company_main, menu);
        savedMenu = menu.findItem(R.id.favourite);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            case R.id.saved_list:

                /** What happens when saved_list is clicked
                * 1. It will save the current list of companies into a bundle
                * 2. It will clear the theCompanies list
                * 3. It will load the saved list from SQL and put it into the theCompanies list
                * 4. It will update the adapter with the new list
                * 5. It will turn the menu from Saved to Entire, so it can revert to the Entire list */
                // Save the current list of companies into a bundle
                savedEntireList = new Bundle();
                savedEntireList.putParcelableArrayList(COMPANIES_LIST, (ArrayList<? extends Parcelable>) theCompanies);
                // Clear the list of companies
                theCompanies.clear();

                // Start the SQL Loader
                getSupportLoaderManager().initLoader(FAVOURITEMOVIES_LOADER_ID, null, this);
                
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Instantiates and returns a new AsyncTaskLoader with the given ID.
     * This loader will return favouriteMovie data as a Cursor or null if an error occurs.
     * <p>
     * Implements the required callbacks to take care of loading data at all stages of loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mFavouriteCompaniesData = null;

            @Override
            protected void onStartLoading() {
                if (mFavouriteCompaniesData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mFavouriteCompaniesData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {

                Log.d(TAG, "loadInBackground");

                try {
                    return getContentResolver().query(
                            FavouriteContract.FavouriteEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            FavouriteContract.FavouriteEntry._ID
                    );

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mFavouriteCompaniesData = data;
                super.deliverResult(data);
            }
        };
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        /** Here We Are Accessing The SQLite Query We Received In The Method getSqlCompanies() Which Is Set To Read All Rows
         * We're Going Through Each Row With A For Loop And Putting Them Into Our FavouriteMovie Model
         *
         * @param cursor
         */

        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            for (int count = 0; count < data.getCount(); count++) {

                TheCompany theCompany = new TheCompany(

                        data.getInt(data.getColumnIndex(FavouriteContract.FavouriteEntry._ID)),
                        data.getInt(data.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_COMPANY_ID)),
                        data.getString(data.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_COMPANY_TICKER)),
                        data.getString(data.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_COMPANY_NAME)),
                        data.getInt(data.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_COMPANY_SECTOR))

                );

                Log.d(TAG, "Row_Id" + data.getLong(data.getColumnIndex(FavouriteContract.FavouriteEntry._ID)));

                theCompanies.add(theCompany);

                Log.d(TAG, "DATA LOADED BY onLoadFinished: " + theCompanies);

                data.moveToNext();
            }

            // Update the adapter with the new list
            adapter.notifyDataSetChanged();


        } else {
            Log.v(TAG, "cursor is Empty");
        }

        assert data != null;
        data.close();
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


}
