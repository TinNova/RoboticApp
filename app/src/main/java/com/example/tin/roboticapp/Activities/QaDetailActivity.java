package com.example.tin.roboticapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tin.roboticapp.Fragments.QaFragment;
import com.example.tin.roboticapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class QaDetailActivity extends AppCompatActivity {

    private static final String TAG = "QaDetailActivity";

    private String mQuestion;
    private String mAnswer;
    private int mId;

    private EditText mAnswerView;
    private RequestQueue mRequestQueue;

    // 0 = New Answer (i.e no answer was passed in the intent)
    // 1 = Editing Answer (i.e we're editing an existing answer)
    int newAnswer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qa_detail);

        mAnswerView = (EditText) findViewById(R.id.answer_editText);
        mRequestQueue = Volley.newRequestQueue(this);


        /** Extracting Data From Intent */
        // if intent is not null, open the intent Bundle
        Intent intentFromQaFrag = getIntent();
        if (intentFromQaFrag.getExtras() != null) {

            mQuestion = intentFromQaFrag.getStringExtra(QaFragment.QUESTION_01);
            mId = intentFromQaFrag.getIntExtra(QaFragment.QUESTION_ID_01, 0);
            Log.v(TAG, "The ID of the Question/Answer is: " + mId);

            // if the answer is not null then extract it, then put the answer within the EditText
            // and mark the newAnswer as 1
            if (intentFromQaFrag.getStringExtra(QaFragment.ANSWER_01) != null) {

                mAnswer = intentFromQaFrag.getStringExtra(QaFragment.ANSWER_01);
                mAnswerView.setText(mAnswer);
                newAnswer = 1;

            }

        } else {

            Toast.makeText(this, "ERROR: Data didn't load correctly", Toast.LENGTH_SHORT).show();

        }

        Log.v(TAG, "This is a New or Existing Answer: " + newAnswer);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_qa_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // If UP button is clicked, it will call onBackPressed (Instead of an actual UP)
            case android.R.id.home:
                super.onBackPressed();
                return true;
            // If Save is clicked, it will launch the POST request
            case R.id.menu_qa_detail_save:
                String answerString = mAnswerView.getText().toString();
                // If it's an unanswered quesiton create a new answer
                if (newAnswer == 0) {
                    addNewAnswer(answerString);
                    // Else we are editing an existing answer
                } else {
                    editAnswer(answerString);
                }

                Toast.makeText(this, "Answer Saved.", Toast.LENGTH_SHORT).show();
                // Then it navigates back to the QaFragment
                // However we need to reLaunch the Get Request in order to get the update list of
                // answers upon return to the QaFragment.
                super.onBackPressed();

        }

        return super.onOptionsItemSelected(item);
    }


    private void addNewAnswer(String answerET) {

        /** Within this method, I'd like the POST request to happen */

        JSONObject params = new JSONObject();
        try {
            JSONObject question = new JSONObject();
            question.put("type", "field");
            question.put("required", false);
            question.put("read_only", false);
            question.put("label", "Question");
            JSONObject company = new JSONObject();
            company.put("type", "field");
            company.put("required", false);
            company.put("read_only", false);
            company.put("label", "Company");
            JSONObject content = new JSONObject();
            content.put("type", "field");
            content.put("required", false);
            content.put("read_only", false);
            content.put("label", "Content");

            int questionNumber = 1;
            int companyID = 31; //EZY Jet

            params.put("question", questionNumber);
            params.put("company", companyID);
            params.put("content", answerET);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://10.0.2.2:8000/rest-api/answers/", params, new Response.Listener<JSONObject>() {
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

    private void editAnswer(String answerET) {

        /** Within this method, I'd like the POST request to happen */

        JSONObject params = new JSONObject();
        try {
            JSONObject question = new JSONObject();
            question.put("type", "field");
            question.put("required", false);
            question.put("read_only", false);
            question.put("label", "Question");
            JSONObject company = new JSONObject();
            company.put("type", "field");
            company.put("required", false);
            company.put("read_only", false);
            company.put("label", "Company");
            JSONObject content = new JSONObject();
            content.put("type", "field");
            content.put("required", false);
            content.put("read_only", false);
            content.put("label", "Content");

            int questionNumber = 1;
            int companyID = 31; //EZY Jet

            //params.put("question", questionNumber);
            params.put("company", companyID);
            params.put("content", answerET);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, "http://10.0.2.2:8000/rest-api/answers/1", params, new Response.Listener<JSONObject>() {
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
                // Headers for the PUT request (Instead of Parameters as done in the Login Request,
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

// COMPLETED: Add a save button that triggers the POST function
// COMMIT!
// COMPLETED: Pass the Answer ID to the qaDetailActivity
// COMMIT!
// COMPLETED: Add POST function for a new answer
// COMMIT!
// TODO: Add POST function for an existing answer
// COMPLETED: Have an UP button that takes you specifically to the QA Fragment (NOT a random fragment)
// COMMIT!
// COMPLETED: Fix the layout a little
// COMMIT!
// TODO: Work on the POST function for Comments
