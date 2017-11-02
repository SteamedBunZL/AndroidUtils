package com.tcl.zhanglong.utils.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.steve.commonlib.DebugLog;

import static android.R.attr.type;

/**
 * ━━━━━━神兽出没━━━━━━
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　>      <　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　⌒　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃  护码神兽
 * 　　　　┃　　　┃
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * <p>
 * Created by Steve on 17/11/1.
 * <p>
 * ━━━━━━━━━━━━━━━━
 */

public class NotificationBroadcastReceiver extends BroadcastReceiver{

    public static final String TYPE = "type";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);

        if (action.equals("notification_clicked")) {
            //处理点击事件
            DebugLog.w("notification_clicked");
        }

        if (action.equals("notification_cancelled")) {
            //处理滑动清除和点击删除事件
            DebugLog.e("notification_cancelled");
        }
    }
}
