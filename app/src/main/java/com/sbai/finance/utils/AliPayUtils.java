package com.sbai.finance.utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.sbai.finance.activity.mutual.BorrowDetailsActivity;
import com.sbai.finance.model.payment.AliPayResult;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created by ${wangJie} on 2017/7/11.
 */

public class AliPayUtils {
    private static final String TAG = "AliPayUtils";

    private static final int ALI_PAY_FLAG = 55000;

    private FragmentActivity mFragmentActivity;
    private boolean mIsIntentionMoney;

    public AliPayUtils(FragmentActivity activity, boolean isIntentionMoney) {
        WeakReference<FragmentActivity> fragmentActivityWeakReference = new WeakReference<>(activity);
        mFragmentActivity = fragmentActivityWeakReference.get();
        mIsIntentionMoney = isIntentionMoney;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ALI_PAY_FLAG:
                    @SuppressWarnings("unchecked")
                    AliPayResult aliPayResult = new AliPayResult((Map<String, String>) msg.obj);
                    /**
                     *对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = aliPayResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = aliPayResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
//                    if (TextUtils.equals(resultStatus, "9000")) {
//                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                        ToastUtil.show("支付成功");
//                    } else {
//                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                        ToastUtil.show("支付失败");
//                    }
                    Log.d(TAG, "客户端 aliPayResult: " + aliPayResult.toString());
                    String toJson = new Gson().toJson(aliPayResult);
                    Log.d(TAG, "handleMessage: " + toJson);

                    Client.requestAliPayWebSign(toJson)
                            .setTag(TAG)
                            .setCallback(new com.sbai.finance.net.Callback<Resp<Object>>() {
                                @Override
                                protected void onRespSuccess(Resp<Object> resp) {
                                    Log.d(TAG, "onRespSuccess: " + resp.toString());
                                    if (resp.isSuccess()) {
                                        ToastUtil.show(resp.getMsg());
                                        if (mIsIntentionMoney) {
                                            Intent intent = new Intent(mFragmentActivity, BorrowDetailsActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            mFragmentActivity.startActivity(intent);
                                        }
                                        mFragmentActivity.finish();
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
    };

    public void aliPay(final String orderInfo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PayTask payTask = new PayTask(mFragmentActivity);
                Map<String, String> result = payTask.payV2(orderInfo, true);
//                String result2 = payTask.pay(orderInfo, true);
//                Log.d(TAG, "run: " + "  \n" + result2);
                Message message = mHandler.obtainMessage();
                message.what = ALI_PAY_FLAG;
                message.obj = result;
                message.sendToTarget();
            }
        }).start();
    }
}
