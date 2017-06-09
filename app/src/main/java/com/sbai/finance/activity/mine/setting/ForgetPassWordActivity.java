package com.sbai.finance.activity.mine.setting;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
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


public class ForgetPassWordActivity extends BaseActivity {

    @BindView(R.id.phoneNumber)
    AppCompatEditText mPhoneNumber;
    @BindView(R.id.phoneNumberClear)
    AppCompatImageView mPhoneNumberClear;
    @BindView(R.id.authCode)
    AppCompatEditText mAuthCode;
    @BindView(R.id.getAuthCode)
    AppCompatTextView mGetAuthCode;
    @BindView(R.id.submit)
    AppCompatTextView mSubmit;

    //获取验证是否开始
    private boolean mFreezeObtainAuthCode;
    private int mCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass_word);
        ButterKnife.bind(this);
        mPhoneNumber.addTextChangedListener(mPhoneValidationWatcher);
        mAuthCode.addTextChangedListener(mValidationWatcher);

    }

    private ValidationWatcher mPhoneValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            mValidationWatcher.afterTextChanged(s);
            formatPhoneNumber();
            mPhoneNumberClear.setVisibility(checkClearPhoneNumButtonVisible() ? View.VISIBLE : View.INVISIBLE);
            boolean authCodeEnable = checkObtainAuthCodeEnable();
            if (mGetAuthCode.isEnabled() != authCodeEnable) {
                mGetAuthCode.setEnabled(authCodeEnable);
            }
        }
    };

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            boolean enable = checkSignInButtonEnable();
            if (enable != mSubmit.isEnabled()) {
                mSubmit.setEnabled(enable);
            }
        }
    };

    private void formatPhoneNumber() {
        String oldPhone = mPhoneNumber.getText().toString();
        String phoneNoSpace = oldPhone.replaceAll(" ", "");
        String newPhone = StrFormatter.getFormatPhoneNumber(phoneNoSpace);
        if (!newPhone.equalsIgnoreCase(oldPhone)) {
            mPhoneNumber.setText(newPhone);
            mPhoneNumber.setSelection(newPhone.length());
        }
    }

    private boolean checkClearPhoneNumButtonVisible() {
        return !TextUtils.isEmpty(mPhoneNumber.getText().toString().trim());
    }

    private boolean checkObtainAuthCodeEnable() {
        String phone = getPhoneNumber();
        return (!TextUtils.isEmpty(phone) && phone.length() > 10 && !mFreezeObtainAuthCode);
    }

    private boolean checkSignInButtonEnable() {
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

    @OnClick({R.id.phoneNumberClear, R.id.getAuthCode, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.phoneNumberClear:
                mPhoneNumber.setText("");
                break;
            case R.id.getAuthCode:
                getAuthCodeForPass();
                break;
            case R.id.submit:
                final String phoneNumber = getPhoneNumber();
                final String authCode = mAuthCode.getText().toString().trim();
                Client.forgetPassWord(authCode, phoneNumber)
                        .setIndeterminate(this)
                        .setCallback(new Callback<Resp<Object>>() {
                            @Override
                            protected void onRespSuccess(Resp<Object> resp) {
                                Log.d(TAG, "onRespSuccess: " + resp.toString());
                                if (resp.isSuccess()) {
                                    ToastUtil.curt(resp.getMsg());
                                    Launcher.with(getActivity(), ModifySafetyPassActivity.class)
                                            .putExtra(Launcher.EX_PAYLOAD, true)
                                            .putExtra(Launcher.EX_PAYLOAD_1, 1).execute();
                                } else {
                                    ToastUtil.curt(resp.getMsg());
                                }
                            }
                        })
                        .fire();
                break;
        }
    }

    private void getAuthCodeForPass() {
        Client.sendMsgCodeForPassWord(getPhoneNumber())
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            mFreezeObtainAuthCode = true;
                            startScheduleJob(1000);
                            mCounter = 60;
                            mGetAuthCode.setEnabled(false);
                            mGetAuthCode.setText(getString(R.string.resend_after_n_seconds, mCounter));

                            ToastUtil.curt(resp.getMsg());
                        } else {
                            ToastUtil.curt(resp.getMsg());
                        }
                    }
                })
                .fire();
        mAuthCode.requestFocus();
    }

    @Override
    public void onTimeUp(int count) {
        mCounter--;
        if (mCounter <= 0) {
            mFreezeObtainAuthCode = false;
            mGetAuthCode.setEnabled(true);
            mGetAuthCode.setText(R.string.obtain_auth_code_continue);
            stopScheduleJob();
        } else {
            mGetAuthCode.setText(getString(R.string.resend_after_n_seconds, mCounter));
        }
    }
}
