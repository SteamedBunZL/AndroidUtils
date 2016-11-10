package com.tcl.security.virusengine;

import com.tcl.security.virusengine.entry.PackagedScanTask;
import com.tcl.security.virusengine.func_interface.IMcAfeeScanListener;
import com.tcl.security.virusengine.func_interface.McAfeeScanTimer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Steve on 2016/8/4.
 */
public class DefaultMcAfeeScanTimerImpl implements McAfeeScanTimer{


    @Override
    public void startTiming(PackagedScanTask task, long scanTime, final IMcAfeeScanListener listener) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (listener!=null)
                    listener.onEnd();
                timer.cancel();
            }
        },scanTime);
        if (listener!=null)
            listener.onStart();
    }



}
