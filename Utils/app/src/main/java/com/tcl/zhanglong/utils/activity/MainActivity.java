package com.tcl.zhanglong.utils.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tcl.zhanglong.utils.R;
import com.tcl.zhanglong.utils.Utils.DebugLog;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private Button button2;

    private Button button3;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);


        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
    }

    private List testException(){
        List<String> list = new ArrayList<>();

        try {
            list.add("Hello");
            list.get(0);
            DebugLog.e("正常走");
        } catch (Exception e) {
            DebugLog.e("走了上面");
            e.printStackTrace();
        }finally {
            DebugLog.e("走了finallys");
        }
        DebugLog.e("走了下面");
        return list;
    }

    private void testObject(){
        List<Entry> list = new ArrayList<>();
        Entry entry =null;
        int i= 1;
        while(i<=3){
            entry = new Entry();
            entry.name = "zhangsan" + i;
            i++;
            list.add(entry);
        }
        DebugLog.e("new List %s",list.toString());
    }


    class Entry{

        public String name;

        @Override
        public String toString() {
            return "name : " + name;
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button2:
                Toast.makeText(this,"Start Function",Toast.LENGTH_SHORT).show();
                Intent intent  = new Intent(this,FunctionListActivity.class);
                startActivity(intent);
                break;
            case R.id.button3:
                getAndroidId();
                break;
        }
    }

    private String getAndroidId(){
        String ANDROID_ID = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
        DebugLog.e("Android Id %s",ANDROID_ID);
        return ANDROID_ID;
    }





}
