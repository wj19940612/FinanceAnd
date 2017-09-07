package com.sbai.finance.activity.mine.fund;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.sbai.finance.BuildConfig;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.FeedbackActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.fund.AliPayOrderInfo;
import com.sbai.finance.model.fund.BankLimit;
import com.sbai.finance.model.fund.PaymentPath;
import com.sbai.finance.model.fund.UsablePlatform;
import com.sbai.finance.model.fund.UserBankCardInfo;
import com.sbai.finance.model.mine.cornucopia.AccountFundDetail;
import com.sbai.finance.model.mine.cornucopia.VirtualProductModel;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.AliPayUtils;
import com.sbai.finance.utils.KeyBoardUtils;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.OnItemClickListener;
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
    @BindView(R.id.crashRecharge)
    LinearLayout mCrashRecharge;
    @BindView(R.id.virtualProductRecycleView)
    RecyclerView mVirtualProductRecycleView;

    private List<UsablePlatform> mUsablePlatformList;
    private UsablePlatform mUsablePlatform;
    private UserBankCardInfo mUserBankCardInfo;
    private BankLimit mBankLimit;

    private int mSelectPayWay;
    private RechargeWayAdapter mRechargeWayAdapter;
    private int mRechargeType;
    private VirtualProductAdapter mVirtualProductAdapter;


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
//        if (BuildConfig.DEBUG | !BuildConfig.IS_PROD) {
//            EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
//        }
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);
        initListView();
        mRechargeType = getIntent().getIntExtra(ExtraKeys.RECHARGE_TYPE, AccountFundDetail.TYPE_CRASH);
        switch (mRechargeType) {
            case AccountFundDetail.TYPE_CRASH:
                initCrashRecharge();
                break;
            default:
                initVirtualProduct();
                break;
        }
    }

    private void initVirtualProduct() {
        mCrashRecharge.setVisibility(View.GONE);
        mVirtualProductRecycleView.setVisibility(View.VISIBLE);
        mVirtualProductAdapter = new VirtualProductAdapter(getActivity(), new ArrayList<VirtualProductModel>());
        mVirtualProductRecycleView.setAdapter(mVirtualProductAdapter);
        mVirtualProductAdapter.setOnItemClickListener(new OnItemClickListener<VirtualProductModel>() {
            @Override
            public void onItemClick(VirtualProductModel virtualProductModel, int position) {
                virtualProductModel.setSelect(true);
                mVirtualProductAdapter.notifyItemChanged(position, virtualProductModel);
            }
        });
        requestVirtualProductList();
    }

    private void initCrashRecharge() {
        mCrashRecharge.setVisibility(View.VISIBLE);
        mVirtualProductRecycleView.setVisibility(View.GONE);
        mRechargeCount.addTextChangedListener(mValidationWatcher);
        formatBankPay();
        requestUserBankInfo();
    }

    private void initListView() {
        mRechargeWayAdapter = new RechargeWayAdapter(new ArrayList<UsablePlatform>());
        mRecyclerView.setAdapter(mRechargeWayAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mVirtualProductRecycleView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
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

    private void requestVirtualProductList() {
        Client.getExchangeProduct()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<VirtualProductModel>>, List<VirtualProductModel>>() {
                    @Override
                    protected void onRespSuccessData(List<VirtualProductModel> data) {
                        mVirtualProductAdapter.addAll(data);
                    }

                })
                .fireFree();
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
        if (mUserBankCardInfo != null && !TextUtils.isEmpty(mUserBankCardInfo.getCardNumber())) {
            return mUserBankCardInfo.getIssuingBankName() + "(" + mUserBankCardInfo.getCardNumber().substring(mUserBankCardInfo.getCardNumber().length() - 4) + ")";
        }
        return "";
    }

    private void requestUserBankInfo() {
        Client.requestUserBankCardInfo()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<UserBankCardInfo>>, List<UserBankCardInfo>>() {
                    @Override
                    protected void onRespSuccessData(List<UserBankCardInfo> data) {
                        if (data != null && !data.isEmpty()) {
                            mUserBankCardInfo = data.get(0);
                            requestUsablePlatformList();
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
        double limitMoney;
        if (BuildConfig.DEBUG) {
            limitMoney = 0.01;
        } else {
            limitMoney = 5;
        }
        if (count.startsWith(".")) return false;
        if (mUsablePlatform != null && mUsablePlatform.isBankPay() && mBankLimit != null) {
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
        if (mUserBankCardInfo != null && mUsablePlatform.isBankPay() && !mUserBankCardInfo.isNotConfirmBankInfo()) {
            bankId = mUserBankCardInfo.getId();
        }

        if (mUsablePlatform.isBankPay()) {
            if (mUserBankCardInfo == null || mUserBankCardInfo.isNotConfirmBankInfo()) {
                Launcher.with(getActivity(), BindBankCardActivity.class)
                        .putExtra(Launcher.EX_PAY_END, mUserBankCardInfo)
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
        KeyBoardUtils.closeKeyboard(mRechargeCount);
        if (mUsablePlatform.isBankPay()) {
            Launcher.with(getActivity(), BankCardPayActivity.class)
                    .putExtra(Launcher.EX_PAYLOAD, money)
                    .putExtra(Launcher.EX_PAY_END, mUserBankCardInfo)
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
                        mPayName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ali_pay, 0, 0, 0);
                    } else if (item.isWeChatPay()) {
                        mPayName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_we_chat_pay, 0, 0, 0);
                    }
                } else {
                    mPayName.setText(item.getName());
                    mPayName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bank_pay, 0, 0, 0);
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


    static class VirtualProductAdapter extends RecyclerView.Adapter<VirtualProductAdapter.ViewHolder> {

        private Context mContext;
        private List<VirtualProductModel> mVirtualProductModelList;
        private OnItemClickListener mOnItemClickListener;

        public VirtualProductAdapter(Context context, List<VirtualProductModel> virtualProductModelList) {
            this.mContext = context;
            this.mVirtualProductModelList = virtualProductModelList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.row_virtual_product, parent, false);
            return new ViewHolder(view);
        }

        public void addAll(List<VirtualProductModel> virtualProductModelList) {
            this.mVirtualProductModelList.addAll(virtualProductModelList);
            notifyItemRangeChanged(0, mVirtualProductModelList.size());
        }

        public void setOnItemClickListener(OnItemClickListener<VirtualProductModel> onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mVirtualProductModelList.get(position), position, mOnItemClickListener);
        }

        @Override
        public int getItemCount() {
            return mVirtualProductModelList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.product)
            AppCompatTextView mProduct;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(final VirtualProductModel virtualProductModel,
                                         final int position, final OnItemClickListener onItemClickListener) {
                if (virtualProductModel == null) return;
                mProduct.setText(virtualProductModel.getToMoney() + "");
                mProduct.setSelected(virtualProductModel.isSelect());
                mProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(virtualProductModel, position);
                        }
                    }
                });
            }
        }
    }
}
