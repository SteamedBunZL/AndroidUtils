package com.steve.copycloudreader.app;

import android.app.Application;

/**
 * Created by Steve on 2017/11/30.
 */

public class ReaderApplication extends Application{

    private static ReaderApplication readerApplication;

    public static ReaderApplication getInstatnce(){
        return readerApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        readerApplication = this;
    }
}
