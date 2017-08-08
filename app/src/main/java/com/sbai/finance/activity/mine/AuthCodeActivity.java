package com.sbai.finance.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.KeyBoardUtils;
import com.sbai.finance.utils.PasswordInputFilter;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.ValidationWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthCodeActivity extends BaseActivity {

    public static final int PAGE_TYPE_REGISTER = 801;

    @BindView(R.id.receivePhone)
    TextView mReceivePhone;
    @BindView(R.id.authCode)
    EditText mAuthCode;
    @BindView(R.id.getAuthCode)
    TextView mGetAuthCode;
    @BindView(R.id.password)
    EditText mPassword;
    @BindView(R.id.showPassword)
    ImageView mShowPassword;
    @BindView(R.id.complete)
    TextView mComplete;
    @BindView(R.id.rootView)
    RelativeLayout mRootView;

    private int mPageType;
    private String mPhone;

    private int mCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_code);
        ButterKnife.bind(this);

        initData(getIntent());

        if (mPageType == PAGE_TYPE_REGISTER) {
            mPassword.setHint(R.string.password);
        } else {
            mPassword.setHint(R.string.new_password);
        }
        mReceivePhone.setText(
                getString(R.string.auth_code_had_sent_, StrFormatter.getFormatPhoneNumber(mPhone)));

        mAuthCode.addTextChangedListener(mValidationWatcher);
        mPassword.addTextChangedListener(mValidationWatcher);
        mPassword.setFilters(new InputFilter[] {new PasswordInputFilter()});

        mAuthCode.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGetAuthCode.performClick();
                mAuthCode.requestFocus();
                KeyBoardUtils.openKeyBoard(mAuthCode);
            }
        }, 200);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuthCode.removeTextChangedListener(mValidationWatcher);
        mPassword.removeTextChangedListener(mValidationWatcher);
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
        String authCode = mAuthCode.getText().toString().trim();
        String password = mPassword.getText().toString();

        if (TextUtils.isEmpty(authCode) || authCode.length() < 4) {
            return false;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            return false;
        }

        return true;
    }

    private void initData(Intent intent) {
        mPageType = intent.getIntExtra(ExtraKeys.PAGE_TYPE, 0);
        mPhone = intent.getStringExtra(ExtraKeys.PHONE);
    }

    @OnClick({R.id.getAuthCode, R.id.showPassword, R.id.complete, R.id.rootView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.getAuthCode:
                getAuthCode();
                break;
            case R.id.showPassword:
                togglePasswordVisible();
                break;
            case R.id.complete:
                doCompleteButtonClick();
                break;
            case R.id.rootView:
                KeyBoardUtils.closeKeyboard(mRootView);
                break;
        }
    }

    private void doCompleteButtonClick() {
        String password = mPassword.getText().toString();
        String authCode = mAuthCode.getText().toString().trim();
        password = md5Encrypt(password);

        if (mPageType == PAGE_TYPE_REGISTER) {
            Client.register(mPhone, password, authCode)
                    .setTag(TAG).setIndeterminate(this)
                    .setCallback(new Callback<Resp<UserInfo>>() {
                        @Override
                        protected void onRespSuccess(Resp<UserInfo> resp) {
                            if (resp.hasData()) {
                                LocalUser.getUser().setUserInfo(resp.getData(), mPhone);
                            }
                            setResult(RESULT_OK);
                            finish();
                        }
                    }).fire();
        } else { // Forget password and set new password
            Client.updateNewPassword(mPhone, password, authCode)
                    .setTag(TAG).setIndeterminate(this)
                    .setCallback(new Callback<Resp>() {
                        @Override
                        protected void onRespSuccess(Resp resp) {
                            ToastUtil.show(R.string.reset_password_success);
                            setResult(RESULT_OK);
                            finish();
                        }
                    }).fire();
        }
    }

    private void togglePasswordVisible() {
        if (mShowPassword.isSelected()) {
            mShowPassword.setSelected(false);
            mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            mShowPassword.setSelected(true);
            mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        mPassword.postInvalidate();
        CharSequence text = mPassword.getText();
        if (text != null) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }

    private void getAuthCode() {
        Client.getAuthCode(mPhone)
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
                            startScheduleJob(1000);
                            mCounter = 60;
                            mGetAuthCode.setEnabled(false);
                            mGetAuthCode.setText(getString(R.string.resend_after_n_seconds, mCounter));
                        }
                    }
                }).fire();
    }

    @Override
    public void onTimeUp(int count) {
        mCounter--;
        if (mCounter <= 0) {
            mGetAuthCode.setEnabled(true);
            mGetAuthCode.setText(R.string.obtain_auth_code_continue);
            stopScheduleJob();
        } else {
            mGetAuthCode.setText(getString(R.string.resend_after_n_seconds, mCounter));
        }
    }
}
