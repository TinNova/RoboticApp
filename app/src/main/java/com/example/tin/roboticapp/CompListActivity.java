package com.example.tin.roboticapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tin.roboticapp.Fragments.ArticlesFragment;
import com.example.tin.roboticapp.Fragments.SectionsPagerAdapter;
import com.example.tin.roboticapp.Fragments.FundamentalsFragment;
import com.example.tin.roboticapp.Fragments.QaFragment;
import com.example.tin.roboticapp.Fragments.ReportsFragment;
import com.example.tin.roboticapp.NetworkUtils.NetworkConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CompListActivity extends AppCompatActivity {

    private static final String TAG = "CompListActivity";

    // RequestQueue is for the Volley Authentication
    private RequestQueue mRequestQueue;
    // SharePreferences, the Cookie will be stored here
    public SharedPreferences mSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comp_list);

        //Creating an instance of SharedPreferences & the RequestQueue
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);


        // Launching the Login Method on App Start
        login();
        Log.i(TAG, "App Launched");


        /** Temporary Button to Launch The CompanyDetailActivity */
        Button btn = (Button) findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CompListActivity.this, CompanyDetailActivity.class);
                startActivity(intent);
            }
        });

    }

    // Login Method
    public void login(){
        // Handler for the JSON response when server returns ok
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String loginResponse) {
                // We are expecting a String as a response and have named it "loginResponse"
                Log.i("Login Response:",loginResponse);
                try{
                    // We are converting the loginResponse String into a JSONObject in order to parse it
                    JSONObject responseJSON = new JSONObject(loginResponse);
                    // Here we're extracting the access_token from the response and naming it "token"
                    String token = responseJSON.getString("access_token");
                    // Here we are enabling the editing of the SharedPreferences in order to save
                    // the String token within it
                    SharedPreferences.Editor editor = mSharedPrefs.edit();
                    editor.putString("token",token);
                    editor.apply();
                    // Upon Successful LogIn, Launch The Companies Feed
                    RequestCompaniesFeed();
                }catch (JSONException je){je.printStackTrace();}
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
        StringRequest request = new StringRequest(Request.Method.POST,"http://10.0.2.2:8000/rest-oauth/token",responseListener,errorListener){
            // Parameters for the POST Request.
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();
                params.put("username","TinNova");
                params.put("password","RoboTinNovLogin");
                params.put("client_id","wsytCUq3OF9aK8eEANZXBTJB6RnQq5cQMmZyDAPF");
                params.put("client_secret","gHdvqyNYjZZ4R5nmOExkI4tEcKHRq82qKyQNmaMYnln9YE4stvh70ZNKWEXoNG6B99tep4IBFF0TgsJZ9IvcnDiP3bKFL6HRnge7yVFkvqf4p5Y75FQNNEMqU6RgT1XZ");
                params.put("grant_type","password");
                return params;
            }

        };
        // Making the Network Request and passing in the request we created
        // TODO: DOES THE LOGIN REQUEST HAPPEN ASYCHRONOSLY??? DOES IT HAVE TO??? CHECK STACKOVERFLOW
        // Research into the RequestQueue function to find out! And in general do Logins have to happen Asychronosly?
        mRequestQueue.add(request);
    }


    // Request on Companies Json w/Cookie attached to request
    public void RequestCompaniesFeed(){
        // Handler for the JSON response when server returns ok
        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("CompaniesFeed Response:",response);
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
        StringRequest request = new StringRequest(Request.Method.GET,"http://10.0.2.2:8000/rest-api/companies",responseListener,errorListener){
            // Headers for the POST request (Instead of Parameters as done in the Login Request,
            // here we are are adding adding headers to the request
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                // Extracting the Cookie/Token from SharedPreferences
                String token = mSharedPrefs.getString("token","");
                // Adding the Cookie/Token to the Header
                headers.put("Authorization", "Bearer "+token);
                return headers;
            }
        };
        // Making the Network Request and passing in the request we created
        // TODO: DOES THIS METHOD AUTOMATICALLY RUN ASHYNCHRONOSLY? IS IT BETTER THAN A LOADER OR ASYNCTASK?
        // Research into the RequestQueue function to find out! WE NEED TO KNOW AS THIS NEEDS TO RUN ASYNCHRONOSLY
        mRequestQueue.add(request);
    }
}
