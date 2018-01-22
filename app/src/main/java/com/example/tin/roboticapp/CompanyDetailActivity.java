package com.example.tin.roboticapp;

import android.app.ActionBar;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.example.tin.roboticapp.Fragments.ArticlesFragment;
import com.example.tin.roboticapp.Fragments.FundamentalsFragment;
import com.example.tin.roboticapp.Fragments.QaFragment;
import com.example.tin.roboticapp.Fragments.ReportsFragment;
import com.example.tin.roboticapp.Fragments.SectionsPagerAdapter;

import static com.example.tin.roboticapp.CompListActivity.CURRENT_COMPANY_ID;
import static com.example.tin.roboticapp.CompListActivity.CURRENT_COMPANY_NAME;
import static com.example.tin.roboticapp.CompListActivity.CURRENT_COMPANY_TICKER;

public class CompanyDetailActivity extends AppCompatActivity {

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

    private static final String TAG = "CompanyDetailActivity";

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_detail);

        Log.d(TAG, "onCreate: Starting.");

        /** Extracting Data From Intent */
        // Extracting Data from the Intent that started this activity
        Intent intentFromMainActivity = getIntent();

        // Here we've taken the Extra containing the the "TheSteps" Model and put it in the variable mTheSteps
        mCompanyName = intentFromMainActivity.getStringExtra(CURRENT_COMPANY_NAME);
        mCompanyTicker = intentFromMainActivity.getStringExtra(CURRENT_COMPANY_TICKER);
        mCompanyId = intentFromMainActivity.getIntExtra(CURRENT_COMPANY_ID, 0);


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

        // Floating action button that appears across all Fragments? Would be better if the Fragments
        // themselves had their own tailored floating buttons when needed
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    //TODO: Do all network calls in the Activity, the Fragments should only recieve the values needed as they will
    // be created and destroyed as the user swipes through them, so we don't want to constantly connect to the internet
    // to download data, it should only be done once.
    //TODO: Do the Articles JSON Feed Call Here Ideally via another Class, and pass in an ArrayList of Article to the ArticlesFragment


//TODO: THIS IS WHERE THE FRAGMENTS ARE BEING ADDED, SO TRY "setArguments()" HERE!!!
    // THE REASON WE WANT TO DO THIS IS BECAUSE OnCreateView Is Often Restarted Mutliple Times When Swiping Between Fragments
    // So It Is Not Able To Persist Data, But The .getItem() Method Is Only Called Once, So By Adding The Arguments Here, They
    // Will Be Passed To The Adapter and To The .getItem()

    // Uses the addFragment method within the SectionsPagerAdapter class to add the Fragments and
    // tab titles, then sets the SectionsPagerAdapter to the viewPager
    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Creating a Bundle to hold the companyName & companyTicker
        Bundle argsForArticleFrag = new Bundle();
        argsForArticleFrag.putString(CURRENT_COMPANY_NAME, mCompanyName);
        argsForArticleFrag.putString(CURRENT_COMPANY_TICKER, mCompanyTicker);

        // Create the mArticlesFrag
        mArticleFrag = new ArticlesFragment();
        // Placing the Bundle Arguments into the mArticlesFrag
        mArticleFrag.setArguments(argsForArticleFrag);


        adapter.addFragment(new FundamentalsFragment(), getString(R.string.tab_text_1));
        adapter.addFragment(new QaFragment(), getString(R.string.tab_text_2));
        adapter.addFragment(mArticleFrag, getString(R.string.tab_text_3));
        adapter.addFragment(new ReportsFragment(), getString(R.string.tab_text_4));
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

}
