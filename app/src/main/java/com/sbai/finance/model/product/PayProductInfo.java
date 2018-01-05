package com.sbai.finance.model.product;

/**
 * Created by ${wangJie} on 2018/1/4.
 * 付费产品信息
 */

public interface PayProductInfo extends ProductFreeStatusCode {

    int getProductType();

    double getPrice();

    int getProductId();
}
