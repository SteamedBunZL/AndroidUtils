package com.tcl.zhanglong.utils.IPC_Messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.tcl.zhanglong.utils.Utils.DebugLog;

/**
 * 最大的缺点是不能并发,消息要一个个处理
 * 优点是轻量级,实现简单
 * Created by Steve on 16/12/19.
 */

public class MessengerService extends Service{

    private static final String TAG = "MessengerService";

    private static class MessengerHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    DebugLog.w("Receive msg from Client:" + msg.getData().getString("msg"));
                    Messenger client = msg.replyTo;
                    Message replyMessage = Message.obtain(null,1);
                    Bundle bundle = new Bundle();
                    bundle.putString("reply","恩,你的消息已经收到");
                    replyMessage.setData(bundle);
                    try {
                        client.send(replyMessage);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private final Messenger mMessenger = new Messenger(new MessengerHandler());


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
