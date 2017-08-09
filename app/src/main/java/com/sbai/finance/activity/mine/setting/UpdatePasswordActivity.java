package com.sbai.finance.activity.mine.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.KeyBoardUtils;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.PasswordEditText;
import com.sbai.finance.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdatePasswordActivity extends BaseActivity {

    @BindView(R.id.rootView)
    LinearLayout mRootView;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @BindView(R.id.oldPassword)
    PasswordEditText mOldPassword;
    @BindView(R.id.password)
    PasswordEditText mPassword;

    @BindView(R.id.complete)
    TextView mComplete;

    private boolean mHasLoginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        ButterKnife.bind(this);

        initData(getIntent());
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPassword.removeTextChangedListener(mValidationWatcher);
        mOldPassword.removeTextChangedListener(mValidationWatcher);
    }

    private void initView() {
        if (mHasLoginPassword) { // modify password view
            mTitleBar.setTitle(R.string.modify_login_password);
            mOldPassword.setVisibility(View.VISIBLE);
            mPassword.setVisibility(View.VISIBLE);
            mOldPassword.setHint(R.string.old_password);
            mPassword.setHint(R.string.new_password);

            mOldPassword.addTextChangedListener(mValidationWatcher);
            mPassword.addTextChangedListener(mValidationWatcher);

            mOldPassword.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mOldPassword.requestFocus();
                    KeyBoardUtils.openKeyBoard(mOldPassword);
                }
            }, 200);

        } else {
            mTitleBar.setTitle(R.string.set_login_password);
            mOldPassword.setVisibility(View.GONE);
            mPassword.setVisibility(View.VISIBLE);
            mPassword.setHint(R.string.password);

            mPassword.addTextChangedListener(mValidationWatcher);

            mPassword.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPassword.requestFocus();
                    KeyBoardUtils.openKeyBoard(mPassword);
                }
            }, 200);
        }
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            boolean enable = checkCompleteButtonEnable();
            if (enable != mComplete.isEnabled()) {
                mComplete.setEnabled(enable);
            }
        }
    };

    private boolean checkCompleteButtonEnable() {
        if (mHasLoginPassword) {
            String password = mPassword.getPassword();
            String oldPassword = mOldPassword.getPassword();
            if (TextUtils.isEmpty(password) || TextUtils.isEmpty(oldPassword)) {
                return false;
            }
        } else {
            String password = mPassword.getPassword();
            if (TextUtils.isEmpty(password)) {
                return false;
            }
        }
        return true;
    }

    private void initData(Intent intent) {
        mHasLoginPassword = intent.getBooleanExtra(ExtraKeys.HAS_LOGIN_PSD, false);
    }

    @OnClick({R.id.complete, R.id.rootView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.complete:
                updateLoginPassword();
                break;
            case R.id.rootView:
                KeyBoardUtils.closeKeyboard(mRootView);
                break;
        }
    }

    private void updateLoginPassword() {
        if (mHasLoginPassword) {
            boolean samePasswords = checkSamePasswords();
            if (samePasswords) {
                ToastUtil.show(R.string.same_passwords);
            } else {
                requestModifyPassword();
            }
        } else {
            requestSetPassword();
        }
    }

    private void requestSetPassword() {
        String password = mPassword.getPassword();
        password = md5Encrypt(password);

        Client.setPassword(password)
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        LocalUser.getUser().getUserInfo().setSetPass(true);

                        ToastUtil.show(R.string.set_success);
                        setResult(RESULT_OK);
                        finish();
                    }
                }).fire();
    }

    private void requestModifyPassword() {
        String password = mPassword.getPassword();
        String oldPassword = mOldPassword.getPassword();
        password = md5Encrypt(password);
        oldPassword = md5Encrypt(oldPassword);

        Client.modifyPassword(password, oldPassword)
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        ToastUtil.show(R.string.modify_success);
                        setResult(RESULT_OK);
                        finish();
                    }
                }).fire();
    }

    private boolean checkSamePasswords() {
        String oldPassword = mOldPassword.getPassword();
        String password = mPassword.getPassword();
        return oldPassword.equals(password);
    }
}
