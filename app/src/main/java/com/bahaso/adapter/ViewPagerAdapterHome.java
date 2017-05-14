package com.bahaso.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.bahaso.gold.GoldActivity;
import com.bahaso.home.HomeActivity;
import com.bahaso.profile.ProfilActivity;
import com.bahaso.voucher.VoucherActivity;


public class ViewPagerAdapterHome extends FragmentStatePagerAdapter {

    private String[] Titles; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapterStoreLocator is created
    private int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapterStoreLocator is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapterHome(FragmentManager fm, String[] mTitles, int mNumbOfTabsumb) {
        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new GoldActivity();
            case 1:
                return new HomeActivity();
            case 2:
                return new VoucherActivity();
        }
        return null;
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }

}
