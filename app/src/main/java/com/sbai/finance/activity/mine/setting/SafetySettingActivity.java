package com.sbai.finance.activity.mine.setting;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.LinearLayout;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SafetySettingActivity extends BaseActivity {

    @BindView(R.id.modify_safety_password)
    AppCompatTextView mModifySafetyPassword;
    @BindView(R.id.forget_safety_password)
    AppCompatTextView mForgetSafetyPassword;
    @BindView(R.id.setting_safety_password)
    AppCompatTextView mSettingSafetyPassword;
    @BindView(R.id.modifySafetyLL)
    LinearLayout mModifySafetyLL;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    //是否是在安全设置界面
    private boolean isSettingSafetyPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_setting);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.modify_safety_password, R.id.forget_safety_password
            , R.id.setting_safety_password, R.id.modifySafetyLL})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.modify_safety_password:
                Launcher.with(getActivity(), ModifySafetyPassActivity.class).execute();
                break;
            case R.id.forget_safety_password:

                break;
            case R.id.setting_safety_password:
                mTitleBar.setTitle(R.string.safety_setting);
                mModifySafetyLL.setVisibility(View.VISIBLE);
                mSettingSafetyPassword.setVisibility(View.GONE);
                isSettingSafetyPage = true;
                break;
            case R.id.modifySafetyLL:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (isSettingSafetyPage) {
            mTitleBar.setTitle(R.string.safety_center);
            mModifySafetyLL.setVisibility(View.GONE);
            mSettingSafetyPassword.setVisibility(View.VISIBLE);
            isSettingSafetyPage = false;
        } else {
            super.onBackPressed();
        }
    }
}
