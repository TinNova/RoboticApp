package com.example.tin.roboticapp.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * There are two types of adapter:
 *      FragmentPagerAdapter, this allows users to swipe between fragments, but it loads all of
 *      the fragments at once, allowing for lighting fast transitions between the fragments
 *      (Ideal for situations when you know how many fragments there are)
 *
 *      FragmentStatePagerAdapter, this also allows user to swipe between fragments, but it only
 *      loads the current visible fragment and destroys it when user navigates to another.
 *      (Ideal for situation when you don't know how many fragments will appear, think recyclerView)
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    // This method will be used in the CompanyDetailActivity to add Fragments to the
    // mFragmentList and the Fragment Title mFragmentTitleList
    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    // Needed, otherwise error will appear, not sure what it does
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    // The current Fragments Title
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    // The current Fragment
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    // The number of Fragments to display
    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
