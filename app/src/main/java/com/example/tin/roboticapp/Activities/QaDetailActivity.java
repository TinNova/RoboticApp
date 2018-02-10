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
            mId = intentFromQaFrag.getIntExtra(QaFragment.QUESTION_ID_01,0);
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_qa_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        String newAnswer = mAnswerView.getText().toString();

        /** HERE INSERT THE POST FUNCTION USING THE NEW ANSWER */

        Toast.makeText(this, "Answer Saved.", Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }




//    // Method to POST answer to server
//    // /rest-api/answers/
//
//    private void login() throws MalformedURLException, URISyntaxException, JSONException {
//
//        /** IF IT IS A NEW ANSWER, (USE "rest-api/answers" */
//        if(newAnswer == 0) {
//
//
//            String urlString = "http://10.0.2.2:8000/rest-api/answers/";
//            URI uri = new URI(urlString);
//            URL url = uri.toURL();
//
//            JSONObject obj = new JSONObject();
//            obj.put("question", "1");
//            obj.put("name", "myname");
//
//            RequestQueue queue = mRequestQueue;
//            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,url,obj,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            System.out.println(response);
//                            hideProgressDialog();
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            hideProgressDialog();
//                        }
//                    });
//            queue.add(jsObjRequest);
//
//
//
//
//
//
//
//
//
//          /** ELSE IF IT IS EDITING AN EXISTING ANSWER, (USE "/rest-api/answers/{ID}/", it needs to include the final "/" */
//        } else {
//
//            // This is the body of the Request (What we are sending to the server in order to get an "ok" or "error" Response)
//            //  - The Request has been named "request"
//            StringRequest request = new StringRequest(Request.Method.POST, "http://10.0.2.2:8000/rest-api/answers/{ID}/", responseListener, errorListener) {
//                // Parameters for the POST Request.
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("username", "TinNova");
//                    params.put("password", "RoboTinNovLogin");
//                    params.put("client_id", "wsytCUq3OF9aK8eEANZXBTJB6RnQq5cQMmZyDAPF");
//                    params.put("client_secret", "gHdvqyNYjZZ4R5nmOExkI4tEcKHRq82qKyQNmaMYnln9YE4stvh70ZNKWEXoNG6B99tep4IBFF0TgsJZ9IvcnDiP3bKFL6HRnge7yVFkvqf4p5Y75FQNNEMqU6RgT1XZ");
//                    params.put("grant_type", "password");
//                    return params;
//                }
//
//            };
//
//
//        }
//
//        mRequestQueue.add(request);
//    }

}

// COMPLETED: Add a save button that triggers the POST function
// COMMIT!
// COMPLETED: Pass the Answer ID to the qaDetailActivity
// COMMIT!
// TODO: Add POST function
// COMMIT!
// TODO: Have an UP button that takes you specifically to the QA Fragment (NOT a random fragment)
// COMMIT!
// TODO: Fix the layout a little
// COMMIT!
// TODO: Work on the POST function for Comments
