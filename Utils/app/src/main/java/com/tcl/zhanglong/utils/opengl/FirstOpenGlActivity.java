package com.tcl.zhanglong.utils.opengl;

import android.widget.LinearLayout;

import com.tcl.zhanglong.utils.R;
import com.tcl.zhanglong.utils.activity.BaseActivity;

/**
 * Created by Steve on 16/12/20.
 */

public class FirstOpenGlActivity extends BaseActivity{


    private MySurfaceView mSurfaceView;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_first_opengl;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        mSurfaceView = new MySurfaceView(this);
        mSurfaceView.requestFocus();//获取焦点
        mSurfaceView.setFocusableInTouchMode(true);//设置为可触控
        LinearLayout ll = (LinearLayout) findViewById(R.id.main_liner);
        ll.addView(mSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSurfaceView.onResume();
    }
}
