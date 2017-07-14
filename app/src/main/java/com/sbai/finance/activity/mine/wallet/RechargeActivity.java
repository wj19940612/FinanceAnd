package com.sbai.finance.activity.mine.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RechargeActivity extends BaseActivity {


    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.recharge)
    AppCompatButton mRecharge;
    @BindView(R.id.connect_service)
    AppCompatTextView mConnectService;
    @BindView(R.id.rechargeCount)
    AppCompatEditText mRechargeCount;

    private List<UsablePlatform> mUsablePlatformList;
    private UsablePlatform mUsablePlatform;
    private UserBankCardInfoModel mUserBankCardInfoModel;
    private BankLimit mBankLimit;

    private int mSelectPayWay;
    private RechargeWayAdapter mRechargeWayAdapter;


    public interface OnPayWayListener {
        void onPayWay(UsablePlatform usablePlatform, int position);
    }

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
        initListView();
        mRechargeCount.addTextChangedListener(mValidationWatcher);
        formatBankPay();
        requestUserBankInfo();
    }

    private void initListView() {
        mRechargeWayAdapter = new RechargeWayAdapter(new ArrayList<UsablePlatform>());
        mRecyclerView.setAdapter(mRechargeWayAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRechargeWayAdapter.setOnPayWayListener(new OnPayWayListener() {
            @Override
            public void onPayWay(UsablePlatform usablePlatform, int position) {
                if (usablePlatform != null) {
                    if (position == mSelectPayWay) return;
                    updateSelectPayWay(usablePlatform, position);
                }
            }

        });
    }

    private void updateSelectPayWay(UsablePlatform usablePlatform, int position) {
        Preference.get().setRechargeWay(LocalUser.getUser().getPhone(), usablePlatform.getPlatform());
        UsablePlatform oldUsablePlatform = mUsablePlatformList.get(mSelectPayWay);
        oldUsablePlatform.setSelectPayWay(false);
        mRechargeWayAdapter.notifyItemChanged(mSelectPayWay, oldUsablePlatform);

        usablePlatform.setSelectPayWay(true);
        mRechargeWayAdapter.notifyItemChanged(position, position);
        mSelectPayWay = position;
        mUsablePlatform = usablePlatform;
        changeRechargeBtnStatus();
    }

    private String formatBankPay() {
        if (mUserBankCardInfoModel != null && !TextUtils.isEmpty(mUserBankCardInfoModel.getCardNumber())) {
            return mUserBankCardInfoModel.getIssuingBankName() + "(" + mUserBankCardInfoModel.getCardNumber().substring(mUserBankCardInfoModel.getCardNumber().length() - 4) + ")";
        }
        return "";
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
                                String bankPay = formatBankPay();
                                if (mUsablePlatform != null && !TextUtils.isEmpty(bankPay)) {
                                    SpannableString payBank = StrUtil.mergeTextWithRatioColor(bankPay,
                                            "\n" + getString(R.string.bank_card_recharge_limit, mBankLimit.getLimitSingle()), 0.98f,
                                            ContextCompat.getColor(RechargeActivity.this, R.color.unluckyText));
                                    if (mUsablePlatformList != null &&
                                            !mUsablePlatformList.isEmpty()) {
                                        for (int i = 0; i < mUsablePlatformList.size(); i++) {
                                            UsablePlatform usablePlatform = mUsablePlatformList.get(i);
                                            if (usablePlatform.isBankPay()) {
                                                usablePlatform.setName(payBank.toString());
                                                mRechargeWayAdapter.notifyItemChanged(i, usablePlatform);
                                                break;
                                            }
                                        }

                                    }

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
                        mRechargeWayAdapter.addAll(mUsablePlatformList);
                        handleUserPayPlatform(usablePlatformList);
                        requestBankLimit();
                    }
                }).fire();
    }

    private void handleUserPayPlatform(List<UsablePlatform> usablePlatformList) {
        for (int i = 0; i < usablePlatformList.size(); i++) {
            UsablePlatform usablePlatform = usablePlatformList.get(i);
            if (usablePlatform.getPlatform().equalsIgnoreCase(Preference.get().getRechargeWay(LocalUser.getUser().getPhone()))) {
                mUsablePlatform = usablePlatform;
                mSelectPayWay = i;
                break;
            } else {
                mUsablePlatform = usablePlatformList.get(0);
                mSelectPayWay = 0;
            }
        }
        mUsablePlatform.setSelectPayWay(true);
        mRechargeWayAdapter.notifyItemChanged(mSelectPayWay, mUsablePlatform);
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

    @OnClick({R.id.recharge, R.id.connect_service})
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


    class RechargeWayAdapter extends RecyclerView.Adapter<RechargeWayAdapter.ViewHolder> {

        private ArrayList<UsablePlatform> mUsablePlatformArrayList;
        private OnPayWayListener mOnPayWayListener;

        public RechargeWayAdapter(ArrayList<UsablePlatform> mUsablePlatformList) {
            this.mUsablePlatformArrayList = mUsablePlatformList;
        }

        public void setOnPayWayListener(OnPayWayListener onPayWayListener) {
            this.mOnPayWayListener = onPayWayListener;
        }

        public void addAll(List<UsablePlatform> usablePlatformList) {
            mUsablePlatformArrayList.clear();
            mUsablePlatformArrayList.addAll(usablePlatformList);
            notifyItemRangeChanged(0, usablePlatformList.size());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recharge, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mUsablePlatformArrayList.get(position), position, mOnPayWayListener);
        }

        @Override
        public int getItemCount() {
            return mUsablePlatformArrayList != null ? mUsablePlatformArrayList.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.payName)
            TextView mPayName;
            @BindView(R.id.checkboxClick)
            ImageView mCheckboxClick;
            @BindView(R.id.recharge)
            RelativeLayout mRecharge;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(final UsablePlatform item, final int position, final OnPayWayListener onPayWayListener) {
                if (item == null) return;
                if (!item.isBankPay()) {
                    mPayName.setText(item.getName());
                    if (item.isAliPay()) {
                        mPayName.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_ali_pay, 0, 0, 0);
                    } else if (item.isWeChatPay()) {
                        mPayName.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_we_chat_pay, 0, 0, 0);
                    }
                } else {
                    mPayName.setText(item.getName());
                    mPayName.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_bank_pay, 0, 0, 0);
                }
                if (item.isSelectPayWay()) {
                    mCheckboxClick.setSelected(true);
                } else {
                    mCheckboxClick.setSelected(false);
                }

                mRecharge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onPayWayListener != null) {
                            onPayWayListener.onPayWay(item, position);
                        }
                    }
                });
            }
        }
    }

}
