package com.sbai.finance.utils.audio;

/**
 * Created by ${wangJie} on 2018/1/4.
 * 商品是否免费code
 */

public interface ProductFreeStatusCode {
    int PRODUCT_RATE_FREE = 0; //免费
    int PRODUCT_RATE_CHARGE = 1; //收费

    int PRODUCT_RECHARGE_STATUS_NOT_PAY = 0;
    int PRODUCT_RECHARGE_STATUS_ALREADY_PAY = 1;
}
