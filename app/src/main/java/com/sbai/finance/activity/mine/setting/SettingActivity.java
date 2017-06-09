package com.sbai.finance.activity.mine.setting;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.AppInfo;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.shieldSetting)
    AppCompatTextView mShieldSetting;
    @BindView(R.id.newNewsNotification)
    AppCompatTextView mNewNewsNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.shieldSetting, R.id.newNewsNotification, R.id.appInfo, R.id.safety_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.shieldSetting:
                Launcher.with(getActivity(), ShieldRelieveSettingActivity.class).execute();
                break;
            case R.id.newNewsNotification:
                Launcher.with(getActivity(), SetNotificationSwitchActivity.class).execute();
                break;
            case R.id.safety_setting:
                Client.getUserHasPassWord()
                        .setTag(TAG)
                        .setIndeterminate(this)
                        .setCallback(new Callback2D<Resp<Boolean>, Boolean>() {
                            @Override
                            protected void onRespSuccessData(Boolean data) {
                                Launcher.with(getActivity(), SafetySettingActivity.class).putExtra(Launcher.EX_PAYLOAD, data.booleanValue()).execute();
                            }
                        })
                        .fire();

                break;
            case R.id.appInfo:
                ToastUtil.singleCurt(AppInfo.getVersionName(getActivity()) + ": "
                        + AppInfo.getMetaData(getActivity(), AppInfo.Meta.UMENG_CHANNEL));
                break;
        }
    }
}
