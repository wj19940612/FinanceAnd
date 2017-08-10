package com.sbai.finance.view.dialog;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.SmartDialog;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

/**
 * 分享弹框，使用看下方代码：
 *
 * ShareDialog.with(getActivity())
 *  .setShareTitle("TEST")
 *  .setShareDescription("TEST DES")
 *  .setShareUrl(String.format(Client.SHARE_URL_QUESTION, 1234))
 *  .setListener(new ShareDialog.OnShareDialogCallback() {
 *          @Override
 *          public void onShareSuccess(ShareDialog.SHARE_PLATFORM platform) {
 *          }
 *
 *          @Override
 *          public void onFeedbackClick(View view) {
 *          }
 *  }).show();
 *
 */
public class ShareDialog {

    public interface OnShareDialogCallback {
        void onShareSuccess(SHARE_PLATFORM platform);

        void onFeedbackClick(View view);
    }

    public enum SHARE_PLATFORM {
        WECHAT_FRIEND,
        WECHAT_CIRCLE,
        SINA_WEIBO
    }

    private Activity mActivity;
    private SmartDialog mSmartDialog;
    private View mView;

    private String mShareTitle;
    private String mShareDescription;
    private String mShareUrl;
    private SHARE_PLATFORM mPlatform;

    private OnShareDialogCallback mListener;

    private class ShareButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.weChatFriend:
                    if (UMShareAPI.get(mActivity).isInstall(mActivity, SHARE_MEDIA.WEIXIN)) {
                        mShareUrl += "&userFrom=friend";
                        mPlatform = SHARE_PLATFORM.WECHAT_FRIEND;
                        shareToPlatform(SHARE_MEDIA.WEIXIN);
                    } else {
                        ToastUtil.show(R.string.you_not_install_weixin);
                    }
                    break;
                case R.id.weChatFriendCircle:
                    if (UMShareAPI.get(mActivity).isInstall(mActivity, SHARE_MEDIA.WEIXIN_CIRCLE)) {
                        mShareUrl += "&userFrom=friend";
                        mPlatform = SHARE_PLATFORM.WECHAT_CIRCLE;
                        shareToPlatform(SHARE_MEDIA.WEIXIN_CIRCLE);
                    } else {
                        ToastUtil.show(R.string.you_not_install_weixin);
                    }
                    break;
                case R.id.sinaWeibo:
                    if (UMShareAPI.get(mActivity).isInstall(mActivity, SHARE_MEDIA.SINA)) {
                        mShareUrl += "&userFrom=weibo";
                        mPlatform = SHARE_PLATFORM.SINA_WEIBO;
                        shareToPlatform(SHARE_MEDIA.SINA);
                    } else {
                        ToastUtil.show(R.string.you_not_install_weibo);
                    }
                    break;
            }
        }
    }

    private void shareToPlatform(SHARE_MEDIA platform) {
        if (platform != SHARE_MEDIA.SINA) {
            UMWeb mWeb = new UMWeb(mShareUrl);
            mWeb.setTitle(mShareTitle);
            mWeb.setDescription(mShareDescription);
            UMImage thumb = new UMImage(mActivity, R.drawable.ic_share_logo);
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
            UMImage image = new UMImage(mActivity, R.drawable.ic_share_logo);
            image.setThumb(new UMImage(mActivity, R.drawable.ic_share_logo));
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
            ToastUtil.show(R.string.share_succeed);
            if (mListener != null) {
                mListener.onShareSuccess(mPlatform);
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            ToastUtil.show(R.string.share_failed);
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            ToastUtil.show(R.string.share_cancel);
        }
    };

    public static ShareDialog with(Activity activity) {
        ShareDialog shareDialog = new ShareDialog();
        shareDialog.mActivity = activity;
        shareDialog.mSmartDialog = SmartDialog.with(activity);
        shareDialog.mView = LayoutInflater.from(activity).inflate(R.layout.dialog_share, null);
        shareDialog.mSmartDialog.setCustomView(shareDialog.mView);
        shareDialog.init();
        return shareDialog;
    }

    private void init() {
        mView.findViewById(R.id.weChatFriend).setOnClickListener(new ShareButtonClickListener());
        mView.findViewById(R.id.weChatFriendCircle).setOnClickListener(new ShareButtonClickListener());
        mView.findViewById(R.id.sinaWeibo).setOnClickListener(new ShareButtonClickListener());
        mView.findViewById(R.id.feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onFeedbackClick(v);
                }
            }
        });
        mView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmartDialog.dismiss();
            }
        });
    }

    public ShareDialog setListener(OnShareDialogCallback listener) {
        mListener = listener;
        return this;
    }

    public ShareDialog setShareUrl(String url) {
        mShareUrl = url;
        return this;
    }

    public ShareDialog setShareTitle(String shareTitle) {
        mShareTitle = shareTitle;
        return this;
    }

    public ShareDialog setShareDescription(String shareDescription) {
        mShareDescription = shareDescription;
        return this;
    }

    public void show() {
        mSmartDialog.setWidthScale(1)
                .setGravity(Gravity.BOTTOM)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .show();
    }
}