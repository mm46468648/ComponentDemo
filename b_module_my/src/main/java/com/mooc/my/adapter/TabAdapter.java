package com.mooc.my.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TabAdapter extends FragmentStatePagerAdapter {

    private final ArrayList<String> titleList;
    private final ArrayList<Fragment> fragments;

    public TabAdapter(FragmentManager fragmentManager, ArrayList<String> titleList, ArrayList<Fragment> fragments) {
        super(fragmentManager);
        this.titleList = titleList;
        this.fragments = fragments;
    }

    @NotNull
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

