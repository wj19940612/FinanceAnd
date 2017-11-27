package com.sbai.finance.utils;

import android.text.TextUtils;

/**
 * Modified by john on 23/11/2017
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class StockUtil {

    public final static String NULL_VALUE = "--";

    /**
     * 一般小数数据
     *
     * @param price
     * @return
     */
    public static String getStockDecimal(String price) {
        return getStockDecimal(price, 2);
    }

    /**
     * 一般小数数据, 带 scale
     *
     * @param price
     * @param scale
     * @return
     */
    public static String getStockDecimal(String price, int scale) {
        if (TextUtils.isEmpty(price)) {
            return NULL_VALUE;
        }
        return FinanceUtil.formatWithScale(price, scale);
    }

    /**
     * 量数据
     *
     * @param volume
     * @return
     */
    public static String getStockVolume(String volume) {
        if (TextUtils.isEmpty(volume)) {
            return NULL_VALUE;
        }
        try {
            return FinanceUtil.unitize(Double.parseDouble(volume));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return NULL_VALUE;
        }
    }

    /**
     * 金额数据
     *
     * @param amount
     * @return
     */
    public static String getStockAmount(String amount) {
        if (TextUtils.isEmpty(amount)) {
            return NULL_VALUE;
        }
        try {
            return FinanceUtil.unitize(Double.parseDouble(amount));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return NULL_VALUE;
        }
    }

    /**
     * 百分比数据
     *
     * @param rate
     * @return
     */
    public static String getStockPercentage(String rate) {
        if (TextUtils.isEmpty(rate)) {
            return NULL_VALUE;
        }
        try {
            return FinanceUtil.formatToPercentage(Double.parseDouble(rate));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return NULL_VALUE;
        }
    }
}
