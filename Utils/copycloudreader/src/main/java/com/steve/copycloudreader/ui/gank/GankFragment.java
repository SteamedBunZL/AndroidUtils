package com.steve.copycloudreader.ui.gank;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.steve.copycloudreader.R;
import com.steve.copycloudreader.base.BaseFragment;
import com.steve.copycloudreader.databinding.FragmentGankBinding;

import java.util.ArrayList;

/**
 * Created by Steve on 2017/12/5.
 */

public class GankFragment extends BaseFragment<FragmentGankBinding>{

    private ArrayList<String> mTitleList = new ArrayList<>(4);
    private ArrayList<Fragment> mFragments = new ArrayList<>(4);

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        showLoading();

    }

    private void initFragmentList(){
        mTitleList.add("每日推荐");
        mTitleList.add("福利");
        mTitleList.add("干货订制");
        mTitleList.add("大安卓");
    }

    @Override
    public int setContent() {
        return R.layout.fragment_gank;
    }
}
