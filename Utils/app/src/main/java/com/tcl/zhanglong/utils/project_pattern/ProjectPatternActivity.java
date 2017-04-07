package com.tcl.zhanglong.utils.project_pattern;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tcl.zhanglong.utils.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Steve on 17/2/5.
 */

public class ProjectPatternActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.button_mvc)
    Button button_mvc;
    @BindView(R.id.button_mvp)
    Button button_mvp;
    @BindView(R.id.button_mvvm)
    Button button_mvvm;
    @BindView(R.id.button_mvpdatabinding)
    Button button_mvpdatabinding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projectpattern);
        ButterKnife.bind(this);

        button_mvc.setOnClickListener(this);
        button_mvp.setOnClickListener(this);
        button_mvvm.setOnClickListener(this);
        button_mvpdatabinding.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_mvc:
                Toast.makeText(this,"MVP",Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_mvp:
                Toast.makeText(this,"MVP",Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_mvvm:
                Toast.makeText(this,"MVVM",Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_mvpdatabinding:
                Toast.makeText(this,"MVP-DataBinding",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
