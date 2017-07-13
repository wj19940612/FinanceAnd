package com.sbai.finance.activity.mine.wallet;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.EnvUtils;
import com.android.volley.DefaultRetryPolicy;
import com.sbai.finance.BuildConfig;
import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.FeedbackActivity;
import com.sbai.finance.activity.recharge.BankCardPayActivity;
import com.sbai.finance.activity.recharge.WeChatPayActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.payment.AliPayOrderInfo;
import com.sbai.finance.model.payment.BankLimit;
import com.sbai.finance.model.payment.PaymentPath;
import com.sbai.finance.model.payment.UsablePlatform;
import com.sbai.finance.model.payment.UserBankCardInfoModel;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.AliPayUtils;
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


public class RechargeActivity extends BaseActivity {


    @BindView(R.id.rechargeCount)
    AppCompatEditText mRechargeCount;
    @BindView(R.id.weChatPay)
    ImageView mWeChatPay;
    @BindView(R.id.weChatPayRl)
    RelativeLayout mWeChatPayRl;
    @BindView(R.id.aliPay)
    ImageView mAliPay;
    @BindView(R.id.aliPayRl)
    RelativeLayout mAliPayRl;
    @BindView(R.id.bankPay)
    ImageView mBankCardPay;
    @BindView(R.id.bankPayRl)
    RelativeLayout mBankPayRl;
    @BindView(R.id.recharge)
    AppCompatButton mRecharge;
    @BindView(R.id.connect_service)
    AppCompatTextView mConnectService;
    @BindView(R.id.bankCardInfo)
    TextView mBankCardInfo;

    private List<UsablePlatform> mUsablePlatformList;
    private UsablePlatform mUsablePlatform;
    private UserBankCardInfoModel mUserBankCardInfoModel;
    private BankLimit mBankLimit;
    //银行卡已经绑定
    private String mBankPay;

