package com.sbai.finance.activity.mine.setting;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.SafetyPasswordEditText;
import com.sbai.finance.view.SmartDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ModifySafetyPassActivity extends BaseActivity {

    @BindView(R.id.safety_password_hint)
    AppCompatTextView mSafetyPasswordHint;
    @BindView(R.id.safety_password_number)
    SafetyPasswordEditText mSafetyPasswordNumber;
    @BindView(R.id.password_hint)
    TextView mPasswordHint;


    private String mOldPassword;
    private String mNewPassWord;
    //输入密码的次数
    private int mPasswordInputCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_safety_pass);
        ButterKnife.bind(this);
        mSafetyPasswordNumber.addTextChangedListener(mValidationWatcher);
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
//            mPasswordHint.setText(s.toString());
            if (s.toString().length() == 6) {
                if (mPasswordInputCount == 0) {
                    mOldPassword = s.toString();
                    mPasswordInputCount++;
                    mSafetyPasswordHint.setText(R.string.please_input_new_password);
                    mSafetyPasswordNumber.clearSafetyNumber();
                } else if (mPasswordInputCount == 1) {
                    mNewPassWord = s.toString();
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
                    if (mNewPassWord.equalsIgnoreCase(s.toString())) {
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
    };

    private void confirmNewPassword(String newPassWord) {
        // TODO: 2017/6/5 提交密码
        ToastUtil.curt("提交密码");
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
