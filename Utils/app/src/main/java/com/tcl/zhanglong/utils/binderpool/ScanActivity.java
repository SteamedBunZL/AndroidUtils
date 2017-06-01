package com.tcl.zhanglong.utils.binderpool;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.tcl.zhanglong.binder.aidl.IScanApk;
import com.tcl.zhanglong.binder.aidl.IScanApkListener;
import com.tcl.zhanglong.binder.aidl.IScanFile;
import com.tcl.zhanglong.utils.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Steve on 17/4/7.
 */

public class ScanActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.btn_scan_apk)
    Button mStartScanApk;

    @BindView(R.id.btn_stop_scan_apk)
    Button mStopScanApk;

    //private IScanApk mScanApk;

    private IScanApkListener scanApkListener;

    private BinderPool binderPool;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);

        binderPool = BinderPool.getInstance(this);
        scanApkListener = new IScanApkListenerImpl();

        mStartScanApk.setOnClickListener(this);
        mStopScanApk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //if (mScanApk==null) return;
        switch (v.getId()){
            case R.id.btn_scan_apk:
                IBinder apkBinder = binderPool.queryBinder(BinderPool.BINDER_SCAN_APK);
                IScanApk scanApk = ScanApkImpl.asInterface(apkBinder);
                try {
                    scanApk.registerListener(scanApkListener);
                    scanApk.startScanApk();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_stop_scan_apk:
                IBinder fileBinder = binderPool.queryBinder(BinderPool.BINDER_SCAN_FILE);
                IScanFile scanFile = ScanFileImpl.asInterface(fileBinder);
                try {
                    scanFile.startScanFile();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    /**
     * 启动ScanActivity
     * @param context
     */
    public static void startScanActivity(Context context){
        Intent intent = new Intent(context,ScanActivity.class);
        context.startActivity(intent);
    }


}
