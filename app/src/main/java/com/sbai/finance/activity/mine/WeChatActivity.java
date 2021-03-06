package com.sbai.finance.activity.mine;

import android.text.TextUtils;
import android.util.Log;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.ToastUtil;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

/**
 * 微信
 */

public abstract class WeChatActivity extends BaseActivity {
    private String mWeChatOpenid;
    private String mWeChatName;
    private String mWeChatIconUrl;
    private int mWeChatGender;

    public void requestWeChatInfo() {
        if (!UMShareAPI.get(getActivity()).isInstall(getActivity(), SHARE_MEDIA.WEIXIN)) {
            ToastUtil.show(R.string.you_not_install_weixin);
            return;
        }
        UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                Log.d(TAG, "onStart " + "授权开始");
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                mWeChatOpenid = map.get("openid");
                mWeChatName = map.get("name");
                mWeChatIconUrl = map.get("iconurl");
                mWeChatGender = map.get("gender").equals("女") ? 1 : 2;
                bindSuccess();
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                Log.d(TAG, "onError " + "授权失败:" + throwable.getMessage());
                bindFailure();
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                Log.d(TAG, "onCancel " + "授权取消");
                bindFailure();
            }
        });
    }

    protected abstract void bindSuccess();

    protected abstract void bindFailure();


    protected boolean isWeChatLogin() {
        return !TextUtils.isEmpty(mWeChatOpenid);
    }

    public String getWeChatOpenid() {
        return mWeChatOpenid;
    }

    public String getWeChatName() {
        return mWeChatName;
    }

    public String getWeChatIconUrl() {
        return mWeChatIconUrl;
    }

    public int getWeChatGender() {
        return mWeChatGender;
    }
}
