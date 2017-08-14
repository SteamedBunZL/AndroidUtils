package com.tcl.security.daemon;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.tcl.security.cloudengine.ProjectEnv;
import com.tcl.security.cloudengine.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.InflaterInputStream;


public class DaemonServiceLP extends Service {
    private static final String TAG = ProjectEnv.bDebug ? "DaemonServiceLP" : DaemonServiceLP.class.getSimpleName();
    private static String pkgName;
    private static final String uninst = "uninst";
    private static String key = "h2PUMdDStbH6//bH/W0=";
    private static String valueFirst = "h2O0NNPV0f7/+aj91g==";
    private static String valueRestart = "h2PUtdLRttPV/v/z8vz5";
    private static String localSockPath;
    private static String arguments1 = "h2O0M6rX0bbT1dax0tU0s7GqLzKstzEwKjS30zAp1LGy0dI101Yr+HZr2xg/+gut7ayMDAwLoz+rz7tUsH99UZGVTr2OgZbuL19dq09G/zAg3Bo=";
    private static String arguments2 = "h2O0M6rX0bbT1dax0tU0s7GqLyrS+s3q8+uLDOttDIwKze00TAp1rGy0dM201Qq+3do2xo/+Qms7KyMDw8Loz+rzzjzY/5e1lU69joGW7i87XatP+v8gINmR";
    private static String arguments3 = "h2PacjbyPL/3/6ReftqyB2a4W6t/z25uCO1PlFoExwmUwVzm1R++6dHJavqt+pH6mlycuFVerJLHrRBGDnF4gQsO6H3MRQ/PctBR21qqo9FDEC0M+tZ2fEiemCgbJQP/2zXWUw==";
    private static String arguments4 = "h2Pacr7xfM/z+0Ad8Dsa7sIJfyvopzlX6du+SfwVEPaPJcZTmdd8+KZHJ1HTb60U9/xb/aPMNXBx+lZ7EZUybkmSUkMfXkxBUbwPddEjsxxw7LZR+iAIg+AamBOtjg8lU/izESH2pS7Tyg==";
    private static String[] hawkArgv;
    private static String[] ehawkArgv;
    private static final String hawkPkg = "com.hawk.security";
    private static final String ehawkPkg = "com.ehawk.antivirus.applock.wifi";
    private static AtomicInteger isRunning = new AtomicInteger(0);

    private WorkerHandler handler;

