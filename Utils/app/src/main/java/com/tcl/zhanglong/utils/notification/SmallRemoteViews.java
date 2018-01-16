package com.tcl.zhanglong.utils.notification;

import android.content.Context;
import android.widget.RemoteViews;

import com.tcl.zhanglong.utils.R;

/**
 * Created by Steve on 2017/12/17.
 */

public class SmallRemoteViews {

    private RemoteViews mRemoteViews;

    public SmallRemoteViews(Context context) {
        this.mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.rv_small);
    }

    public RemoteViews getRemoteViews() {
        return mRemoteViews;
    }
}
