package com.steve.copycloudreader.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bumptech.glide.Glide;
import com.steve.copycloudreader.MainActivity;
import com.steve.copycloudreader.R;
import com.steve.copycloudreader.app.ConstantsImageUrl;
import com.steve.copycloudreader.databinding.ActivitySplashBinding;
import com.steve.copycloudreader.utils.CommonUtils;

import java.util.Random;

/**
 * Created by Steve on 2017/11/30.
 */

public class SplashActivity extends AppCompatActivity{

    private ActivitySplashBinding mBinding;
    private boolean isIn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        int i = new Random().nextInt(ConstantsImageUrl.TRANSITION_URLS.length);
        //先显示默认图
        mBinding.ivDefaultPic.setImageDrawable(CommonUtils.getDrawable(R.drawable.img_transition_default));

        Glide.with(this).load(ConstantsImageUrl.TRANSITION_URLS[i])
                .placeholder(R.drawable.img_transition_default)
                .error(R.drawable.img_transition_default)
                .into(mBinding.ivPic);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.ivDefaultPic.setVisibility(View.GONE);
            }
        },1500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toMainActivity();
            }
        },3500);

    }

    private void toMainActivity(){
        if (isIn)
            return;

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        finish();
        isIn = true;
    }
}
