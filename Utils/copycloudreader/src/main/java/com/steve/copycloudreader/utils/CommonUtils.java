package com.steve.copycloudreader.utils;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.steve.copycloudreader.app.ReaderApplication;

/**
 * Created by Steve on 2017/11/30.
 */

public class CommonUtils {

    public static Drawable getDrawable(int resid){
        return getResource().getDrawable(resid);
    }

    public static Resources getResource(){
        return ReaderApplication.getInstatnce().getResources();
    }
}
