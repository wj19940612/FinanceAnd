package com.sbai.finance.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.net.API;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.CustomToast;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 对战分享好友弹框
 */
public class ShareDialogFragment extends DialogFragment {

    private static final String SHARE_URL = API.getHost() + "/mobi/login/share?code=";

    @BindView(R.id.weChatFriend)
    TextView mWeChatFriend;
    @BindView(R.id.weChatFriendCircle)
    TextView mWeChatFriendCircle;
    @BindView(R.id.weibo)
    TextView mWeibo;
    @BindView(R.id.cancel)
    ImageButton mCancel;

    private Unbinder mBind;
    private Context mActivity;

    private String mShareTitle;  //分享标题
    private String mShareUrl;  //链接地址
    private String mShareDescription; //分享描述

    private boolean isFutureGame = false;//默认分享不是游戏

    public interface OnShareDialogCallback {
        void onSharePlatformClick(SHARE_PLATFORM platform);
    }
    public enum SHARE_PLATFORM {
        WECHAT_FRIEND,
        WECHAT_CIRCLE,
        SINA_WEIBO
    }

    private OnShareDialogCallback mListener;

    public ShareDialogFragment setListener(OnShareDialogCallback listener) {
        mListener = listener;
        return this;
    }

    public static ShareDialogFragment newInstance() {
        ShareDialogFragment fragment = new ShareDialogFragment();
        return fragment;
    }

    public ShareDialogFragment setShareMode(boolean isFutureGame) {
        this.isFutureGame = isFutureGame;
        return this;
    }

    public ShareDialogFragment setShareContent(String shareTitle, String shareDescription, String batchCode) {
        mShareTitle = shareTitle;
        mShareDescription = shareDescription;
        mShareUrl = isFutureGame ? SHARE_URL + batchCode : batchCode;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.UpLoadHeadImageDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_share, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            window.setLayout(dm.widthPixels, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        mActivity = getActivity();
    }

    @OnClick({R.id.weChatFriend, R.id.weChatFriendCircle, R.id.weibo, R.id.cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.weChatFriend:
                if (UMShareAPI.get(getContext()).isInstall(getActivity(), SHARE_MEDIA.WEIXIN)) {
                    if (isFutureGame) mShareUrl += "&userFrom=" + "friend";
                    shareToPlatform(SHARE_MEDIA.WEIXIN);
                    onSharePlatformClicked(SHARE_PLATFORM.WECHAT_FRIEND);
                } else {
                    ToastUtil.show(R.string.you_not_install_weixin);
                }
                dismiss();
                break;
            case R.id.weChatFriendCircle:
                if (UMShareAPI.get(getContext()).isInstall(getActivity(), SHARE_MEDIA.WEIXIN_CIRCLE)) {
                    if (isFutureGame) mShareUrl += "&userFrom=" + "friend";
                    shareToPlatform(SHARE_MEDIA.WEIXIN_CIRCLE);
                    onSharePlatformClicked(SHARE_PLATFORM.WECHAT_CIRCLE);
                } else {
                    ToastUtil.show(R.string.you_not_install_weixin);
                }
                dismiss();
                break;
            case R.id.weibo:
                if (UMShareAPI.get(getContext()).isInstall(getActivity(), SHARE_MEDIA.SINA)) {
                    if (isFutureGame) mShareUrl += "&userFrom=" + "weibo";
                    shareToPlatform(SHARE_MEDIA.SINA);
                    onSharePlatformClicked(SHARE_PLATFORM.SINA_WEIBO);
                } else {
                    ToastUtil.show(R.string.you_not_install_weibo);
                }
                dismiss();
                break;
            case R.id.cancel:
                dismiss();
                break;

        }
    }

    private void onSharePlatformClicked(SHARE_PLATFORM platform) {
        if (mListener != null) {
            mListener.onSharePlatformClick(platform);
        }
    }

    private void shareToPlatform(SHARE_MEDIA platform) {
        if (platform != SHARE_MEDIA.SINA) {
            UMWeb mWeb = new UMWeb(mShareUrl);
            mWeb.setTitle(mShareTitle);
            mWeb.setDescription(mShareDescription);

            UMImage thumb = null;
            if (isFutureGame) {
                thumb = new UMImage(mActivity, R.drawable.ic_future_battle_game);
            } else {
                thumb = new UMImage(mActivity, R.drawable.ic_launcher_share);
            }
            mWeb.setThumb(thumb);
            if (getActivity() != null && !getActivity().isFinishing()) {
                new ShareAction(getActivity())
                        .withMedia(mWeb)
                        .setPlatform(platform)
                        .setCallback(mUMShareListener)
                        .share();
            }
        } else {
            String text = mShareTitle + mShareUrl;
            UMImage image = null;
            if (isFutureGame) {
                image = new UMImage(mActivity, R.drawable.ic_future_battle_game);
                image.setThumb(new UMImage(mActivity, R.drawable.ic_future_battle_game));
            } else {
                image = new UMImage(mActivity, R.drawable.ic_launcher_share);
                image.setThumb(new UMImage(mActivity, R.drawable.ic_launcher_share));
            }
            if (getActivity() != null && !getActivity().isFinishing()) {
                new ShareAction(getActivity())
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

    public void show(FragmentManager manager) {
        this.show(manager, ShareDialogFragment.class.getSimpleName());
    }

    public void showAsync(FragmentManager manager) {
        if (!isAdded()) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, this.getClass().getSimpleName());
            ft.commitAllowingStateLoss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }
}
