package com.sbai.finance.utils;

import java.text.NumberFormat;

/**
 * Created by ${wangJie} on 2017/8/6.
 */

public class NumberFormatUtils {

    public static String formatPercentString(double number) {
        return formatPercentString(number, 1);
    }

    public static String formatPercentString(double number, int minimumFractionDigits) {
        NumberFormat percentInstance = NumberFormat.getPercentInstance();
        percentInstance.setMaximumFractionDigits(1);
        percentInstance.setMinimumFractionDigits(minimumFractionDigits);
        return percentInstance.format(number);
    }


    public static String formatPercentStringEndReplaceZero(double number, int minimumFractionDigits) {
        NumberFormat percentInstance = NumberFormat.getPercentInstance();
        percentInstance.setMaximumFractionDigits(1);
        percentInstance.setMinimumFractionDigits(minimumFractionDigits);
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
