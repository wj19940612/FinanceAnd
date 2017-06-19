package com.sbai.finance.activity.recharge;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.payment.PaymentPath;
import com.sbai.finance.model.payment.UserBankCardInfoModel;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
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

    private int mCounter;
    //获取验证是否开始
    private boolean mFreezeObtainAuthCode;
    private PaymentPath mPaymentPath;
    private UserBankCardInfoModel mUserBankCardInfoModel;
    private String mMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_pay);
        ButterKnife.bind(this);
        mAgreeProtocol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAgreeProtocol.setChecked(isChecked);
            }
        });
        mAuthCode.addTextChangedListener(mValidationWatcher);

        mPaymentPath = getIntent().getParcelableExtra(Launcher.EX_PAYLOAD);
        mUserBankCardInfoModel = getIntent().getParcelableExtra(Launcher.EX_PAY_END);
        if (mUserBankCardInfoModel != null) {
            String cardNumber = mUserBankCardInfoModel.getCardNumber();
            mBankCard.setText(getString(R.string.text_number, mUserBankCardInfoModel.getIssuingBankName(), cardNumber.substring(cardNumber.length() - 4)));
            mName.setText(formatUserName(mUserBankCardInfoModel.getRealName()));
            mIdentityCard.setText(formatIdentityCard(mUserBankCardInfoModel.getIdCard()));
            mPhone.setText(mUserBankCardInfoModel.getCardPhone());
        }
        if (mPaymentPath != null) {
            mDealTime.setText(DateUtil.format(mPaymentPath.getTime(), DateUtil.DEFAULT_FORMAT));
            mDealMoney.setText(getString(R.string.RMB, String.valueOf(mPaymentPath.getMoney())));
        }
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
            if (!mSubmitRechargeInfo.isEnabled()) {
                mSubmitRechargeInfo.setEnabled(checkSubmitEnable);
            }
        }
    };

    private boolean checkSubmitEnable() {
        return !TextUtils.isEmpty(mAuthCode.getText().toString()) && mAgreeProtocol.isChecked() && !mFreezeObtainAuthCode;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuthCode.removeTextChangedListener(mValidationWatcher);
    }

    @OnClick({R.id.getAuthCode, R.id.submitRechargeInfo, R.id.serviceProtocol})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.getAuthCode:
                getBankPayAuthCode();
                break;
            case R.id.submitRechargeInfo:
                recharge();
                break;
            case R.id.serviceProtocol:
                break;
        }
    }

    private void recharge() {
        String authCode = mAuthCode.getText().toString().trim();
        Client.confirmBankPay(mPaymentPath.getMerchantOrderId(), authCode)
                .setRetryPolicy(new DefaultRetryPolicy(100000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        Log.d(TAG, "onRespSuccess: " + resp.toString());
                        ToastUtil.curt(resp.toString());
                    }
                })
                .fire();
    }

    private void getBankPayAuthCode() {
        if (mUserBankCardInfoModel != null) {
            Client.submitRechargeData(mPaymentPath.getPlatform(), String.valueOf(mPaymentPath.getMoney()), mUserBankCardInfoModel.getId())
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
            mFreezeObtainAuthCode = false;
            mGetAuthCode.setEnabled(true);
            mGetAuthCode.setText(R.string.obtain_auth_code_continue);
            stopScheduleJob();
        } else {
            mGetAuthCode.setText(getString(R.string.resend_after_n_seconds, mCounter));
        }
    }
}
