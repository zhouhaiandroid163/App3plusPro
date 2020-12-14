package com.zjw.apps3pluspro.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 全局fragment的父类
 *
 * @author Administrator
 */
public abstract class BaseFragment extends Fragment {
    public Context context;
    public View view;
    protected Bundle savedInstanceState;
    private Unbinder bind;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 上下文环境
        context = getActivity();
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = initView();
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        bind = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    @Override
    public void onDestroy() {
        if(bind != null){
            bind.unbind();
        }
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {

        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public abstract View initView();

    public abstract void initData();

}
