package com.sbai.finance.activity.recharge;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.google.gson.JsonPrimitive;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mutual.BorrowDetailsActivity;
import com.sbai.finance.model.payment.PaymentPath;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
import static com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES;


public class AliPayActivity extends BaseActivity {

    @BindView(R.id.imageView)
    ImageView mImageView;
    @BindView(R.id.ContinueToPay)
    TextView mContinueToPay;
    @BindView(R.id.completePayment)
    TextView mCompletePayment;

    private String mPlatform;
    private int mDataId;
    private String mThirdOrderId;
    private String mMoney;
    private boolean mIsFromRechargePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ali_pay);
        ButterKnife.bind(this);

        initData(getIntent());
    }

    private void initData(Intent intent) {
        mPlatform = intent.getStringExtra(Launcher.EX_PAYLOAD);
        mDataId = intent.getIntExtra(Launcher.EX_PAYLOAD_1, -1);
        //充值
        mThirdOrderId = intent.getStringExtra(Launcher.EX_PAYLOAD_2);
        mMoney = intent.getStringExtra(Launcher.EX_PAYLOAD_3);
        mIsFromRechargePage = intent.getBooleanExtra(Launcher.EX_PAY_END, false);
    }

    @OnClick({R.id.ContinueToPay, R.id.completePayment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ContinueToPay:
                if (mIsFromRechargePage) {
                    Client.submitRechargeData(mPlatform, mMoney, null)
                            .setIndeterminate(this)
                            .setCallback(new Callback2D<Resp<PaymentPath>, PaymentPath>() {
                                @Override
                                protected void onRespSuccessData(PaymentPath data) {
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_VIEW);
                                    Uri content_url = Uri.parse(data.getCodeUrl());
                                    intent.setData(content_url);
                                    startActivity(intent);
                                }
                            })
                            .fire();
                } else {
                    Client.getPaymentPath(mDataId, mPlatform).setTag(TAG).setIndeterminate(this)
                            .setRetryPolicy(new DefaultRetryPolicy(10000, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT))
                            .setCallback(new Callback2D<Resp<PaymentPath>, PaymentPath>() {
                                @Override
                                protected void onRespSuccessData(PaymentPath paymentPath) {
                                    Intent intent = new Intent();
                                    intent.setAction("android.intent.action.VIEW");
                                    Uri content_url = Uri.parse(paymentPath.getCodeUrl());
                                    intent.setData(content_url);
                                    startActivity(intent);
                                }
                            }).fire();
                }
                break;
            case R.id.completePayment:
                if (mIsFromRechargePage) {
                    Client.queryConfirmPay(mThirdOrderId)
                            .setTag(TAG)
                            .setIndeterminate(this)
                            .setCallback(new Callback<Resp<Object>>() {
                                @Override
                                protected void onRespSuccess(Resp<Object> resp) {
                                    if (resp.isSuccess()) {
                                        finish();
                                    }
                                }
                            })
                            .fire();
                } else {
                    Client.paymentQuery(mDataId).setTag(TAG).setIndeterminate(this)
                            .setCallback(new Callback<Resp<JsonPrimitive>>() {
                                @Override
                                protected void onRespSuccess(Resp<JsonPrimitive> resp) {
                                    Intent intent = new Intent(getActivity(), BorrowDetailsActivity.class);
//									intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//								    intent.putExtra(Launcher.EX_PAYLOAD, mDataId);
                                    startActivity(intent);
                                }
                            }).fire();
                }

                break;
        }
    }
}
