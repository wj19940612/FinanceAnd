package com.sbai.finance.activity.mine;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.KeyBoardHelper;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ValidationWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.deletePage)
    AppCompatImageView mDeletePage;
    @BindView(R.id.appIconName)
    AppCompatTextView mAppIconName;
    @BindView(R.id.phoneNumber)
    AppCompatEditText mPhoneNumber;
    @BindView(R.id.phoneNumberClear)
    AppCompatImageView mPhoneNumberClear;
    @BindView(R.id.getAuthCode)
    AppCompatTextView mGetAuthCode;
    @BindView(R.id.phoneLl)
    LinearLayoutCompat mPhoneLl;
    @BindView(R.id.authCode)
    AppCompatEditText mAuthCode;
    @BindView(R.id.errorHint)
    AppCompatTextView mErrorHint;
    @BindView(R.id.login)
    AppCompatButton mLogin;
    @BindView(R.id.showLayout)
    RelativeLayout mShowLayout;
    @BindView(R.id.hideLayout)
    TextView mHideLayout;

    private KeyBoardHelper mKeyBoardHelper;
    private int bottomHeight;
    private int mCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        translucentStatusBar();
        mPhoneNumber.setText(LocalUser.getUser().getPhone());
        mPhoneNumber.addTextChangedListener(mPhoneValidationWatcher);
        mAuthCode.addTextChangedListener(mValidationWatcher);
        setKeyboardHelper();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPhoneNumber.removeTextChangedListener(mPhoneValidationWatcher);
        mAuthCode.removeTextChangedListener(mValidationWatcher);
        mKeyBoardHelper.onDestroy();
    }

    /**
     * 设置对键盘高度的监听
     */
    private void setKeyboardHelper() {
        mKeyBoardHelper = new KeyBoardHelper(this);
        mKeyBoardHelper.onCreate();
        mKeyBoardHelper.setOnKeyBoardStatusChangeListener(onKeyBoardStatusChangeListener);
        mHideLayout.post(new Runnable() {
            @Override
            public void run() {
                bottomHeight = mHideLayout.getHeight();
            }
        });
    }

    private KeyBoardHelper.OnKeyBoardStatusChangeListener onKeyBoardStatusChangeListener = new KeyBoardHelper.OnKeyBoardStatusChangeListener() {

        @Override
        public void OnKeyBoardPop(int keyboardHeight) {
            if (bottomHeight < keyboardHeight) {
                int offset = bottomHeight - keyboardHeight + 80;
                final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mShowLayout
                        .getLayoutParams();
                lp.topMargin = offset;
                mShowLayout.setLayoutParams(lp);
                mAppIconName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }

        }

        @Override
        public void OnKeyBoardClose(int oldKeyboardHeight) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mShowLayout
                    .getLayoutParams();
            if (lp.topMargin != 0) {
                lp.topMargin = 0;
                mShowLayout.setLayoutParams(lp);
                mAppIconName.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_launcher_round, 0, 0);
            }

        }
    };

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
        return (!TextUtils.isEmpty(phone) && phone.length() > 10);
    }


    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            if (mErrorHint.isShown()) {
                mErrorHint.setVisibility(View.INVISIBLE);
            }
            boolean enable = checkSignInButtonEnable();
            if (enable != mLogin.isEnabled()) {
                mLogin.setEnabled(enable);
            }
        }
    };

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

    @OnClick({R.id.deletePage, R.id.phoneNumberClear, R.id.getAuthCode, R.id.login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.deletePage:
                finish();
                break;
            case R.id.phoneNumberClear:
                mPhoneNumber.setText("");
                break;
            case R.id.getAuthCode:
                getAuthCode();
                break;
            case R.id.login:
                login();
                break;
        }
    }

    private void login() {
        final String phoneNumber = getPhoneNumber();
        String authCode = mAuthCode.getText().toString().trim();
        Client.login(authCode, phoneNumber)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<UserInfo>>() {
                    @Override
                    protected void onRespSuccess(Resp<UserInfo> resp) {
                        if (resp.isSuccess()) {
                            if (resp.hasData()) {
                                LocalUser.getUser().setUserInfo(resp.getData(), phoneNumber);
                                Log.d(TAG, "onRespSuccess: " + resp.getData().toString());
                            }
                            setResult(RESULT_OK);
                            finish();

                        } else {
                            mErrorHint.setVisibility(View.VISIBLE);
                            mErrorHint.setText(resp.getMsg());
                        }
                    }
                })
                .fire();
    }

    private void getAuthCode() {
        String phoneNumber = getPhoneNumber();
        Client.getAuthCode(phoneNumber)
                .setTag(TAG)
                .setIndeterminate(this)
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
                })
                .fire();
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
