package com.example.music.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class BenDiYuePuPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<String> list;
    private ArrayList<Fragment> fragments;

    public BenDiYuePuPagerAdapter(@NonNull FragmentManager fm, ArrayList<String> list, ArrayList<Fragment> fragments) {
        super(fm);
        this.list = list;
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return fragments != null ? fragments.size() : 0;
    }
}
