package com.sbai.finance.activity.mine.setting;

import android.os.Bundle;
import android.view.View;

import com.sbai.finance.BuildConfig;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.AppInfo;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.shieldSetting, R.id.newMessageNotification, R.id.appInfo, R.id.securityCenter})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.shieldSetting:
                break;
            case R.id.newMessageNotification:
                umengEventCount(UmengCountEventId.ME_NEW_AWAIT);
                Launcher.with(getActivity(), SetNotificationSwitchActivity.class).execute();
                break;
            case R.id.securityCenter:
                umengEventCount(UmengCountEventId.ME_SAFETY_CENTER);
                Client.getUserHasPassWord()
                        .setTag(TAG).setIndeterminate(this)
                        .setCallback(new Callback2D<Resp<Boolean>, Boolean>() {
                            @Override
                            protected void onRespSuccessData(Boolean data) {
                                Launcher.with(getActivity(), SecurityCenterActivity.class)
                                        .putExtra(ExtraKeys.HAS_SECURITY_PSD, data.booleanValue())
                                        .execute();
                            }
                        })
                        .fire();

                break;
            case R.id.appInfo:
                if (BuildConfig.DEBUG) {
                    ToastUtil.show(AppInfo.getVersionName(getActivity()) + ": "
                            + AppInfo.getMetaData(getActivity(), AppInfo.Meta.UMENG_CHANNEL));
                }
                break;
        }
    }
}
