package com.tcl.security.daemon;


import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import com.tcl.security.cloudengine.ProjectEnv;


@TargetApi(21)
public class DaemonServiceLN extends JobService {
    private static final String TAG = ProjectEnv.bDebug ? "DaemonServiceLN" : DaemonServiceLP.class.getSimpleName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        try {
            Intent i = new Intent();
            i.setAction("com.tcl.applockpubliclibrary.library.module.lock.service.PrivacyService");
//            i.setClass(this, PrivacyService.class);
            startService(i);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

}