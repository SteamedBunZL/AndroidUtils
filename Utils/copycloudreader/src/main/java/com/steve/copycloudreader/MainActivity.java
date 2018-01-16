package com.steve.copycloudreader;

import android.databinding.DataBindingUtil;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.steve.copycloudreader.databinding.ActivityMainBinding;
import com.steve.copycloudreader.databinding.NavHeaderMainBinding;
import com.steve.copycloudreader.ui.one.OneFragment;
import com.steve.copycloudreader.utils.StatusBarUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private Toolbar toolbar;
    private NavigationView navView;
    private DrawerLayout drawerLayout;

    private ViewPager vpContent;


    private ActivityMainBinding mBinding;
    private ImageView llTitleGank;
    private ImageView llTitleOne;
    private ImageView llTitleDou;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        initStatusView();
        
        initId();
        initContentFragment();
        initDrawerLayout();
    }

    private void initStatusView() {
        ViewGroup.LayoutParams layoutParams = mBinding.include.viewStatus.getLayoutParams();
        layoutParams.height = StatusBarUtil.getStatusBarHeight(this);
        mBinding.include.viewStatus.setLayoutParams(layoutParams);
    }

    private void initId(){
        drawerLayout = mBinding.drawerLayout;
        navView = mBinding.navView;

        vpContent = mBinding.include.vpContent;
        toolbar = mBinding.include.toolbar;
        llTitleGank = mBinding.include.ivTitleGank;
        llTitleOne = mBinding.include.ivTitleOne;
        llTitleDou = mBinding.include.ivTitleDou;
    }

    NavHeaderMainBinding bind;

    private void initDrawerLayout(){
        navView.inflateHeaderView(R.layout.nav_header_main);
        View headerView = navView.getHeaderView(0);
        bind = DataBindingUtil.bind(headerView);

    }

    private void initContentFragment(){
        ArrayList<Fragment> mFragmentList = new ArrayList<>();
        mFragmentList.add(new OneFragment());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_title_menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.iv_title_gank:
                if (vpContent.getCurrentItem()!=0){
                    llTitleGank.setSelected(true);
                    llTitleOne.setSelected(false);
                    llTitleDou.setSelected(false);
                    vpContent.setCurrentItem(0);
                }
                break;
            case R.id.iv_title_one:
                if (vpContent.getCurrentItem()!=1){
                    llTitleOne.setSelected(true);
                    llTitleGank.setSelected(false);
                    llTitleDou.setSelected(false);
                    vpContent.setCurrentItem(1);
                }
                break;
            case R.id.iv_title_dou:
                if (vpContent.getCurrentItem()!=2){
                    llTitleDou.setSelected(true);
                    llTitleOne.setSelected(false);
                    llTitleGank.setSelected(false);
                    vpContent.setCurrentItem(2);
                }
                break;
        }
    }
}
