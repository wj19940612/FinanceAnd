package com.sbai.finance.activity.mine;

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

    public void requestWeChatInfo() {
        if (!UMShareAPI.get(getActivity()).isInstall(getActivity(), SHARE_MEDIA.WEIXIN)) {
            ToastUtil.show(R.string.you_not_install_weixin);
            return;
        }
        onHttpUiShow(TAG);
        UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                Log.d(TAG, "onStart " + "授权开始");
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                onHttpUiDismiss(TAG);
                bindSuccess(map.get("openid"), map.get("name"), map.get("gender"), map.get("iconurl"));
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                Log.d(TAG, "onError " + "授权失败:" + throwable.getMessage());
                onHttpUiDismiss(TAG);
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                Log.d(TAG, "onCancel " + "授权取消");
                onHttpUiDismiss(TAG);
            }
        });
    }

    protected abstract void bindSuccess(String openid, String name, String gender, String iconUrl);
}
