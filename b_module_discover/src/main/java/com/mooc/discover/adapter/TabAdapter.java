package com.mooc.discover.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class TabAdapter extends FragmentStatePagerAdapter {

    private ArrayList<String> titleList;
    private ArrayList<Fragment> fragments;

    public TabAdapter(FragmentManager fragmentManager, ArrayList<String> titleList, ArrayList<Fragment> fragments) {
        super(fragmentManager);
        this.titleList = titleList;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }


    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }


    @Override
    public int getCount() {
        return titleList.size();
    }

}

