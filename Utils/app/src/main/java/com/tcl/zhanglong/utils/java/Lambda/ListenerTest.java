package com.tcl.zhanglong.utils.java.Lambda;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Steve on 16/11/26.
 */

public class ListenerTest {

    public void listenerTest(Context context){

        View view = new View(context);

        view.setOnClickListener((v) -> Toast.makeText(context,"Button Clicked",Toast.LENGTH_SHORT).show());
    }
}
