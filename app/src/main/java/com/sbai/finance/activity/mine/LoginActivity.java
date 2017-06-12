package com.sbai.finance.activity.mine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.httplib.CookieManger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    public static final String LOGIN_SUCCESS_ACTION = "LOGIN_SUCCESS_ACTION";
    @BindView(R.id.deletePage)
    AppCompatImageView mDeletePage;
    @BindView(R.id.phoneNumber)
    AppCompatEditText mPhoneNumber;
    @BindView(R.id.phoneNumberClear)
    AppCompatImageView mPhoneNumberClear;
    @BindView(R.id.phoneLl)
    LinearLayoutCompat mPhoneLl;
    @BindView(R.id.authCode)
    AppCompatEditText mAuthCode;
    @BindView(R.id.getAuthCode)
    AppCompatTextView mGetAuthCode;
    @BindView(R.id.contentLL)
    LinearLayout mContentLL;
    @BindView(R.id.login)
    AppCompatTextView mLogin;
    @BindView(R.id.showLayout)
    LinearLayout mShowLayout;
    @BindView(R.id.finance_protocol)
    TextView mFinanceProtocol;
    @BindView(R.id.hideLayout)
    LinearLayout mHideLayout;
    @BindView(R.id.loading)
    ImageView mLoading;


    private KeyBoardHelper mKeyBoardHelper;
    private int bottomHeight;
    private int mCounter;
    //获取验证是否开始
    private boolean mFreezeObtainAuthCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        translucentStatusBar(Color.TRANSPARENT);
        mPhoneNumber.addTextChangedListener(mPhoneValidationWatcher);
        mAuthCode.addTextChangedListener(mValidationWatcher);
        mPhoneNumber.setText(LocalUser.getUser().getPhone());
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
            }

        }

        @Override
        public void OnKeyBoardClose(int oldKeyboardHeight) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mShowLayout
                    .getLayoutParams();
            if (lp.topMargin != 0) {
                lp.topMargin = 0;
                mShowLayout.setLayoutParams(lp);
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
        return (!TextUtils.isEmpty(phone) && phone.length() > 10 && !mFreezeObtainAuthCode);
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

    @OnClick({R.id.deletePage, R.id.phoneNumberClear, R.id.getAuthCode,
            R.id.login, R.id.finance_protocol})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.deletePage:
                finish();
                overridePendingTransition(0, R.anim.slide_out_to_bottom);
                break;
            case R.id.phoneNumberClear:
                mPhoneNumber.setText("");
                break;
            case R.id.getAuthCode:
                getAuthCode();
                mAuthCode.requestFocus();
                break;
            case R.id.login:
                login();
                break;
            case R.id.finance_protocol:
                openUserProtocolPage();
                break;
            default:
                break;
        }
    }

    private void openUserProtocolPage() {
        Client.getArticleProtocol(3).setTag(TAG)
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
                                .putExtra(WebActivity.EX_TITLE, getString(R.string.protocol))
                                .putExtra(WebActivity.EX_URL, Client.WEB_USER_PROTOCOL_PAGE_URL)
                                .putExtra(WebActivity.EX_RAW_COOKIE, CookieManger.getInstance().getRawCookie())
                                .execute();
                    }
                }).fire();
    }

    private void login() {
        final String phoneNumber = getPhoneNumber();
        final String authCode = mAuthCode.getText().toString().trim();
        mLogin.setText(R.string.login_ing);
        mLoading.setVisibility(View.VISIBLE);
        mLoading.startAnimation(AnimationUtils.loadAnimation(this, R.anim.loading));
        Client.login(authCode, phoneNumber)
                .setTag(TAG)
                .setCallback(new Callback<Resp<UserInfo>>() {
                    @Override
                    protected void onRespSuccess(Resp<UserInfo> resp) {
                        if (resp.isSuccess()) {
                            if (resp.hasData()) {
                                LocalUser.getUser().setUserInfo(resp.getData(), phoneNumber);
                                Log.d(TAG, "onRespSuccess: " + resp.getData().toString());
                            }
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(ACTION_TOKEN_EXPIRED));
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            ToastUtil.curt(resp.getMsg());
                            mLoading.clearAnimation();
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
                            mFreezeObtainAuthCode = true;
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
            mFreezeObtainAuthCode = false;
            mGetAuthCode.setEnabled(true);
            mGetAuthCode.setText(R.string.obtain_auth_code_continue);
            stopScheduleJob();
        } else {
            mGetAuthCode.setText(getString(R.string.resend_after_n_seconds, mCounter));
        }
    }

}
