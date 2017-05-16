package com.sbai.finance.fragment.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
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
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by linrongfang on 2017/5/16.
 */

public class ShareDiglogFragment extends DialogFragment {

    @BindView(R.id.weChatFriend)
    TextView mWeChatFriend;
    @BindView(R.id.weChatFriendCircle)
    TextView mWeChatFriendCircle;
    @BindView(R.id.weibo)
    TextView mWeibo;
    @BindView(R.id.cancel)
    ImageButton mCancel;

    private Unbinder mBind;
    private Activity mActivity;

    private String mShareTitle;  //分享标题
    private String mShareUrl;   //链接地址
    private String mShareImageUrl; //头像地址

    public static ShareDiglogFragment newInstance() {
        ShareDiglogFragment fragment = new ShareDiglogFragment();
        return fragment;
    }

    public void setShareContent(Activity activity, String shareTitle, String shareUrl, String shareImageUrl) {
        mActivity = activity;
        mShareTitle = shareTitle;
        mShareUrl = shareUrl;
        mShareImageUrl = shareImageUrl;
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
    }

    @OnClick({R.id.weChatFriend, R.id.weChatFriendCircle, R.id.weibo, R.id.cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.weChatFriend:
                shareToPlatform(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.weChatFriendCircle:
                shareToPlatform(SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
            case R.id.weibo:
                shareToPlatform(SHARE_MEDIA.SINA);
                break;
            case R.id.cancel:
                dismiss();
                break;

        }
    }

    private void shareToPlatform(SHARE_MEDIA platform) {
        UMWeb mWeb = new UMWeb(mShareUrl);
        mWeb.setTitle(mShareTitle);
        UMImage thumb = new UMImage(mActivity, mShareImageUrl);
        mWeb.setThumb(thumb);
        new ShareAction(mActivity)
                .withMedia(mWeb)
                .setPlatform(platform)
                .setCallback(mUMShareListener)
                .share();
    }


    private UMShareListener mUMShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
            
        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {

        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {

        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            dismiss();
        }
    };

    public void show(FragmentManager manager) {
        this.show(manager, ShareDiglogFragment.class.getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }
}
