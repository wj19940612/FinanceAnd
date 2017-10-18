package com.sbai.finance;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.sbai.finance.activity.CatchCrashActivity;
import com.sbai.finance.net.API;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.Logger;
import com.sbai.httplib.CookieManger;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.util.Stack;

public class App extends Application implements Application.ActivityLifecycleCallbacks {

	private static Context sContext;
	public static Stack<Activity> sActivityStack;
	private int mCount = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		sContext = this;
		API.init(sContext.getCacheDir());
		registerActivityLifecycleCallbacks(this);
		CookieManger.getInstance().init(sContext.getFilesDir());

		if (!BuildConfig.IS_PROD) {
			handleUncaughtException();
			Logger.init();
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
			public void uncaughtException(Thread t, Throwable e) {
				Intent intent = new Intent(sContext, CatchCrashActivity.class);
				intent.putExtra(Launcher.EX_PAYLOAD, e);
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

	public static Stack<Activity> getActivityStack() {
		return sActivityStack;
	}

	public static void registerActivity(Activity activity) {
		if (sActivityStack == null) {
			synchronized (App.class) {
				if (sActivityStack == null) {
					sActivityStack = new Stack<>();
				}
			}
		}
		sActivityStack.push(activity);
	}

	public static void unregisterActivity(Activity activity) {
		if (sActivityStack != null) {
			sActivityStack.remove(activity);
		}
	}

	static {
		// 注意：以下全为正式的 appId & secret
		PlatformConfig.setWeixin("wxf53be05ac695d994", "aab33a762834f9f0722190a67aefcef0");
		PlatformConfig.setSinaWeibo("522354160", "af7a654293ada586a62534ac9fd03845", "");
	}

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
		mCount++;
	}

	@Override
	public void onActivityStarted(Activity activity) {

	}

	@Override
	public void onActivityResumed(Activity activity) {

	}

	@Override
	public void onActivityPaused(Activity activity) {

	}

	@Override
	public void onActivityStopped(Activity activity) {
		mCount--;
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

	}

	@Override
	public void onActivityDestroyed(Activity activity) {

	}

	public int getCount() {
		return mCount;
	}
}
