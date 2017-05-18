package com.sbai.finance.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.igexin.sdk.GTServiceManager;

import static com.igexin.push.core.a.f;

/**
 * Created by ${wangJie} on 2017/5/3.
 */

public class PushService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        GTServiceManager.getInstance().onCreate(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
         super.onStartCommand(intent, flags, startId);
       return GTServiceManager.getInstance().onStartCommand(this, intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return GTServiceManager.getInstance().onBind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GTServiceManager.getInstance().onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        GTServiceManager.getInstance().onLowMemory();
    }
}
