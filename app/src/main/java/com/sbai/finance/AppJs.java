package com.sbai.finance;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.webkit.JavascriptInterface;

import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.battle.BattleListActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.utils.AppInfo;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.dialog.ShareDialog;

import java.util.HashSet;
import java.util.Set;

public class AppJs {

    private Context mContext;

    public AppJs(Context context) {
        mContext = context;
    }

    /**
     * 打开分享弹窗
     */
    @JavascriptInterface
    public void openShareDialog(String title, String description, String shareUrl, String shareThumbnailUrl) {
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            ShareDialog.with(activity)
                    .setTitle(title)
                    .setTitleVisible(false)
                    .setShareTitle(title)
                    .setShareDescription(description)
                    .setShareUrl(shareUrl)
                    .setShareThumbUrl(shareThumbnailUrl)
                    .hasFeedback(false)
                    .setListener(new ShareDialog.OnShareDialogCallback() {
                        @Override
                        public void onSharePlatformClick(ShareDialog.SHARE_PLATFORM platform) {

                        }

                        @Override
                        public void onFeedbackClick(View view) {
                        }
                    }).show();
        }
    }

    /**
     * 打开对战列表
     */
    @JavascriptInterface
    public void openBattlePage() {
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            Launcher.with(activity, BattleListActivity.class).execute();
        }
    }

    /**
     * 设备号
     */
    @JavascriptInterface
    public String getDeviceId() {
        if (mContext != null) {
            return AppInfo.getDeviceHardwareId(mContext);
        }
        return null;
    }

    /**
     * 显示兑换记录按钮
     */
    @JavascriptInterface
    public void showExchangeButton(final boolean isShow, final String text, final String url, final boolean isNeedLogin) {
        if (mContext instanceof WebActivity) {
            final WebActivity activity = (WebActivity) mContext;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isShow) {
                        activity.showRightView(text, url, isNeedLogin);
                    } else {
                        activity.hideRightView();
                    }
                }
            });
        }
    }

    /**
     * 截图
     */
    @JavascriptInterface
    public void screenShot() {
        if (mContext instanceof WebActivity) {
            WebActivity activity = (WebActivity) mContext;
            activity.screenShot();
        }
    }

    /**
     * 登录
     */
    @JavascriptInterface
    public void login() {
        if (mContext instanceof WebActivity) {
            WebActivity activity = (WebActivity) mContext;
            Launcher.with(activity, LoginActivity.class).execute();
        }
    }

}
