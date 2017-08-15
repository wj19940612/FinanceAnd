package com.sbai.finance.utils;

import java.text.NumberFormat;

/**
 * Created by ${wangJie} on 2017/8/6.
 */

public class NumberFormatUtils {

    public static String formatPercentString(double number) {
        return formatPercentString(number, 0);
    }

    public static String formatPercentString(double number, int minimumFractionDigits) {
        NumberFormat percentInstance = NumberFormat.getPercentInstance();
        percentInstance.setMaximumFractionDigits(1);
        percentInstance.setMinimumFractionDigits(minimumFractionDigits);
        return percentInstance.format(number);
    }
}
