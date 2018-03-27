package com.example.tin.roboticapp.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tin.roboticapp.Activities.CompanyDetailActivity;
import com.example.tin.roboticapp.Adapters.CommentAdapter;
import com.example.tin.roboticapp.Activities.CompanyMainActivity;
import com.example.tin.roboticapp.Models.Comment;
import com.example.tin.roboticapp.Notifications.SnackBarUtils;
import com.example.tin.roboticapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.tin.roboticapp.KeyboardUtils.KeyBoardUtils.hideSoftKeyboard;

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

    private int mCompanyId;

    /**
     * Needed to save the state of the Fragment when Fragment enter onDestroyView
     * onSavedInstate state is not good enough as it only saves state when the Activty's View is Destroyed
     */
    Bundle fragSavedInstanceState;

    // TextViews for when Json results array is empty
    private TextView tvNoDataTitle;
    private TextView tvNoDataBody;

    // Used to check if the device has internet connection
    private ConnectivityManager connectionManager;
    private NetworkInfo networkInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        tvNoDataTitle = view.findViewById(R.id.tv_comments_no_data_title);
        tvNoDataBody = view.findViewById(R.id.tv_comments_no_data_body);

        // Checking If The Device Is Connected To The Internet
        connectionManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

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

        adapter = new CommentAdapter(mComments, getContext(), CommentsFragment.this);

        mRecyclerView.setAdapter(adapter);

        if (fragSavedInstanceState != null) {

            mComments = fragSavedInstanceState.getParcelableArrayList(COMMENT_ARRAY);
            Log.d(TAG, "mySavedInstanceState: " + mComments);

            adapter = new CommentAdapter(mComments, getContext(), CommentsFragment.this);
            mRecyclerView.setAdapter(adapter);

        } else {

            if (getArguments() != null) {

                mCompanyId = getArguments().getInt(CompanyMainActivity.CURRENT_COMPANY_ID);

            }

            // Creating a Request Queue for the Volley Network Connection
            mRequestQueue = Volley.newRequestQueue(getActivity());
            // Original: http://10.0.2.2:8000/rest-api/comments/?company=31
            RequestFeed("https://robotic-site.herokuapp.com/rest-api/comments/?company=" + mCompanyId);

        }

        sendIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // if phone is connected to internet, start the intent
                if (connectionManager != null)
                    networkInfo = connectionManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {


                    // This is the users comment placed into a String
                    String commentToPost = mCommentEditText.getText().toString().trim();

                    // if commentToPost is NOT empty, then start the Post
                    if (!TextUtils.isEmpty(commentToPost)) {

                        // Launch the POST request
                        postComment(commentToPost);

                        // Removes text from EditText
                        mCommentEditText.getText().clear();

                        Toast.makeText(getActivity(), "Comment Sent", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(getActivity(), "Insert A Comment!", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    hideSoftKeyboard(getActivity());
                    SnackBarUtils.snackBar(getActivity().findViewById(R.id.company_detail), getString(R.string.check_connection), Snackbar.LENGTH_INDEFINITE);

                }
            }
        });

        return view;
    }


    /**
     * Request on Articles Json w/Cookie attached to request
     */
    public void RequestFeed(String url) {

        Log.d(TAG, "RequestFeed");
        // Handler for the JSON response when server returns ok
        final Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(final String response) {
                Log.d(TAG, "Feed Response: " + response);

                /** Parsing JSON */

                try {
                    // Define the entire response as a JSON Object
                    JSONObject responseJsonObject = new JSONObject(response);
                    // Define the "results" JsonArray as a JSONArray
                    JSONArray jsonArray = responseJsonObject.getJSONArray("results");

                    // if the JsonArray within "results" is NOT 0, it has data, else it is empty, so show the no data screen
                    if (jsonArray.length() != 0) {

                        // Now we need to get the individual Company JsonObjects from the companyJsonArray
                        // using a for loop
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Comment comment = new Comment(
                                    jsonObject.getInt("id"),
                                    jsonObject.getInt("author"),
                                    jsonObject.getString("creation_date"),
                                    jsonObject.getInt("company"),
                                    jsonObject.getString("content"),
                                    jsonObject.getString("author_full_name")

                            );

                            mComments.add(comment);
                            Log.d(TAG, "Comments List: " + comment);

                        }

                    } else {

                        mRecyclerView.setVisibility(View.GONE);
                        tvNoDataTitle.setVisibility(View.VISIBLE);
                        tvNoDataBody.setVisibility(View.VISIBLE);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "Comments After Parse: " + mComments);
                adapter.notifyDataSetChanged();

                CompanyDetailActivity.favouriteMenu.setVisible(true);

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

            params.put("company", mCompanyId);
            params.put("content", commentET);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://robotic-site.herokuapp.com/rest-api/comments/", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Response", response.toString());

                    // After POST, clear the current mComments list before a new one is downloaded
                    mComments.clear();

                    if (mRecyclerView.getVisibility() == View.GONE) {

                        mRecyclerView.setVisibility(View.VISIBLE);
                        tvNoDataTitle.setVisibility(View.GONE);
                        tvNoDataBody.setVisibility(View.GONE);

                    }

                    // Refresh the Database the moment a Post has been made
                    // Original: http://10.0.2.2:8000/rest-api/comments/?company=31
                    RequestFeed("https://robotic-site.herokuapp.com/rest-api/comments/?company=" + mCompanyId);


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

//TODO: Add code to correctly manage the App for when there are no articles.
//      - Maybe have a graphic that appears like tumble weed to show that there are no comments and a message that says, "be the first to leave a comment"