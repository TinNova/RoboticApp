package com.example.tin.roboticapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tin.roboticapp.Fragments.QaFragment;
import com.example.tin.roboticapp.Notifications.SnackBarUtils;
import com.example.tin.roboticapp.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.tin.roboticapp.KeyboardUtils.KeyBoardUtils.hideSoftKeyboard;

public class QaDetailActivity extends AppCompatActivity {

    private static final String TAG = "QaDetailActivity";

    // Needed when returning back to qaFragment
    public static String FRAGMENT_POSITION = "fragment_position";
    public static String INTENT_FROM_QA_DETAIL_ACTIVITY = "intent_from_qa_detail_activity";
    public static String COMPANY_ID = "company_id";

    public static String REFRESH_QA = "refresh_qa";
    public static String FRAGMENT_QA = "fragment_qa";

    private int mCompanyId;
    private String mQuestion;
    private String mAnswer;
    private int mAnswerId;
    private int mQId;

    private TextView mQuestionTv;
    private EditText mAnswerEt;
    private RequestQueue mRequestQueue;

    // 0 = New Answer (i.e no answer was passed in the intent) else, 1 = Editing Answer (i.e we're editing an existing answer)
    int newAnswer = 0;

    // Used to check if the device has internet connection
    private ConnectivityManager connectionManager;
    private NetworkInfo networkInfo;

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

            mCompanyId = intentFromQaFrag.getIntExtra(QaFragment.COMPANY_ID, -1);
            mQuestion = intentFromQaFrag.getStringExtra(QaFragment.QUESTION);
            mQuestionTv.setText(mQuestion);
            mQId = intentFromQaFrag.getIntExtra(QaFragment.QUESTION_ID, 0);
            mAnswer = "";
            Log.d(TAG, "The ID of the Question/Answer is: " + mQId);

            // if the answer is not null then extract it, then put the answer within the EditText
            // and mark the newAnswer as 1
            if (intentFromQaFrag.getStringExtra(QaFragment.ANSWER) != null) {

                mAnswer = intentFromQaFrag.getStringExtra(QaFragment.ANSWER);
                mAnswerId = intentFromQaFrag.getIntExtra(QaFragment.ANSWER_ID, -1);
                mAnswerEt.setText(mAnswer);
                newAnswer = 1;


            }

        } else {

            Toast.makeText(this, "ERROR: Data didn't load correctly", Toast.LENGTH_SHORT).show();

        }

        Log.d(TAG, "This is a New or Existing Answer: " + newAnswer);

        // Checking If The Device Is Connected To The Internet
        connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

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

                // if phone is connected to internet, start the intent
                if (connectionManager != null)
                    networkInfo = connectionManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {

                    // Get the text from the EditText and save it as a String.
                    String answerString = mAnswerEt.getText().toString();
                    Log.d(TAG, "The text in the EditText: " + answerString);

                    // if the answerString is NOT empty, and it is NOT the same as the one retrieved
                    // from the Bundle, either addAnswer or editAnswer
                    if (!answerString.matches("") && !answerString.matches(mAnswer)) {
                        Log.d(TAG, "if Statement in line 126 was successful");
                        // If it's an unanswered question create a new answer
                        if (newAnswer == 0) {

                            addAnswer(answerString);

                            Log.d(TAG, "addAnswer Launched from if/else statement");

                            // Else we are editing an existing answer
                        } else {

                            if (mAnswerId == -1) {

                                Toast.makeText(this, "Post cannot be saved, copy your entry and refresh the page", Toast.LENGTH_SHORT).show();

                                Log.d(TAG, "editAnswer cannot launch as mAnswerId == -1, (the default value in the intent");

                            }

                            editAnswer(answerString);

                            Log.d(TAG, "editAnswer Launched from if/else statement");

                        }

                        // if the answerString is empty, notify user
                    } else if (answerString.matches("")) {

                        SnackBarUtils.snackBar(findViewById(R.id.qa_activity), getString(R.string.answer_missing), Snackbar.LENGTH_LONG);

                        // else the only other option is the answer retrieved from the Bundle wasn't edited
                    } else {

                        SnackBarUtils.snackBar(findViewById(R.id.qa_activity), getString(R.string.answer_not_edited), Snackbar.LENGTH_LONG);

                    }

                } else {

                    hideSoftKeyboard(this);
                    SnackBarUtils.snackBar(findViewById(R.id.qa_activity), getString(R.string.check_connection), Snackbar.LENGTH_INDEFINITE);

                }
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

            //int companyID = 31; //EZY Jet

            params.put("question", mQId);
            params.put("company", mCompanyId);
            params.put("content", answerET);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                // Original: http://10.0.2.2:8000/rest-api/answers/
                    (Request.Method.POST, "https://robotic-site.herokuapp.com/rest-api/answers/", params, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "Response: " + response.toString());

                            onSuccessfulPostPut();

                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "onErrorResponse in addAnswer");
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

            //int companyID = 31; //EZY Jet

            params.put("question", mQId);
            params.put("company", mCompanyId);
            params.put("content", answerET);
            // Original: http://10.0.2.2:8000/rest-api/answers/
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, "https://robotic-site.herokuapp.com/rest-api/answers/" + mAnswerId + "/", params, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "Response: " + response.toString());

                    onSuccessfulPostPut();

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse in editAnswer");
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

    private void onSuccessfulPostPut() {

        Toast.makeText(this, "Answer Saved.", Toast.LENGTH_SHORT).show();

        // Intent to launch the QAFragment
        Intent intent = new Intent(QaDetailActivity.this, CompanyDetailActivity.class);
        // Here we are passing in position 1, to load the QAFragment when the activity starts
        // The string is passed so that we can do an if statement with it, in onCreate
        intent.putExtra(FRAGMENT_POSITION, 1);
        intent.putExtra(INTENT_FROM_QA_DETAIL_ACTIVITY, "From QaDetailActivity");
        intent.putExtra(COMPANY_ID, mCompanyId);
        startActivity(intent);

    }

}


// TODO: POST and PUT should happen in a ServiceIntent..or Service? As the result of the data is not linked to the ui or lifecycle of this activity
// TODO: Check for internet connection before posting to server (the if Statements didn't work