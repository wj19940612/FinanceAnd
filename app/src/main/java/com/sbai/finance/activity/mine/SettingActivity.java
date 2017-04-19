package com.sbai.finance.activity.mine;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.Launcher;

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

    @OnClick({R.id.shieldSetting, R.id.newNewsNotification})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.shieldSetting:
                Launcher.with(getActivity(), ShieldRelieveSettingActivity.class).execute();
                break;
            case R.id.newNewsNotification:
                Launcher.with(getActivity(), SetNotificationSwitchActivity.class).execute();
                break;
        }
    }
}
