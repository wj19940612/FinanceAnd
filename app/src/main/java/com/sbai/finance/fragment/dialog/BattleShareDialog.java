package com.sbai.finance.fragment.dialog;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.CustomToast;
import com.sbai.finance.view.SmartDialog;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import static com.umeng.socialize.utils.ContextUtil.getContext;

/**
 * 对战分享好友弹框
 */
public class BattleShareDialog implements View.OnClickListener {


    private Activity mActivity;
    private SmartDialog mSmartDialog;
    private View mView;

    TextView mWeChatFriend;
    TextView mWeChatFriendCircle;
    TextView mWeibo;
    ImageButton mCancel;

    private String mShareTitle;  //分享标题
    private String mShareUrl;  //链接地址
    private String mShareDescription; //分享描述
    private String mShareThumbUrl;

    public static BattleShareDialog with(Activity activity) {
        BattleShareDialog battleShareDialog = new BattleShareDialog();
        battleShareDialog.mActivity = activity;
        battleShareDialog.mSmartDialog = SmartDialog.with(activity);
        battleShareDialog.mView = LayoutInflater.from(activity).inflate(R.layout.dialog_fragment_share, null);
        battleShareDialog.mSmartDialog.setCustomView(battleShareDialog.mView).setGravity(Gravity.BOTTOM);
        battleShareDialog.init();
        return battleShareDialog;
    }

    private void init() {
        mWeChatFriend = (TextView) mView.findViewById(R.id.weChatFriend);
        mWeChatFriendCircle = (TextView) mView.findViewById(R.id.weChatFriendCircle);
        mWeibo = (TextView) mView.findViewById(R.id.weibo);
        mCancel = (ImageButton) mView.findViewById(R.id.cancel);
        mWeChatFriend.setOnClickListener(this);
        mWeChatFriendCircle.setOnClickListener(this);
        mWeibo.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weChatFriend:
                if (UMShareAPI.get(getContext()).isInstall(mActivity, SHARE_MEDIA.WEIXIN)) {
                    shareToPlatform(SHARE_MEDIA.WEIXIN);
                } else {
                    ToastUtil.show(R.string.you_not_install_weixin);
                }
                dismissDialog();
                break;
            case R.id.weibo:
                if (UMShareAPI.get(getContext()).isInstall(mActivity, SHARE_MEDIA.SINA)) {
                    shareToPlatform(SHARE_MEDIA.SINA);
                } else {
                    ToastUtil.show(R.string.you_not_install_weibo);
                }
                dismissDialog();
                break;
            case R.id.weChatFriendCircle:
                if (UMShareAPI.get(getContext()).isInstall(mActivity, SHARE_MEDIA.WEIXIN_CIRCLE)) {
                    shareToPlatform(SHARE_MEDIA.WEIXIN_CIRCLE);
                } else {
                    ToastUtil.show(R.string.you_not_install_weixin);
                }
                dismissDialog();
                break;
            case R.id.cancel:
                dismissDialog();
                break;
        }

    }

    private void dismissDialog() {
        if (mSmartDialog != null) {
            mSmartDialog.dismiss();
        }
    }

    public BattleShareDialog setShareThumbUrl(String shareThumbUrl) {
        mShareThumbUrl = shareThumbUrl;
        return this;
    }
    public BattleShareDialog setShareTitle(String shareTitle) {
        mShareTitle = shareTitle;
        return this;
    }
    public BattleShareDialog setShareDescription(String shareDescription) {
        mShareDescription = shareDescription;
        return this;
    }
    public BattleShareDialog setShareUrl(String shareUrl) {
        mShareUrl = shareUrl;
        return this;
    }

    public void show(){
        mSmartDialog.setWidthScale(1)
                .setGravity(Gravity.BOTTOM)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .show();
    }

    private void shareToPlatform(SHARE_MEDIA platform) {
        if (platform != SHARE_MEDIA.SINA) {
            UMWeb mWeb = new UMWeb(mShareUrl);
            mWeb.setTitle(mShareTitle);
            mWeb.setDescription(mShareDescription);

            UMImage thumb = null;
            if (TextUtils.isEmpty(mShareThumbUrl)) {
                thumb = new UMImage(mActivity, R.drawable.ic_future_battle_game);
            } else {
                thumb = new UMImage(mActivity, mShareThumbUrl);
            }

            mWeb.setThumb(thumb);
            if (mActivity != null && !mActivity.isFinishing()) {
                new ShareAction(mActivity)
                        .withMedia(mWeb)
                        .setPlatform(platform)
                        .setCallback(mUMShareListener)
                        .share();
            }
        } else {
            String text = mShareTitle + mShareUrl;
            UMImage image = null;
            if (TextUtils.isEmpty(mShareThumbUrl)) {
                image = new UMImage(mActivity, R.drawable.ic_future_battle_game);
            } else {
                image = new UMImage(mActivity, mShareThumbUrl);
            }

            if (mActivity != null && !mActivity.isFinishing()) {
                new ShareAction(mActivity)
                        .withText(text)
                        .withMedia(image)
                        .setPlatform(platform)
                        .setCallback(mUMShareListener)
                        .share();
            }
        }
    }


    private UMShareListener mUMShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            CustomToast.getInstance().showText(mActivity, R.string.share_succeed);
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            ToastUtil.show(R.string.share_cancel);
        }
    };

}
