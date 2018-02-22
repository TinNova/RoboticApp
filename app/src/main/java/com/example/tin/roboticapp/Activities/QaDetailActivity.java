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

    public static String REFRESH_QA = "refresh_qa";
    public static String FRAGMENT_QA = "fragment_qa";

    private String mQuestion;
    private String mAnswer;
    private int mQId;

    private TextView mQuestionTv;
    private EditText mAnswerEt;
    private RequestQueue mRequestQueue;

    // 0 = New Answer (i.e no answer was passed in the intent) else, 1 = Editing Answer (i.e we're editing an existing answer)
    int newAnswer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qa_detail);

        mAnswerEt = (EditText) findViewById(R.id.answer_editText);
        mQuestionTv = (TextView) findViewById(R.id.question_tV_qaDetail);
        mRequestQueue = Volley.newRequestQueue(this);


        /** Extracting Data From Intent */
        // if intent is not null, open the intent Bundle
        Intent intentFromQaFrag = getIntent();
        if (intentFromQaFrag.getExtras() != null) {

            mQuestion = intentFromQaFrag.getStringExtra(QaFragment.QUESTION);
            mQuestionTv.setText(mQuestion);
            mQId = intentFromQaFrag.getIntExtra(QaFragment.QUESTION_ID, 0);
            Log.d(TAG, "The ID of the Question/Answer is: " + mQId);

            // if the answer is not null then extract it, then put the answer within the EditText
            // and mark the newAnswer as 1
            if (intentFromQaFrag.getStringExtra(QaFragment.ANSWER) != null) {

                mAnswer = intentFromQaFrag.getStringExtra(QaFragment.ANSWER);
                mAnswerEt.setText(mAnswer);
                newAnswer = 1;

            }

        } else {

            Toast.makeText(this, "ERROR: Data didn't load correctly", Toast.LENGTH_SHORT).show();

        }

        Log.d(TAG, "This is a New or Existing Answer: " + newAnswer);

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

            // When Save is clicked, it will launch the POST or PUT request
            case R.id.menu_qa_detail_save:

                // Get the text from the EditText and save it as a String.
                String answerString = mAnswerEt.getText().toString();

                // if the answerString is NOT empty, and it is not the same as the one retrieved
                // from the Bundle, either addAnswer or editAnswer
                if (!answerString.matches("") && !answerString.matches(mAnswer)) {

                    // If it's an unanswered question create a new answer
                    if (newAnswer == 0) {
                        addAnswer(answerString);

                        // Else we are editing an existing answer
                    } else {
                        editAnswer(answerString);
                    }

                    Toast.makeText(this, "Answer Saved.", Toast.LENGTH_SHORT).show();

//                    // Use onBackPressed to navigate back to QAFragment
//                    //TODO: CAN AN INTENT BE PASSED WITH onBACKPRESSED???
//                    super.onBackPressed();
//                    return true;

                    Intent intent = new Intent(QaDetailActivity.this, CompanyDetailActivity.class);

                    intent.putExtra("QaFragment",1);
                    intent.putExtra("QaFragmentString", "From QaDetailActivity");

                    startActivity(intent);

                    // if the answerString is empty, notify user
                } else if (answerString.matches("")) {

                    Toast.makeText(this, "Enter An Answer.", Toast.LENGTH_SHORT).show();

                    // else the only other option is the answer retrieved from the Bundle wasn't edited
                } else {

                    Toast.makeText(this, "Answer Wasn't Edited, Nothing To Save.", Toast.LENGTH_SHORT).show();

                }

                //TODO: The app now uses the backUp button upon saving a comment. This however doesn't work because the questions in the QAFragment are saved in a bundle
                // But when returning to the QAFragment it also seems to load a new set of questions, so we end up with two lists of questions
                // 1. Check what is called when backup is pressed (is onCreateView launched?, is onResume launched? is another type of lifecycle stage launched? Check them all)
                //    - if any of those is correct, maybe an if statement can be used to clear the saved Bundle and launch a new request?



        }

        return super.

                onOptionsItemSelected(item);

    }


    /**
     * This should be an Intent Service? Because it doesn't affect the UI in this Activity?
     */
    private void addAnswer(String answerET) {

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

            int companyID = 31; //EZY Jet

            params.put("question", mQId);
            params.put("company", companyID);
            params.put("content", answerET);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, "http://10.0.2.2:8000/rest-api/answers/", params, new Response.Listener<JSONObject>() {

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

            int companyID = 31; //EZY Jet


            params.put("company", companyID);
            params.put("content", answerET);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, "http://10.0.2.2:8000/rest-api/answers/" + mQId + "/", params, new Response.Listener<JSONObject>() {
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
