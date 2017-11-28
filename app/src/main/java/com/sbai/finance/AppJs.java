package com.sbai.finance;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.view.View;
import android.webkit.JavascriptInterface;

import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.arena.RewardActivity;
import com.sbai.finance.activity.battle.BattleListActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.fund.VirtualProductExchangeActivity;
import com.sbai.finance.activity.miss.MissProfileDetailActivity;
import com.sbai.finance.model.mine.cornucopia.AccountFundDetail;
import com.sbai.finance.model.system.JsOpenAppPageType;
import com.sbai.finance.net.Client;
import com.sbai.finance.utils.AppInfo;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.dialog.ShareDialog;

public class AppJs {

    private static final String TAG = "AppJs";
    private static final String ONLY_WE_CHAT_SHARE = "onlywx";

    private Context mContext;

    public AppJs(Context context) {
        mContext = context;
    }

    @JavascriptInterface
    public void openShareDialogWithoutReward(String title, String description, String shareUrl, String shareThumbnailUrl) {
        if (mContext instanceof Activity) {
            final Activity activity = (Activity) mContext;
            ShareDialog.with(activity)
                    .setTitle(mContext.getString(R.string.share_to))
                    .setTitleVisible(true)
                    .setShareTitle(title)
                    .setShareDescription(description)
                    .setShareUrl(shareUrl)
                    .setShareThumbUrl(shareThumbnailUrl)
                    .hasFeedback(false)
                    .show();
        }
    }

    /**
     * 打开分享弹窗
     */
    @JavascriptInterface
    public void openShareDialog(String title, String description, String shareUrl, String shareThumbnailUrl) {
        openShareDialog(title, description, shareUrl, shareThumbnailUrl, "");
    }

    /**
     * @param title
     * @param description
     * @param shareUrl
     * @param shareThumbnailUrl
     * @param shareChannel      分享渠道配置 onlyywx 只有微信分享
     */
    @JavascriptInterface
    public void openShareDialog(String title, String description, String shareUrl, String shareThumbnailUrl, String shareChannel) {
        boolean isOnlyWeChatShare = ONLY_WE_CHAT_SHARE.equalsIgnoreCase(shareChannel);
        if (mContext instanceof Activity) {
            final Activity activity = (Activity) mContext;
            ShareDialog.with(activity)
                    .setTitle(mContext.getString(R.string.share_and_get_ingot))
                    .setTitleVisible(true)
                    .setShareTitle(title)
                    .hasWeiBo(!isOnlyWeChatShare)
                    .setShareDescription(description)
                    .setShareUrl(shareUrl)
                    .setShareThumbUrl(shareThumbnailUrl)
                    .hasFeedback(false)
                    .setListener(new ShareDialog.OnShareDialogCallback() {
                        @Override
                        public void onSharePlatformClick(ShareDialog.SHARE_PLATFORM platform) {
                            Client.share().setTag(activity.getClass().getSimpleName()).fire();
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

    /**
     * h5打开原生页面
     *
     * @param type
     * @param id
     * @param url
     * @param data 保留字段 ，后期可能用到
     */
    @JavascriptInterface
    public void jsOpenAppPage(int type, String id, String url, String data) {
        switch (type) {
            case JsOpenAppPageType.DAILY_REPORT:
                break;
            case JsOpenAppPageType.USER_ABOUT_INFO:
                break;
            case JsOpenAppPageType.SELF_STUDY_ROOM:
                break;
            case JsOpenAppPageType.QUESTION_DETAIL:
                break;
            case JsOpenAppPageType.TRAINING:
                break;
            case JsOpenAppPageType.ACTIVITY:
                break;
            case JsOpenAppPageType.MODULE:
                break;
            case JsOpenAppPageType.FEED_BACK_REPLY:
                break;
            case JsOpenAppPageType.MISS_HOME_PAGE:
                int missId = Integer.parseInt(id);
                Launcher.with(mContext, MissProfileDetailActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, missId)
                        .execute();
                break;
            case JsOpenAppPageType.FUTURE_BATTLE:
                break;
            case JsOpenAppPageType.ARENA:
                if (mContext instanceof WebActivity) {
                    ComponentName callingActivity = ((WebActivity) mContext).getCallingActivity();
                    if (callingActivity != null
                            && RewardActivity.class.getName().equals(callingActivity.getClassName())) {
                        ((WebActivity) mContext).finish();
                    } else {
                        Launcher.with(mContext, RewardActivity.class).execute();
                    }
                }
                break;
            case JsOpenAppPageType.RECHARGE:
                Launcher.with(mContext, VirtualProductExchangeActivity.class)
                        .putExtra(ExtraKeys.RECHARGE_TYPE, AccountFundDetail.TYPE_INGOT)
                        .execute();
                break;
            case JsOpenAppPageType.WALLET:
                break;

        }

    }

    @JavascriptInterface
    public void jsOpenAppPage(int type) {
        jsOpenAppPage(type, null, null, null);
    }

    @JavascriptInterface
    public void jsOpenAppPage(int type, String id) {
        jsOpenAppPage(type, id, null, null);
    }
}
