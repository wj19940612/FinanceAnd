package com.sbai.finance.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class FinanceUtil {

    public static final String UNIT_WANG = "万";
    public static final String UNIT_YI = "亿";
    public static final String UNIT_WANG_GE = "万个";
    public static final String UNIT_GE = "个";
    private static final int DEFAULT_SCALE = 2;
    private static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_EVEN;
    public static final int TEN_THOUSAND = 10000;
    public static final int ONE_HUNDRED_MILLION = 10000_0000;

    /**
     * 格式化 double 数据, 并使用‘银行家算法’精确（保留）到小数点后两位
     *
     * @param value
     * @return
     */
    public static float accurateToFloat(double value) {
        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        return bigDecimal.setScale(2, RoundingMode.HALF_EVEN).floatValue();
    }

    /**
     * 格式化 String 数据, 并使用‘银行家算法’精确（保留）到小数点后两位
     *
     * @param value
     * @return
     */
    public static String formatWithScale(String value) {
        double v = 0;
        if (!TextUtils.isEmpty(value)) {
            try {
                v = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return formatWithScale(v, DEFAULT_SCALE);
    }

    /**
     * 格式化 double 数据成百分数格式，并使用‘银行家算法’精确（保留）到小数点后两位
     *
     * @param value
     * @return
     */
    public static String formatToPercentage(double value) {
        BigDecimal bigDecimal = multiply(value, 100d);
        return formatWithScale(bigDecimal.doubleValue(), 2) + "%";
    }

    /**
     * 格式化 double 数据成百分数格式，并使用‘银行家算法’精确（保留）到小数点后 scale 位
     *
     * @param value
     * @param scale
     * @return
     */
    public static String formatToPercentage(double value, int scale) {
        BigDecimal bigDecimal = multiply(value, 100d);
        return formatWithScale(bigDecimal.doubleValue(), scale) + "%";
    }

    /**
     * 格式化 double 数据成百分数格式，不精确（保留）到小数
     *
     * @param value
     * @param scale
     * @return
     */
    public static String formatToPercentageReplaceZero(double value, int scale) {
        BigDecimal bigDecimal = multiply(value, 100d);
        return removeNeedlessZero(formatWithScale(bigDecimal.doubleValue(), scale) + "%");
    }

    /**
     * 当数字大于 10,000 或小于 -10,000 时候，添加‘万’单位，并使用‘银行家算法’精确（保留）到小数点后两位
     *
     * @param value
     * @return 处理后的字符串
     */
    public static String addUnitWhenBeyondTenThousand(double value) {
        if (value > 10000 || value < -10000) {
            return formatWithThousandsSeparatorAndUnit(value, UNIT_WANG);
        }
        return formatWithThousandsSeparator(value);
    }

    /**
     * 当数字大于 100,000 或小于 -100,000 时候，添加‘万’单位，并使用‘银行家算法’精确（保留）到小数点后两位
     *
     * @param value
     * @return 处理后的字符串
     */
    public static String addUnitWhenBeyondHundredThousand(double value) {
        if (value > 100000 || value < -100000) {
            return formatWithThousandsSeparatorAndUnit(value, UNIT_WANG);
        }
        return formatWithThousandsSeparator(value);
    }

    /**
     * 当数字大于 1,000,000 或小于 -1,000,000 时候，添加‘万’单位，并使用‘银行家算法’精确（保留）到小数点后两位
     *
     * @param value
     * @return 处理后的字符串
     */
    public static String addUnitWhenBeyondMillion(double value) {
        if (value > 1000000 || value < -1000000) {
            return formatWithThousandsSeparatorAndUnit(value, UNIT_WANG);
        }
        return formatWithThousandsSeparator(value);
    }

    /**
     * 当数字大于 100,000,000 或小于 -100,000,000 时候，添加‘亿’单位，并使用‘银行家算法’精确（保留）到小数点后两位
     *
     * @param value
     * @return 处理后的字符串
     */
    public static String addUnitWhenBeyondHundredMillion(double value) {
        if (value > 100000000 || value < -100000000) {
            return formatWithThousandsSeparatorAndUnit(value, UNIT_YI);
        }
        return formatWithThousandsSeparator(value);
    }

    /**
     * 添加单位
     *
     * @param value
     * @return
     */
    public static String addUnitWithoutSeparator(double value) {
        if (value > 100000000 || value < -100000000) {
            return formatWithUnit(value, UNIT_YI);
        } else if (value > 10000 || value < -10000) {
            return formatWithUnit(value, UNIT_WANG);
        }
        return formatWithThousandsSeparator(value);
    }

    private static String formatWithUnit(double value, String unit) {
        if (unit == UNIT_WANG) {
            BigDecimal newValue = divide(value, 10000.000);
            return formatWithScale(newValue.doubleValue()) + unit;
        } else if (unit == UNIT_YI) {
            BigDecimal newValue = divide(value, 100000000.000);
            return formatWithScale(newValue.doubleValue()) + unit;
        }
        return String.valueOf(value);
    }

    /**
     * 使用大额单位和千位分隔符格式化数字
     * <p/>
     * 1 基于使用的大额单位做除法（现在暂时只支持‘万’，以后或许会有百万，千万，亿）
     * 2 调用 formatWithThousandsSeparator 进行千位分隔以及两位小数保留
     * 3 添加单位 unit
     *
     * @param value
     * @param unit
     * @return 处理后的字符串
     */
    private static String formatWithThousandsSeparatorAndUnit(double value, String unit) {
        if (unit == UNIT_WANG) {
            BigDecimal newValue = divide(value, 10000.000);
            return formatWithThousandsSeparator(newValue.doubleValue()) + unit;
        } else if (unit == UNIT_YI) {
            BigDecimal newValue = divide(value, 100000000.000);
            return formatWithThousandsSeparator(newValue.doubleValue()) + unit;
        }
        return String.valueOf(value);
    }

    /**
     * 使用千位分隔符分割 double 数据，并使用‘银行家算法’精确（保留）到小数点后两位
     *
     * @param value
     * @return 处理后的字符串
     */
    public static String formatWithThousandsSeparator(double value) {
        return formatWithThousandsSeparatorAndScale(value, DEFAULT_SCALE);
    }

    /**
     * 使用千位分隔符分割 double，并使用‘银行家算法’精确（保留）到小数点后 scale 位
     *
     * @param value
     * @param scale
     * @return 处理后的字符串
     */
    public static String formatWithThousandsSeparatorAndScale(double value, int scale) {
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance();

        decimalFormat.setMaximumFractionDigits(scale);
        decimalFormat.setMinimumFractionDigits(scale);
        decimalFormat.setMinimumIntegerDigits(1);
        decimalFormat.setRoundingMode(RoundingMode.HALF_EVEN);
        decimalFormat.setGroupingSize(3);

        return decimalFormat.format(value);
    }


    /**
     * 使用‘银行家算法’精确（保留）到小数点后 2 位
     *
     * @param value
     * @return 处理后的字符串
     */
    public static String formatWithScale(double value) {
        return formatWithScale(value, DEFAULT_SCALE);
    }

    /**
     * 使用‘银行家算法’不保留到小数点后多余的0
     *
     * @param value
     * @return 处理后的字符串
     */
    public static String formatWithScaleNoZero(double value) {
        return removeNeedlessZero(formatWithScale(value, DEFAULT_SCALE));
    }

    /**
     * 使用‘银行家算法’精确（保留）到小数点后 scale 位
     *
     * @param value
     * @param scale 小数位数
     * @return 处理后的字符串
     */
    public static String formatWithScale(double value, int scale) {
        return formatWithScale(value, scale, RoundingMode.HALF_EVEN);
    }

    public static String formatWithScale(double value, int scale, RoundingMode roundingMode) {
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance();
        decimalFormat.setMaximumFractionDigits(scale);
        decimalFormat.setMinimumFractionDigits(scale);
        decimalFormat.setMinimumIntegerDigits(1);
        decimalFormat.setGroupingUsed(false);
        decimalFormat.setRoundingMode(roundingMode);
        String v = decimalFormat.format(value);
        return v;
    }


    /**
     * 处理 Double 的大数据减法
     *
     * @param minuend    被减数
     * @param subtrahend 减数
     * @return 减法结果
     */
    public static BigDecimal subtraction(double minuend, double subtrahend) {
        BigDecimal bigMinuend = BigDecimal.valueOf(minuend);
        BigDecimal bigSubtrahend = BigDecimal.valueOf(subtrahend);
        return bigMinuend.subtract(bigSubtrahend);
    }

    /**
     * 处理 Double 的大数据减法
     *
     * @param minuend    被减数
     * @param subtrahend 减数
     * @return 减法结果
     */
    public static BigDecimal subtraction(Double minuend, Double subtrahend) {
        if (minuend == null || subtrahend == null) {
            throw new NullPointerException("Minuend or subtrahend is null");
        }
        return subtraction(minuend.doubleValue(), subtrahend.doubleValue());
    }

    /**
     * 处理 Double 的大数据除法
     *
     * @param dividend     被除数
     * @param divisor      除数
     * @param scale        保留小数位数
     * @param roundingMode 保留位数方式
     * @return 除法结果
     */
    public static BigDecimal divide(double dividend, double divisor, int scale, RoundingMode roundingMode) {
        BigDecimal bigDividend = BigDecimal.valueOf(dividend);
        BigDecimal bigDivisor = BigDecimal.valueOf(divisor);
        return bigDividend.divide(bigDivisor, scale, roundingMode);
    }

    /**
     * 处理 Double 的大数据除法
     *
     * @param dividend     被除数
     * @param divisor      除数
     * @param scale        保留小数位数
     * @param roundingMode 保留位数方式
     * @return 除法结果
     */
    public static BigDecimal divide(Double dividend, Double divisor, int scale, RoundingMode roundingMode) {
        if (dividend == null && divisor == null) {
            throw new NullPointerException("Dividend or divisor is null");
        }
        return divide(dividend.doubleValue(), divisor.doubleValue(), scale, roundingMode);

    }

    /**
     * 处理 Double 的大数据除法，默认使用‘银行家算法’保留2位小数
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @param scale    保留小数位数
     * @return 除法结果
     */
    public static BigDecimal divide(double dividend, double divisor, int scale) {
        return divide(dividend, divisor, scale, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 处理 Double 的大数据除法，默认使用‘银行家算法’保留2位小数
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @param scale    保留小数位数
     * @return 除法结果
     */
    public static BigDecimal divide(Double dividend, Double divisor, int scale) {
        return divide(dividend, divisor, scale, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 处理 Double 的大数据除法，默认使用‘银行家算法’保留2位小数
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @return 除法结果
     */
    public static BigDecimal divide(double dividend, double divisor) {
        return divide(dividend, divisor, DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 处理 Double 的大数据除法，默认使用‘银行家算法’保留2位小数
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @return 除法结果
     */
    public static BigDecimal divide(Double dividend, Double divisor) {
        return divide(dividend, divisor, DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 处理 Double 的大数据乘法
     *
     * @param multiplicand 被乘数
     * @param multiplier   乘数
     * @param scale        保留小数位数
     * @param roundingMode 保留位数方式
     * @return 乘法结果
     */
    public static BigDecimal multiply(double multiplicand, double multiplier, int scale, RoundingMode roundingMode) {
        BigDecimal bigMultiplicand = BigDecimal.valueOf(multiplicand);
        BigDecimal bigMultiplier = BigDecimal.valueOf(multiplier);
        return bigMultiplier.multiply(bigMultiplicand).setScale(scale, roundingMode);
    }

    /**
     * 处理 Double 的大数据乘法
     *
     * @param multiplicand 被乘数
     * @param multiplier   乘数
     * @param scale        保留小数位数
     * @param roundingMode 保留位数方式
     * @return 乘法结果
     */
    public static BigDecimal multiply(Double multiplicand, Double multiplier, int scale, RoundingMode roundingMode) {
        if (multiplicand == null || multiplier == null) {
            throw new NullPointerException("multiplicand or multiplier is null");
        }
        return multiply(multiplier.doubleValue(), multiplicand.doubleValue(), scale, roundingMode);
    }

    /**
     * 处理 Double 的大数据乘法
     *
     * @param multiplicand 被乘数
     * @param multiplier   乘数
     * @return 乘法结果
     */
    public static BigDecimal multiply(double multiplicand, double multiplier) {
        BigDecimal bigMultiplicand = BigDecimal.valueOf(multiplicand);
        BigDecimal bigMultiplier = BigDecimal.valueOf(multiplier);
        return bigMultiplier.multiply(bigMultiplicand);
    }

    /**
     * 处理 Double 的大数据乘法
     *
     * @param multiplicand 被乘数
     * @param multiplier   乘数
     * @return 乘法结果
     */
    public static BigDecimal multiply(Double multiplicand, Double multiplier) {
        if (multiplicand == null || multiplier == null) {
            throw new NullPointerException("multiplicand or multiplier is null");
        }
        return multiply(multiplicand.doubleValue(), multiplier.doubleValue());
    }

    /**
     * 处理 Double 的大数据加法
     *
     * @param summand 被加数
     * @param addend  加数
     * @return 加法结果
     */
    public static BigDecimal add(double summand, double addend) {
        BigDecimal bigSummand = BigDecimal.valueOf(summand);
        BigDecimal bigAddend = BigDecimal.valueOf(addend);
        return bigSummand.add(bigAddend);
    }

    /**
     * 处理 Double 的大数据加法
     *
     * @param summand 被加数
     * @param addend  加数
     * @return 加法结果
     */
    public static BigDecimal add(Double summand, Double addend) {
        if (summand == null || addend == null) {
            throw new NullPointerException("multiplicand or multiplier is null");
        }
        return add(summand.doubleValue(), addend.doubleValue());
    }

    /**
     * 去除两位小数点后多余的.00
     *
     * @param number
     * @return
     */
    public static String removeNeedlessZero(String number) {
        return number.replace(".00", "");
    }


    /**
     * 格式化 我的问答页面的人数数字显示   超过10000 用 万来表示
     * 例子  123456
     *
     * @param number
     * @return 12.3万
     */
    public static String formatTenThousandNumber(int number) {
        if (number > 9999) {
            String s = String.valueOf(number);
            String substring = s.substring(s.length() - 4, s.length() - 3);
            return number / 10000 + "." + substring + FinanceUtil.UNIT_WANG;
        }
        return String.valueOf(number);
    }

    /**
     * 直接舍去百分化后的小数
     *
     * @param number 0.9956
     * @return 99%
     */
    public static String formatFloorPercent(double number) {
        return formatFloorPercent(number, 0);
    }

    /**
     * @param number 0.99545
     * @param scale
     * @return 99.54%
     */
    public static String formatFloorPercent(double number, int scale) {
        BigDecimal bigDecimal = multiply(number, 100d);
        return removeNeedlessZero(formatWithScale(bigDecimal.doubleValue(), scale, RoundingMode.DOWN) + "%");
    }
}
