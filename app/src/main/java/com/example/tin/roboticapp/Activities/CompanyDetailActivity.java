package com.example.tin.roboticapp.Activities;

import android.app.ActionBar;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.tin.roboticapp.Fragments.ArticlesFragment;
import com.example.tin.roboticapp.Fragments.CommentsFragment;
import com.example.tin.roboticapp.Fragments.FundamentalsFragment;
import com.example.tin.roboticapp.Fragments.QaFragment;
import com.example.tin.roboticapp.Fragments.SectionsPagerAdapter;
import com.example.tin.roboticapp.R;

public class CompanyDetailActivity extends AppCompatActivity {

    private static final String TAG = "CompanyDetailActivity";

    public static final String ARTICLES_LIST = "articles_List";

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

    private String mCompanyName;
    private String mCompanyTicker;
    private int mCompanyId;

    private ArticlesFragment mArticleFrag;
    private FundamentalsFragment mFundFrag;
    private CommentsFragment mDiscussionFrag;
    private QaFragment mQaFragment;



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



        } else {

            Toast.makeText(this, "ERROR: Data didn't load correctly", Toast.LENGTH_SHORT).show();

        }

        // The Toolbar in the activity_company_detail.xml
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each tab in the toolbar
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Create the ViewPager (the container is in the activity_company_detail.xml
        mViewPager = (ViewPager) findViewById(R.id.container);
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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_company_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
