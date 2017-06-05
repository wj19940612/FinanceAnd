package com.sbai.finance.activity.mine.setting;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SafetySettingActivity extends BaseActivity {

    @BindView(R.id.modify_safety_password)
    AppCompatTextView mModifySafetyPassword;
    @BindView(R.id.forget_safety_password)
    AppCompatTextView mForgetSafetyPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_setting);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.modify_safety_password, R.id.forget_safety_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.modify_safety_password:
                Launcher.with(getActivity(), ModifySafetyPassActivity.class).execute();
                break;
            case R.id.forget_safety_password:
                break;
        }
    }
}
