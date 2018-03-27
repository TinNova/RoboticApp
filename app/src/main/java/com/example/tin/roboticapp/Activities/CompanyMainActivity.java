package com.example.tin.roboticapp.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
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
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tin.roboticapp.AdModUtils.ToastAdListener;
import com.example.tin.roboticapp.Adapters.CompanyAdapter;
import com.example.tin.roboticapp.Models.TheCompany;
import com.example.tin.roboticapp.Notifications.SnackBarUtils;
import com.example.tin.roboticapp.R;
import com.example.tin.roboticapp.SQLite.FavouriteContract;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CompanyMainActivity extends AppCompatActivity implements CompanyAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "CompanyMainActivity";
    /**
     * Needed for AdMod
     */
    private AdView mAdView;

    /**
     * Needed for Intent
     */
    public static String CURRENT_COMPANY_NAME = "current_company_name";
    public static String CURRENT_COMPANY_TICKER = "current_company_ticker";
    public static String CURRENT_COMPANY_ID = "current_company_id";
    public static String CURRENT_COMPANY_SECTOR = "current_company_sector";
    public static String LIST_TYPE = "list_type";
    public static String CURRENT_COMPANY__ID = "current_company__id";

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
    private ArrayList<TheCompany> mTheCompanies;

    /**
     * Needed for loading data from SQL
     */
    // Constant to save state of the loader
    private static final String ROTATION_TAG = "rotation_tag";
    // Constant for logging and referring to a unique loader
    private static final int FAVOURITECOMPANIES_LOADER_ID = 0;
    // 0 = the SQL Loader has never run before, 1 = it has run before, therefore it needs to be reset
    // before running it again
    private int loaderCreated = 0;
    // 0 = FTSE 350 List, 1 = List from SQL Db
    private int listType = 0;


    // This Is For The Save Button In The Menu Item
    public static MenuItem savedMenu;

    // int to hold companyId of the company the user clicked on from the RecyclerView
    private int mCompanyId;
    private int mCompanySector;

    // Used to check if the device has internet connection
    private ConnectivityManager connectionManager;
    private NetworkInfo networkInfo;

    private int MY_SOCKET_TIMEOUT_MS = 5000;


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
        // Creating the mTheCompanies ArrayList<> to avoid a null exception
        mTheCompanies = new ArrayList<>();

        // Checking If The Device Is Connected To The Internet
        connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // If the connManager and networkInfo is NOT null, start the login() method
        if (connectionManager != null)
            networkInfo = connectionManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            // Launching the Login Method on App Start
            login();
            Log.d(TAG, "App Launched");

        } else {

            snackBarOnCreate(findViewById(R.id.main_activity), getString(R.string.check_connection), Snackbar.LENGTH_INDEFINITE);

        }

        // Setting Up The AdMod
        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setAdListener(new ToastAdListener(this));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

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
                Log.d(TAG, "onErrorResponse login() " + error);
                error.printStackTrace();

            }
        };

        // This is the body of the Request (What we are sending to the server in order to get an "ok" or "error" Response)
        //  - The Request has been named "request" https://robotic-site.herokuapp.com http://10.0.2.2:8000
        StringRequest request = new StringRequest(Request.Method.POST, "https://robotic-site.herokuapp.com/rest-oauth/token", responseListener, errorListener) {
            // Parameters for the POST Request.
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", "tinnovamail"); // TinNova
                params.put("password", "roboticauth"); // RoboTinNovLogin
                params.put("client_id", "deMOvj2c5MNn2mqD1v6ShOHx9mqYktvFhWsAwNLs"); // wsytCUq3OF9aK8eEANZXBTJB6RnQq5cQMmZyDAPF
                params.put("client_secret", "w04Ci7zfTLPSjzYUY7Bnku93LlVpcu1IgPy0SOmtDotCYPC0V35iYsCuOCAArmtShEdMRCM5FwOi2cVE4SgwtOZW68fW20nJbpgm2Y5GnpyvsxPrALq9DVN6uyhq8Lvs"); // gHdvqyNYjZZ4R5nmOExkI4tEcKHRq82qKyQNmaMYnln9YE4stvh70ZNKWEXoNG6B99tep4IBFF0TgsJZ9IvcnDiP3bKFL6HRnge7yVFkvqf4p5Y75FQNNEMqU6RgT1XZ
                params.put("grant_type", "password");
                return params;
            }

        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Log.d(TAG, "request login(): " + request);
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
                                companyJsonObject.optInt("sector")
                        );

                        mTheCompanies.add(theCompany);
                        Log.d(TAG, "Companies List: " + theCompany);
                        Log.d(TAG, "Code Ran 1");

                    }


                    if (listType == 0) {
                        Log.d(TAG, "Code Ran 2");
                        adapter = new CompanyAdapter(mTheCompanies, getApplicationContext(), CompanyMainActivity.this);
                        Log.d(TAG, "adapter 1: " + adapter);
                        mRecyclerView.setAdapter(adapter);
                        Log.d(TAG, "mRecyclerView 1: " + mRecyclerView);


                    } else {

                        adapter.notifyDataSetChanged();

                        listType = 0;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "JSONException: 1 " + e);
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
        StringRequest request = new StringRequest(Request.Method.GET, "https://robotic-site.herokuapp.com/rest-api/companies/?limit=500", responseListener, errorListener) {
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

        // if phone is connected to internet, start the intent
        if (connectionManager != null)
            networkInfo = connectionManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            Intent intent = new Intent(this, CompanyDetailActivity.class);

            // The company ID is not part of the recycler view, so we have to pass it through slightly differently
            mCompanyId = mTheCompanies.get(clickedItemIndex).getCompanyId();
            mCompanySector = mTheCompanies.get(clickedItemIndex).getCompanySector();

            // Company Name is needed for the Title of the Activity
            // Ticker is needed for Articles Feed and Title of The Activity
            Bundle companyListBundle = new Bundle();
            companyListBundle.putString(CURRENT_COMPANY_NAME, mTheCompanies.get(clickedItemIndex).getCompanyName());
            companyListBundle.putString(CURRENT_COMPANY_TICKER, mTheCompanies.get(clickedItemIndex).getCompanyticker());
            companyListBundle.putInt(CURRENT_COMPANY_ID, mCompanyId);
            companyListBundle.putInt(CURRENT_COMPANY_SECTOR, mCompanySector);
            companyListBundle.putInt(LIST_TYPE, listType);

            if (listType == 1) {

                companyListBundle.putInt(CURRENT_COMPANY__ID, mTheCompanies.get(clickedItemIndex).getCompany_id());
            }

            intent.putExtras(companyListBundle);

            startActivity(intent);

            // else if not connected to internet, show SnackBar
        } else {

            SnackBarUtils.snackBar(findViewById(R.id.main_activity), getString(R.string.check_connection), Snackbar.LENGTH_INDEFINITE);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_company_main, menu);
        savedMenu = menu.findItem(R.id.saved_list);

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            case R.id.saved_list:

                if (listType == 0) {
                    /** What happens when saved_list is clicked
                     * 1. It will save the current list of companies into a bundle
                     * 2. It will clear the mTheCompanies list
                     * 3. It will load the saved list from SQL and put it into the mTheCompanies list
                     * 4. It will update the adapter with the new list
                     * 5. It will turn the menu from Saved to Entire, so it can revert to the Entire list */

                    // Clear the list of companies
                    mTheCompanies.clear();

                    if (loaderCreated == 1) {

                        getSupportLoaderManager().restartLoader(FAVOURITECOMPANIES_LOADER_ID, null, this);
                    }

                    // Start the SQL Loader
                    getSupportLoaderManager().initLoader(FAVOURITECOMPANIES_LOADER_ID, null, this);

                    // Change title
                    savedMenu.setTitle(getString(R.string.ftse_list));
                    // Change List Type
                    listType = 1;

                } else {

                    /** What happens when ftse_list is clicked
                     * 1. It will save the current list of saved companies into a bundle
                     * 2. It will clear mTheCompanies list
                     * 3. It will populate mTheCompanies list with the savedEntireList Bundle
                     * 4. It will make the ftse_list invisible and the save_list visible */
                    // Save the current list of companies into a bundle

                    // Clear the list of companies
                    mTheCompanies.clear();

                    // Download a new list, within onResponse notifyChanges to Adapter & change List Type
                    RequestCompaniesFeed();
                    // Change Title
                    savedMenu.setTitle(getString(R.string.saved_list));

                }

        }

        return super.onOptionsItemSelected(item);

    }


    /**
     * Instantiates and returns a new AsyncTaskLoader with the given ID.
     * This loader will return favouriteMovie data as a Cursor or null if an error occurs.
     * <p>
     * Implements the required callbacks to take care of loading data at all stages of loading.
     */
    @SuppressLint("StaticFieldLeak")
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

                mTheCompanies.add(theCompany);

                Log.d(TAG, "DATA LOADED BY onLoadFinished: " + mTheCompanies);

                data.moveToNext();
            }

            // Update the adapter with the new list
            adapter.notifyDataSetChanged();

            loaderCreated = 1;


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

    // Method which is launched when user clicks "REFRESH" on the SnackBar
    public void connectToNetworkMainActivity() {

        // If the connManager and networkInfo is NOT null, start the login() method
        if (connectionManager != null)
            networkInfo = connectionManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            // Launching the Login Method on App Start
            login();
            Log.d(TAG, "App Launched");

        } else {

            snackBarOnCreate(findViewById(R.id.main_activity), getString(R.string.check_connection), Snackbar.LENGTH_INDEFINITE);

        }

    }

    public void snackBarOnCreate(final View view, String message, int duration) {

        // Else if the connManager and networkInfo IS null, show a snakeBar informing the user
        final Snackbar snackbar = Snackbar.make(view, message, duration);
        View snackBarView = snackbar.getView();

        // Set an action on it, and a handler
        snackbar.setAction("REFRESH", new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {

                connectToNetworkMainActivity();
                snackbar.dismiss();

            }
        });

        snackbar.show();

    }

}
