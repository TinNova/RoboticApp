package com.example.tin.roboticapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tin.roboticapp.Activities.CompanyMainActivity;
import com.example.tin.roboticapp.Activities.QaDetailActivity;
import com.example.tin.roboticapp.Adapters.ArticleAdapter;
import com.example.tin.roboticapp.Adapters.QaCombinedAdapter;
import com.example.tin.roboticapp.Models.Answer;
import com.example.tin.roboticapp.Models.QACombined;
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

public class QaFragment extends Fragment implements QaCombinedAdapter.ListItemClickListener {

    private static final String TAG = "QAFragment";

    public static final String QUESTION = "question";
    public static final String QUESTION_ID = "question_id";
    public static final String ANSWER = "answer";
    public static final String ANSWER_ID = "answer_id";


    /**
     * Needed for Volley Network Connection
     */
    // RequestQueue is for the Volley Network Connection
    private RequestQueue mRequestQueue;

    /**
     * Needed to save the state of the Fragment when Fragment enter onDestroyView
     * onSavedInstate state is not good enough as it only saves state when the Activty's View is Destroyed
     */
    private Bundle fragSavedInstanceState;
    private ArrayList<Question> mQuestions;
    private ArrayList<Answer> mAnswers;
    // Public because it is used in CompanyDetailActivity to addToDatabase
    public ArrayList<QACombined> mQaCombined;

    private RecyclerView mRecyclerView;
    private QaCombinedAdapter adapter;

    private Bundle onClickBundle;

