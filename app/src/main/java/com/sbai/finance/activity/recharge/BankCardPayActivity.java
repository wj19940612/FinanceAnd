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

import com.google.gson.JsonObject;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ValidationWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BankCardPayActivity extends BaseActivity {

    @BindView(R.id.dealMoney)
    TextView mDealMoney;
    @BindView(R.id.dealTime)
    TextView mDealTime;
    @BindView(R.id.dealId)
    TextView mDealId;
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
    private String mMerchantOrderId;

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
        mMerchantOrderId = getIntent().getStringExtra(Launcher.EX_PAYLOAD);
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
        return TextUtils.isEmpty(mAuthCode.getText().toString()) && mAgreeProtocol.isChecked() && !mFreezeObtainAuthCode;
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
        Client.confirmBankPay(mMerchantOrderId, "0000")
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        Log.d(TAG, "onRespSuccess: " + resp.toString());
                    }
                })
                .fire();
    }

    private void getBankPayAuthCode() {
        // TODO: 2017/6/16 险些死 
        Client.sendMsgCodeForPassWordOrBankCardPay("18182568000")
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
