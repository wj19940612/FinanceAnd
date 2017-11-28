package com.sbai.finance.model.stock;

import android.text.TextUtils;

import com.sbai.finance.utils.FinanceUtil;

/**
 * Created by linrongfang on 2017/6/16.
 */

public class StockData {

    /**
     * name:唐朝科技
     * instrumentId : 000004
     * lastPrice : 1111.1
     * tradeDay : 2017-06-05
     * upDropPrice : 1.1
     * upDropSpeed : 0.005
     * upTime : 1496644067439
     * upTimeFormat : 2017-06-05 12:00:00
     */

    private String name;
    private String instrumentId;
    private String lastPrice;
    private String tradeDay;
    private String upDropPrice;
    private double upDropSpeed;
    private long upTime;
    private String upTimeFormat;
    private String preClsPrice;
    private String status;

    public String getPreClsPrice() {
        return preClsPrice;
    }

    public void setPreClsPrice(String preClsPrice) {
        this.preClsPrice = preClsPrice;
    }

    public String getName() {
        return name;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public String getFormattedLastPrice() {
        if (TextUtils.isEmpty(lastPrice)) {
            return "--";
        }
        return FinanceUtil.formatWithScale(lastPrice);
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public String getTradeDay() {
        return tradeDay;
    }

    public String getFormattedUpDropPrice() {
        if (TextUtils.isEmpty(upDropPrice)) {
            return "--";
        }
        return FinanceUtil.formatWithScale(upDropPrice);
    }

    public String getUpDropPrice() {
        return upDropPrice;
    }

    public double getUpDropSpeed() {
        return upDropSpeed;
    }

    public long getUpTime() {
        return upTime;
    }

    public String getUpTimeFormat() {
        return upTimeFormat;
    }


    public boolean isDelist() {
        return status.equals(StockRTData.STATUS_DELIST);
    }
}
