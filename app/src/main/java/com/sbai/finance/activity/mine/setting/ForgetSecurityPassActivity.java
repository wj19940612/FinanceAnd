package com.sbai.finance.activity.mine.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.ImageAuthCodeActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.ValidationWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ForgetSecurityPassActivity extends BaseActivity {

    private static final int REQ_CODE_MODIFY_PASS = 24700;
    private static final int REQ_CODE_IMAGE_AUTH_CODE = 889;

    @BindView(R.id.phoneNumber)
    AppCompatEditText mPhoneNumber;
    @BindView(R.id.authCode)
    AppCompatEditText mAuthCode;
    @BindView(R.id.getAuthCode)
    AppCompatTextView mGetAuthCode;
    @BindView(R.id.submit)
    AppCompatTextView mSubmit;

    //获取验证是否开始
    private int mCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_security_pass);
        ButterKnife.bind(this);
        mAuthCode.addTextChangedListener(mValidationWatcher);

        mPhoneNumber.setText(StrFormatter.getFormatSafetyPhoneNumber(LocalUser.getUser().getPhone()));
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            boolean enable = checkSubmitButtonEnable();
            if (enable != mSubmit.isEnabled()) {
                mSubmit.setEnabled(enable);
            }
        }
    };

    private boolean checkSubmitButtonEnable() {
        String phone = getPhoneNumber();
        String authCode = mAuthCode.getText().toString().trim();
        if (TextUtils.isEmpty(phone) || phone.length() < 11) {
            return false;
        } else if (TextUtils.isEmpty(authCode) || authCode.length() < 4) {
            return false;
        }
        return true;
    }

    private String getPhoneNumber() {
        return mPhoneNumber.getText().toString().trim().replaceAll(" ", "");
    }

    @OnClick({R.id.getAuthCode, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.getAuthCode:
                requestAuthCodeForPass();
                break;
            case R.id.submit:
                final String phoneNumber = LocalUser.getUser().getPhone();
                final String authCode = mAuthCode.getText().toString().trim();
                Client.forgetPassWord(authCode, phoneNumber)
                        .setIndeterminate(this)
                        .setCallback(new Callback<Resp<Object>>() {
                            @Override
                            protected void onRespSuccess(Resp<Object> resp) {
                                if (resp.isSuccess()) {
                                    ToastUtil.show(resp.getMsg());
                                    restartGetAuthCode();
                                    Launcher.with(getActivity(), UpdateSecurityPassActivity.class)
                                            .putExtra(ExtraKeys.HAS_SECURITY_PSD, true)
                                            .putExtra(ExtraKeys.AUTH_CODE, authCode)
                                            .executeForResult(REQ_CODE_MODIFY_PASS);
                                    ToastUtil.show(resp.getMsg());
                                } else {
                                    ToastUtil.show(resp.getMsg());
                                }
                            }
                        })
                        .fire();
                break;
        }
    }

    private void requestAuthCodeForPass() {
        Client.getAuthCode(LocalUser.getUser().getPhone())
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        postAuthCodeRequested();
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        if (failedResp.getCode() == Resp.CODE_IMAGE_AUTH_CODE_REQUIRED) {
                            Launcher.with(getActivity(), ImageAuthCodeActivity.class)
                                    .putExtra(ExtraKeys.PHONE, LocalUser.getUser().getPhone())
                                    .executeForResult(REQ_CODE_IMAGE_AUTH_CODE);
                        } else {
                            super.onRespFailure(failedResp);
                        }
                    }
                }).fire();
        mAuthCode.requestFocus();
    }

    private void postAuthCodeRequested() {
        startScheduleJob(1000);
        mCounter = 60;
        mGetAuthCode.setEnabled(false);
        mGetAuthCode.setText(getString(R.string.resend_after_n_seconds, mCounter));
    }

    @Override
    public void onTimeUp(int count) {
        mCounter--;
        if (mCounter <= 0) {
            restartGetAuthCode();
        } else {
            mGetAuthCode.setText(getString(R.string.resend_after_n_seconds, mCounter));
        }
    }

    private void restartGetAuthCode() {
        mGetAuthCode.setEnabled(true);
        mGetAuthCode.setText(R.string.obtain_auth_code_continue);
        stopScheduleJob();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_MODIFY_PASS && resultCode == RESULT_OK) {
            finish();
        }

        if (requestCode == REQ_CODE_IMAGE_AUTH_CODE && resultCode == RESULT_OK) {
            // 发送图片验证码去 获取验证码 成功
            postAuthCodeRequested();
        }
    }
}
