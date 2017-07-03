package com.sbai.finance.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ScrollView;

import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.battle.BattleActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.local.SysTime;
import com.sbai.finance.net.API;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.TimerHandler;
import com.sbai.finance.view.RequestProgress;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.websocket.PushCode;
import com.sbai.finance.websocket.WSClient;
import com.sbai.finance.websocket.WSPush;
import com.sbai.finance.websocket.callback.OnPushReceiveListener;
import com.sbai.httplib.ApiIndeterminate;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends AppCompatActivity implements
        ApiIndeterminate, TimerHandler.TimerCallback {

    public static final String ACTION_TOKEN_EXPIRED = "com.sbai.fx.token_expired";

    public static final String EX_TOKEN_EXPIRED_MESSAGE = "token_expired_msg";

    public static final int REQ_CODE_TOKEN_EXPIRED_LOGIN = 800;

    public static final int REQ_CODE_USERDATA = 1001;

    protected String TAG;

    private TimerHandler mTimerHandler;
    private RequestProgress mRequestProgress;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LocalUser.getUser().logout();
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

        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    private OnPushReceiveListener<WSPush<Battle>> mPushReceiveListener = new OnPushReceiveListener<WSPush<Battle>>() {
        @Override
        public void onPushReceive(WSPush<Battle> battleWSPush) {
            switch (battleWSPush.getContent().getType()) {
                case PushCode.BATTLE_JOINED:
                    if (!(getActivity() instanceof BattleActivity)) {
                        if (battleWSPush.getContent() != null) {
                            Battle data = (Battle) battleWSPush.getContent().getData();
                            showJoinBattleDialog(data);
                        }
                    }
                    break;
            }
            onBattlePushReceived(battleWSPush);
        }
    };

    protected void onBattlePushReceived(WSPush<Battle> battleWSPush) {
    }

    private void showJoinBattleDialog(final Battle battle) {
        //只有在自己是房主的情况下才显示
        if (LocalUser.getUser().isLogin()) {
            boolean isRoomCreator = battle.getLaunchUser() == LocalUser.getUser().getUserInfo().getId();
            if (isRoomCreator) {
                SmartDialog.single(getActivity(), getString(R.string.quick_join_battle))
                        .setTitle(getString(R.string.join_battle))
                        .setPositive(R.string.quick_battle, new SmartDialog.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                dialog.dismiss();
                                Launcher.with(getActivity(), BattleActivity.class)
                                        .putExtra(Launcher.EX_PAYLOAD, battle)
                                        .putExtra(BattleActivity.PAGE_TYPE, BattleActivity.PAGE_TYPE_VERSUS)
                                        .execute();
                            }
                        }).setNegative(R.string.cancel)
                        .show();
            }
        }
    }

    private void scrollToTop(View view) {
        if (view instanceof AbsListView) {
            ((AbsListView) view).smoothScrollToPositionFromTop(0, 0);
        } else if (view instanceof RecyclerView) {
            ((RecyclerView) view).smoothScrollToPosition(0);
        } else if (view instanceof ScrollView) {
            ((ScrollView) view).smoothScrollTo(0, 0);
        }
    }

    protected void scrollToTop(View anchor, final View view) {
        anchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollToTop(view);
            }
        });
    }

    protected void translucentStatusBar() {
        //make full transparent statusBar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    protected void addStatusBarHeightTopPadding(View view) {
        int paddingTop = getStatusBarHeight();
        view.setPadding(0, paddingTop, 0, 0);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 友盟统计埋点
     *
     * @param eventKey
     */
    protected void umengEventCount(String eventKey) {
        MobclickAgent.onEvent(getActivity(), eventKey);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Preference.get().setForeground(true);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(ACTION_TOKEN_EXPIRED));
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
        WSClient.get().setOnPushReceiveListener(mPushReceiveListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Preference.get().setForeground(false);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
        WSClient.get().removePushReceiveListener(mPushReceiveListener);
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
