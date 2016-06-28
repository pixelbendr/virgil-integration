package com.psyphertxt.android.cyfa.ui.adapters;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.ui.fragment.ContextUserFragment;
import com.psyphertxt.android.cyfa.ui.fragment.StatusFragment;

public class StatusPagerAdapter extends FragmentPagerAdapter {

    private String[] tabs;

    public StatusPagerAdapter(Context context, FragmentManager fm) {
        super(fm);

        tabs = context.getResources().getStringArray(R.array.status_tabs);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new ContextUserFragment();

            default:
                return new StatusFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public int getCount() {
        return tabs.length;
    }

}

