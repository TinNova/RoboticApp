package com.example.tin.roboticapp.Activities;

import android.app.ActionBar;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.tin.roboticapp.Fragments.ArticlesFragment;
import com.example.tin.roboticapp.Fragments.CommentsFragment;
import com.example.tin.roboticapp.Fragments.FundamentalsFragment;
import com.example.tin.roboticapp.Fragments.QaFragment;
import com.example.tin.roboticapp.Adapters.SectionsPagerAdapter;
import com.example.tin.roboticapp.IntentServices.SqlIntentService;
import com.example.tin.roboticapp.Models.Article;
import com.example.tin.roboticapp.Models.QACombined;
import com.example.tin.roboticapp.R;
import com.example.tin.roboticapp.SQLite.FavouriteContract;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CompanyDetailActivity extends AppCompatActivity {

    private static final String TAG = "CompanyDetailActivity";

    public static final String ARTICLES_LIST = "articles_List";

    /** Strings for the SQL Intent Service */
    public static final String SQL_QA_LIST = "sql_qa_list";
    public static final String SQL_ARTICLES_LIST = "sql_articles_list";
    public static final String SQL_FUND = "sql_fund_list";
    public static final String SQL_COMPANY_ID = "sql_company_id";
    public static final String SQL_COMPANY_TICKER = "sql_company_ticker";
    public static final String SQL_COMPANY_NAME = "sql_company_name";
    public static final String SQL_COMPANY_SECTOR = "sql_company_sector";



    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ArticlesFragment mArticleFrag;
    private FundamentalsFragment mFundFrag;
    private CommentsFragment mDiscussionFrag;
    private QaFragment mQaFragment;

    private String mCompanyName;
    private String mCompanyTicker;
    private int mCompanyId;
    private int mCompanySector;
    private int mListType;
    private int m_id;
    private int isSaved;


    // Used to Insert Data into SQLite Database
    private String mQaCombine;
    private ArrayList<QACombined> mQaInputArray;
    private ArrayList<Article> mArticleInputArray;
    private String mArticles;
    private String mPrice;

    int mFragmentToLoad = 0;

    // This Is For The Favourite Icon In The Menu Item
    public static MenuItem favouriteMenu;


    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_detail);

        Log.d(TAG, "onCreate");


        /** Extracting Data From Intent */
        Intent intent = getIntent();
        if (intent.getExtras() != null) {

            // Here we've taken the Extra containing the the "TheSteps" Model and put it in the variable mTheSteps
            mCompanyName = intent.getStringExtra(CompanyMainActivity.CURRENT_COMPANY_NAME);
            mCompanyTicker = intent.getStringExtra(CompanyMainActivity.CURRENT_COMPANY_TICKER);
            mCompanyId = intent.getIntExtra(CompanyMainActivity.CURRENT_COMPANY_ID, 0);
            mCompanySector = intent.getIntExtra(CompanyMainActivity.CURRENT_COMPANY_SECTOR, 0);
            mListType = intent.getIntExtra(CompanyMainActivity.LIST_TYPE,0);

            // if Intent was triggered from the SQL list, we in addition should take the _id
            if (intent.getIntExtra(CompanyMainActivity.LIST_TYPE,0) != 0) {

                m_id = intent.getIntExtra(CompanyMainActivity.CURRENT_COMPANY__ID,0);

                Log.d(TAG, "Selected m_id: " + m_id);

            }

            Log.d(TAG, "Intent From CompanyMainActivity");

        } else {

            Toast.makeText(this, "ERROR: Data didn't load correctly", Toast.LENGTH_SHORT).show();

        }

        // This checks if the this Activity was started from the QaDetailActivity, if yes we want
        // to load the QAFragment
        if (intent.getStringExtra(QaDetailActivity.INTENT_FROM_QA_DETAIL_ACTIVITY) != null) {

            // The QAFragment position is passed from the QaDetailActivity
            mFragmentToLoad = intent.getIntExtra(QaDetailActivity.FRAGMENT_POSITION, 1);
            mCompanyId = intent.getIntExtra(QaDetailActivity.COMPANY_ID, -1);
            Log.d(TAG, "Intent From QaDetailActivity");

        }

        // The Toolbar in the activity_company_detail.xml
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each tab in the toolbar
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Create the ViewPager (the container is in the activity_company_detail.xml
        mViewPager = (ViewPager) findViewById(R.id.container);

        // Set the number of off-screen fragment that must be loaded on each side of the current one.
        mViewPager.setOffscreenPageLimit(3);

        // Launch the setupViewPager method and pass in the newly created mViewPager
        setupViewPager(mViewPager);

        // Create the tabLayout and connect it to the mViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }

    /**
     * THIS IS WHERE THE FRAGMENTS ARE BEING ADDED, SO TRY "setArguments()" HERE!!!
     */
    // THE REASON WE WANT TO DO THIS IS BECAUSE OnCreateView Is Often Restarted Mutliple Times When Swiping Between Fragments
    // So It Is Not Able To Persist Data, But The .getItem() Method Is Only Called Once, So By Adding The Arguments Here, They
    // Will Be Passed To The Adapter and To The .getItem()

    // Uses the addFragment method within the SectionsPagerAdapter class to add the Fragments and
    // tab titles, then sets the SectionsPagerAdapter to the viewPager
    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // args are the same for every Fragment
        Bundle argsForFrags = new Bundle();
        argsForFrags.putString(CompanyMainActivity.CURRENT_COMPANY_NAME, mCompanyName);
        argsForFrags.putString(CompanyMainActivity.CURRENT_COMPANY_TICKER, mCompanyTicker);
        argsForFrags.putInt(CompanyMainActivity.CURRENT_COMPANY_ID, mCompanyId);
        argsForFrags.putInt(CompanyMainActivity.LIST_TYPE, mListType);

        // if m_id exists, we need to pass that to Fragments as well, as the data will be loaded
        // from SQL not the API
        if (mListType == 1) {

            argsForFrags.putInt(CompanyMainActivity.CURRENT_COMPANY__ID, m_id);

        }

        // Create the mArticlesFrag
        mArticleFrag = new ArticlesFragment();
        // Placing the Bundle Arguments into the mArticlesFrag
        mArticleFrag.setArguments(argsForFrags);

        mFundFrag = new FundamentalsFragment();
        mFundFrag.setArguments(argsForFrags);

        mDiscussionFrag = new CommentsFragment();
        mDiscussionFrag.setArguments(argsForFrags);

        mQaFragment = new QaFragment();
        mQaFragment.setArguments(argsForFrags);

        adapter.addFragment(mFundFrag, getString(R.string.tab_text_1));
        adapter.addFragment(mQaFragment, getString(R.string.tab_text_2));
        adapter.addFragment(mArticleFrag, getString(R.string.tab_text_3));
        adapter.addFragment(mDiscussionFrag, getString(R.string.tab_text_4));
        viewPager.setAdapter(adapter);

        // Here we are specifying which Fragment should appear first, but default it is 0, but if
        // this activity was started from QaDetailActivity it will be 1 meaning the QAFragment will load
        mViewPager.setCurrentItem(mFragmentToLoad);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_company_detail, menu);
        favouriteMenu = menu.findItem(R.id.favourite);
        favouriteMenu.setVisible(false);

        if (mListType != 0) {

            favouriteMenu.setIcon(R.drawable.ic_star_white_24dp);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            case R.id.favourite:

                if (mListType == 0) {

                    // Change the Heart Icon from white outline to white heart
                    favouriteMenu.setIcon(R.drawable.ic_star_white_24dp);

                    // Method which adds Movie to SQL
                    startSqlIntentService();
                    Toast.makeText(this, "Added To Favourites!", Toast.LENGTH_SHORT).show();

                } else {

                    // Change the Heart Icon from white to white outline,
                    // Then remove the company from the db
                    favouriteMenu.setIcon(R.drawable.ic_star_border_white_24dp);
                    removeCompany(m_id);

                }

        }

        return super.onOptionsItemSelected(item);
    }


    private void startSqlIntentService() {

        Intent saveSqlIntent = new Intent(this, SqlIntentService.class);

        Bundle saveSqlIntentBundle = new Bundle();

        if (mQaFragment.mQaCombined != null) {
            saveSqlIntentBundle.putParcelableArrayList(SQL_QA_LIST, mQaFragment.mQaCombined);
        }
        if (mArticleFrag.mArticles != null) {
            saveSqlIntentBundle.putParcelableArrayList(SQL_ARTICLES_LIST, mArticleFrag.mArticles);
        }
        if (mFundFrag.mPrice != null) {
            saveSqlIntentBundle.putString(SQL_FUND, mFundFrag.mPrice);
        }

        saveSqlIntentBundle.putString(SQL_COMPANY_TICKER, mCompanyTicker);
        saveSqlIntentBundle.putString(SQL_COMPANY_NAME, mCompanyName);
        saveSqlIntentBundle.putInt(SQL_COMPANY_SECTOR, mCompanySector);


        saveSqlIntent.putExtras(saveSqlIntentBundle);

        startService(saveSqlIntent);

    }

    /**
     * This Method Deletes a Movie form the Database
     * - It takes a long as the input which is the ID of the Row
     * - It returns a boolean to say if the deletion was successful or not
     */
    private void removeCompany(int id) {

        // Here we are building up the uri using the row_id in order to tell the ContentResolver
        // to delete the item
        String stringRowId = Long.toString(id);
        Uri uri = FavouriteContract.FavouriteEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringRowId).build();

        getContentResolver().delete(uri, null, null);

        Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        Log.d(TAG, "REMOVE: " + getBaseContext() + uri.toString());
    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop");

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d(TAG, "onRestart");

        /** onRestart and if statement should appear,
         * if (A bundle has been passed through by)
         */

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy");

    }
}

// TODO Improvements:
// TODO 1: Allow user to remove company from SQL DB whether they accessed the DetailActivity via FTSE 350 or saveList
// TODO 2: Prevent the ability to add the same Company twice to the SQL DB
// TODO 3: If user keeps clicking the star icon, it should add, then remove, then add, then remove ect the Company form the DB