    private int mSelectPayWay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用于切换沙箱环境与生产环境；
        //如果不使用此方法，默认使用生产环境；
        //在钱包不存在的情况下，会唤起h5支付；
        //注：在生产环境，必须将此代码注释！
        if (BuildConfig.DEBUG) {
            EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        }
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);
        mRechargeCount.addTextChangedListener(mValidationWatcher);
        formatBankPay();
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
                                if (mUsablePlatform != null && !TextUtils.isEmpty(mBankPay)) {
                                    SpannableString payBank = StrUtil.mergeTextWithRatioColor(mBankPay, "\n" + getString(R.string.bank_card_recharge_limit, mBankLimit.getLimitSingle()), 0.98f,
                                            ContextCompat.getColor(RechargeActivity.this, R.color.unluckyText));
                                    mBankCardInfo.setText(payBank);
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
                        if (usablePlatformList == null || usablePlatformList.isEmpty()) return;
                        mUsablePlatformList = usablePlatformList;
                        requestBankLimit();
                        handleUserPayPlatform(usablePlatformList);
                    }
                }).fire();
    }

    private void handleUserPayPlatform(List<UsablePlatform> usablePlatformList) {
        for (int i = 0; i < usablePlatformList.size(); i++) {
            UsablePlatform usablePlatform = usablePlatformList.get(i);

            showPayView(usablePlatform);
            if (usablePlatform.getType() == Preference.get().getRechargeWay(LocalUser.getUser().getPhone())) {
                mUsablePlatform = usablePlatform;
                mSelectPayWay = mUsablePlatform.getType();
                break;
            } else {
                mUsablePlatform = usablePlatformList.get(0);
                mSelectPayWay = usablePlatformList.get(0).getType();
            }
        }
        switch (mSelectPayWay) {
            case UsablePlatform.TYPE_AIL_PAY:
                setSelectPayWay(mAliPay);
                break;
            case UsablePlatform.TYPE_WECHAT_PAY:
                setSelectPayWay(mWeChatPay);
                break;
            case UsablePlatform.TYPE_BANK_PAY:
                setSelectPayWay(mBankCardPay);
                break;
            default:
                setSelectPayWay(mAliPay);
                break;
        }
    }

    private void showPayView(UsablePlatform usablePlatform) {
        if (usablePlatform.isBankPay()) {
            mBankPayRl.setVisibility(View.VISIBLE);
        } else {
            mBankPayRl.setVisibility(View.GONE);
        }

        if (usablePlatform.getType() == UsablePlatform.TYPE_AIL_PAY) {
            mAliPayRl.setVisibility(View.VISIBLE);
        } else {
            mAliPayRl.setVisibility(View.GONE);
        }

        if (usablePlatform.getType() == UsablePlatform.TYPE_WECHAT_PAY) {
            mWeChatPayRl.setVisibility(View.VISIBLE);
        } else {
            mWeChatPayRl.setVisibility(View.GONE);
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
            // TODO: 2017/7/12 先将限额改为0.01元
            return !TextUtils.isEmpty(count)
                    && Double.parseDouble(count) >= 0.01
                    && mBankLimit.getLimitSingle() >= Double.parseDouble(count);
        } else {
            return !TextUtils.isEmpty(count)
                    && Double.parseDouble(count) >= 0.01;
        }
    }

    @OnClick({R.id.recharge, R.id.connect_service, R.id.weChatPayRl, R.id.aliPayRl, R.id.bankPayRl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.recharge:
                umengEventCount(UmengCountEventIdUtils.RECHARGE_NEXT_STEP);
                submitRechargeData();
                break;
            case R.id.connect_service:
                umengEventCount(UmengCountEventIdUtils.RECHARGE_CONTACT_CUSTOMER_SERVICE);
                Launcher.with(getActivity(), FeedbackActivity.class).execute();
                break;
            case R.id.weChatPayRl:
                setSelectPayWay(mWeChatPay);
                getSelectPayWayUsablePlatform(UsablePlatform.TYPE_WECHAT_PAY);
                break;
            case R.id.aliPayRl:
                setSelectPayWay(mAliPay);
                getSelectPayWayUsablePlatform(UsablePlatform.TYPE_AIL_PAY);
                break;
            case R.id.bankPayRl:
                setSelectPayWay(mBankCardPay);
                getSelectPayWayUsablePlatform(UsablePlatform.TYPE_BANK_PAY);
                break;
        }
    }

    private void getSelectPayWayUsablePlatform(int payType) {
        if (mUsablePlatformList != null && !mUsablePlatformList.isEmpty()) {
            for (UsablePlatform data : mUsablePlatformList) {
                if (data.getType() == payType) {
                    mUsablePlatform = data;
                    break;
                }
            }
            Preference.get().setRechargeWay(LocalUser.getUser().getPhone(), payType);
            changeRechargeBtnStatus();
        }
    }

    private void unPayWayAllNotChecked() {
        mWeChatPay.setSelected(false);
        mAliPay.setSelected(false);
        mBankCardPay.setSelected(false);
    }

    private void setSelectPayWay(ImageView image) {
        unPayWayAllNotChecked();
        image.setSelected(true);
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
        } else if (mUsablePlatform.isAliPay()) {
            requestAilPaySign(money);
        } else {
            Client.submitRechargeData(mUsablePlatform.getPlatform(), money, bankId)
                    .setIndeterminate(this)
                    .setRetryPolicy(new DefaultRetryPolicy(20000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                    .setCallback(new Callback2D<Resp<PaymentPath>, PaymentPath>() {
                        @Override
                        protected void onRespSuccessData(PaymentPath data) {
                            if (mUsablePlatform.getType() == UsablePlatform.TYPE_WECHAT_PAY) {
                                Launcher.with(getActivity(), WeChatPayActivity.class)
                                        .putExtra(Launcher.EX_PAYLOAD, data.getCodeUrl())
                                        .putExtra(Launcher.EX_PAYLOAD_2, data.getThridOrderId())
                                        .putExtra(Launcher.EX_PAYLOAD_3, true)
                                        .execute();
                            }
                            finish();
                        }
                    })
                    .fire();
        }
    }

    private void requestAilPaySign(String money) {
        Client.requestAliPayOrderInfo(money, 0)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<AliPayOrderInfo>, AliPayOrderInfo>() {
                    @Override
                    protected void onRespSuccessData(AliPayOrderInfo data) {
                        Log.d(TAG, "onRespSuccessData: " + data.getOrderString());
                        new AliPayUtils(RechargeActivity.this, false).aliPay(data.getOrderString());
                    }
                })
                .setTag(TAG)
                .fire();
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
