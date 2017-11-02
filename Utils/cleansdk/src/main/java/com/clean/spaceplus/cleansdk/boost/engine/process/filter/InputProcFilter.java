package com.clean.spaceplus.cleansdk.boost.engine.process.filter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengtao.kuang
 * @Description: 输入法过滤
 * @date 2016/4/6 16:24
 * @copyright TCL-MIG
 */
public class InputProcFilter {

    private List<String> mInputerArray = new ArrayList<String>();

    public InputProcFilter(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent();

        intent.setAction("android.view.InputMethod");
        List<ResolveInfo> resList = pm.queryIntentServices(intent,
                PackageManager.GET_SERVICES);

        if (resList != null && resList.size() > 0) {
            for (ResolveInfo info : resList) {
                if (info.serviceInfo != null
                        && !TextUtils.isEmpty(info.serviceInfo.packageName))

                    mInputerArray.add(info.serviceInfo.packageName);
            }
        }
    }

    public boolean isInputerPkg(String pkgName) {
        return mInputerArray.contains(pkgName);
    }
}
