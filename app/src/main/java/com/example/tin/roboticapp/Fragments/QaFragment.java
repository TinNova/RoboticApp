package com.example.tin.roboticapp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tin.roboticapp.CompanyMainActivity;
import com.example.tin.roboticapp.Models.Answer;
import com.example.tin.roboticapp.Models.Fundamental;
import com.example.tin.roboticapp.Models.Question;
import com.example.tin.roboticapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tin on 09/01/2018.
 */

public class QaFragment extends Fragment {

    private static final String TAG = "QAFragment";

    private static final String QUESTION_01 = "question_01";
    private static final String QUESTION_02 = "question_02";
    private static final String QUESTION_03 = "question_03";
    private static final String QUESTION_04 = "question_04";
    private static final String QUESTION_05 = "question_05";

    private static final String ANSWER_01 = "answer_01";
    private static final String ANSWER_02 = "answer_02";
    private static final String ANSWER_03 = "answer_03";
    private static final String ANSWER_04 = "answer_04";
    private static final String ANSWER_05 = "answer_05";

    String sQuestion01;
    String sQuestion02;

    /**
     * Needed for Volley Network Connection
     */
    // RequestQueue is for the Volley Network Connection
    private RequestQueue mRequestQueue;

    private TextView tvQuestion01, tvQuestion02, tvQuestion03, tvQuestion04, tvQuestion05;
    private TextView tvAnswer01, tvAnswer02, tvAnswer03, tvAnswer04, tvAnswer05;

    /**
     * Needed to save the state of the Fragment when Fragment enter onDestroyView
     * onSavedInstate state is not good enough as it only saves state when the Activty's View is Destroyed
     */
    Bundle fragSavedInstanceState;
    ArrayList<Question> mQuestionArray;
    ArrayList<Answer> mAnswerArray;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qa, container, false);

        Log.d(TAG, "OnCreateView");

        tvQuestion01 = view.findViewById(R.id.tv_question_01);
        tvQuestion02 = view.findViewById(R.id.tv_question_02);
        tvQuestion03 = view.findViewById(R.id.tv_question_03);
        tvQuestion04 = view.findViewById(R.id.tv_question_04);
        tvQuestion05 = view.findViewById(R.id.tv_question_05);

        tvAnswer01 = view.findViewById(R.id.tv_answer_01);
        tvAnswer02 = view.findViewById(R.id.tv_answer_02);
        tvAnswer03 = view.findViewById(R.id.tv_answer_03);
        tvAnswer04 = view.findViewById(R.id.tv_answer_04);
        tvAnswer05 = view.findViewById(R.id.tv_answer_05);


        if (fragSavedInstanceState != null) {

            tvQuestion01.setText(fragSavedInstanceState.getString(QUESTION_01));
            tvQuestion02.setText(fragSavedInstanceState.getString(QUESTION_02));
            tvQuestion03.setText(fragSavedInstanceState.getString(QUESTION_03));
            tvQuestion04.setText(fragSavedInstanceState.getString(QUESTION_04));
            tvQuestion05.setText(fragSavedInstanceState.getString(QUESTION_05));

            tvAnswer01.setText(fragSavedInstanceState.getString(ANSWER_01));
            tvAnswer02.setText(fragSavedInstanceState.getString(ANSWER_02));
            tvAnswer03.setText(fragSavedInstanceState.getString(ANSWER_03));
            tvAnswer04.setText(fragSavedInstanceState.getString(ANSWER_04));
            tvAnswer05.setText(fragSavedInstanceState.getString(ANSWER_05));

            /** FOR LOOP THAT OPENS THE CONTENT OF THE ARRAYLIST THEN CHECKS THEM
             * AGAINST A SWITCH STATEMENT TO PUT THEM IN THE CORRECT TEXTVIEWS */

        } else {

            // Creating a Request Queue for the Volley Network Connection
            mRequestQueue = Volley.newRequestQueue(getActivity());
            RequestQuestionsFeed("http://10.0.2.2:8000/rest-api/questions");
            RequestAnswersFeed("http://10.0.2.2:8000/rest-api/answers/?company=31");

        }

        return view;
    }

    /**
     * Request on Articles Json w/Cookie attached to request
     */
    public void RequestQuestionsFeed(String url) {

        Log.i(TAG, "RequestQuestionsFeed");
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

                        Question question = new Question(
                                companyJsonObject.getInt("id"),
                                companyJsonObject.getString("question"),
                                companyJsonObject.getInt("position")

                        );

                        int id = question.getId();

                        // Switch statement that says, if the id of the question
                        // is X, then insert that question into tvQuestionX
                        switch (id) {
                            case 1:
                                tvQuestion01.setText(question.getQuestion());
                                break;
                            case 2:
                                tvQuestion02.setText(question.getQuestion());
                                break;
                            case 3:
                                tvQuestion03.setText(question.getQuestion());
                                break;
                            case 4:
                                tvQuestion04.setText(question.getQuestion());
                                break;
                            case 5:
                                tvQuestion05.setText(question.getQuestion());
                                break;
                        }
                    }

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

    /**
     * Request on Articles Json w/Cookie attached to request
     */
    public void RequestAnswersFeed(String url) {

        Log.i(TAG, "RequestAnswersFeed");
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

                        Answer answer = new Answer(
                                companyJsonObject.getInt("question"),
                                companyJsonObject.getInt("company"),
                                companyJsonObject.getString("content")

                        );

                        int questionId = answer.getQuestion();

                        // Switch statement that says, if the id of the question
                        // is X, then insert that question into tvQuestionX
                        switch (questionId) {
                            case 1:
                                tvAnswer01.setText(answer.getContent());
                                break;
                            case 2:
                                tvAnswer02.setText(answer.getContent());
                                break;
                            case 3:
                                tvAnswer03.setText(answer.getContent());
                                break;
                            case 4:
                                tvAnswer04.setText(answer.getContent());
                                break;
                            case 5:
                                tvAnswer05.setText(answer.getContent());
                                break;
                        }
                    }

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        fragSavedInstanceState = new Bundle();

        fragSavedInstanceState.putString(QUESTION_01, tvQuestion01.getText().toString());
        fragSavedInstanceState.putString(QUESTION_02, tvQuestion02.getText().toString());
        fragSavedInstanceState.putString(QUESTION_03, tvQuestion03.getText().toString());
        fragSavedInstanceState.putString(QUESTION_04, tvQuestion04.getText().toString());
        fragSavedInstanceState.putString(QUESTION_05, tvQuestion05.getText().toString());

        fragSavedInstanceState.putString(ANSWER_01, tvAnswer01.getText().toString());
        fragSavedInstanceState.putString(ANSWER_02, tvAnswer02.getText().toString());
        fragSavedInstanceState.putString(ANSWER_03, tvAnswer03.getText().toString());
        fragSavedInstanceState.putString(ANSWER_04, tvAnswer04.getText().toString());
        fragSavedInstanceState.putString(ANSWER_05, tvAnswer05.getText().toString());

        Log.v(TAG, "onDestroyView");

    }
}
