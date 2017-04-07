package com.tcl.zhanglong.utils.data_binding;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.tcl.zhanglong.utils.R;
import com.tcl.zhanglong.utils.databinding.ResouceBinding;

/**
 * Created by Steve on 17/2/5.
 */

public class DataBingdingResouceActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ResouceBinding resouceBinding = DataBindingUtil.setContentView(this, R.layout.activity_resourcebinding);
        resouceBinding.setLarge(false);
    }
}
