package com.bahaso.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.bahaso.fragmenthelper.FragmentBahaso;

import java.util.List;

/**
 * Created by shiperus on 3/10/2017.
 */

public class ViewPagerAdapterCase extends FragmentStatePagerAdapter {
    private int numOfTabs;
    private List<FragmentBahaso> listCaseFragment;
    public ViewPagerAdapterCase(FragmentManager fm , int numOfTabs , List<FragmentBahaso> listCaseFragment) {
        super(fm);
        this.listCaseFragment = listCaseFragment;
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        if(listCaseFragment!=null) {
            Log.i("PagerAdapter","Pager Run " + position);

            return listCaseFragment.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
