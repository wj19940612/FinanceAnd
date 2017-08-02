package com.sbai.finance.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.model.mutual.ArticleProtocol;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.KeyBoardHelper;
import com.sbai.finance.utils.KeyBoardUtils;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.httplib.CookieManger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.R.id.authCode;

public class LoginActivity extends BaseActivity {

    public static final String LOGIN_SUCCESS_ACTION = "LOGIN_SUCCESS_ACTION";

    @BindView(R.id.rootView)
    RelativeLayout mRootView;

    @BindView(R.id.closePage)
    ImageView mClosePage;

    @BindView(R.id.phoneNumber)
    EditText mPhoneNumber;
    @BindView(R.id.phoneNumberClear)
    ImageView mPhoneNumberClear;
    @BindView(authCode)
    EditText mAuthCode;
    @BindView(R.id.getAuthCode)
    TextView mGetAuthCode;

    @BindView(R.id.login)
    TextView mLogin;
    @BindView(R.id.loading)
    ImageView mLoading;

    @BindView(R.id.pageTitle)
    TextView mPageTitle;
    @BindView(R.id.loginSwitchTop)
    TextView mLoginSwitchTop;
    @BindView(R.id.loginSwitch)
    TextView mLoginSwitch;

    @BindView(R.id.authCodeArea)
    LinearLayout mAuthCodeArea;
    @BindView(R.id.passwordArea)
    LinearLayout mPasswordArea;
    @BindView(R.id.passwordLoginOperations)
    LinearLayout mPasswordLoginOperations;

    @BindView(R.id.showPassword)
    ImageView mShowPassword;
    @BindView(R.id.password)
    EditText mPassword;

    private KeyBoardHelper mKeyBoardHelper;

    private int mCounter;
    private boolean mFreezeObtainAuthCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        translucentStatusBar();

        mPhoneNumber.addTextChangedListener(mPhoneValidationWatcher);
        mAuthCode.addTextChangedListener(mValidationWatcher);
        mPassword.addTextChangedListener(mValidationWatcher);
        mPhoneNumber.setText(LocalUser.getUser().getPhone());

        initListener();

