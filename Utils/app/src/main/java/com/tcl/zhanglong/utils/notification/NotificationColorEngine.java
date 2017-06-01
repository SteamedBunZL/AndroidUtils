package com.tcl.zhanglong.utils.notification;

import android.app.Notification;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steve on 17/4/10.
 */

public class NotificationColorEngine {

    private String DUMMY_TITLE = "DUMMY_TITLE";
    private int titleColor;
    private static final double COLOR_THRESHOLD = 180.0;

    public int getNotificationColor(Context context){
        if (context instanceof AppCompatActivity){
            return getNotificationColorCompat(context);
        }else{
            return getNotificationColorInternal(context);
        }
    }


    private int getNotificationColorInternal(Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(DUMMY_TITLE);
        Notification notification = builder.build();
        ViewGroup notificationRoot = (ViewGroup) notification.contentView.apply(context,new FrameLayout(context));
        TextView title = (TextView) notificationRoot.findViewById(android.R.id.title);
        if (title == null){
            iteratorView(notificationRoot, new Filter() {
                @Override
                public void filter(View view) {
                    if (view instanceof TextView){
                        TextView textView = (TextView) view;
                        if (DUMMY_TITLE.equals(textView.getText().toString())){
                            titleColor = textView.getCurrentTextColor();
                        }
                    }
                }
            });
            return titleColor;
        }else{
            return title.getCurrentTextColor();
        }
    }

    private int getNotificationColorCompat(Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.build();
        int layoutId = notification.contentView.getLayoutId();
        ViewGroup notificationRoot = (ViewGroup) LayoutInflater.from(context).inflate(layoutId,null);
        TextView title = (TextView) notificationRoot.findViewById(android.R.id.title);
        if (title == null){
            final List<TextView> textViews = new ArrayList<>();
            iteratorView(notificationRoot, new Filter() {
                @Override
                public void filter(View view) {
                    if (view instanceof TextView){
                        textViews.add((TextView) view);
                    }
                }
            });

            float minTextSize = Integer.MIN_VALUE;
            int index = 0;
            float currentSize = 0;
            for(int i = 0,j = textViews.size();i<j;i++){
                currentSize = textViews.get(i).getTextSize();
                if (currentSize>minTextSize){
                    minTextSize = currentSize;
                    index = i;
                }
            }
            return textViews.get(index).getCurrentTextColor();

        }else {
            return title.getCurrentTextColor();
        }
    }

    private void iteratorView(View view,Filter filter){
        if (view == null || filter == null)
            return;

        filter.filter(view);

        if (view instanceof ViewGroup){
            ViewGroup container = (ViewGroup) view;
            for(int i = 0,j = container.getChildCount();i<j;i++){
                iteratorView(container.getChildAt(i),filter);
            }
        }
    }

    interface Filter{
        void filter(View view);
    }

    public boolean isDarkNotificationBar(Context context){
        return !isColorSimilar(Color.BLACK,getNotificationColor(context));
    }

    public static boolean isColorSimilar(int baseColor,int color){
        int simpleBaseColor = baseColor | 0xff000000;
        int simpleColor = color | 0xff000000;
        int baseRed = Color.red(simpleBaseColor) - Color.red(simpleColor);
        int baseBlue = Color.blue(simpleBaseColor) - Color.blue(simpleColor);
        int baseGreen = Color.green(simpleBaseColor) - Color.green(simpleColor);
        double value = Math.sqrt(baseRed * baseRed + baseGreen * baseGreen + baseBlue * baseBlue);
        if (value < COLOR_THRESHOLD)
            return true;
        return false;
    }
}
