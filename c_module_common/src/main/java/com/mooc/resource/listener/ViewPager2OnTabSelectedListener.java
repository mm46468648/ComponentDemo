package com.mooc.resource.listener;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

public class ViewPager2OnTabSelectedListener  implements TabLayout.OnTabSelectedListener {
    private final ViewPager2 viewPager;

    public ViewPager2OnTabSelectedListener(ViewPager2 viewPager) {
        this.viewPager = viewPager;
    }

    @Override
    public void onTabSelected(@NonNull TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        // No-op
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        // No-op
    }
}
