package com.sbai.finance.activity.mine.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.UmengCountEventIdUtils;
import com.sbai.finance.view.IconTextRow;
import com.sbai.finance.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SecurityCenterActivity extends BaseActivity {

    private static final int REQ_CODE_UPDATE_SECURITY_PSD = 247;
    private static final int REQ_CODE_UPDATE_LOGIN_PSD = 246;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @BindView(R.id.setLoginPassword)
    IconTextRow mSetLoginPassword;
    @BindView(R.id.modifyLoginPassword)
    IconTextRow mModifyLoginPassword;

    @BindView(R.id.setSecurityPassword)
    IconTextRow mSetSecurityPassword;

    @BindView(R.id.modifySecurityPasswordArea)
    LinearLayout mModifySecurityPasswordArea;
    @BindView(R.id.modifySecurityPassword)
    IconTextRow mModifySecurityPassword;
    @BindView(R.id.forgetSecurityPassword)
    IconTextRow mForgetSecurityPassword;

    private boolean mHasSecurityPassword;
    private boolean mHasLoginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_center);
        ButterKnife.bind(this);

        initData(getIntent());
        initViews();
    }

    private void initViews() {
        if (mHasLoginPassword) {
            mSetLoginPassword.setVisibility(View.GONE);
            mModifyLoginPassword.setVisibility(View.VISIBLE);
        } else {
            mSetLoginPassword.setVisibility(View.VISIBLE);
            mModifyLoginPassword.setVisibility(View.GONE);
        }

        if (mHasSecurityPassword) {
            mModifySecurityPasswordArea.setVisibility(View.VISIBLE);
            mSetSecurityPassword.setVisibility(View.GONE);
        } else {
            mModifySecurityPasswordArea.setVisibility(View.GONE);
            mSetSecurityPassword.setVisibility(View.VISIBLE);
        }
    }

    private void initData(Intent intent) {
        mHasSecurityPassword = intent.getBooleanExtra(ExtraKeys.HAS_SECURITY_PSD, false);
        mHasLoginPassword = LocalUser.getUser().getUserInfo().isSetPass();
    }

    @OnClick({R.id.modifySecurityPassword, R.id.forgetSecurityPassword, R.id.setSecurityPassword,
            R.id.setLoginPassword, R.id.modifyLoginPassword})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.forgetSecurityPassword:
                umengEventCount(UmengCountEventIdUtils.ME_FORGIVE_SAFETY_PASSWORD);
                Launcher.with(getActivity(), ForgetSecurityPassActivity.class)
                        .executeForResult(REQ_CODE_UPDATE_SECURITY_PSD);
                break;

            case R.id.modifySecurityPassword:
                umengEventCount(UmengCountEventIdUtils.ME_MODIFY_SAFETY_PASSWORD);
                Launcher.with(getActivity(), UpdateSecurityPassActivity.class)
                        .putExtra(ExtraKeys.HAS_SECURITY_PSD, mHasSecurityPassword)
                        .executeForResult(REQ_CODE_UPDATE_SECURITY_PSD);
                break;
            case R.id.setSecurityPassword:
                umengEventCount(UmengCountEventIdUtils.ME_SET_SAFETY_PASSWORD);
                Launcher.with(getActivity(), UpdateSecurityPassActivity.class)
                        .putExtra(ExtraKeys.HAS_SECURITY_PSD, mHasSecurityPassword)
                        .executeForResult(REQ_CODE_UPDATE_SECURITY_PSD);
                break;

            case R.id.setLoginPassword:
                umengEventCount(UmengCountEventIdUtils.ME_SET_LOGIN_PASSWORD);
                Launcher.with(getActivity(), UpdatePasswordActivity.class)
                        .putExtra(ExtraKeys.HAS_LOGIN_PSD, mHasLoginPassword)
                        .executeForResult(REQ_CODE_UPDATE_LOGIN_PSD);
                break;
            case R.id.modifyLoginPassword:
                umengEventCount(UmengCountEventIdUtils.ME_MODIFY_LOGIN_PASSWORD);
                Launcher.with(getActivity(), UpdatePasswordActivity.class)
                        .putExtra(ExtraKeys.HAS_LOGIN_PSD, mHasLoginPassword)
                        .executeForResult(REQ_CODE_UPDATE_LOGIN_PSD);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_UPDATE_SECURITY_PSD && resultCode == RESULT_OK) {
            if (mHasSecurityPassword) {
                mModifySecurityPasswordArea.setVisibility(View.VISIBLE);
                mSetSecurityPassword.setVisibility(View.GONE);
            } else { // 第一次添加设置安全密码后关闭安全中心，回到设置
                finish();
            }
        }

        if (requestCode == REQ_CODE_UPDATE_LOGIN_PSD && resultCode == RESULT_OK) {
            mHasLoginPassword = LocalUser.getUser().getUserInfo().isSetPass();
            mSetLoginPassword.setVisibility(View.GONE);
            mModifyLoginPassword.setVisibility(View.VISIBLE);
        }
    }
}