        setKeyboardHelper();
    }

    private void initListener() {
        mAuthCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhoneNumberClear.setVisibility(View.INVISIBLE);
                mAuthCode.requestFocus();
            }
        });
        mAuthCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mPhoneNumberClear.setVisibility(View.INVISIBLE);
                }
            }
        });
        mPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhoneNumberClear.setVisibility(View.INVISIBLE);
                mPassword.requestFocus();
            }
        });
        mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mPhoneNumberClear.setVisibility(View.INVISIBLE);
                }
            }
        });
        mPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!TextUtils.isEmpty(mPhoneNumber.getText().toString()) && hasFocus) {
                    mPhoneNumberClear.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPhoneNumber.removeTextChangedListener(mPhoneValidationWatcher);
        mAuthCode.removeTextChangedListener(mValidationWatcher);
        mPassword.removeTextChangedListener(mValidationWatcher);
        mKeyBoardHelper.onDestroy();
        mLoading.clearAnimation();
    }

    private void setKeyboardHelper() {
        mKeyBoardHelper = new KeyBoardHelper(this);
        mKeyBoardHelper.onCreate();
        mKeyBoardHelper.setOnKeyBoardStatusChangeListener(onKeyBoardStatusChangeListener);
    }

    private KeyBoardHelper.OnKeyBoardStatusChangeListener onKeyBoardStatusChangeListener = new KeyBoardHelper.OnKeyBoardStatusChangeListener() {

        @Override
        public void OnKeyBoardPop(int keyboardHeight) {
            mLoginSwitchTop.setVisibility(View.VISIBLE);
        }

        @Override
        public void OnKeyBoardClose(int oldKeyboardHeight) {
            mLoginSwitchTop.setVisibility(View.GONE);
        }
    };

    private ValidationWatcher mPhoneValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            mValidationWatcher.afterTextChanged(s);

            formatPhoneNumber();

            mPhoneNumberClear.setVisibility(checkClearBtnVisible() ? View.VISIBLE : View.INVISIBLE);

            if (isAuthCodeLogin()) {
                if (getPhoneNumber().length() == 11) {
                    mPhoneNumber.clearFocus();
                    mAuthCode.requestFocus();
                    mPhoneNumberClear.setVisibility(View.INVISIBLE);
                }

                boolean authCodeEnable = checkObtainAuthCodeEnable();
                if (mGetAuthCode.isEnabled() != authCodeEnable) {
                    mGetAuthCode.setEnabled(authCodeEnable);
                }
            } else {
                if (getPhoneNumber().length() == 11) {
                    mPhoneNumber.clearFocus();
                    mPassword.requestFocus();
                    mPhoneNumberClear.setVisibility(View.INVISIBLE);
                }
            }
        }
    };

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            boolean enable = checkSignInButtonEnable();
            if (enable != mLogin.isEnabled()) {
                mLogin.setEnabled(enable);
            }
        }
    };

    private boolean checkClearBtnVisible() {
        String phone = mPhoneNumber.getText().toString();
        if (!TextUtils.isEmpty(phone)) {
            return true;
        }
        return false;
    }

    private void formatPhoneNumber() {
        String oldPhone = mPhoneNumber.getText().toString();
        String phoneNoSpace = oldPhone.replaceAll(" ", "");
        String newPhone = StrFormatter.getFormatPhoneNumber(phoneNoSpace);
        if (!newPhone.equalsIgnoreCase(oldPhone)) {
            mPhoneNumber.setText(newPhone);
            mPhoneNumber.setSelection(newPhone.length());
        }
    }

    private boolean checkObtainAuthCodeEnable() {
        String phone = getPhoneNumber();
        return (!TextUtils.isEmpty(phone) && phone.length() > 10 && !mFreezeObtainAuthCode);
    }

    private boolean checkSignInButtonEnable() {
        String phone = getPhoneNumber();
        String authCode = mAuthCode.getText().toString().trim();
        String password = mPassword.getText().toString();

        if (TextUtils.isEmpty(phone) || phone.length() < 11) {
            return false;
        }

        if (isAuthCodeLogin() && (TextUtils.isEmpty(authCode) || authCode.length() < 4)) {
            return false;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            return false;
        }

        return true;
    }

    private String getPhoneNumber() {
        return mPhoneNumber.getText().toString().trim().replaceAll(" ", "");
    }

    @OnClick({R.id.closePage, R.id.phoneNumberClear, R.id.getAuthCode, R.id.login, R.id.rootView,
            R.id.loginSwitch, R.id.loginSwitchTop, R.id.showPassword,
            R.id.register, R.id.forgetPassword})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.closePage:
                finish();
                overridePendingTransition(0, R.anim.slide_out_to_bottom);
                break;
            case R.id.phoneNumberClear:
                mPhoneNumber.setText("");
                break;
            case R.id.getAuthCode:
                getAuthCode();
                mPhoneNumberClear.setVisibility(View.INVISIBLE);
                mAuthCode.requestFocus();
                break;
            case R.id.login:
                login();
                break;
            case R.id.rootView:
                KeyBoardUtils.closeKeyboard(this, mRootView);
                break;
            case R.id.showPassword:
                togglePasswordVisible();
                break;

            case R.id.register:

                break;
            case R.id.forgetPassword:

                break;

            case R.id.loginSwitch:
            case R.id.loginSwitchTop:
                switchLoginMode();
                break;
            default:
                break;
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

    private void switchLoginMode() {
        if (isAuthCodeLogin()) { // 当前是验证码登录 -> 密码登录
            mPageTitle.setText(R.string.password_login);
            mLoginSwitch.setText(R.string.auth_code_login);
            mLoginSwitchTop.setText(R.string.auth_code_login);
            mAuthCodeArea.setVisibility(View.GONE);
            mPasswordArea.setVisibility(View.VISIBLE);
            mPasswordLoginOperations.setVisibility(View.VISIBLE);
        } else {
            mPageTitle.setText(R.string.auth_code_login);
            mLoginSwitch.setText(R.string.password_login);
            mLoginSwitchTop.setText(R.string.password_login);
            mAuthCodeArea.setVisibility(View.VISIBLE);
            mPasswordArea.setVisibility(View.GONE);
            mPasswordLoginOperations.setVisibility(View.GONE);
        }
    }

    private boolean isAuthCodeLogin() {
        String pageTitle = mPageTitle.getText().toString();
        String authCodeLoginTitle = getString(R.string.auth_code_login);
        if (!TextUtils.isEmpty(pageTitle) && pageTitle.equals(authCodeLoginTitle)) {
            return true;
        }
        return false;
    }

    private void openUserProtocolPage() {
        Client.getArticleProtocol(ArticleProtocol.PROTOCOL_USER).setTag(TAG)
                .setCallback(new Callback2D<Resp<ArticleProtocol>, ArticleProtocol>(false) {
                    @Override
                    protected void onRespSuccessData(ArticleProtocol data) {

                        Launcher.with(getActivity(), WebActivity.class)
                                .putExtra(WebActivity.EX_TITLE, getString(R.string.user_protocol))
                                .putExtra(WebActivity.EX_HTML, data.getContent())
                                .putExtra(WebActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                                .execute();
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        Launcher.with(getActivity(), WebActivity.class)
                                .putExtra(WebActivity.EX_TITLE, getString(R.string.user_protocol))
                                .putExtra(WebActivity.EX_URL, Client.WEB_USER_PROTOCOL_PAGE_URL)
                                .putExtra(WebActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                                .execute();
                    }
                }).fire();
    }

    private void login() {
        KeyBoardUtils.closeKeyboard(this, mLogin);

        final String phoneNumber = getPhoneNumber();
        final String authCode = mAuthCode.getText().toString().trim();
        String password = mPassword.getText().toString();

        mLogin.setText(R.string.login_ing);
        mLoading.setVisibility(View.VISIBLE);
        mLoading.startAnimation(AnimationUtils.loadAnimation(this, R.anim.loading));

        if (isAuthCodeLogin()) {
            Client.authCodeLogin(phoneNumber, authCode).setTag(TAG)
                    .setCallback(new Callback<Resp<UserInfo>>() {
                        @Override
                        protected void onRespSuccess(Resp<UserInfo> resp) {
                            postLogin(resp, phoneNumber);
                        }

                        @Override
                        public void onFailure(VolleyError volleyError) {
                            super.onFailure(volleyError);
                            resetLoginButton();
                        }
                    }).fire();
        } else {
            Client.login(phoneNumber, password).setTag(TAG)
                    .setCallback(new Callback<Resp<UserInfo>>() {
                        @Override
                        protected void onRespSuccess(Resp<UserInfo> resp) {
                            postLogin(resp, phoneNumber);
                        }

                        @Override
                        public void onFailure(VolleyError volleyError) {
                            super.onFailure(volleyError);
                            resetLoginButton();
                        }
                    }).fire();
        }
    }

    private void postLogin(Resp<UserInfo> resp, String phoneNumber) {
        resetLoginButton();
        if (resp.isSuccess()) {
            if (resp.hasData()) {
                LocalUser.getUser().setUserInfo(resp.getData(), phoneNumber);
            }

            LocalBroadcastManager.getInstance(getActivity())
                    .sendBroadcast(new Intent(LOGIN_SUCCESS_ACTION));
            finish();
        } else {
            ToastUtil.show(resp.getMsg());
            mLoading.clearAnimation();
        }
    }

    private void resetLoginButton() {
        mLogin.setText(R.string.login);
        mLoading.setVisibility(View.GONE);
        mLoading.clearAnimation();
    }

    private void getAuthCode() {
        String phoneNumber = getPhoneNumber();
        Client.getAuthCode(phoneNumber)
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
                            mFreezeObtainAuthCode = true;
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
            mFreezeObtainAuthCode = false;
            mGetAuthCode.setEnabled(true);
            mGetAuthCode.setText(R.string.obtain_auth_code_continue);
            stopScheduleJob();
        } else {
            mGetAuthCode.setText(getString(R.string.resend_after_n_seconds, mCounter));
        }
    }

}
