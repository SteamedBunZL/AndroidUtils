package com.steve.copycloudreader.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.steve.copycloudreader.R;
import com.steve.copycloudreader.utils.PerfectClickListener;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by Steve on 2017/12/5.
 */

public abstract class BaseFragment<SV extends ViewDataBinding> extends Fragment{

    protected SV bindingView;

    protected boolean mIsVisible = false;

    private LinearLayout mLlProgressBar;

    private LinearLayout mRefresh;

    protected RelativeLayout mContainer;

    private AnimationDrawable mAnimationDrawable;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View ll = inflater.inflate(R.layout.fragment_base,null);
        bindingView = DataBindingUtil.inflate(getActivity().getLayoutInflater(),setContent(),null,false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bindingView.getRoot().setLayoutParams(params);
        mContainer = (RelativeLayout) ll.findViewById(R.id.container);
        mContainer.addView(bindingView.getRoot());
        return ll;
    }


    public abstract int setContent();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()){
            mIsVisible = true;
            onVisible();
        }else {
            mIsVisible = false;
            onInvisible();
        }
    }

    protected void loadData(){
    }

    protected void onInvisible(){
    }

    protected void onVisible(){
        loadData();
    }

    protected void onRefresh(){

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLlProgressBar = getView(R.id.ll_progress_bar);
        ImageView img = getView(R.id.img_progress);

        mAnimationDrawable = (AnimationDrawable) img.getDrawable();
        if (!mAnimationDrawable.isRunning()){
            mAnimationDrawable.start();
        }

        mRefresh = getView(R.id.ll_error_refresh);
        mRefresh.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                showLoading();
                onRefresh();
            }
        });
        bindingView.getRoot().setVisibility(View.GONE);
    }


    protected <T extends View> T getView(int id){
        return (T)getView().findViewById(id);
    }

    protected void showLoading(){
        if (mLlProgressBar.getVisibility()!=View.VISIBLE){
            mLlProgressBar.setVisibility(View.VISIBLE);
        }

        if (!mAnimationDrawable.isRunning()){
            mAnimationDrawable.start();
        }

        if (bindingView.getRoot().getVisibility()!=View.GONE){
            bindingView.getRoot().setVisibility(View.GONE);
        }

        if (mRefresh.getVisibility()!=View.GONE){
            mRefresh.setVisibility(View.GONE);
        }
    }

    /**
     * 加载完成的状态
     */
    protected void showContentView() {
        if (mLlProgressBar.getVisibility() != View.GONE) {
            mLlProgressBar.setVisibility(View.GONE);
        }
        // 停止动画
        if (mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
        if (mRefresh.getVisibility() != View.GONE) {
            mRefresh.setVisibility(View.GONE);
        }
        if (bindingView.getRoot().getVisibility() != View.VISIBLE) {
            bindingView.getRoot().setVisibility(View.VISIBLE);
        }
    }

    /**
     * 加载失败点击重新加载的状态
     */
    protected void showError() {
        if (mLlProgressBar.getVisibility() != View.GONE) {
            mLlProgressBar.setVisibility(View.GONE);
        }
        // 停止动画
        if (mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
        if (mRefresh.getVisibility() != View.VISIBLE) {
            mRefresh.setVisibility(View.VISIBLE);
        }
        if (bindingView.getRoot().getVisibility() != View.GONE) {
            bindingView.getRoot().setVisibility(View.GONE);
        }
    }


}
