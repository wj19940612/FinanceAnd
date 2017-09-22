package com.sbai.finance.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.evaluation.EvaluationStartActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.KeyBoardHelper;
import com.sbai.finance.utils.KeyBoardUtils;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.PasswordEditText;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.R.id.authCode;

public class LoginActivity extends BaseActivity {

    private static final int REQ_CODE_REGISTER = 888;

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

    @BindView(R.id.authCodeArea)
    LinearLayout mAuthCodeArea;

    @BindView(R.id.passwordLoginOperations)
    LinearLayout mPasswordLoginOperations;

    @BindView(R.id.password)
    PasswordEditText mPassword;
    @BindView(R.id.weChatLogin)
    TextView mWeChatLogin;

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
        if (!TextUtils.isEmpty(LocalUser.getUser().getPhone())) {
            mPhoneNumber.setText(LocalUser.getUser().getPhone());
        }

        initListener();

        setKeyboardHelper();
    }

    private void initListener() {
        mPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!TextUtils.isEmpty(mPhoneNumber.getText().toString()) && hasFocus) {
                    mPhoneNumberClear.setVisibility(View.VISIBLE);
                } else {
                    mPhoneNumberClear.setVisibility(View.INVISIBLE);
                }
            }
        });
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
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_to_bottom);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_REGISTER && resultCode == RESULT_OK) { // 注册成功 发送广播 以及 关闭页面
            postLogin();
        }
    }

    private void sendLoginSuccessBroadcast() {
        LocalBroadcastManager.getInstance(getActivity())
                .sendBroadcast(new Intent(ACTION_LOGIN_SUCCESS));
    }

    private void setKeyboardHelper() {
        mKeyBoardHelper = new KeyBoardHelper(this);
        mKeyBoardHelper.onCreate();
        mKeyBoardHelper.setOnKeyBoardStatusChangeListener(onKeyBoardStatusChangeListener);
    }

    private KeyBoardHelper.OnKeyBoardStatusChangeListener onKeyBoardStatusChangeListener = new KeyBoardHelper.OnKeyBoardStatusChangeListener() {

        @Override
        public void OnKeyBoardPop(int keyboardHeight) {
            //    mLoginSwitchTop.setVisibility(View.VISIBLE);
        }

        @Override
        public void OnKeyBoardClose(int oldKeyboardHeight) {
            //     mLoginSwitchTop.setVisibility(View.GONE);
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
        return !TextUtils.isEmpty(phone);
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
        String password = mPassword.getPassword();

        if (TextUtils.isEmpty(phone) || phone.length() < 11) {
            return false;
        }

        if (isAuthCodeLogin() && (TextUtils.isEmpty(authCode) || authCode.length() < 4)) {
            return false;
        }

        return !(!isAuthCodeLogin() && (TextUtils.isEmpty(password) || password.length() < 6));

    }

    private String getPhoneNumber() {
        return mPhoneNumber.getText().toString().trim().replaceAll(" ", "");
    }

    @OnClick({R.id.closePage, R.id.phoneNumberClear, R.id.getAuthCode, R.id.login, R.id.rootView,
            R.id.loginSwitchTop, R.id.register, R.id.forgetPassword, R.id.weChatLogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.closePage:
                finish();
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
                umengEventCount(UmengCountEventId.ME_LOGIN);
                login();
                break;
            case R.id.rootView:
                KeyBoardUtils.closeKeyboard(mRootView);
                break;

            case R.id.register:
                Launcher.with(getActivity(), InputPhoneActivity.class)
                        .putExtra(ExtraKeys.PAGE_TYPE, InputPhoneActivity.PAGE_TYPE_REGISTER)
                        .executeForResult(REQ_CODE_REGISTER);
                break;
            case R.id.forgetPassword:
                Launcher.with(getActivity(), InputPhoneActivity.class)
                        .putExtra(ExtraKeys.PAGE_TYPE, InputPhoneActivity.PAGE_TYPE_FORGET_PSD)
                        .execute();
                break;

            case R.id.loginSwitchTop:
                switchLoginMode();
                break;
            case R.id.weChatLogin:
                weChatLogin();
            default:
                break;
        }
    }

    private void weChatLogin() {
        UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                Log.d(TAG, "onStart " + "授权开始");
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                //sdk是6.4.4的,但是获取值的时候用的是6.2以前的(access_token)才能获取到值,未知原因
                String uid = map.get("uid");
                String openid = map.get("openid");//微博没有
                String unionid = map.get("unionid");//微博没有
                String access_token = map.get("access_token");
                String refresh_token = map.get("refresh_token");//微信,qq,微博都没有获取到
                String expires_in = map.get("expires_in");
                String name = map.get("name");
                String gender = map.get("gender");
                String iconurl = map.get("iconurl");
                ToastUtil.show("name=" + name + ",gender=" + gender);
                //拿到信息去请求登录接口。。。
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                Log.d(TAG, "onError " + "授权失败:" + throwable.getMessage());
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                Log.d(TAG, "onCancel " + "授权取消");
            }
        });
    }

    private void switchLoginMode() {
        if (isAuthCodeLogin()) { // 当前是验证码登录 -> 密码登录
            mPageTitle.setText(R.string.password_login);
            mLoginSwitchTop.setText(R.string.auth_code_login);
            mAuthCodeArea.setVisibility(View.GONE);
            mPassword.setVisibility(View.VISIBLE);
            mPasswordLoginOperations.setVisibility(View.VISIBLE);
            mAuthCode.setText("");
        } else {
            mPageTitle.setText(R.string.auth_code_login);
            mLoginSwitchTop.setText(R.string.password_login);
            mAuthCodeArea.setVisibility(View.VISIBLE);
            mPassword.setVisibility(View.GONE);
            mPasswordLoginOperations.setVisibility(View.GONE);
            mPassword.setPassword("");
        }
    }

    private boolean isAuthCodeLogin() {
        String pageTitle = mPageTitle.getText().toString();
        String authCodeLoginTitle = getString(R.string.auth_code_login);
        return !TextUtils.isEmpty(pageTitle) && pageTitle.equals(authCodeLoginTitle);
    }

    private void login() {
        KeyBoardUtils.closeKeyboard(mLogin);

        final String phoneNumber = getPhoneNumber();
        final String authCode = mAuthCode.getText().toString().trim();
        String password = mPassword.getPassword();
        password = md5Encrypt(password);

        mLogin.setText(R.string.login_ing);
        mLoading.setVisibility(View.VISIBLE);
        mLoading.startAnimation(AnimationUtils.loadAnimation(this, R.anim.loading));

        if (isAuthCodeLogin()) {
            Client.authCodeLogin(phoneNumber, authCode).setTag(TAG)
                    .setCallback(new Callback<Resp<UserInfo>>() {
                        @Override
                        public void onFinish() {
                            super.onFinish();
                            resetLoginButton();
                        }

                        @Override
                        protected void onRespSuccess(Resp<UserInfo> resp) {
                            LocalUser.getUser().setUserInfo(resp.getData(), phoneNumber);
                            ToastUtil.show(R.string.login_success);
                            postLogin();
                        }
                    }).fire();
        } else {
            Client.login(phoneNumber, password).setTag(TAG)
                    .setCallback(new Callback<Resp<UserInfo>>() {
                        @Override
                        public void onFinish() {
                            super.onFinish();
                            resetLoginButton();
                        }

                        @Override
                        protected void onRespSuccess(Resp<UserInfo> resp) {
                            LocalUser.getUser().setUserInfo(resp.getData(), phoneNumber);
                            ToastUtil.show(R.string.login_success);
                            postLogin();
                        }
                    }).fire();
        }
    }

    private void postLogin() {
        if (LocalUser.getUser().getUserInfo().isNewUser()) {
            Launcher.with(getActivity(), EvaluationStartActivity.class)
                    .putExtra(ExtraKeys.FIRST_TEST, true)
                    .execute();
        }

        sendLoginSuccessBroadcast();
        setResult(RESULT_OK);
        finish();
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
