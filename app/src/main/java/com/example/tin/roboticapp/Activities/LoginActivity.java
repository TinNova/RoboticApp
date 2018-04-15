package com.example.tin.roboticapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tin.roboticapp.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LoginActivity:";
    private static final int RC_SIGN_IN = 9001;
    GoogleApiClient mGoogleApiClient;
    TextView statusTexView;
    SignInButton signInButton;
    Button signOutButton;

    /**
     * Needed for Login & Authentication
     */
    // RequestQueue is for the Volley Authentication
    private RequestQueue mRequestQueue;
    // SharePreferences, the Cookie will be stored here
    public static SharedPreferences mSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Creating an instance of SharedPreferences & the RequestQueue
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mRequestQueue = Volley.newRequestQueue(this);


        statusTexView = (TextView) findViewById(R.id.tv_status);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);

        signOutButton = (Button) findViewById(R.id.sign_out);
        signOutButton.setOnClickListener(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
//                .requestServerAuthCode("1021511247299-9u02su026vnn8pa0kkvhf3n657gkllfb.apps.googleusercontent.com")
//                .requestServerAuthCode("823803460569-fjkohhvkpv6qpodf42sfcigc0tede504.apps.googleusercontent.com")
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                Log.d(TAG, "Sign In Button Clicked");
                break;
            case R.id.sign_out:
                signOut();
                Log.d(TAG, "Sign Out Button Clicked");
                break;

        }
    }

    private void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            handleSignInResult(result);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult" + result.isSuccess());
        if (result.isSuccess()) {
            // Sign in successful, show authenticated on UI
            GoogleSignInAccount acct = result.getSignInAccount();
            statusTexView.setText(getString(R.string.hello) + acct.getDisplayName());


            Intent intent = new Intent(getBaseContext(), CompanyMainActivity.class);
            startActivity(intent);

        }
    }

    /** AMEETs Code which connect Google+ Auth with the ROBOTIC WEBSITE */
