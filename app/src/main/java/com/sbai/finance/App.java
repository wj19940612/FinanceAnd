package com.sbai.finance;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import com.sbai.finance.activity.CatchCrashActivity;
import com.sbai.finance.net.API;
import com.sbai.finance.utils.Launcher;
import com.sbai.httplib.CookieManger;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

public class App extends MultiDexApplication {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        API.init(sContext.getCacheDir());
        CookieManger.getInstance().init(sContext.getFilesDir());

        if (!BuildConfig.IS_PROD) {
            handleUncaughtException();
        }
        
        if (BuildConfig.DEBUG) {
            handleUncaughtException();
            Config.DEBUG = true;
        }
        //友盟
        UMShareAPI.get(this);

    }

    private void handleUncaughtException() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Intent intent = new Intent(sContext, CatchCrashActivity.class);
                intent.putExtra(Launcher.EX_PAYLOAD, ex);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                Preference.get().setForeground(false);
                System.exit(1);
            }
        });
    }

    public static Context getAppContext() {
        return sContext;
    }

    static {
        // 注意：以下全为正式的 appId & secret
        PlatformConfig.setWeixin("wxf53be05ac695d994", "aab33a762834f9f0722190a67aefcef0");
        PlatformConfig.setSinaWeibo("522354160", "af7a654293ada586a62534ac9fd03845", "");
    }
}
