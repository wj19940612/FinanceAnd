package com.sbai.finance.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

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
 * <p>
 * ShareDialog.with(getActivity())
 * .setShareTitle("TEST")
 * .setShareDescription("TEST DES")
 * .setShareUrl(String.format(Client.SHARE_URL_QUESTION, 1234))
 * .setListener(new ShareDialog.OnShareDialogCallback() {
 *
 * @Override public void onSharePlatformClick(ShareDialog.SHARE_PLATFORM platform) {
 * }
 * @Override public void onFeedbackClick(View view) {
 * }
 * }).show();
 */
public class ShareDialog {

    public interface OnShareDialogCallback {
        void onSharePlatformClick(SHARE_PLATFORM platform);

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

    private CharSequence mTitle;
    private String mShareTitle;
    private String mShareDescription;
    private String mShareUrl;
    private String mShareThumbUrl;
    private boolean mHasFeedback;
    private boolean mHasWeiBo = true;
    private boolean mHasTitle = true;
    private boolean mOnlyShareImage;
    private Bitmap mBitmap;

    private OnShareDialogCallback mListener;

    private class ShareButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.weChatFriend:
                    if (UMShareAPI.get(mActivity).isInstall(mActivity, SHARE_MEDIA.WEIXIN)) {
                        if (mOnlyShareImage) {
                            shareImageToPlatform(SHARE_MEDIA.WEIXIN);
                        } else {
                            mShareUrl += "&userFrom=friend";
                            shareToPlatform(SHARE_MEDIA.WEIXIN);
                        }
                        onSharePlatformClicked(SHARE_PLATFORM.WECHAT_FRIEND);
                        mSmartDialog.dismiss();
                    } else {
                        ToastUtil.show(R.string.you_not_install_weixin);
                    }
                    mSmartDialog.dismiss();
                    break;
                case R.id.weChatFriendCircle:
                    if (UMShareAPI.get(mActivity).isInstall(mActivity, SHARE_MEDIA.WEIXIN_CIRCLE)) {
                        if (mOnlyShareImage) {
                            shareImageToPlatform(SHARE_MEDIA.WEIXIN);
                        } else {
                            mShareUrl += "&userFrom=friend";
                            shareToPlatform(SHARE_MEDIA.WEIXIN_CIRCLE);
                        }
                        onSharePlatformClicked(SHARE_PLATFORM.WECHAT_CIRCLE);
                        mSmartDialog.dismiss();
                    } else {
                        ToastUtil.show(R.string.you_not_install_weixin);
                    }
                    mSmartDialog.dismiss();
                    break;
                case R.id.sinaWeibo:
                    if (UMShareAPI.get(mActivity).isInstall(mActivity, SHARE_MEDIA.SINA)) {
                        mShareUrl += "&userFrom=weibo";
                        shareToPlatform(SHARE_MEDIA.SINA);
                        onSharePlatformClicked(SHARE_PLATFORM.SINA_WEIBO);
                        mSmartDialog.dismiss();
                    } else {
                        ToastUtil.show(R.string.you_not_install_weibo);
                    }
                    mSmartDialog.dismiss();
                    break;
            }
        }
    }

    private void shareToPlatform(SHARE_MEDIA platform) {
        if (platform != SHARE_MEDIA.SINA) {
            UMWeb mWeb = new UMWeb(mShareUrl);
            mWeb.setTitle(mShareTitle);
            mWeb.setDescription(mShareDescription);
            UMImage thumb;
            if (TextUtils.isEmpty(mShareThumbUrl)) {
                thumb = new UMImage(mActivity, R.drawable.ic_launcher_share);
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
            UMImage image;
            if (TextUtils.isEmpty(mShareThumbUrl)) {
                image = new UMImage(mActivity, R.drawable.ic_launcher_share);
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

    private void shareImageToPlatform(SHARE_MEDIA platform) {
        UMImage image = new UMImage(mActivity, mBitmap);
        if (mActivity != null && !mActivity.isFinishing()) {
            new ShareAction(mActivity)
                    .withMedia(image)
                    .setPlatform(platform)
                    .setCallback(mUMShareListener)
                    .share();
        }
    }

    private void onSharePlatformClicked(SHARE_PLATFORM platform) {
        if (mListener != null) {
            mListener.onSharePlatformClick(platform);
        }
    }

    private UMShareListener mUMShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            ToastUtil.show(R.string.share_succeed);
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
        shareDialog.mSmartDialog = SmartDialog.single(activity);
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
                mSmartDialog.dismiss();
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

    public ShareDialog hasFeedback(boolean hasFeedback) {
        mHasFeedback = hasFeedback;
        return this;
    }

    public ShareDialog hasWeiBo(boolean hasWeiBo) {
        mHasWeiBo = hasWeiBo;
        return this;
    }

    public ShareDialog setTitle(int titleRes) {
        mTitle = mActivity.getText(titleRes);
        return this;
    }

    public ShareDialog setTitleVisible(boolean visible) {
        mHasTitle = visible;
        return this;
    }

    public ShareDialog setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        return this;
    }

    public ShareDialog setOnShareImage(boolean isShow) {
        mOnlyShareImage = isShow;
        return this;
    }

    public ShareDialog setTitle(CharSequence title) {
        mTitle = title;
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

    public ShareDialog setShareThumbUrl(String shareThumbUrl) {
        mShareThumbUrl = shareThumbUrl;
        return this;
    }

    public void show() {
        if (!mHasFeedback) {
            mView.findViewById(R.id.secondSplitLine).setVisibility(View.GONE);
            mView.findViewById(R.id.secondArea).setVisibility(View.GONE);
        }
        if (!mHasWeiBo) {
            mView.findViewById(R.id.sinaWeibo).setVisibility(View.INVISIBLE);
        }
        if (!TextUtils.isEmpty(mTitle)) {
            TextView title = (TextView) mView.findViewById(R.id.title);
            title.setText(mTitle);
            if (mHasTitle) {
                title.setVisibility(View.VISIBLE);
            } else {
                title.setVisibility(View.GONE);
            }
        }

        mSmartDialog.setWidthScale(1)
                .setGravity(Gravity.BOTTOM)
                .setWindowAnim(R.style.BottomDialogAnimation)
                .show();
    }
}
