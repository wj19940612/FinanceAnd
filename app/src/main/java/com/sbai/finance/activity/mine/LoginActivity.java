package com.sbai.finance.activity.mine;

import android.graphics.Color;
import android.os.Build;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.MainActivity;
import com.sbai.finance.utils.KeyBoardHelper;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ValidationWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.deletePage)
    AppCompatImageView mDeletePage;
    @BindView(R.id.appIconName)
    AppCompatTextView mAppIcon;
    @BindView(R.id.phoneNumber)
    AppCompatEditText mPhoneNumber;
    @BindView(R.id.phoneNumberClear)
    AppCompatImageView mPhoneNumberClear;
    @BindView(R.id.getAuthCode)
    AppCompatTextView mGetAuthCode;
    @BindView(R.id.authCode)
    AppCompatEditText mAuthCode;
    @BindView(R.id.login)
    AppCompatButton mLogin;
    @BindView(R.id.showLayout)
    LinearLayoutCompat mShowLayout;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
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
                int offset = bottomHeight - keyboardHeight;
                final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mShowLayout
                        .getLayoutParams();
                lp.topMargin = offset;
                mShowLayout.setLayoutParams(lp);
                mAppIcon.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }

        }

        @Override
        public void OnKeyBoardClose(int oldKeyboardHeight) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mShowLayout
                    .getLayoutParams();
            if (lp.topMargin != 0) {
                lp.topMargin = 0;
                mShowLayout.setLayoutParams(lp);
                mAppIcon.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_launcher_round, 0, 0);
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
            Log.d(TAG, "是否可获取验证码  " + authCodeEnable);
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
        String phone = mPhoneNumber.getText().toString().trim().replaceAll(" ", "");
        return (!TextUtils.isEmpty(phone) && phone.length() > 10);
    }


    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            boolean enable = checkSignInButtonEnable();
            if (enable != mLogin.isEnabled()) {
                mLogin.setEnabled(enable);
            }
        }
    };

    private boolean checkSignInButtonEnable() {
        String phone = mPhoneNumber.getText().toString().trim().replaceAll(" ", "");
        String authCode = mAuthCode.getText().toString().trim();
        if (TextUtils.isEmpty(phone) || phone.length() < 11) {
            return false;
        } else if (TextUtils.isEmpty(authCode) || authCode.length() < 4) {
            return false;
        }
        return true;
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
        Launcher.with(this, MainActivity.class).execute();
        finish();
    }

    private void getAuthCode() {
        String phoneNumber = mPhoneNumber.getText().toString().trim().replaceAll(" ", "");
        startScheduleJob(1000);
        mCounter = 60;
        mGetAuthCode.setEnabled(false);
        mGetAuthCode.setText(getString(R.string.resend_after_n_seconds, mCounter));
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
