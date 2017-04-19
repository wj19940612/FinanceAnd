package com.sbai.finance.activity.mine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

import com.sbai.finance.R;
import com.sbai.finance.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SetNotificationSwitchActivity extends AppCompatActivity {

    @BindView(R.id.acceptNewsSwitch)
    SwitchCompat mAcceptNewsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_notification_switch);
        ButterKnife.bind(this);
        mAcceptNewsSwitch.setChecked(true);
        mAcceptNewsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                confirmAcceptNotification();
            }
        });
    }

    private void confirmAcceptNotification() {
        ToastUtil.curt("提交");
    }
}
