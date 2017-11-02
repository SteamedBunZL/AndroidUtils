package com.clean.spaceplus.cleansdk.boost.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.util.ArrayList;
import java.util.List;

import static com.jaredrummler.android.processes.AndroidProcesses.getRunningAppProcesses;

/**
 * @author zengtao.kuang
 * @Description: 用于获取运行进程
 * @date 2016/4/18 20:10
 * @copyright TCL-MIG
 */
public class ProcessManager {

    private static IProcessManager sImplCompat;

    static {
        if (Build.VERSION.SDK_INT <= 20) {
            //  4.0 - 4.4
            sImplCompat = new ProcessManagerImpl14();
        } else if (Build.VERSION.SDK_INT <= 23) {
            //  5.0 - 6.0
            sImplCompat = new ProcessManagerImpl21();
        } else {
            //  7.0
            sImplCompat = new ProcessManagerImpl24();
        }

    }

    /**
     * 返回手机版本的对应实现
     */
    public static IProcessManager getCurrent(){
        return sImplCompat;
    }


    public interface IProcessManager{
        /* 获取后台运行进程的信息 */
        List<ActivityManager.RunningAppProcessInfo> getRunningAppProcessInfo(Context ctx);

        //
    }




    /* =================== IMPL ===================*/



    /**  4.0 - 4.4 */
    private static class ProcessManagerImpl14 implements IProcessManager{
        @Override
        public List<ActivityManager.RunningAppProcessInfo> getRunningAppProcessInfo(Context ctx) {
            return ((ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
        }
    }


    /** 5.0 - 6.0 */
    private static class ProcessManagerImpl21 implements IProcessManager{
        @Override
        public List<ActivityManager.RunningAppProcessInfo> getRunningAppProcessInfo(Context ctx) {
            List<AndroidAppProcess> processes = getRunningAppProcesses();
            if(processes == null || processes.isEmpty())
                return new ProcessManagerImpl14().getRunningAppProcessInfo(ctx);
            List<ActivityManager.RunningAppProcessInfo> appProcessInfos = new ArrayList<>();
            for(AndroidAppProcess appProcess:processes){
                if(appProcess.name.contains("/"))
                    continue;
                ActivityManager.RunningAppProcessInfo info = new ActivityManager.RunningAppProcessInfo(appProcess.name, appProcess.pid, new String[]{appProcess.getPackageName()});
                info.uid = appProcess.uid;
                appProcessInfos.add(info);
            }
            return appProcessInfos;
        }
    }


    /**  7.0  */
    private static class ProcessManagerImpl24 implements IProcessManager{
        @Override
        public List<ActivityManager.RunningAppProcessInfo> getRunningAppProcessInfo(Context ctx) {
            ///获取全部运行service的信息///
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> runningServiceList = am.getRunningServices(200);
            ///根据运行service的信息，提取出pid，uid，package name等有用信息进行拼装///
            List<ActivityManager.RunningAppProcessInfo> appProcessInfos = new ArrayList<>();
            ArrayList<Integer> pidList = new ArrayList<>();
            for (ActivityManager.RunningServiceInfo item : runningServiceList) {
                // pid : If non-zero, this is the process the service is running in. so..
                if(item.pid == 0)
                    continue;
                //因为是依据service获取进程，会有多个service运行在同一个进程的情况，去重一下
                if(pidList.contains(item.pid))
                    continue;
                pidList.add(item.pid);
                String pckName = item.process.contains(":") ? item.process.split(":")[0] : item.process;
                ActivityManager.RunningAppProcessInfo info = new ActivityManager.RunningAppProcessInfo(item.process, item.pid, new String[]{ pckName });
                info.uid = item.uid;
                appProcessInfos.add(info);
            }
            return appProcessInfos;
        }
    }

}
