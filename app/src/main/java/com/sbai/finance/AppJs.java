package com.sbai.finance;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.webkit.JavascriptInterface;

import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.battle.BattleListActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.utils.AppInfo;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.dialog.ShareDialog;
import com.sbai.httplib.CookieManger;

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
        if (mContext != null && mContext instanceof Activity) {
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
        if (mContext != null && mContext instanceof Activity) {
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
    public void showExchangeButton(boolean isShow, String text, final String url) {
        if (mContext != null && mContext instanceof Activity) {
            final Activity activity = (Activity) mContext;
            if (activity instanceof WebActivity) {
                TitleBar titleBar = ((WebActivity) activity).getTitleBar();
                if (titleBar != null) {
                    if (isShow) {
                        titleBar.setRightVisible(true);
                        titleBar.setRightText(text);
                        titleBar.setOnRightViewClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (LocalUser.getUser().isLogin()) {
                                    Launcher.with(activity, WebActivity.class)
                                            .putExtra(WebActivity.EX_URL, url)
                                            .putExtra(WebActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                                            .execute();
                                } else {
                                    Launcher.with(activity, LoginActivity.class).execute();
                                }
                            }
                        });
                    } else {
                        titleBar.setRightVisible(false);
                    }
                }
            }

        }
    }
}