    static {
        key = Utils.xde(key);
        valueFirst = Utils.xde(valueFirst);
        valueRestart = Utils.xde(valueRestart);
        arguments1 = Utils.xde(arguments1);
        arguments2 = Utils.xde(arguments2);
        arguments3 = Utils.xde(arguments3);
        arguments4 = Utils.xde(arguments4);
        hawkArgv = new String[]{arguments1, arguments2};
        ehawkArgv = new String[]{arguments3, arguments4};
    }   

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null)
            return  super.onStartCommand(intent , flags , startId);

        String v = intent.getStringExtra(key);
        boolean first = valueFirst.equals(v);
        boolean restart = valueRestart.equals(v);
        if (!first && !restart) {
            if (ProjectEnv.bDebug) {
                Log.w(TAG, "unknown service.");
            }
            return START_NOT_STICKY;
        }
        if (!isRunning.compareAndSet(0, 1)) {
            if (first == true) {
                return START_NOT_STICKY;
            }
        }
        // call applock service explicitly.
        if (restart == true) {
            if (ProjectEnv.bDebug) {
                Log.d(TAG, "startup applock.");
            }
            Intent i = new Intent();
            i.setAction("com.tcl.applockpubliclibrary.library.module.lock.service.PrivacyService");
//            i.setClass(this, PrivacyService.class);
            startService(i);
        }

        pkgName = this.getApplicationContext().getPackageName();
		localSockPath = new File(this.getApplicationContext().getFilesDir(), "daemon").getAbsolutePath();
        HandlerThread worker = new HandlerThread("worker");
        worker.start();
        handler = new WorkerHandler(worker.getLooper());
        handler.obtainMessage(MSG_INIT, uninst + "0").sendToTarget();
        return START_NOT_STICKY;
    }
	
	 private static String getSystemProperty(String key) {
        try {
            Class<?> clazz= Class.forName("android.os.SystemProperties");
            Method get = clazz.getMethod("get", String.class);
            return (String)get.invoke(clazz, key);
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isArmCpu() {
        String arm = "arm";
        String value = getSystemProperty("ro.product.cpu.abilist");
        if (value != null) {
            if (value.toLowerCase().indexOf(arm) != -1) {
                return true;
            }
        }
        value = getSystemProperty("ro.product.cpu.abi");
        if (value != null) {
            if (value.toLowerCase().indexOf(arm) != -1) {
                return true;
            }
        }
        if (ProjectEnv.bDebug) {
            Log.w(TAG, "unsupport non-arm cpu now.");
        }
        return false;
    }

    public static void start(Context c) {
		if (isArmCpu()) {
            Intent i = new Intent();
            i.setClass(c, DaemonServiceLP.class);
            i.putExtra(key, valueFirst);
            c.startService(i);
        }
    }

    private  interface ICallback {
        void onResponse(byte[] data);
    }

    private int send(String data, ICallback callback) {
        LocalSocket client = new LocalSocket();
        try {
            client.connect(new LocalSocketAddress(localSockPath));
        } catch (Exception e) {
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "connect error.");
            }
            return RUN_ERROR;
        }

        if (client.isConnected()) {
            try {
                client.getOutputStream().write(data.getBytes("utf-8"));
                if (callback != null) {
                    DataInputStream input = new DataInputStream(new BufferedInputStream(client.getInputStream()));
                    byte[] buffer = new byte[1024];
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    while (true) {
                        int len = input.read(buffer);
                        if (len < 0) {
                            break;
                        }
                        if (len > 0) {
                            bos.write(buffer, 0, len);
                        }
                    }
                    callback.onResponse(bos.toByteArray());
                }
                return 0;
            } catch (Exception e) {
                if (ProjectEnv.bDebug) {
                    Log.e(TAG, "send error:\n");
                    e.printStackTrace();
                }
                return -1;
            } finally {
                try {
                    client.close();
                } catch (Exception e) {
                }
            }
        }
        if (ProjectEnv.bDebug) {
            Log.e(TAG, "connect broken.");
        }
        return -1;
    }

    private String getWatchPath(String path) {
        int index = path.indexOf(pkgName);
        if (index < 0) {
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "path:" + path + " not include pkg:" + pkgName);
            }
            return null;
        }
        return path.substring(0, index) + pkgName;
    }

    private static final int MSG_INIT = 1;
    private static final int MSG_LOOP = 2;
    private static final int MSG_LOOP_START = 3*1000;
    private static final int MSG_LOOP_INTERVAL = 3*1000;
    private static final int RUN_RETRY = 3;
    private static final int RUN_ERROR = -99;

    private class WorkerHandler extends Handler {
        private String path;
        private int retry;

        public WorkerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            int id = msg.what;
            switch (id) {
                case MSG_INIT:
                    String name = (String) msg.obj;
                    path = extractExe(name);
                    if (path != null) {
                        String watchPath = getWatchPath(path);
                        if (watchPath != null) {
                            String[] argv;
                            if (pkgName.equals(hawkPkg)) {
                                argv = hawkArgv;
                            } else if (pkgName.equals(ehawkPkg)) {
                                argv = ehawkArgv;
                            } else {
                                if (ProjectEnv.bDebug) {
                                    Log.e(TAG, "unsupported pkg:" + pkgName);
                                }
                                return;
                            }

                            String arguments = argv[0];
                            if (Build.VERSION.SDK_INT >= 17) {
                                arguments = argv[1];
                            }
                            try {
                                Runtime.getRuntime().exec(new String[]{path, localSockPath, watchPath, arguments});
                                sendEmptyMessageDelayed(MSG_LOOP, MSG_LOOP_START);
                            } catch (Exception e) {
                                if (ProjectEnv.bDebug) {
                                    Log.e(TAG, "exe error:");
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    break;
                case MSG_LOOP:
                    int result = send(pkgName, null);
                    if (result == 0) {
                        sendEmptyMessageDelayed(MSG_LOOP, MSG_LOOP_INTERVAL);
                    } else if (result == RUN_ERROR) {
                        if (ProjectEnv.bDebug) {
                            Log.w(TAG, "re-init");
                        }
                        obtainMessage(MSG_INIT, uninst + "1").sendToTarget();
                    } else {
                        if (ProjectEnv.bDebug) {
                            Log.e(TAG, "retry:" + retry);
                        }
                        if (++retry <= RUN_RETRY) {
                            sendEmptyMessageDelayed(MSG_LOOP, MSG_LOOP_INTERVAL);
                        }
                    }
                    break;
            }

        }
    }

    private static void copyStream(InputStream source, OutputStream target) throws IOException {
        final int bsize = 4096;
        byte[] buffer = new byte[bsize];
        int length = 0;
        while ((length = source.read(buffer)) > 0) {
            target.write(buffer, 0, length);
        }
        target.flush();
    }

    private String extractExe(String name) {
        Context c = this.getApplication();
        InputStream in = null;
        OutputStream out = null;
        File f = new File(c.getFilesDir(), uninst);
        if (f.exists()) {
            f.delete();
        }
        try {
            in = new BufferedInputStream(new InflaterInputStream(c.getAssets().open(name)));
            out = new BufferedOutputStream(new FileOutputStream(f));
            copyStream(in, out);
        } catch (Exception e) {
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "extract error:");
                e.printStackTrace();
            }
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                }
            }
        }

        boolean b = f.setReadable(true);
        if (!b) {
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "set readable failed.");
            }
            return null;
        }
        b = f.setExecutable(true);
        if (!b) {
            if (ProjectEnv.bDebug) {
                Log.e(TAG, "set executable failed.");
            }
            return null;
        }
        return f.getAbsolutePath();
    }


}


