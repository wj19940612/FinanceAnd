package com.sbai.finance.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.sbai.finance.model.fund.AliPayResult;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created by ${wangJie} on 2017/7/11.
 */

public class AliPayHelper {
    private static final String TAG = "AliPayUtils";
    //普通充值
    public static final int PAY_DEFAULT = 0;
    //元宝充值
    public static final int PAY_INGOT = 1;


    private static final int ALI_PAY_FLAG = 55000;

    private FragmentActivity mFragmentActivity;
    private String mFailToastMsg;

    public AliPayHelper(FragmentActivity activity) {
        WeakReference<FragmentActivity> fragmentActivityWeakReference = new WeakReference<>(activity);
        mFragmentActivity = fragmentActivityWeakReference.get();
    }

    public AliPayHelper setToastFailMsg(String msg) {
        mFailToastMsg = msg;
        return this;
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
                    String toJson = new Gson().toJson(aliPayResult);
                    if (aliPayResult.isUserCancelPay()) {
                        if (!TextUtils.isEmpty(mFailToastMsg)) {
                            ToastUtil.show(mFailToastMsg);
                        }
                        return;
                    }
                    Client.requestAliPayWebSign(toJson)
                            .setTag(TAG)
                            .setCallback(new com.sbai.finance.net.Callback<Resp<Object>>() {
                                @Override
                                protected void onRespSuccess(Resp<Object> resp) {
                                    if (resp.isSuccess()) {
                                        ToastUtil.show(resp.getMsg());
                                        mFragmentActivity.setResult(Activity.RESULT_OK);
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
                Map<String, String> result = payTask.payV2(orderInfo, false);
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
