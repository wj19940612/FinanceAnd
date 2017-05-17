package com.sbai.finance.model.stock;

import com.sbai.chart.domain.KlineViewData;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class StockKlineData extends KlineViewData {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat TO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static final int PERIOD_DAY = 6;
    public static final int PERIOD_WEEK = 7;
    public static final int PERIOD_MONTH = 8;

    /**
     * business_amount : 28
     * business_balance : 113
     * close_price : 40.49
     * date : 2704191433
     * high_price : 40.50
     * low_price : 40.49
     * open_price : 40.49
     */

    private String business_amount; // 交易量（91.80万）
    private String business_balance; // 交易额（8.09亿）
    private String close_price;
    private String date;
    private String high_price;
    private String low_price;
    private String open_price;

    public String getBusiness_amount() {
        return business_amount;
    }

    public String getBusiness_balance() {
        return business_balance;
    }

    public String getClose_price() {
        return close_price;
    }

    public String getDate() {
        return date;
    }

    public String getHigh_price() {
        return high_price;
    }

    public String getLow_price() {
        return low_price;
    }

    public String getOpen_price() {
        return open_price;
    }

    @Override
    public float getClosePrice() {
        return Float.valueOf(getClose_price()).floatValue();
    }

    @Override
    public float getMaxPrice() {
        return Float.valueOf(getHigh_price()).floatValue();
    }

    @Override
    public float getMinPrice() {
        return Float.valueOf(getLow_price()).floatValue();
    }

    @Override
    public float getOpenPrice() {
        return Float.valueOf(getOpen_price()).floatValue();
    }

    @Override
    public String getDay() {
        try {
            return TO_DATE_FORMAT.format(DATE_FORMAT.parse(getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public long getNowVolume() {
        return Long.valueOf(getBusiness_amount()).longValue();
    }
}
