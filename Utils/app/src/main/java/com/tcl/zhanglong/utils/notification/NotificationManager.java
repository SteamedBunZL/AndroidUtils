package com.tcl.zhanglong.utils.notification;

import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.tcl.zhanglong.utils.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Steve on 16/10/26.
 */

public class NotificationManager {

    /**
     * 测试最简单的通知
     * @param context
     */
    private void testNotification(Context context){
        android.app.NotificationManager manager = (android.app.NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder
                .setContentTitle("这是通知标题")
                .setContentText("这是通知内容")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)  //5.0  如果不设置小图标 会崩溃????
                //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))  //但是可以不设置大图标,哦哦,原来如此
                .build();
        manager.notify(1, notification);
    }
}
