package com.tcl.security.daemon;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.tcl.security.cloudengine.ProjectEnv;

public class DaemonStartup {
    private static final String TAG = ProjectEnv.bDebug ? "DaemonStartup" : DaemonStartup.class.getSimpleName();
    private static final int jobId = 1;
    private static final long jobInterval = 60l * 60l * 1000l;

    public static void start(Context c) {
        if (Build.VERSION.SDK_INT >= 21) {
            startLN(c);
        } else {
            DaemonServiceLP.start(c);
        }
    }

    @TargetApi(21)
    private static void startLN(Context c) {
        try {
            JobScheduler scheduler =(JobScheduler)c.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            scheduler.cancel(jobId);
            JobInfo.Builder builder = new JobInfo.Builder(jobId, new ComponentName(c, DaemonServiceLN.class));
            builder.setPeriodic(jobInterval);
            scheduler.schedule(builder.build());
        } catch (Exception e) {
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "start error:\n");
                e.printStackTrace();
            }
        }
    }
}