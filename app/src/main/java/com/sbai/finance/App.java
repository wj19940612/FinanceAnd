package com.sbai.finance;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.sbai.finance.activity.CatchCrashActivity;
import com.sbai.finance.net.API;
import com.sbai.finance.utils.Launcher;
import com.sbai.httplib.CookieManger;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

public class App extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        API.init(sContext.getCacheDir());
        CookieManger.getInstance().init(sContext.getFilesDir());

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
                /**
                 * 如果开发者调用Process.kill或者System.exit之类的方法杀死进程，请务必在此之前调用MobclickAgent.onKillProcess(Context context)方法，用来保存统计数据。
                 */
                System.exit(1);
            }
        });
    }

    public static Context getAppContext() {
        return sContext;
    }

    static {
        // TODO: 2017/5/16 帐号还未申请 所以还没配置 
        PlatformConfig.setWeixin("", "");
        PlatformConfig.setSinaWeibo("", "", "");
    }
}
