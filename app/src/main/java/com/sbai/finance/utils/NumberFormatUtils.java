package com.sbai.finance.utils;

import java.text.NumberFormat;

/**
 * Created by ${wangJie} on 2017/8/6.
 */

public class NumberFormatUtils {

    public static String formatPercentString(double number) {
        NumberFormat percentInstance = NumberFormat.getPercentInstance();
        percentInstance.setMaximumFractionDigits(1);
        percentInstance.setMinimumFractionDigits(1);
        return percentInstance.format(number);
    }
}
