package com.zjw.apps3pluspro.adapter;

import android.view.ViewGroup;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 主页viewpager 适配器
 */

public class HomePagerAdapter extends FragmentPagerAdapter {


    private List<Fragment> fragLists;

    public HomePagerAdapter(FragmentManager fm, List<Fragment> fragLists) {
        super(fm);
        this.fragLists = fragLists;
        // TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int arg0) {
        // TODO Auto-generated method stub
        return fragLists.get(arg0);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return fragLists.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub
        super.destroyItem(container, position, object);
    }
}
