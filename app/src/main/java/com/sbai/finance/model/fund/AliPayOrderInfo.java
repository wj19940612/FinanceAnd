package com.sbai.finance.model.fund;

/**
 * Created by ${wangJie} on 2017/7/13.
 */

public class AliPayOrderInfo {

    /**
     * orderString : alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_id=2016080500169457&biz_content=%7B%22out_trade_no%22%3A%22alipay20090520170713093301%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22%E5%85%85%E5%80%BC%22%2C%22timeout_express%22%3A%2230m%22%2C%22total_amount%22%3A%225.0%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2Fvar.esongbai.xyz%2Fuser%2Fpaycallback%2Faliyungate.do&sign=okbn8x2It7SQWoooVVAj54vGSQa6AglfZtCwex7twbu2Jq7K3LhLqh5s%2FuwaRCB8oKf7qVPzpTg%2FA0%2FrANp9paOfBg8%2BHnAqpXPVPqDn28qJCIHArbbiRjzY3pkBczNr1B2c9laAmcdOizQM%2F3lOYmPW7mJsOdUZzvdt%2FA3DseRuY3pKoy%2Fz7ct%2BMEMFoOpe%2B0dtSKogbBUuN%2FYGbgpfxEkKRjjN93198lkajBpZQgQphbP4EYZ70uYhKY5hmDkEqazFaZ6jvokiXryO05Snn35TryKsJmLgzk6WtKsISa%2F%2FvCQvmzoapzjMEFeLVHKXOBYxg6%2BGQaFEK%2BclAokcnQ%3D%3D&sign_type=RSA2&timestamp=2017-07-13+09%3A33%3A01&version=1.0
     */

    private String orderString;

    public String getOrderString() {
        return orderString;
    }

    public void setOrderString(String orderString) {
        this.orderString = orderString;
    }

    @Override
    public String toString() {
        return "AliPayOrderInfo{" +
                "orderString='" + orderString + '\'' +
                '}';
    }
}
