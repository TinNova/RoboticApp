package com.example.tin.roboticapp.IntentServices;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.tin.roboticapp.Activities.CompanyDetailActivity;
import com.example.tin.roboticapp.Models.Article;
import com.example.tin.roboticapp.Models.QACombined;
import com.example.tin.roboticapp.SQLite.FavouriteContract;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by Tin on 13/03/2018.
 */

public class SqlIntentService extends IntentService {

    private static final String TAG = "SqlIntentService";

    ArrayList<QACombined> mQaCombined;
    ArrayList<Article> mArticles;
    String mFundPrice;
    int mCompanyId;
    String mCompanyTicker;
    String mCompanyName;
    int mCompanySector;

    public SqlIntentService() {
        super("SqlIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null) {

            mQaCombined = intent.getParcelableArrayListExtra(CompanyDetailActivity.SQL_QA_LIST);
            mArticles = intent.getParcelableArrayListExtra(CompanyDetailActivity.SQL_ARTICLES_LIST);
            mFundPrice = intent.getStringExtra(CompanyDetailActivity.SQL_FUND);
            mCompanyId = intent.getIntExtra(CompanyDetailActivity.SQL_COMPANY_ID,0);
            mCompanyTicker = intent.getStringExtra(CompanyDetailActivity.SQL_COMPANY_TICKER);
            mCompanyName = intent.getStringExtra(CompanyDetailActivity.SQL_COMPANY_NAME);
            mCompanySector = intent.getIntExtra(CompanyDetailActivity.SQL_COMPANY_SECTOR,0);

            Log.d(TAG, "Intent Service Variables QA: " + mQaCombined);
            Log.d(TAG, "Intent Service Variables Articles: " + mArticles);
            Log.d(TAG, "Intent Service Variables Fund: " + mFundPrice);

            prepareData();

        }
    }

    private void prepareData() {

        Gson gson = new Gson();

        String mQaInputString = gson.toJson(mQaCombined);

        Log.d(TAG, "mQaInputString: " + mQaInputString);

        String mArticlesInputString = gson.toJson(mArticles);

        Log.d(TAG, "mArticlesInputString: " + mArticlesInputString);

        addToDatabase(mCompanyId, mCompanyTicker, mCompanyName, mCompanySector, mQaInputString, mArticlesInputString, mFundPrice);

    }

    /**
     * Code Which Inserts A Company To The SQL Database
     */
    private void addToDatabase(int companyId, String companyTicker, String companyName, int companySector, String companyQa, String companyArticles, String companyPrice) {

        // ContentValues passes the values onto the SQLite insert query
        ContentValues cv = new ContentValues();

        // We don't need to include the ID of the row, because BaseColumns in the Contract Class does this
        // for us. If we didn't have the BaseColumns we would have to add the ID ourselves.
        cv.put(FavouriteContract.FavouriteEntry.COLUMN_COMPANY_ID, companyId);
        cv.put(FavouriteContract.FavouriteEntry.COLUMN_COMPANY_TICKER, companyTicker);
        cv.put(FavouriteContract.FavouriteEntry.COLUMN_COMPANY_NAME, companyName);
        cv.put(FavouriteContract.FavouriteEntry.COLUMN_COMPANY_SECTOR, companySector);
        cv.put(FavouriteContract.FavouriteEntry.COLUMN_COMPANY_QA_LIST, companyQa);
        cv.put(FavouriteContract.FavouriteEntry.COLUMN_COMPANY_ARTICLES_LIST, companyArticles);
        cv.put(FavouriteContract.FavouriteEntry.COLUMN_COMPANY_PRICE, companyPrice);

        // Insert the new company to the Favourite SQLite Db via a ContentResolver
        Uri uri = getContentResolver().insert(FavouriteContract.FavouriteEntry.CONTENT_URI, cv);

        Log.d(TAG, "The Data Added: " + cv);

        // Display the URI that's returned with a Toast
        if (uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
            Log.d(TAG, "INSERT: " + getBaseContext() + uri.toString());
        }

    }
}
