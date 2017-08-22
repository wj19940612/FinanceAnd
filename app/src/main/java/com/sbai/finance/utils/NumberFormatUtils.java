package com.sbai.finance.utils;

import java.text.NumberFormat;

/**
 * Created by ${wangJie} on 2017/8/6.
 * 格式化数字
 */

public class NumberFormatUtils {

    //已百分数形式显示数字
    public static String formatPercentString(double number) {
        return formatPercentString(number, 0);
    }

    /**
     * @param number
     * @param maximumFractionDigits 小数点后保留几位
     * @return
     */
    public static String formatPercentString(double number, int maximumFractionDigits) {
        NumberFormat percentInstance = NumberFormat.getPercentInstance();
        percentInstance.setMaximumFractionDigits(maximumFractionDigits);
        percentInstance.setMinimumFractionDigits(0);
        return percentInstance.format(number);
    }


    public static String formatPercentStringEndReplaceZero(double number, int maximumFractionDigits) {
        NumberFormat percentInstance = NumberFormat.getPercentInstance();
        percentInstance.setMaximumFractionDigits(1);
        percentInstance.setMinimumFractionDigits(maximumFractionDigits);
        return replaceZero(percentInstance.format(number));
    }

    //20.55%  去除50.00%中的00
    public static String replaceZero(String data) {
        if (data.contains(".00")) {
            return data.replace(".00", "");
        }
        return data;
    }
}
