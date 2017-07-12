package com.sbai.finance.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;
import com.sbai.finance.model.payment.AliPayResult;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created by ${wangJie} on 2017/7/11.
 */

public class AliPayUtils {
    private static final String TAG = "AliPayUtils";

    private static final int ALI_PAY_FLAG = 55000;

    private FragmentActivity mFragmentActivity;

    public AliPayUtils(FragmentActivity activity) {
        WeakReference<FragmentActivity> fragmentActivityWeakReference = new WeakReference<>(activity);
        mFragmentActivity = fragmentActivityWeakReference.get();
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
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        ToastUtil.show("支付成功");
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        ToastUtil.show("支付失败");
                    }
                    mFragmentActivity.finish();
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
                Log.i("msp", result.toString());
                Message message = mHandler.obtainMessage();
                message.what = ALI_PAY_FLAG;
                message.obj = result;
                message.sendToTarget();
            }
        }).start();
    }
}
