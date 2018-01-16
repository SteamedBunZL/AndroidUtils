package com.zl.javacallc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private JNI jni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jni = new JNI();
    }


    public void string(View view){
        String str = jni.sayHello("I am from Java");
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }


    public void array(View view){

    }

    public void add(View view){
        int result = jni.add(1,99);
        Toast.makeText(this,result + "",Toast.LENGTH_SHORT).show();
    }


    public void checkpwd(View view){

    }
}
