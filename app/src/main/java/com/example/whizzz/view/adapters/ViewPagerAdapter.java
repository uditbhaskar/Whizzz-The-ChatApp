package com.example.whizzz.view.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> listFragment = new ArrayList<>();
    private final List<String> listTitles = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
       return listFragment.get(position);
    }

    @Override
    public int getCount() {
        return listTitles.size();
    }


    public void addFragment(Fragment fragment, String title) {
        listFragment.add(fragment);
        listTitles.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return listTitles.get(position);
    }

}
