package com.sbai.finance.activity.mine.fund;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.BuildConfig;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.mine.FeedbackActivity;
import com.sbai.finance.model.fund.AliPayOrderInfo;
import com.sbai.finance.model.fund.BankLimit;
import com.sbai.finance.model.fund.UsableRechargeWay;
import com.sbai.finance.model.fund.UserBankCardInfo;
import com.sbai.finance.model.fund.UserFundInfo;
import com.sbai.finance.model.mine.cornucopia.AccountFundDetail;
import com.sbai.finance.model.mutual.ArticleProtocol;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.AliPayHelper;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.KeyBoardUtils;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RechargeActivity extends BaseActivity {

    private static final int REQ_CODE_BANK_PAY = 492033;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.recharge)
    AppCompatButton mRecharge;
    @BindView(R.id.connect_service)
    AppCompatTextView mConnectService;
    @BindView(R.id.rechargeCount)
    AppCompatEditText mRechargeCount;
    @BindView(R.id.crashRecharge)
    LinearLayout mCrashRecharge;
    @BindView(R.id.split)
    View mSplit;
    @BindView(R.id.virtualProductRecycleView)
    RecyclerView mVirtualProductRecycleView;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;


    private BankLimit mBankLimit;

    protected List<UsableRechargeWay> mUsableRechargeWayList;

    protected UsableRechargeWay mUserSelectRechargeWay;
    protected UserBankCardInfo mUserBankCardInfo;
    protected int mHistorySelectPayWayPosition;
    protected RechargeWayAdapter mRechargeWayAdapter;

    protected int mRechargeType;

    protected double mUserFundCount;

    //对不是支付宝或者微信的充值方式,选择项目的时候  就算没有选择该支付方式，对应的文案也要变化
    protected UsableRechargeWay mOtherRechargeWay;
    protected int mOtherRechargeWayPosition;
    private UserFundInfo mUserFundInfo;

    public interface OnPayWayListener {
        void onPayWay(UsableRechargeWay usableRechargeWay, int position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用于切换沙箱环境与生产环境；
        //如果不使用此方法，默认使用生产环境；
        //在钱包不存在的情况下，会唤起h5支付；
        //注：在生产环境，必须将此代码注释！
//        if (BuildConfig.DEBUG | !BuildConfig.IS_PROD) {
//            EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
//        }
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);

        initData();
        initView();
        requestUsablePlatformList();
        requestUserFund();
    }

    private void initData() {
        mRechargeType = getIntent().getIntExtra(ExtraKeys.RECHARGE_TYPE, AccountFundDetail.TYPE_CRASH);
        mUserFundCount = getIntent().getDoubleExtra(ExtraKeys.USER_FUND, 0);
        //友盟统计埋点统一入口
        switch (mRechargeType) {
            case AccountFundDetail.TYPE_CRASH:
                umengEventCount(UmengCountEventId.WALLET_RECHARGE);
                mTitleBar.setRightVisible(false);
                break;
            case AccountFundDetail.TYPE_INGOT:
                mTitleBar.setRightVisible(true);
                break;
            default:
                mTitleBar.setRightVisible(true);
                break;
        }
    }


    private void initView() {
        mRechargeCount.addTextChangedListener(mValidationWatcher);
        mRechargeWayAdapter = new RechargeWayAdapter(new ArrayList<UsableRechargeWay>());
        mRecyclerView.setAdapter(mRechargeWayAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRechargeWayAdapter.setOnPayWayListener(new OnPayWayListener() {
            @Override
            public void onPayWay(UsableRechargeWay usableRechargeWay, int position) {
                if (usableRechargeWay != null) {
                    if (position == mHistorySelectPayWayPosition) return;
                    updateSelectPayWay(usableRechargeWay, position);
                }
            }
        });
        if (mRechargeType != AccountFundDetail.TYPE_CRASH) {
            mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    umengEventCount(UmengCountEventId.WALLET_EXCHANGE_RULES);
                    openExchangeRulePage();
                }
            });
        } else {
            mRechargeCount.postDelayed(new Runnable() {
                @Override
                public void run() {
                    KeyBoardUtils.openKeyBoard(mRechargeCount);
                }
            }, 500);
        }
    }

    private void openExchangeRulePage() {
        Client.getArticleProtocol(ArticleProtocol.PROTOCOL_EXCHANGE)
                .setTag(TAG)
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

    private void requestUsablePlatformList() {
        Client.getUsablePlatform(mRechargeType).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<UsableRechargeWay>>, List<UsableRechargeWay>>() {
                    @Override
                    protected void onRespSuccessData(List<UsableRechargeWay> usableRechargeWayList) {
                        if (usableRechargeWayList == null || usableRechargeWayList.isEmpty())
                            return;
                        mUsableRechargeWayList = usableRechargeWayList;
                        mRechargeWayAdapter.addAll(mUsableRechargeWayList);
                        handleUserPayPlatform(usableRechargeWayList);
                        if (mRechargeType == AccountFundDetail.TYPE_CRASH) {
                            requestUserBankInfo();
                        }
                    }
                }).fire();
    }


    private void updateSelectPayWay(UsableRechargeWay usableRechargeWay, int position) {
        if (mRechargeType == AccountFundDetail.TYPE_CRASH) {
            UsableRechargeWay oldUsableRechargeWay = mUsableRechargeWayList.get(mHistorySelectPayWayPosition);
            oldUsableRechargeWay.setSelectPayWay(false);
            mRechargeWayAdapter.notifyItemChanged(mHistorySelectPayWayPosition, oldUsableRechargeWay);

            usableRechargeWay.setSelectPayWay(true);
            mRechargeWayAdapter.notifyItemChanged(position, position);
            mHistorySelectPayWayPosition = position;
            mUserSelectRechargeWay = usableRechargeWay;
            changeRechargeBtnStatus();
        } else {
            UsableRechargeWay oldUsableRechargeWay = mUsableRechargeWayList.get(mHistorySelectPayWayPosition);
            updateVirtualProductSelect(usableRechargeWay, position, oldUsableRechargeWay, mHistorySelectPayWayPosition);
        }
    }

    /**
     * @param nowSelectRechargeWay           现在选中的充值方式
     * @param nowSelectPosition              当前选中的方式在数据中的索引
     * @param historySelectUsableRechargeWay
     * @param historySelectPosition
     */
    protected void updateVirtualProductSelect(UsableRechargeWay nowSelectRechargeWay, int nowSelectPosition, UsableRechargeWay historySelectUsableRechargeWay, int historySelectPosition) {
    }

    private void requestUserBankInfo() {
        Client.requestUserBankCardInfo()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<UserBankCardInfo>>, List<UserBankCardInfo>>() {
                    @Override
                    protected void onRespSuccessData(List<UserBankCardInfo> data) {
                        if (data != null && !data.isEmpty()) {
                            mUserBankCardInfo = data.get(0);
                            requestBankLimit();
                        }
                    }
                })
                .fire();
    }

    private void requestBankLimit() {
        if (mUserBankCardInfo != null) {
            Client.getBankLimit(mUserBankCardInfo.getBankId())
                    .setTag(TAG)
                    .setCallback(new Callback2D<Resp<List<BankLimit>>, List<BankLimit>>() {
                        @Override
                        protected void onRespSuccessData(List<BankLimit> data) {
                            if (data != null && !data.isEmpty()) {
                                formatBankCardPay(data);
                            }
                        }
                    })
                    .fire();

        }
    }

    private void formatBankCardPay(List<BankLimit> data) {
        mBankLimit = data.get(0);
        String bankPay = formatBankPay();
        if (mUserSelectRechargeWay != null && !TextUtils.isEmpty(bankPay)) {
            SpannableString payBank = StrUtil.mergeTextWithRatioColor(bankPay,
                    "\n" + getString(R.string.bank_card_recharge_limit, mBankLimit.getLimitSingle()), 0.98f,
                    ContextCompat.getColor(RechargeActivity.this, R.color.unluckyText));
            if (mUsableRechargeWayList != null &&
                    !mUsableRechargeWayList.isEmpty()) {
                for (int i = 0; i < mUsableRechargeWayList.size(); i++) {
                    UsableRechargeWay usableRechargeWay = mUsableRechargeWayList.get(i);
                    if (usableRechargeWay.isBankPay()) {
                        usableRechargeWay.setName(payBank.toString());
                        mRechargeWayAdapter.notifyItemChanged(i, usableRechargeWay);
                        break;
                    }
                }
            }
        }
    }

    private String formatBankPay() {
        if (mUserBankCardInfo != null && !TextUtils.isEmpty(mUserBankCardInfo.getCardNumber())) {
            return mUserBankCardInfo.getIssuingBankName() + "(" +
                    mUserBankCardInfo.getCardNumber().substring(mUserBankCardInfo.getCardNumber().length() - 4) + ")";
        }
        return "";
    }


    private void handleUserPayPlatform(List<UsableRechargeWay> usableRechargeWayList) {
        if (usableRechargeWayList != null && !usableRechargeWayList.isEmpty()) {
            if (mOtherRechargeWay == null || !mOtherRechargeWay.isSelectPayWay()) {
                mUserSelectRechargeWay = usableRechargeWayList.get(0);
                mUserSelectRechargeWay.setSelectPayWay(true);
                mRechargeWayAdapter.notifyItemChanged(0, mUserSelectRechargeWay);
            }

        }

        for (int i = 0; i < usableRechargeWayList.size(); i++) {
            UsableRechargeWay usableRechargeWay = usableRechargeWayList.get(i);
            if (usableRechargeWay.isBalancePay()) {
                String name = usableRechargeWay.getName();
                if (name.contains(":")) {
                    name = name.substring(0, name.indexOf(":"));
                }
                String balanceName = name + ": " + FinanceUtil.formatWithScale(mUserFundCount) + "元";
                usableRechargeWay.setName(balanceName);

                mRechargeWayAdapter.notifyItemChanged(i, usableRechargeWay);
                mOtherRechargeWay = usableRechargeWay;
                mOtherRechargeWayPosition = i;
                break;
            } else if (usableRechargeWay.isIngotPay()) {
                String name = usableRechargeWay.getName();
                if (name.contains(":")) {
                    name = name.substring(0, name.indexOf(":"));
                }
                String balanceName = name + ": " + FinanceUtil.formatWithScale(mUserFundCount, 0);
                usableRechargeWay.setName(balanceName);
                mRechargeWayAdapter.notifyItemChanged(i, usableRechargeWay);
                mOtherRechargeWay = usableRechargeWay;
                mOtherRechargeWayPosition = i;
                break;
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
        if (TextUtils.isEmpty(oldMoney)) {
            mRecharge.setText(R.string.confirm_money);
        } else {
            String formatRechargeMoney = StrFormatter.getFormatMoney(oldMoney);
            if (TextUtils.isEmpty(formatRechargeMoney)) {
                mRechargeCount.setText("");
                mRechargeCount.setSelection(0);
            } else if (!oldMoney.equalsIgnoreCase(formatRechargeMoney)) {
                mRechargeCount.setText(formatRechargeMoney);
                mRechargeCount.setSelection(formatRechargeMoney.length());
            }
            if (!TextUtils.isEmpty(formatRechargeMoney)) {
                mRecharge.setText(getString(R.string.confirm_payment_money, FinanceUtil.formatWithScale(formatRechargeMoney)));
            }
        }
    }

    private boolean checkRechargeBtnEnable() {
        String count = mRechargeCount.getText().toString();
        double limitMoney;
        if (!BuildConfig.IS_PROD) {
            limitMoney = 0.01;
        } else {
            limitMoney = 5;
        }
        if (count.startsWith(".")) return false;
        if (mUserSelectRechargeWay != null && mUserSelectRechargeWay.isBankPay() && mBankLimit != null) {
            return !TextUtils.isEmpty(count)
                    && Double.parseDouble(count) >= limitMoney
                    && mBankLimit.getLimitSingle() >= Double.parseDouble(count);
        } else {
            return !TextUtils.isEmpty(count)
                    && Double.parseDouble(count) >= limitMoney;
        }
    }

    @OnClick({R.id.recharge, R.id.connect_service})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.recharge:
                umengEventCount(UmengCountEventId.RECHARGE_NEXT_STEP);
                submitRechargeData();
                break;
            case R.id.connect_service:
                umengEventCount(UmengCountEventId.RECHARGE_CONTACT_CUSTOMER_SERVICE);
                Launcher.with(getActivity(), FeedbackActivity.class).execute();
                break;
        }
    }

    public void requestUserFund() {
        Client.requestUserFundInfo()
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<UserFundInfo>, UserFundInfo>() {
                    @Override
                    protected void onRespSuccessData(UserFundInfo data) {
                        mUserFundInfo = data;
                        updateUserFund(mUserFundInfo);
                    }
                })
                .fireFree();
    }

    public void updateUserFund(UserFundInfo userFundInfo) {
        if (userFundInfo == null) return;
        switch (mRechargeType) {
            case AccountFundDetail.TYPE_INGOT:
                mUserFundCount = userFundInfo.getMoney();
                break;
            case AccountFundDetail.TYPE_SCORE:
                mUserFundCount = userFundInfo.getYuanbao();
                break;
        }
        handleUserPayPlatform(mUsableRechargeWayList);
    }

    private void submitRechargeData() {
        if (mUserSelectRechargeWay == null) return;
        final String money = mRechargeCount.getText().toString();
        Integer bankId = null;
        if (mUserBankCardInfo != null && mUserSelectRechargeWay.isBankPay() && !mUserBankCardInfo.isNotConfirmBankInfo()) {
            bankId = mUserBankCardInfo.getId();
        }

        if (mUserSelectRechargeWay.isBankPay()) {
            if (mUserBankCardInfo == null || mUserBankCardInfo.isNotConfirmBankInfo()) {
                Launcher.with(getActivity(), BindBankCardActivity.class)
                        .putExtra(Launcher.EX_PAY_END, mUserBankCardInfo)
                        .executeForResult(BindBankCardActivity.REQ_CODE_BIND_CARD);
            } else {
                if (mUserSelectRechargeWay.isBankPay() && mBankLimit != null && mBankLimit.getLimitSingle() < Double.parseDouble(money)) {
                    ToastUtil.show(R.string.input_money_more_than_limit);
                    return;
                }
                confirmRecharge(money, bankId);
            }
        } else {
            confirmRecharge(money, null);
        }
        KeyBoardUtils.closeKeyboard(mRechargeCount);
    }

    protected void confirmRecharge(final String money, Integer bankId) {
        if (mUserSelectRechargeWay.isBankPay()) {
            Launcher.with(getActivity(), BankCardPayActivity.class)
                    .putExtra(Launcher.EX_PAYLOAD, money)
                    .putExtra(Launcher.EX_PAY_END, mUserBankCardInfo)
                    .putExtra(Launcher.EX_PAYLOAD_1, mUserSelectRechargeWay)
                    .executeForResult(REQ_CODE_BANK_PAY);
        } else if (mUserSelectRechargeWay.isAliPay()) {
            requestAliPayProductInfo(money);
        } else if (mUserSelectRechargeWay.isWeChatPay()) {
            // weChat pay
        } else {
            //            Client.submitRechargeData(mUserSelectRechargeWay.getPlatform(), money, bankId)
//                    .setIndeterminate(this)
//                    .setRetryPolicy(new DefaultRetryPolicy(20000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
//                    .setCallback(new Callback2D<Resp<PaymentPath>, PaymentPath>() {
//                        @Override
//                        protected void onRespSuccessData(PaymentPath data) {
//                            finish();
//                        }
//                    })
//                    .fire();
            confirmOtherPay(mUserSelectRechargeWay);
        }
    }

    //其他的一些支付
    protected void confirmOtherPay(UsableRechargeWay userSelectRechargeWay) {

    }

    protected void requestAliPayProductInfo(String money) {
        Client.requestAliPayOrderInfo(money, AliPayHelper.PAY_DEFAULT, -1)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<AliPayOrderInfo>, AliPayOrderInfo>() {
                    @Override
                    protected void onRespSuccessData(AliPayOrderInfo data) {
                        new AliPayHelper(RechargeActivity.this).aliPay(data.getOrderString());
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
                            .setCallback(new Callback<Resp<List<UserBankCardInfo>>>() {
                                @Override
                                protected void onRespSuccess(Resp<List<UserBankCardInfo>> resp) {
                                    if (resp.isSuccess()) {
                                        formatBankPay();
                                        requestUsablePlatformList();
                                        if (resp.hasData()) {
                                            mUserBankCardInfo = resp.getData().get(0);
                                            submitRechargeData();
                                        }
                                    } else {
                                        ToastUtil.show(resp.getMsg());
                                    }
                                }
                            })
                            .fire();
                    break;
                case REQ_CODE_BANK_PAY:
                    setResult(RESULT_OK);
                    finish();
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

        private ArrayList<UsableRechargeWay> mUsableRechargeWayArrayList;
        private OnPayWayListener mOnPayWayListener;

        public RechargeWayAdapter(ArrayList<UsableRechargeWay> mUsableRechargeWayList) {
            this.mUsableRechargeWayArrayList = mUsableRechargeWayList;
        }

        public void setOnPayWayListener(OnPayWayListener onPayWayListener) {
            this.mOnPayWayListener = onPayWayListener;
        }

        public void addAll(List<UsableRechargeWay> usableRechargeWayList) {
            mUsableRechargeWayArrayList.clear();
            mUsableRechargeWayArrayList.addAll(usableRechargeWayList);
            notifyItemRangeChanged(0, usableRechargeWayList.size());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recharge, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mUsableRechargeWayArrayList.get(position), position, mOnPayWayListener);
        }

        @Override
        public int getItemCount() {
            return mUsableRechargeWayArrayList != null ? mUsableRechargeWayArrayList.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.payName)
            TextView mPayName;
            @BindView(R.id.checkboxClick)
            CheckBox mCheckboxClick;
            @BindView(R.id.recharge)
            RelativeLayout mRecharge;
            @BindView(R.id.balanceNotEnough)
            TextView mBalanceNotEnough;


            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(final UsableRechargeWay item, final int position, final OnPayWayListener onPayWayListener) {
                if (item == null) return;
                mPayName.setText(item.getName());

                if (item.isIngotOrBalancePay()) {
                    if (item.isBalancePay()) {
                        mPayName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pay_balance, 0, 0, 0);
                    } else {
                        mPayName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pay_ignot, 0, 0, 0);
                    }
                    if (item.isBalanceIsEnough()) {
                        mBalanceNotEnough.setVisibility(View.GONE);
                        mCheckboxClick.setVisibility(View.VISIBLE);
                    } else {
                        mBalanceNotEnough.setVisibility(View.VISIBLE);
                        mCheckboxClick.setVisibility(View.GONE);
                    }

                } else {
                    if (item.isAliPay()) {
                        mPayName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ali_pay, 0, 0, 0);
                    } else if (item.isWeChatPay()) {
                        mPayName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_we_chat_pay, 0, 0, 0);
                    } else {
                        mPayName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bank_pay, 0, 0, 0);
                    }
                }

                if (item.isSelectPayWay()) {
                    mCheckboxClick.setChecked(true);
                } else {
                    mCheckboxClick.setChecked(false);
                }

                if (item.isBalanceIsEnough()) {
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

}