    private Bundle parsedABundle;
    private Bundle parsedQBundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qa, container, false);

        Log.d(TAG, "OnCreateView");

        mQuestions = new ArrayList<>();
        mAnswers = new ArrayList<>();
        mQaCombined = new ArrayList<>();

        // Creating a Request Queue for the Volley Network Connection
        mRequestQueue = Volley.newRequestQueue(getActivity());
        RequestQuestionsFeed("http://10.0.2.2:8000/rest-api/questions");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rV_Qa_list);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        adapter = new QaCombinedAdapter(mQaCombined, getContext(), QaFragment.this);
        mRecyclerView.setAdapter(adapter);


        parsedQBundle = new Bundle();
        parsedABundle = new Bundle();

        int match;

        return view;
    }


    /**
     * Request on Articles Json w/Cookie attached to request
     */
    public void RequestQuestionsFeed(String url) {

        Log.d(TAG, "RequestQuestionsFeed");
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

                    // Now we need to get the individual Company JsonObjects from the companyJsonArray
                    // using a for loop
                    for (int i = 0; i < companyJsonArray.length(); i++) {

                        JSONObject companyJsonObject = companyJsonArray.getJSONObject(i);

                        Question question = new Question(
                                companyJsonObject.getInt("id"),
                                companyJsonObject.getString("question"),
                                companyJsonObject.getInt("position")

                        );

                        mQuestions.add(question);
                        Log.d(TAG, "Questions: " + question);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "All Questions: " + mQuestions);
//                parsedQBundle = new Bundle();
                parsedQBundle.clear();
                parsedQBundle.putParcelableArrayList("parsedQuestions", mQuestions);
                Log.d(TAG, "Size of mQuesiton in onResponse (Should be 5): " + mQuestions.size());

                RequestAnswersFeed("http://10.0.2.2:8000/rest-api/answers/?company=31");
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

        Log.d(TAG, "RequestAnswersFeed");
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

                    // Now we need to get the individual Company JsonObjects from the companyJsonArray
                    // using a for loop
                    for (int i = 0; i < companyJsonArray.length(); i++) {

                        JSONObject companyJsonObject = companyJsonArray.getJSONObject(i);

                        Answer answer = new Answer(
                                companyJsonObject.getInt("id"),
                                companyJsonObject.getInt("question"),
                                companyJsonObject.getInt("company"),
                                companyJsonObject.getString("content")

                        );

                        mAnswers.add(answer);
                        Log.d(TAG, "Answers: " + answer);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "All Answers: " + mAnswers);

                parsedABundle.clear();
                parsedABundle.putParcelableArrayList("parsedAnswers", mAnswers);

                Log.d(TAG, "Size of mAnswer in onResponse (Should be 5): " + mAnswers.size());

                unpackBundles();

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

    /** This unpacks the Answers & Questions that were saved in Bundles after being downloaded */
    public void unpackBundles() {

        Log.d(TAG, "Size of mQaCombined BEFORE PARSE: " + mQaCombined.size());


        if (mQuestions != null) {

            mQuestions = parsedQBundle.getParcelableArrayList("parsedQuestions");
        }

        if (mAnswers != null) {

            mAnswers = parsedABundle.getParcelableArrayList("parsedAnswers");
        }

        Log.d(TAG, "mAnswers in unpackBundles: " + mAnswers.size());
        Log.d(TAG, "mQuestions in unpackBundles: " + mQuestions.size());

        // If there are not Answer, then skip for looping through answers
        if (mAnswers.size() == 0) {

            // Loop through mQuestions while i is smaller than mQuestions
            for (int i = 0; i < mQuestions.size(); i++) {

                int questionId = mQuestions.get(i).getId();
                String questionQuestion = mQuestions.get(i).getQuestion();
                int questionPosition = mQuestions.get(i).getPosition();

                QACombined qACombined = new QACombined(
                        questionId,
                        questionQuestion,
                        questionPosition,
                        -1,
                        -1,
                        -1,
                        ""
                );

                mQaCombined.add(qACombined);

            }

            //Log.d(TAG, "QACombined ArrayList: " + mQaCombined);
            Log.d(TAG, "Size of mQaCombined after combining the list: " + mQaCombined.size());
            adapter.notifyDataSetChanged();

        } else {

            // Loop through mQuestions while i is smaller than mQuestions
            for (int i = 0; i < mQuestions.size(); i++) {

                int questionId = mQuestions.get(i).getId();
                String questionQuestion = mQuestions.get(i).getQuestion();
                int questionPosition = mQuestions.get(i).getPosition();

                // match is used to calculate whether mock data needs to be added to an answer
                int match = 0;

                for (int j = 0; j < mAnswers.size(); j++) {

                    int answerId = mAnswers.get(j).getId();
                    int answerQuestion = mAnswers.get(j).getQuestion();
                    int answerCompany = mAnswers.get(j).getCompany();
                    String answersContent = mAnswers.get(j).getContent();

                    Log.d(TAG, "Id of Current Quesiont" + mQuestions.get(i).getId());
                    Log.d(TAG, "Question Id of Current Answer" + mAnswers.get(j).getQuestion());

                    // if QuestionId & AnswerQuestion ID Match, add to qACombined
                    if (mQuestions.get(i).getId() == mAnswers.get(j).getQuestion()) {

                        QACombined qACombined = new QACombined(
                                questionId,
                                questionQuestion,
                                questionPosition,
                                answerId,
                                answerQuestion,
                                answerCompany,
                                answersContent
                        );

                        Log.d(TAG, "Question's With An Answer: " + qACombined.getQuestion());

                        mQaCombined.add(qACombined);
                        // make match = 1 to indicate that an answer was found and added
                        match = 1;
                        // stop the for loop, it doesn't need to keep running
                        j = mAnswers.size();

                    }

                }
                // if after completing the answer loop an answer was not found, aka match is still equal to 0
                // add the question with mock answer data
                if (match == 0) {

                    QACombined qACombined = new QACombined(
                            questionId,
                            questionQuestion,
                            questionPosition,
                            -1,
                            -1,
                            -1,
                            ""

                    );

                    mQaCombined.add(qACombined);

                }

            }

            Log.d(TAG, "Size of mQaCombined after combining the list: " + mQaCombined.size());
            adapter.notifyDataSetChanged();

        }

    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

        Log.d(TAG, "CLICK!!!");
        Log.d(TAG, "ClickedItemIndex: " + mQaCombined.get(clickedItemIndex));


        Intent intent = new Intent(getActivity(), QaDetailActivity.class);

        Bundle onClickBundle = new Bundle();
        onClickBundle.putString(QUESTION, mQaCombined.get(clickedItemIndex).getQuestion());
        onClickBundle.putInt(QUESTION_ID, mQaCombined.get(clickedItemIndex).getqId());

        if (mQaCombined.get(clickedItemIndex).getContent() != "") {

            onClickBundle.putString(ANSWER, mQaCombined.get(clickedItemIndex).getContent());
            onClickBundle.putInt(ANSWER_ID, mQaCombined.get(clickedItemIndex).getaId());

        }

        intent.putExtras(onClickBundle);
        startActivity(intent);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "onActivityCreated");

    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");

    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "onPause");

    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d(TAG, "onStop");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        fragSavedInstanceState = new Bundle();

        Log.d(TAG, "onDestroyView");

    }
}


