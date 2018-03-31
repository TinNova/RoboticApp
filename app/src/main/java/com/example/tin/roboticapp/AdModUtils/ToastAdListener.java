package com.example.tin.roboticapp.AdModUtils;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

/**
 * Created by Tin on 18/03/2018.
 */

public class ToastAdListener extends AdListener {

    String TAG = "ToastAdListener";

    private Context mContext;
    private String mErrorReason;

    public ToastAdListener(Context context) {
        this.mContext = context;
    }

    @Override
    public void onAdLoaded() {

        Log.d(TAG, mContext + "onAdLoaded()");
    }

    @Override
    public void onAdOpened() {
        super.onAdOpened();

        Log.d(TAG, mContext + "onAdOpened()");
    }

    @Override
    public void onAdClosed() {
        super.onAdClosed();

        Log.d(TAG, mContext + "onAdClosed()");
    }

    @Override
    public void onAdLeftApplication() {
        super.onAdLeftApplication();

        Log.d(TAG, mContext + "onAdLeftApplication()");
    }

    @Override
    public void onAdFailedToLoad(int errorCode) {
        super.onAdFailedToLoad(errorCode);

        mErrorReason = "";
        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                mErrorReason = "Internal error";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                mErrorReason = "Invalid request";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                mErrorReason = "Network Error";
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                mErrorReason = "No fill";
                break;
        }

        Log.d(TAG, mContext + "onAdFailedToLoad(): " + mErrorReason);
    }

    public String getErrorReason() {
        return mErrorReason == null ? "" : mErrorReason;
    }
}