//    private void handleSignInResult(GoogleSignInResult result) {
//
//        // Handler for the JSON response when server returns ok
//        Response.Listener<String> responseListener = new Response.Listener<String>() {
//            @Override
//            public void onResponse(String loginResponse) {
//                // We are expecting a String as a response and have named it "loginResponse"
//                Log.i("Login Response:", loginResponse);
//                try {
//                    // We are converting the loginResponse String into a JSONObject in order to parse it
//                    JSONObject responseJSON = new JSONObject(loginResponse);
//                    // Here we're extracting the access_token from the response and naming it "token"
//                    String token = responseJSON.getString("access_token");
//                    // Here we are enabling the editing of the SharedPreferences in order to save
//                    // the String token within it
//                    SharedPreferences.Editor editor = mSharedPrefs.edit();
//                    editor.putString("token", token);
//                    editor.apply();
//                    // Upon Successful LogIn, Launch The Companies Feed
//                    Intent intent = new Intent(getBaseContext(), CompanyMainActivity.class);
//                    startActivity(intent);
//                } catch (JSONException je) {
//                    je.printStackTrace();
//                }
//            }
//        };
//
//        Response.Listener<String> convertResponseListener = new Response.Listener<String>() {
//            Response.Listener<String> responseListener = new Response.Listener<String>() {
//                @Override
//                public void onResponse(String loginResponse) {
//                    // We are expecting a String as a response and have named it "loginResponse"
//                    Log.i("Login Response:", loginResponse);
//                    try {
//                        // We are converting the loginResponse String into a JSONObject in order to parse it
//                        JSONObject responseJSON = new JSONObject(loginResponse);
//                        // Here we're extracting the access_token from the response and naming it "token"
//                        String token = responseJSON.getString("access_token");
//                        // Here we are enabling the editing of the SharedPreferences in order to save
//                        // the String token within it
//                        SharedPreferences.Editor editor = CompanyMainActivity.mSharedPrefs.edit();
//                        editor.putString("token", token);
//                        editor.apply();
//                        // Upon Successful LogIn, Launch The Companies Feed
//                        Intent intent = new Intent(getBaseContext(), CompanyMainActivity.class);
//                        startActivity(intent);
//                    } catch (JSONException je) {
//                        je.printStackTrace();
//                    }
//                }
//            };
//
//            // Handler for when the server returns an error response
//            Response.ErrorListener errorListener = new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.d(TAG, "onErrorResponse login() " + (new String(error.networkResponse.data)));
//                    error.printStackTrace();
//
//                }
//            };
//
//
//            @Override
//            public void onResponse(String convertResponse) {
//                // We are expecting a String as a response and have named it "loginResponse"
//                Log.i("Login Convert Response:", convertResponse);
//                try {
//                    // We are converting the loginResponse String into a JSONObject in order to parse it
//                    JSONObject responseJSON = new JSONObject(convertResponse);
//                    // Here we're extracting the access_token from the response and naming it "token"
//                    final String token = responseJSON.getString("access_token");
//                    // This is the body of the Request (What we are sending to the server in order to get an "ok" or "error" Response)
//                    //  - The Request has been named "request" https://robotic-site.herokuapp.com http://10.0.2.2:8000
//
//                    // Handler for when the server returns an error response
//                    Response.ErrorListener errorListener = new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Log.d(TAG, "onErrorResponse login() " + error);
//                            error.printStackTrace();
//
//                        }
//                    };
//
//                    StringRequest request = new StringRequest(Request.Method.POST, "https://robotic-site.herokuapp.com/rest-oauth/convert-token", responseListener, errorListener) {
//                        // Parameters for the POST Request.
//                        @Override
//                        protected Map<String, String> getParams() throws AuthFailureError {
//                            Map<String, String> params = new HashMap<>();
//                            params.put("client_id", "deMOvj2c5MNn2mqD1v6ShOHx9mqYktvFhWsAwNLs"); // wsytCUq3OF9aK8eEANZXBTJB6RnQq5cQMmZyDAPF
//                            params.put("client_secret", "w04Ci7zfTLPSjzYUY7Bnku93LlVpcu1IgPy0SOmtDotCYPC0V35iYsCuOCAArmtShEdMRCM5FwOi2cVE4SgwtOZW68fW20nJbpgm2Y5GnpyvsxPrALq9DVN6uyhq8Lvs"); // gHdvqyNYjZZ4R5nmOExkI4tEcKHRq82qKyQNmaMYnln9YE4stvh70ZNKWEXoNG6B99tep4IBFF0TgsJZ9IvcnDiP3bKFL6HRnge7yVFkvqf4p5Y75FQNNEMqU6RgT1XZ
//                            params.put("grant_type", "convert_token");
//                            params.put("token", token);
//                            params.put("backend", "google-oauth2");
//                            return params;
//                        }
//
//                    };
//
//                    mRequestQueue.add(request);
//
//                } catch (JSONException je) {
//                    je.printStackTrace();
//                }
//            }
//        };
//
//        Log.d(TAG, "handleSignInResult" + result.isSuccess());
//        if (result.isSuccess()) {
//            // Sign in successful, show authenticated on UI
//            GoogleSignInAccount acct = result.getSignInAccount();
//            statusTexView.setText(getString(R.string.hello) + acct.getDisplayName());
//
//            final String serverAuthCode = acct.getServerAuthCode();
//
//            // Handler for when the server returns an error response
//            Response.ErrorListener errorListener = new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.d(TAG, "onErrorResponse login() " + error);
//                    error.printStackTrace();
//
//                }
//            };
//
//            // Sends the serverAuthCode to the Robotic Site
//            StringRequest request = new StringRequest(Request.Method.POST, "https://robotic-site.herokuapp.com/rest-oauth/oauth2-wrapper/google-code-redirect", convertResponseListener, errorListener) {
//                // Parameters for the POST Request.
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("code", serverAuthCode);
//                    return params;
//                }
//            };
//
//            mRequestQueue.add(request);
//
//        } else {
//
//            Log.d(TAG, "status code: " + result.getStatus().getStatusCode());
//            Log.d(TAG, "status code message: " + result.getStatus().getStatusMessage());
//
//        }
//
//    }

    private void signOut() {

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                statusTexView.setText(getString(R.string.log_out));
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed" + connectionResult.isSuccess());

    }

}