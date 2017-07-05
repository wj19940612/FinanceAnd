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

import com.android.volley.DefaultRetryPolicy;
import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.FeedbackActivity;
import com.sbai.finance.activity.recharge.AliPayActivity;
import com.sbai.finance.activity.recharge.BankCardPayActivity;
import com.sbai.finance.activity.recharge.WeChatPayActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.payment.BankLimit;
import com.sbai.finance.model.payment.PaymentPath;
import com.sbai.finance.model.payment.UsablePlatform;
import com.sbai.finance.model.payment.UserBankCardInfoModel;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventIdUtils;
import com.sbai.finance.utils.ValidationWatcher;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;

import static com.sbai.finance.R.id.rechargeCount;


public class RechargeActivity extends BaseActivity {

    @BindView(R.id.rechargeWay)
    AppCompatTextView mRechargeWay;
    @BindView(rechargeCount)
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

        formatBankPay();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        requestUserBankInfo();
    }

    private void formatBankPay() {
        if (mUserBankCardInfoModel != null && !TextUtils.isEmpty(mUserBankCardInfoModel.getCardNumber())) {
            mBankPay = mUserBankCardInfoModel.getIssuingBankName() + "(" + mUserBankCardInfoModel.getCardNumber().substring(mUserBankCardInfoModel.getCardNumber().length() - 4) + ")";
        }
    }

    private void requestUserBankInfo() {
        Client.requestUserBankCardInfo()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<UserBankCardInfoModel>>, List<UserBankCardInfoModel>>() {
                    @Override
                    protected void onRespSuccessData(List<UserBankCardInfoModel> data) {
                        if (data != null && !data.isEmpty()) {
                            mUserBankCardInfoModel = data.get(0);
                            formatBankPay();
                            requestUsablePlatformList();
                        }
                    }

                })
                .fire();
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
                                if (mUsablePlatform != null && mUsablePlatform.isBankPay() && !TextUtils.isEmpty(mBankPay)) {
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
                        handleUserPayPlatform(usablePlatformList);
                    }
                }).fire();
    }

    private void handleUserPayPlatform(List<UsablePlatform> usablePlatformList) {
        for (int i = 0; i < usablePlatformList.size(); i++) {
            UsablePlatform usablePlatform = usablePlatformList.get(i);
            if (usablePlatform.isBankPay() && mUserBankCardInfoModel != null && !TextUtils.isEmpty(mBankPay)) {
                mPayData[i] = mBankPay;
            } else {
                mPayData[i] = usablePlatform.getName();
            }
            if (usablePlatform.getPlatform().equalsIgnoreCase(Preference.get().getRechargeWay(LocalUser.getUser().getPhone()))) {
                mUsablePlatform = usablePlatform;
                if (mUsablePlatform.isBankPay()
                        && mUserBankCardInfoModel != null
                        && !mUserBankCardInfoModel.isNotConfirmBankInfo()
                        && !TextUtils.isEmpty(mBankPay)) {
                    mSelectPayWayName = mBankPay;
                } else {
                    mSelectPayWayName = mUsablePlatform.getName();
                }
                mRechargeWay.setText(mSelectPayWayName);
                Log.d(TAG, "onRespSuccessData: " + mSelectPayWayName);
            }
        }
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            formatRechargeCount();
            changeRechargeBtnStatus();
        }
    };

    private void changeRechargeBtnStatus() {
        boolean rechargeBtnEnable = checkRechargeBtnEnable();
        if (mRecharge.isEnabled() != rechargeBtnEnable) {
            mRecharge.setEnabled(rechargeBtnEnable);
        }
    }

    private void formatRechargeCount() {
        String oldMoney = mRechargeCount.getText().toString().trim();
        String formatRechargeMoney = StrFormatter.getFormatMoney(oldMoney);
        if (!oldMoney.equalsIgnoreCase(formatRechargeMoney)) {
            mRechargeCount.setText(formatRechargeMoney);
            mRechargeCount.setSelection(formatRechargeMoney.length());
        }
    }

    private boolean checkRechargeBtnEnable() {
        String count = mRechargeCount.getText().toString();
        if (count.startsWith(".")) return false;
        if (mUsablePlatform != null && mUsablePlatform.isBankPay() && mBankLimit != null) {
            return !TextUtils.isEmpty(count)
                    && Double.parseDouble(count) >= 5
                    && mBankLimit.getLimitSingle() >= Double.parseDouble(count);
        } else {
            return !TextUtils.isEmpty(count)
                    && Double.parseDouble(count) >= 5;
        }
    }

    @OnClick({R.id.rechargeWay, rechargeCount, R.id.recharge, R.id.connect_service, R.id.rechargeLL})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rechargeWay:
                if (mPayData != null && mPayData.length > 0) {
                    showRechargePicker();
                } else {
                    requestUsablePlatformList();
                }
                break;
            case rechargeCount:
                break;
            case R.id.recharge:
                umengEventCount(UmengCountEventIdUtils.RECHARGE_NEXT_STEP);
                submitRechargeData();
                break;
            case R.id.connect_service:
                umengEventCount(UmengCountEventIdUtils.RECHARGE_CONTACT_CUSTOMER_SERVICE);
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
        if (mUserBankCardInfoModel != null && mUsablePlatform.isBankPay() && !mUserBankCardInfoModel.isNotConfirmBankInfo()) {
            bankId = mUserBankCardInfoModel.getId();
        }

        if (mUsablePlatform.isBankPay()) {
            if (mUserBankCardInfoModel == null || mUserBankCardInfoModel.isNotConfirmBankInfo()) {
                Launcher.with(getActivity(), BindBankCardActivity.class)
                        .putExtra(Launcher.EX_PAY_END, mUserBankCardInfoModel)
                        .executeForResult(BindBankCardActivity.REQ_CODE_BIND_CARD);
            } else {
                if (mUsablePlatform.isBankPay() && mBankLimit != null && mBankLimit.getLimitSingle() < Double.parseDouble(money)) {
                    ToastUtil.show(R.string.input_money_more_than_limit);
                    return;
                }
                confirmRecharge(money, bankId);
            }
        } else {
            confirmRecharge(money, null);
        }
    }

    private void confirmRecharge(final String money, Integer bankId) {
        if (mUsablePlatform.isBankPay()) {
            Launcher.with(getActivity(), BankCardPayActivity.class)
                    .putExtra(Launcher.EX_PAYLOAD, money)
                    .putExtra(Launcher.EX_PAY_END, mUserBankCardInfoModel)
                    .putExtra(Launcher.EX_PAYLOAD_1, mUsablePlatform)
                    .execute();
            finish();
        } else {
            Client.submitRechargeData(mUsablePlatform.getPlatform(), money, bankId)
                    .setIndeterminate(this)
                    .setRetryPolicy(new DefaultRetryPolicy(20000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                    .setCallback(new Callback2D<Resp<PaymentPath>, PaymentPath>() {
                        @Override
                        protected void onRespSuccessData(PaymentPath data) {
                            handelRechargeSubmitSuccess(data, money);
                            finish();
                        }
                    })
                    .fire();
        }
    }

    private void handelRechargeSubmitSuccess(PaymentPath data, String money) {
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
    }

    private void showRechargePicker() {
        OptionPicker picker = new OptionPicker(this, mPayData);
        picker.setCancelTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
        picker.setSubmitTextColor(ContextCompat.getColor(getActivity(), R.color.warningText));
        picker.setTopBackgroundColor(ContextCompat.getColor(getActivity(), R.color.background));
        picker.setPressedTextColor(ContextCompat.getColor(getActivity(), R.color.picker_press));
        picker.setTopHeight(50);
        picker.setAnimationStyle(R.style.BottomDialogAnimation);
        picker.setOffset(2);
        if (!TextUtils.isEmpty(mSelectPayWayName)) {
            picker.setSelectedItem(mSelectPayWayName);
        }
        picker.setTextColor(ContextCompat.getColor(getActivity(), R.color.primaryText));
        WheelView.LineConfig lineConfig = new WheelView.LineConfig(0);//使用最长的分割线
        lineConfig.setColor(ContextCompat.getColor(getActivity(), R.color.split));
        picker.setLineConfig(lineConfig);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                if (!TextUtils.isEmpty(item)) {
                    changeChooseBankInfo(item);
                }
            }
        });
        picker.show();
    }

    private void changeChooseBankInfo(String item) {
        changeRechargeBtnStatus();
        mRechargeWay.setText(item);
        mSelectPayWayName = item;
        for (UsablePlatform data : mUsablePlatformList) {
            if (data.getName().equalsIgnoreCase(item)) {
                Preference.get().setRechargeWay(LocalUser.getUser().getPhone(), data.getPlatform());
                mUsablePlatform = data;
                break;
            } else if (item.contains("银行") && data.getName().contains("银行")) {
                Preference.get().setRechargeWay(LocalUser.getUser().getPhone(), data.getPlatform());
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
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case BindBankCardActivity.REQ_CODE_BIND_CARD:
                    Client.requestUserBankCardInfo()
                            .setTag(TAG)
                            .setIndeterminate(this)
                            .setCallback(new Callback<Resp<List<UserBankCardInfoModel>>>() {
                                @Override
                                protected void onRespSuccess(Resp<List<UserBankCardInfoModel>> resp) {
                                    if (resp.isSuccess()) {
                                        formatBankPay();
                                        requestUsablePlatformList();
                                        if (resp.hasData()) {
                                            mUserBankCardInfoModel = resp.getData().get(0);
                                            submitRechargeData();
                                        }
                                    } else {
                                        ToastUtil.show(resp.getMsg());
                                    }
                                }
                            })
                            .fire();
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
