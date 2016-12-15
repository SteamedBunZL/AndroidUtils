package com.tcl.zhanglong.utils.activity.intentfilter;

import android.content.Intent;
import android.content.pm.PackageManager;

import com.tcl.zhanglong.utils.activity.BaseOneButtonActivity;

/**
 * Created by Steve on 16/12/15.
 */

public class ActionActivity extends BaseOneButtonActivity{

    @Override
    protected void click() {
        super.click();
        //不加入category default启动不了!!!!!!!!
        Intent intent = new Intent("com.util.actionaaction");
        //PackageManager.MATCH_DEFAULT_ONLY,仅匹配那些在intent-filter中声明了category.DEFAULT的
        //能保证startActivity一定成功,如果不加可能启动失败
        if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)!=null){
            startActivity(intent);
        }

        
    }
}
