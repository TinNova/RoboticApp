package com.example.tin.roboticapp.NetworkUtils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tin.roboticapp.Activities.CompanyMainActivity;
import com.example.tin.roboticapp.Models.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class NetworkConnection {

    private static final String TAG = NetworkConnection.class.getSimpleName();

    private ArrayList<Article> mArticles;

    private RequestQueue mRequestQueue;

    // Needed because a context is required,
    // If this is run from an Activity, it will automatically take the activity as the context
    public NetworkConnection(Context context) {
        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    /** Request on Articles Json w/Cookie attached to request */
    public void RequestArticlesFeed(String url) {

        Log.i(TAG, "RequestArticlesFeed");
        // Handler for the JSON response when server returns ok
        final Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(final String response) {
                Log.i(TAG,"ArticlesFeed Response: " + response);

                /** Parsing JSON */

                try {
                    // Define the entire response as a JSON Object
                    JSONObject companyResponseJsonObject = new JSONObject(response);
                    // Define the "results" JsonArray as a JSONArray
                    JSONArray companyJsonArray = companyResponseJsonObject.getJSONArray("results");

                    mArticles = new ArrayList<>();

                    // Now we need to get the individual Company JsonObjects from the companyJsonArray
                    // using a for loop
                    for (int i = 0; i < companyJsonArray.length(); i++) {

                        int j =0;

                        JSONObject companyJsonObject = companyJsonArray.getJSONObject(i);

                        Article article = new Article(
                                companyJsonObject.getInt("id"),
                                companyJsonObject.getString("publish_date"),
                                companyJsonObject.getString("headline"),
                                companyJsonObject.getString("summary"),
                                companyJsonObject.getString("source_url")

                        );

                        mArticles.add(article);
                        Log.v(TAG, "Article List: " + article);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i(TAG,"Articles After Parse: " + mArticles);

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
