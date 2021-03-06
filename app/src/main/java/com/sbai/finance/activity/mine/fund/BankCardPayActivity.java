package com.sbai.finance.activity.mine.fund;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.model.fund.PaymentPath;
import com.sbai.finance.model.fund.UsableRechargeWay;
import com.sbai.finance.model.fund.UserBankCardInfo;
import com.sbai.finance.model.local.SysTime;
import com.sbai.finance.model.mutual.ArticleProtocol;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.KeyBoardHelper;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.utils.ValidationWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BankCardPayActivity extends BaseActivity {

    @BindView(R.id.dealMoney)
    TextView mDealMoney;
    @BindView(R.id.dealTime)
    TextView mDealTime;
    @BindView(R.id.bank_card)
    TextView mBankCard;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.identity_card)
    TextView mIdentityCard;
    @BindView(R.id.phone)
    TextView mPhone;
    @BindView(R.id.getAuthCode)
    AppCompatTextView mGetAuthCode;
    @BindView(R.id.submitRechargeInfo)
    AppCompatButton mSubmitRechargeInfo;
    @BindView(R.id.agreeProtocol)
    CheckBox mAgreeProtocol;
    @BindView(R.id.serviceProtocol)
    TextView mServiceProtocol;
    @BindView(R.id.authCode)
    AppCompatEditText mAuthCode;
    @BindView(R.id.showLayout)
    LinearLayout mShowLayout;
    @BindView(R.id.hideLayout)
    LinearLayout mHideLayout;

    private int mCounter;
    //获取验证是否开始
    private boolean mFreezeObtainAuthCode;
    private UserBankCardInfo mUserBankCardInfo;
    private String mMoney;
    private KeyBoardHelper mKeyBoardHelper;
    private int bottomHeight;
    private UsableRechargeWay mUsableRechargeWay;
    private PaymentPath mPaymentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_pay);
        ButterKnife.bind(this);
        setKeyboardHelper();
        mMoney = getIntent().getStringExtra(Launcher.EX_PAYLOAD);
        mUserBankCardInfo = getIntent().getParcelableExtra(Launcher.EX_PAY_END);
        mUsableRechargeWay = getIntent().getParcelableExtra(Launcher.EX_PAYLOAD_1);

        mAuthCode.addTextChangedListener(mValidationWatcher);
        mAgreeProtocol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAgreeProtocol.setChecked(isChecked);
                boolean checkSubmitEnable = checkSubmitEnable();
                if (mSubmitRechargeInfo.isEnabled() != checkSubmitEnable) {
                    mSubmitRechargeInfo.setEnabled(checkSubmitEnable);
                }
            }
        });

        initPayInfo();
    }

    private void initPayInfo() {
        if (mUserBankCardInfo != null) {
            String cardNumber = mUserBankCardInfo.getCardNumber();
            mBankCard.setText(getString(R.string.text_number, mUserBankCardInfo.getIssuingBankName(), cardNumber.substring(cardNumber.length() - 4)));
            mName.setText(formatUserName(mUserBankCardInfo.getRealName()));
            mIdentityCard.setText(formatIdentityCard(mUserBankCardInfo.getIdCard()));
            mPhone.setText(mUserBankCardInfo.getCardPhone());
        }

        mDealTime.setText(DateUtil.format(SysTime.getSysTime().getSystemTimestamp(), DateUtil.DEFAULT_FORMAT));
        mDealMoney.setText(getString(R.string.RMB, FinanceUtil.formatWithScale(mMoney)));
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

    private String formatUserName(String userName) {
        int length = userName.length();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length - 1; i++) {
            stringBuilder.append("*");
        }
        return userName.substring(0, 1) + stringBuilder.toString();
    }


    private String formatIdentityCard(String certCode) {
        int length = certCode.length();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length - 8; i++) {
            stringBuilder.append("*");
        }
        return certCode.substring(0, 4) + stringBuilder.toString() + certCode.substring(length - 4);
    }

    ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            boolean checkSubmitEnable = checkSubmitEnable();
            if (mSubmitRechargeInfo.isEnabled() != checkSubmitEnable) {
                mSubmitRechargeInfo.setEnabled(checkSubmitEnable);
            }
        }
    };

    private boolean checkSubmitEnable() {
        return !TextUtils.isEmpty(mAuthCode.getText().toString().trim()) && mAgreeProtocol.isChecked();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuthCode.removeTextChangedListener(mValidationWatcher);
        mKeyBoardHelper.onDestroy();
    }

    @OnClick({R.id.getAuthCode, R.id.submitRechargeInfo, R.id.serviceProtocol})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.getAuthCode:
                umengEventCount(UmengCountEventId.GET_VERIFICATION_CODE);
                getBankPayAuthCode();
                break;
            case R.id.submitRechargeInfo:
                umengEventCount(UmengCountEventId.CONFIRM_RECHARGE);
                recharge();
                break;
            case R.id.serviceProtocol:
                openUserProtocolPage();
                break;
        }
    }

    private void openUserProtocolPage() {
        Client.getArticleProtocol(ArticleProtocol.PROTOCOL_RECHARGE_SERVICE).setTag(TAG)
                .setCallback(new Callback2D<Resp<ArticleProtocol>, ArticleProtocol>() {
                    @Override
                    protected void onRespSuccessData(ArticleProtocol data) {
                        Launcher.with(getActivity(), WebActivity.class)
                                .putExtra(WebActivity.EX_TITLE, data.getTitle())
                                .putExtra(WebActivity.EX_HTML, data.getContent())
                                .execute();
                    }

                }).fire();
    }

    private void recharge() {
        String authCode = mAuthCode.getText().toString().trim();
        if (mPaymentPath != null) {
            Client.confirmBankPay(mPaymentPath.getMerchantOrderId(), authCode)
                    .setRetryPolicy(new DefaultRetryPolicy(20000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                    .setIndeterminate(this)
                    .setCallback(new Callback<Resp<Object>>() {
                        @Override
                        protected void onRespSuccess(Resp<Object> resp) {
                            ToastUtil.show(resp.getMsg());
                            setResult(RESULT_OK);
                            finish();
                        }
                    })
                    .fire();
        }
    }

    private void getBankPayAuthCode() {
        if (mUserBankCardInfo != null) {
            Client.submitRechargeData(mUsableRechargeWay.getPlatform(), mMoney, mUserBankCardInfo.getId())
                    .setRetryPolicy(new DefaultRetryPolicy(20000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                    .setIndeterminate(this)
                    .setCallback(new Callback2D<Resp<PaymentPath>, PaymentPath>() {
                        @Override
                        protected void onRespSuccessData(PaymentPath data) {
                            mPaymentPath = data;
                            mFreezeObtainAuthCode = true;
                            startScheduleJob(1000);
                            mCounter = 60;
                            mGetAuthCode.setEnabled(false);
                            mGetAuthCode.setText(getString(R.string.resend_after_n_seconds, mCounter));
                        }
                    })
                    .fire();

        }
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
