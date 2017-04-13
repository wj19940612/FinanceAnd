package com.sbai.finance.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.sbai.finance.Preference;
import com.sbai.finance.model.SysTime;
import com.sbai.finance.net.API;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.TimerHandler;
import com.sbai.finance.view.RequestProgress;
import com.sbai.finance.view.SmartDialog;
import com.sbai.httplib.ApiIndeterminate;

public class BaseActivity extends AppCompatActivity implements
        ApiIndeterminate, TimerHandler.TimerCallback {

    public static final String ACTION_TOKEN_EXPIRED = "com.sbai.fx.token_expired";

    public static final String EX_TOKEN_EXPIRED_MESSAGE = "token_expired_msg";

    public static final int REQ_CODE_TOKEN_EXPIRED_LOGIN = 800;

    protected String TAG;

    private TimerHandler mTimerHandler;
    private RequestProgress mRequestProgress;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String expiredMessage = intent.getStringExtra(EX_TOKEN_EXPIRED_MESSAGE);
//            SmartDialog.single(getActivity(), expiredMessage)
//                    .setCancelableOnTouchOutside(false)
//                    .setCancelListener(new SmartDialog.OnCancelListener() {
//                        @Override
//                        public void onCancel(Dialog dialog) {
//                            dialog.dismiss();
//                            onTokenExpiredCancel();
//                        }
//                    })
//                    .setNegative(R.string.cancel, new SmartDialog.OnClickListener() {
//                        @Override
//                        public void onClick(Dialog dialog) {
//                            dialog.dismiss();
//                            onTokenExpiredCancel();
//                        }
//                    })
//                    .setPositive(R.string.login, new SmartDialog.OnClickListener() {
//                        @Override
//                        public void onClick(Dialog dialog) {
//                            dialog.dismiss();
//                            Launcher.with(getActivity(), LoginActivity.class)
//                                    .executeForResult(REQ_CODE_TOKEN_EXPIRED_LOGIN);
//                        }
//                    }).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_TOKEN_EXPIRED_LOGIN && resultCode != RESULT_OK) {
            onTokenExpiredCancel();
        }
    }

    private void onTokenExpiredCancel() {
        //LocalUser.getUser().logout();
        ///Netty.get().keepALive();
        Launcher.with(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .execute();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        mRequestProgress = new RequestProgress(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                API.cancel(TAG);
            }
        });
        SysTime.getSysTime().sync();
    }

    protected void translucentStatusBar() {
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);
        // set a custom tint color for all system bars
        tintManager.setTintColor(Color.parseColor("#00000000"));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Preference.get().setForeground(true);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(ACTION_TOKEN_EXPIRED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Preference.get().setForeground(false);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        API.cancel(TAG);
        SmartDialog.dismiss(this);
        mRequestProgress.dismissAll();
        stopScheduleJob();
    }

    protected FragmentActivity getActivity() {
        return this;
    }

    @Override
    public void onHttpUiShow(String tag) {
        if (mRequestProgress != null) {
            mRequestProgress.show(this);
        }
    }

    @Override
    public void onHttpUiDismiss(String tag) {
        if (mRequestProgress != null) {
            mRequestProgress.dismiss();
        }
    }

    protected void startScheduleJob(int millisecond, long delayMillis) {
        stopScheduleJob();

        if (mTimerHandler == null) {
            mTimerHandler = new TimerHandler(this);
        }
        mTimerHandler.sendEmptyMessageDelayed(millisecond, delayMillis);
    }

    protected void startScheduleJob(int millisecond) {
        startScheduleJob(millisecond, 0);
    }

    protected void stopScheduleJob() {
        if (mTimerHandler != null) {
            mTimerHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onTimeUp(int count) {
    }
}
