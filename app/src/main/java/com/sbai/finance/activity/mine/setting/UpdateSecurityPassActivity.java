package com.sbai.finance.activity.mine.setting;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.SafetyPasswordEditText;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UpdateSecurityPassActivity extends BaseActivity {

    private static final int PAGE_TYPE_SET = 100;
    private static final int PAGE_TYPE_MODIFY = 101;
    private static final int PAGE_TYPE_FORGET_MODIFY = 102;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @BindView(R.id.securityPasswordHint)
    AppCompatTextView mSafetyPasswordHint;

    @BindView(R.id.securityPassword)
    SafetyPasswordEditText mSecurityPassword;

    @BindView(R.id.password_hint)
    TextView mPasswordHint;

    private String mOldPassword;
    private String mNewPassWord;

    //输入密码的次数
    private int mPasswordInputCount;
    //用户是否设置过密码
    private boolean mHasSecurityPassword;
    private String mAuthCode;

    private int mPageType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_security_pass);
        ButterKnife.bind(this);

        initData(getIntent());

        mSecurityPassword.addTextChangedListener(mValidationWatcher);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
            mSecurityPassword.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        if (mPageType == PAGE_TYPE_SET) {
            mTitleBar.setTitle(R.string.add_security_pass);
            mSafetyPasswordHint.setText(R.string.please_set_security_pass);
        }

        if (mPageType == PAGE_TYPE_FORGET_MODIFY) {
            mSafetyPasswordHint.setText(R.string.please_input_new_password);
        }
    }

    private void initData(Intent intent) {
        mHasSecurityPassword = intent.getBooleanExtra(ExtraKeys.HAS_SECURITY_PSD, false);
        mAuthCode = intent.getStringExtra(ExtraKeys.AUTH_CODE);

        if (mHasSecurityPassword) { // forget password & modify password
            if (TextUtils.isEmpty(mAuthCode)) {
                mPageType = PAGE_TYPE_MODIFY;
            } else {
                mPageType = PAGE_TYPE_FORGET_MODIFY;
                mPasswordInputCount = 1;
            }
        } else {
            mPageType = PAGE_TYPE_SET;
        }
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            String password = s.toString();
            if (mPasswordHint.isShown()) {
                mPasswordHint.setVisibility(View.GONE);
            }

            if (mPageType == PAGE_TYPE_FORGET_MODIFY) {
                setForgetPass(password);
            } else if (mPageType == PAGE_TYPE_SET) {
                addPassWord(password);
            } else {
                setNewPassWord(password);
            }
        }
    };

    private void setForgetPass(String password) {
        if (password.length() == 6) {
            if (mPasswordInputCount == 1) {
                mNewPassWord = password;
                mPasswordInputCount++;
//                mSafetyPasswordHint.setText(R.string.please_confirm_new_password);
                mSecurityPassword.clearSafetyNumber();

            } else if (mPasswordInputCount == 2) {
                if (mNewPassWord.equalsIgnoreCase(password)) {
                    confirmNewPassword(mNewPassWord);
                } else {
                    SmartDialog.with(UpdateSecurityPassActivity.this,
                            R.string.twice_password_is_different, R.string.modify_fail)
                            .show();
                    mSecurityPassword.clearSafetyNumber();
                }
            }
        }
    }

    //添加密码流程
    private void addPassWord(String password) {
        if (password.length() == 6) {
            if (mPasswordInputCount == 0) {
                mNewPassWord = password;
                mPasswordInputCount++;
                mSafetyPasswordHint.setText(R.string.please_confirm_security_pass);
                mSecurityPassword.clearSafetyNumber();
            } else if (mPasswordInputCount == 1) {
                if (!password.equalsIgnoreCase(mNewPassWord)) {
                    mSecurityPassword.clearSafetyNumber();
                    mPasswordHint.setVisibility(View.VISIBLE);
                    mPasswordHint.setText(R.string.twice_pass_is_different);
                } else {
                    Client.submitSetPassword(password)
                            .setTag(TAG)
                            .setIndeterminate(this)
                            .setCallback(new Callback<Resp<Object>>() {
                                @Override
                                protected void onRespSuccess(Resp<Object> resp) {
                                    Log.d(TAG, "onRespSuccess: " + resp.toString());
                                    if (resp.isSuccess()) {
                                        toastMassage(resp);
                                        setResult(RESULT_OK);
                                        finish();
                                    } else {
                                        toastMassage(resp);
                                    }
                                }

                            })
                            .fire();
                }
            }
        }
    }

    private void toastMassage(Resp<Object> resp) {
        ToastUtil.show(resp.getMsg());
    }

    //设置新的安全密码流程
    private void setNewPassWord(final String passWord) {
        if (passWord.length() == 6) {
            if (mPasswordInputCount == 0) {
                Client.checkPassword(passWord)
                        .setTag(TAG)
                        .setCallback(new Callback<Resp<Object>>() {
                            @Override
                            protected void onRespSuccess(Resp<Object> resp) {
                                mOldPassword = passWord;
                                mPasswordInputCount++;
                                mSafetyPasswordHint.setText(R.string.please_input_new_password);
                                mSecurityPassword.clearSafetyNumber();
                                toastMassage(resp);
                            }

                            @Override
                            protected void onRespFailure(Resp failedResp) {
                                super.onRespFailure(failedResp);
                                if (failedResp.getCode() == 600) {
                                    mSecurityPassword.clearSafetyNumber();
                                }
                            }
                        }).fire();

            } else if (mPasswordInputCount == 1) {
                mNewPassWord = passWord;
                if (!isSameNewPasswordAndOldPass(mOldPassword, mNewPassWord)) {
                    mPasswordInputCount++;
                    mSafetyPasswordHint.setText(R.string.please_confirm_new_password);
                    mSecurityPassword.clearSafetyNumber();
                } else {
                    mSafetyPasswordHint.setText(R.string.please_input_new_password);
                    mPasswordInputCount = 1;
                    mSecurityPassword.clearSafetyNumber();
                    mPasswordHint.setText(R.string.new_password_is_same_as_old_pass);
                    mPasswordHint.setVisibility(View.VISIBLE);
                }
            } else if (mPasswordInputCount == 2) {
                if (mNewPassWord.equalsIgnoreCase(passWord)) {
                    confirmNewPassword(mNewPassWord);
                } else {
                    mSecurityPassword.clearSafetyNumber();
                    mPasswordHint.setVisibility(View.VISIBLE);
                    mPasswordHint.setText(R.string.twice_pass_is_different);
                    mSafetyPasswordHint.setText(R.string.please_input_new_password);
                    mPasswordInputCount = 1;
                }
            }
        }
    }

    private void confirmNewPassword(String newPassWord) {
        Client.updatePassword(newPassWord, mOldPassword, mAuthCode)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            toastMassage(resp);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            toastMassage(resp);
                        }
                    }
                })
                .fire();
    }

    private boolean isSameNewPasswordAndOldPass(String oldPassword, String newPassWord) {
        return oldPassword.equalsIgnoreCase(newPassWord);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSecurityPassword.removeTextChangedListener(mValidationWatcher);
    }
}
