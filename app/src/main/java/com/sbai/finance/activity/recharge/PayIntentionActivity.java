package com.sbai.finance.activity.recharge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.payment.AliPayOrderInfo;
import com.sbai.finance.model.payment.PaymentPath;
import com.sbai.finance.model.payment.UsablePlatform;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.AliPayUtils;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.MyListView;
import com.sbai.finance.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
import static com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES;

public class PayIntentionActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.intentionAmount)
    TextView mIntentionAmount;
    @BindView(R.id.scrollView)
    ScrollView mScrollView;
    @BindView(android.R.id.list)
    MyListView mListView;
    @BindView(R.id.confirmPayment)
    TextView mConfirmPayment;

    private int mDataId;
    private int mType;
    private String mPlatform;
    private String mPaymentPath;
    private List<UsablePlatform> mUsablePlatformList;
    private PayIntentionAdapter mPayIntentionAdapter;

    private double mIntentionMoney = 0.01;


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
        setContentView(R.layout.activity_pay_intention);
        ButterKnife.bind(this);

        initData(getIntent());

        mPayIntentionAdapter = new PayIntentionAdapter(this);
        mPayIntentionAdapter.setOnPayWayListener(new PayIntentionAdapter.OnPayWayListener() {
            @Override
            public void onPayWay(String platform, int type) {
                mPlatform = platform;
                mType = type;
            }
        });
        mUsablePlatformList = new ArrayList<>();
        mListView.setAdapter(mPayIntentionAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mConfirmPayment.setEnabled(true);
                mPayIntentionAdapter.setChecked(position);
                mPayIntentionAdapter.notifyDataSetChanged();
            }
        });

        requestIntentionAmount();
        requestUsablePlatformList();
        mTitleBar.setOnTitleBarClickListener(this);
        mScrollView.smoothScrollTo(0, 0);
    }

    @Override
    public void onClick(View v) {
        mScrollView.smoothScrollTo(0, 0);
    }

    private void initData(Intent intent) {
        mDataId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
    }

    private void requestIntentionAmount() {
        Client.getIntentionAmount().setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<Double>, Double>() {
                    @Override
                    protected void onRespSuccessData(Double amount) {
                        mIntentionMoney = amount;
                        updateIntentionAmount(amount);
                    }
                }).fire();
    }

    private void updateIntentionAmount(Double amount) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder();
        spannableString.append("￥");
        spannableString.append(String.valueOf(amount));
        AbsoluteSizeSpan span = new AbsoluteSizeSpan((int) Display.sp2Px(15, getResources()));
        spannableString.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mIntentionAmount.setText(spannableString);
    }

    private void requestUsablePlatformList() {
        Client.getUsablePlatform().setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<UsablePlatform>>, List<UsablePlatform>>() {
                    @Override
                    protected void onRespSuccessData(List<UsablePlatform> usablePlatformList) {
                        for (UsablePlatform usablePlatform : usablePlatformList) {
                            if (usablePlatform.getStatus() == 1 && !usablePlatform.isBankPay()) {
                                mUsablePlatformList.add(usablePlatform);
                            }
                        }
                        updateUsablePlatformList();
                    }
                }).fire();
    }

    private void updateUsablePlatformList() {
        mPayIntentionAdapter.clear();
        mPayIntentionAdapter.addAll(mUsablePlatformList);
        mPayIntentionAdapter.notifyDataSetChanged();
    }

    static class PayIntentionAdapter extends ArrayAdapter<UsablePlatform> {

        private Context mContext;
        private int mChecked = -1;
        OnPayWayListener mOnPayWayListener;

        public interface OnPayWayListener {
            void onPayWay(String platform, int type);
        }

        public void setOnPayWayListener(OnPayWayListener onPayWayListener) {
            mOnPayWayListener = onPayWayListener;
        }

        private PayIntentionAdapter(Context context) {
            super(context, 0);
            this.mContext = context;
        }

        private void setChecked(int checked) {
            this.mChecked = checked;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_pay_intention, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindingData(mContext, getItem(position), mChecked, position, mOnPayWayListener);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.checkboxClick)
            ImageView mCheckboxClick;
            @BindView(R.id.payName)
            TextView mPayName;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindingData(Context context, UsablePlatform item, int checked, int position, OnPayWayListener onPayWayListener) {
                if (item == null) return;
                mPayName.setText(item.getName());

                if (item.getType() == 1) {
                    mPayName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_alipay, 0, 0, 0);
                } else {
                    mPayName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_we_chat_pay, 0, 0, 0);
                }

                if (checked == position) {
                    mCheckboxClick.setImageResource(R.drawable.ic_pay_way_checkbox_checked);
                    if (onPayWayListener != null) {
                        onPayWayListener.onPayWay(item.getPlatform(), item.getType());
                    }
                } else {
                    mCheckboxClick.setImageResource(R.drawable.ic_pay_way_checkbox_unchecked);
                }
            }
        }
    }

    @OnClick(R.id.confirmPayment)
    public void onViewClicked() {
        if (mType == 1) {
            requestAliPaySign();
            return;
        }
        Log.d(TAG, "onViewClicked: " + mDataId);
        Client.getPaymentPath(mDataId, mPlatform).setTag(TAG).setIndeterminate(this)
                .setRetryPolicy(new DefaultRetryPolicy(10000, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT))
                .setCallback(new Callback2D<Resp<PaymentPath>, PaymentPath>() {
                    @Override
                    protected void onRespSuccessData(PaymentPath paymentPath) {
                        mPaymentPath = paymentPath.getCodeUrl();
                        if (mType == 2) {
                            Launcher.with(getActivity(), WeChatPayActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, mPaymentPath)
                                    .putExtra(Launcher.EX_PAYLOAD_1, mDataId)
                                    .putExtra(Launcher.EX_PAYLOAD_2, mPlatform)
                                    .execute();
                        }
                    }
                }).fire();
    }

    private void requestAliPaySign() {
        Client.requestAliPayOrderInfo(String.valueOf(mIntentionMoney), mDataId)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<AliPayOrderInfo>, AliPayOrderInfo>() {
                    @Override
                    protected void onRespSuccessData(AliPayOrderInfo data) {
                        Log.d(TAG, "onRespSuccessData: " + data.getOrderString());
                        new AliPayUtils(PayIntentionActivity.this, true).aliPay(data.getOrderString());
                    }
                })
                .setTag(TAG)
                .fire();
    }
}
