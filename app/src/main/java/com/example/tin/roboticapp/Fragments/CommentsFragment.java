package com.example.tin.roboticapp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tin.roboticapp.Adapters.CommentAdapter;
import com.example.tin.roboticapp.Activities.CompanyMainActivity;
import com.example.tin.roboticapp.Models.Comment;
import com.example.tin.roboticapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tin on 09/01/2018.
 */

public class CommentsFragment extends Fragment implements CommentAdapter.ListItemClickListener {

    private static final String TAG = "CommentsFragment";
    private static final String COMMENT_ARRAY = "comment_array";

    /**
     * Needed for Volley Network Connection
     */
    // RequestQueue is for the Volley Network Connection
    private RequestQueue mRequestQueue;

    /**
     * Needed for the RecyclerView
     */
    private RecyclerView mRecyclerView;
    private CommentAdapter adapter;
    private ArrayList<Comment> mComments;

    private ImageView sendIcon;
    private EditText mCommentEditText;


    /**
     * Needed to save the state of the Fragment when Fragment enter onDestroyView
     * onSavedInstate state is not good enough as it only saves state when the Activty's View is Destroyed
     */
    Bundle fragSavedInstanceState;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        mComments = new ArrayList<>();

        ImageView sendIcon = (ImageView) view.findViewById(R.id.comment_send_icon);

        mCommentEditText = (EditText) view.findViewById(R.id.comment_editText);

        /** Creating The RecyclerView */
        // This will be used to attach the RecyclerView to the MovieAdapter
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_comments_list);
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

            mComments = fragSavedInstanceState.getParcelableArrayList(COMMENT_ARRAY);
            Log.d(TAG, "mySavedInstanceState: " + mComments);

            adapter = new CommentAdapter(mComments, getContext(), CommentsFragment.this);
            mRecyclerView.setAdapter(adapter);

        } else {

            // Creating a Request Queue for the Volley Network Connection
            mRequestQueue = Volley.newRequestQueue(getActivity());
            RequestFeed("http://10.0.2.2:8000/rest-api/comments/?company=31");

        }

        sendIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // This is the users comment placed into a String
                String commentToPost = mCommentEditText.getText().toString().trim();

                if (!TextUtils.isEmpty(commentToPost)) {

                    postComment(commentToPost);
                    Toast.makeText(getActivity(), "Comment Sent", Toast.LENGTH_SHORT).show();

                    // Before reloading the comments to view the latest, we need to clear the current
                    // list of mComments, but first check if it exists as it's only created on DestroyView
                    if(mComments != null) {

                        mComments.clear();
                    }
                    mRequestQueue = Volley.newRequestQueue(getActivity());
                    RequestFeed("http://10.0.2.2:8000/rest-api/comments/?company=31");

                } else {

                    Toast.makeText(getActivity(), "Insert A Comment!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        return view;
    }


    /**
     * Request on Articles Json w/Cookie attached to request
     */
    public void RequestFeed(String url) {

        Log.i(TAG, "RequestFeed");
        // Handler for the JSON response when server returns ok
        final Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(final String response) {
                Log.i(TAG, "Feed Response: " + response);

                /** Parsing JSON */

                try {
                    // Define the entire response as a JSON Object
                    JSONObject responseJsonObject = new JSONObject(response);
                    // Define the "results" JsonArray as a JSONArray
                    JSONArray jsonArray = responseJsonObject.getJSONArray("results");

                    // Now we need to get the individual Company JsonObjects from the companyJsonArray
                    // using a for loop
                    for (int i = 0; i < jsonArray.length(); i++) {

                        int j = 0;

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        Comment comment = new Comment(
                                jsonObject.getInt("id"),
                                jsonObject.getInt("author"),
                                jsonObject.getString("creation_date"),
                                jsonObject.getInt("company"),
                                jsonObject.getString("content")

                        );

                        mComments.add(comment);
                        Log.d(TAG, "Comments List: " + comment);

                    }

                    adapter = new CommentAdapter(mComments, getContext(), CommentsFragment.this);
                    mRecyclerView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i(TAG, "Comments After Parse: " + mComments);

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
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG, ">>>>>>>>>>>>>>>>ON DESTROY VIEW<<<<<<<<<<<<<<<<");

        /** Saving an instance of the Articles List, because as the user navigates through the tabs
         * this Fragment will enter onDestroyView and there for the data will be lost unless saved
         *  HOWEVER: The onSaveInstanceState only saves data when the ACTIVITY is destroyed, therefore
         *  we need a fragSavedInstanceState to save state when the Fragments View is Destroyed. */
        fragSavedInstanceState = new Bundle();
        fragSavedInstanceState.putParcelableArrayList(COMMENT_ARRAY, mComments);
    }

    /**
     * OnSavedInstanceState saves the list of Articles, ensuring we don't need to do additional
     * unnecessary Network Connections
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(TAG, ">>>>>>>>>>>>>>>On SAVED INSTANCE STATE<<<<<<<<<<<<<<<<<<<<");

        outState.putParcelableArrayList(COMMENT_ARRAY, mComments);

    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

    }


    private void postComment(String commentET) {

        /** Within this method, I'd like the POST request to happen */

        JSONObject params = new JSONObject();
        try {
            JSONObject company = new JSONObject();
            company.put("type", "field");
            company.put("required", true);
            company.put("read_only", false);
            company.put("label", "Company");
            JSONObject content = new JSONObject();
            content.put("type", "string");
            content.put("required", true);
            content.put("read_only", false);
            content.put("label", "Content");

            int companyID = 31; //EZY Jet

            params.put("company", companyID);
            params.put("content", commentET);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://10.0.2.2:8000/rest-api/comments/", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("Response", response.toString());


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }) {
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

            mRequestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
