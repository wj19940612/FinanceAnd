package com.sbai.finance.activity.mine.wallet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.FeedbackActivity;
import com.sbai.finance.activity.recharge.AliPayActivity;
import com.sbai.finance.activity.recharge.BankCardPayActivity;
import com.sbai.finance.activity.recharge.WeChatPayActivity;
import com.sbai.finance.model.payment.BankLimit;
import com.sbai.finance.model.payment.PaymentPath;
import com.sbai.finance.model.payment.UsablePlatform;
import com.sbai.finance.model.payment.UserBankCardInfoModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.ValidationWatcher;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;

import static com.sbai.finance.utils.Launcher.EX_PAYLOAD;


public class RechargeActivity extends BaseActivity {

    @BindView(R.id.rechargeWay)
    AppCompatTextView mRechargeWay;
    @BindView(R.id.rechargeCount)
    AppCompatEditText mRechargeCount;
    @BindView(R.id.recharge)
    AppCompatButton mRecharge;
    @BindView(R.id.connect_service)
    AppCompatTextView mConnectService;
    @BindView(R.id.rechargeLL)
    LinearLayout mRechargeLL;

    private String[] mPayData;
    private List<UsablePlatform> mUsablePlatformList;
    private UsablePlatform mUsablePlatform;
    private UserBankCardInfoModel mUserBankCardInfoModel;
    private String mSelectPayWayName;
    private BankLimit mBankLimit;
    //银行卡已经绑定
    private String mBankPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);
        mRechargeCount.addTextChangedListener(mValidationWatcher);

        mUserBankCardInfoModel = getIntent().getParcelableExtra(Launcher.EX_PAY_END);
        requestUsablePlatformList();
        if (mUserBankCardInfoModel != null) {
            mBankPay = mUserBankCardInfoModel.getIssuingBankName() + "(" + mUserBankCardInfoModel.getCardNumber().substring(mUserBankCardInfoModel.getCardNumber().length() - 4) + ")";
        }

    }

    private void requestBankLimit() {
        if (mUserBankCardInfoModel != null) {
            Client.getBankLimit(mUserBankCardInfoModel.getBankId())
                    .setTag(TAG)
                    .setCallback(new Callback2D<Resp<List<BankLimit>>, List<BankLimit>>() {
                        @Override
                        protected void onRespSuccessData(List<BankLimit> data) {
                            if (data != null && !data.isEmpty()) {
                                mBankLimit = data.get(0);
                                if (mUsablePlatform != null && mUsablePlatform.isBankPay()) {
                                    SpannableString payBank = StrUtil.mergeTextWithRatioColor(mBankPay, "\n" + getString(R.string.bank_card_recharge_limit, mBankLimit.getLimitSingle()), 0.98f,
                                            ContextCompat.getColor(RechargeActivity.this, R.color.unluckyText));
                                    mRechargeWay.setText(payBank);
                                }
                            }

                        }
                    })
                    .fire();

        }
    }

    private void requestUsablePlatformList() {
        Client.getUsablePlatform().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<UsablePlatform>>, List<UsablePlatform>>() {
                    @Override
                    protected void onRespSuccessData(List<UsablePlatform> usablePlatformList) {
                        mPayData = new String[usablePlatformList.size()];
                        mUsablePlatformList = usablePlatformList;
                        requestBankLimit();
                        for (int i = 0; i < usablePlatformList.size(); i++) {
                            UsablePlatform usablePlatform = usablePlatformList.get(i);
                            if (usablePlatform.isBankPay() && mUserBankCardInfoModel != null) {
                                mPayData[i] = mBankPay;
                            } else {
                                mPayData[i] = usablePlatform.getName();
                            }
                            if (usablePlatform.getType() == Preference.get().getRechargeWay()) {
                                mUsablePlatform = usablePlatform;
                                if (mUsablePlatform.isBankPay() && mUserBankCardInfoModel != null) {
                                    mSelectPayWayName = mBankPay;
                                } else {
                                    mSelectPayWayName = mUsablePlatform.getName();
                                }
                                mRechargeWay.setText(mSelectPayWayName);
                            }
                        }
                    }
                }).fire();
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            boolean rechargeBtnEnable = checkRechargeBtnEnable();
            if (mRecharge.isEnabled() != rechargeBtnEnable) {
                mRecharge.setEnabled(rechargeBtnEnable);
            }
        }
    };

    private boolean checkRechargeBtnEnable() {
        String count = mRechargeCount.getText().toString();
        return !TextUtils.isEmpty(count) && Double.parseDouble(count) >= 5;
    }

    @OnClick({R.id.rechargeWay, R.id.rechargeCount, R.id.recharge, R.id.connect_service, R.id.rechargeLL})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rechargeWay:
                if (mPayData != null && mPayData.length > 0) {
                    showRechargePicker();
                } else {
                    requestUsablePlatformList();
                }
                break;
            case R.id.rechargeCount:
                break;
            case R.id.recharge:
                submitRechargeData();
                break;
            case R.id.connect_service:
                Launcher.with(getActivity(), FeedbackActivity.class).execute();
                break;
            case R.id.rechargeLL:
                if (mPayData != null && mPayData.length > 0) {
                    showRechargePicker();
                } else {
                    requestUsablePlatformList();
                }
                break;
        }
    }

    private void submitRechargeData() {
        if (mUsablePlatform == null) return;
        final String money = mRechargeCount.getText().toString();
        Integer bankId = null;
        if (mUserBankCardInfoModel != null && mUsablePlatform.isBankPay()) {
            bankId = mUserBankCardInfoModel.getId();
        }

        if (mUsablePlatform.isBankPay() && mUserBankCardInfoModel == null) {
            Launcher.with(getActivity(), BindBankCardActivity.class)
                    .putExtra(Launcher.EX_PAY_END, mUserBankCardInfoModel)
                    .executeForResult(BindBankCardActivity.REQ_CODE_BIND_CARD);
        } else {
            if (mBankLimit != null && mBankLimit.getLimitSingle() < Double.parseDouble(money)) {
                ToastUtil.curt(R.string.input_money_more_than_limit);
                return;
            }
            Client.submitRechargeData(mUsablePlatform.getPlatform(), money, bankId)
                    .setIndeterminate(this)
                    .setCallback(new Callback2D<Resp<PaymentPath>, PaymentPath>() {
                        @Override
                        protected void onRespSuccessData(PaymentPath data) {
                            Log.d(TAG, "onRespSuccessData: " + data.toString());
                            if (mUsablePlatform.getType() == UsablePlatform.TYPE_AIL_PAY) {
                                Launcher.with(getActivity(), AliPayActivity.class)
                                        .putExtra(Launcher.EX_PAYLOAD, data.getPlatform())
                                        .putExtra(Launcher.EX_PAYLOAD_2, data.getThridOrderId())
                                        .putExtra(Launcher.EX_PAYLOAD_3, money)
                                        .putExtra(Launcher.EX_PAY_END, true)
                                        .execute();
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                Uri content_url = Uri.parse(data.getCodeUrl());
                                intent.setData(content_url);
                                startActivity(intent);
                            } else if (mUsablePlatform.getType() == UsablePlatform.TYPE_WECHAT_PAY) {
                                Launcher.with(getActivity(), WeChatPayActivity.class)
                                        .putExtra(Launcher.EX_PAYLOAD, data.getCodeUrl())
                                        .putExtra(Launcher.EX_PAYLOAD_2, data.getThridOrderId())
                                        .putExtra(Launcher.EX_PAYLOAD_3, true)
                                        .execute();
                            } else if (mUsablePlatform.getType() == UsablePlatform.TYPE_BANK_PAY) {
                                Launcher.with(getActivity(), BankCardPayActivity.class)
                                        .putExtra(Launcher.EX_PAYLOAD, data)
                                        .putExtra(Launcher.EX_PAY_END, mUserBankCardInfoModel)
                                        .execute();
                            }
                            finish();
                        }
                    })
                    .fire();
        }
    }

    private void showRechargePicker() {
        OptionPicker picker = new OptionPicker(this, mPayData);
        picker.setCancelTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
        picker.setSubmitTextColor(ContextCompat.getColor(getActivity(), R.color.warningText));
        picker.setTopBackgroundColor(ContextCompat.getColor(getActivity(), R.color.background));
        picker.setTopHeight(50);
        picker.setAnimationStyle(R.style.BottomDialogAnimation);
        picker.setOffset(2);
        picker.setSelectedItem(mSelectPayWayName);
        picker.setTextColor(ContextCompat.getColor(getActivity(), R.color.primaryText));
        WheelView.LineConfig lineConfig = new WheelView.LineConfig(0);//使用最长的分割线
        lineConfig.setColor(ContextCompat.getColor(getActivity(), R.color.split));
        picker.setLineConfig(lineConfig);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                if (!TextUtils.isEmpty(item)) {
                    mRechargeWay.setText(item);
                    mSelectPayWayName = item;
                    for (UsablePlatform data : mUsablePlatformList) {
                        if (data.getName().equalsIgnoreCase(item)) {
                            Preference.get().setRechargeWay(data.getType());
                            mUsablePlatform = data;
                            break;
                        } else if (item.contains("银行") && data.getName().contains("银行")) {
                            Log.d(TAG, "银行 : " + data.toString());
                            Preference.get().setRechargeWay(data.getType());
                            if (mBankLimit != null) {
                                SpannableString payBank = StrUtil.mergeTextWithRatioColor(item, "\n" + getString(R.string.bank_card_recharge_limit, mBankLimit.getLimitSingle()), 0.98f,
                                        ContextCompat.getColor(RechargeActivity.this, R.color.unluckyText));
                                mRechargeWay.setText(payBank);
                            } else {
                                SpannableString payBank = StrUtil.mergeTextWithRatioColor(item, "", 0.98f,
                                        ContextCompat.getColor(RechargeActivity.this, R.color.unluckyText));
                                mRechargeWay.setText(payBank);
                            }
                            mUsablePlatform = data;
                        }
                    }
                }
            }
        });
        picker.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case BindBankCardActivity.REQ_CODE_BIND_CARD:
                    mUserBankCardInfoModel = data.getParcelableExtra(EX_PAYLOAD);
                    submitRechargeData();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRechargeCount.removeTextChangedListener(mValidationWatcher);
    }
}
