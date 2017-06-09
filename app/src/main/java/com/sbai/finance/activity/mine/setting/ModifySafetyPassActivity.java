package com.sbai.finance.activity.mine.setting;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.SafetyPasswordEditText;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ModifySafetyPassActivity extends BaseActivity {

    @BindView(R.id.safety_password_hint)
    AppCompatTextView mSafetyPasswordHint;
    @BindView(R.id.safety_password_number)
    SafetyPasswordEditText mSafetyPasswordNumber;
    @BindView(R.id.password_hint)
    TextView mPasswordHint;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;


    private String mOldPassword;
    private String mNewPassWord;
    //输入密码的次数
    private int mPasswordInputCount;
    //用户是否设置过密码
    private boolean mHasPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_safety_pass);
        ButterKnife.bind(this);
        mSafetyPasswordNumber.addTextChangedListener(mValidationWatcher);
        mHasPassword = getIntent().getBooleanExtra(Launcher.EX_PAYLOAD, false);
        if (!mHasPassword) {
            mTitleBar.setTitle(R.string.add_safety_pass);
            mSafetyPasswordHint.setText(R.string.please_set_safety_pass);
        }
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            String password = s.toString();
            mPasswordHint.setText(s.toString());
            if (!mHasPassword) {
                addPassWord(password);
            } else {
                setNewPassWord(password);
            }
        }
    };

    //添加密码流程
    private void addPassWord(String password) {
        if (password.length() == 6) {
            if (mPasswordInputCount == 0) {
                mNewPassWord = password;
                mPasswordInputCount++;
                mSafetyPasswordHint.setText(R.string.please_confirm_safety_pass);
                mSafetyPasswordNumber.clearSafetyNumber();
            } else if (mPasswordInputCount == 1) {
                if (!password.equalsIgnoreCase(mNewPassWord)) {
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
                                        ToastUtil.curt(resp.getMsg());
                                    } else {
                                        ToastUtil.curt(resp.getMsg());
                                    }
                                }
                            })
                            .fire();
                }
            }
        }
    }

    //设置新的安全密码流程
    private void setNewPassWord(String passWord) {
        if (passWord.length() == 6) {
            if (mPasswordInputCount == 0) {
                mOldPassword = passWord;
                mPasswordInputCount++;
                mSafetyPasswordHint.setText(R.string.please_input_new_password);
                mSafetyPasswordNumber.clearSafetyNumber();
            } else if (mPasswordInputCount == 1) {
                mNewPassWord = passWord;
                if (!isSameNewPasswordAndOldPass(mOldPassword, mNewPassWord)) {
                    mPasswordInputCount++;
                    mSafetyPasswordHint.setText(R.string.please_confirm_new_password);
                    mSafetyPasswordNumber.clearSafetyNumber();
                } else {
                    SmartDialog.with(ModifySafetyPassActivity.this,
                            R.string.new_password_is_same_as_old_pass, R.string.modify_fail)
                            .setMessageTextSize(14)
                            .show();
                    mSafetyPasswordNumber.clearSafetyNumber();
                }
            } else if (mPasswordInputCount == 2) {
                if (mNewPassWord.equalsIgnoreCase(passWord)) {
                    confirmNewPassword(mNewPassWord);
                } else {
                    SmartDialog.with(ModifySafetyPassActivity.this,
                            R.string.twice_password_is_different, R.string.modify_fail)
                            .setMessageTextSize(14)
                            .show();
                    mSafetyPasswordNumber.clearSafetyNumber();
                }
            }
        }
    }

    private void confirmNewPassword(String newPassWord) {
        Client.updatePassword(newPassWord)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            ToastUtil.curt(resp.getMsg());
                        } else {
                            ToastUtil.curt(resp.getMsg());
                        }
                    }
                })
                .fireSync();
    }

    private boolean isSameNewPasswordAndOldPass(String oldPassword, String newPassWord) {
        return oldPassword.equalsIgnoreCase(newPassWord);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSafetyPasswordNumber.removeTextChangedListener(mValidationWatcher);
    }
}
