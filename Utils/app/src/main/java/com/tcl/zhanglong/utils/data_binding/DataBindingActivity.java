package com.tcl.zhanglong.utils.data_binding;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.tcl.zhanglong.utils.R;
import com.tcl.zhanglong.utils.data_binding.pojo.User;
import com.tcl.zhanglong.utils.databinding.AcitivtyDatabindingBinding;

/**
 * Created by Steve on 17/2/5.
 */

public class DataBindingActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AcitivtyDatabindingBinding binding = DataBindingUtil.setContentView(this, R.layout.acitivty_databinding);
        User user = new User("fei","Liang",15);
        binding.setUser(user);
    }
}
