package com.sbai.finance.model.stock;

public class StockTrendData {

    /**
     * business_amount : "116600"
     * last_price : "8.68"
     */

    private String business_amount;
    private String last_price;

    // extra local value
    private long businessVolume;

    public String getBusiness_amount() {
        return business_amount;
    }

    public String getLast_price() {
        return last_price;
    }

    public float getLastPrice() {
        return Float.valueOf(last_price).floatValue();
    }

    public long getBusinessAmount() {
        return Long.valueOf(business_amount).longValue();
    }

    public long getBusinessVolume() {
        return businessVolume;
    }

    public void setBusinessVolume(long businessVolume) {
        this.businessVolume = businessVolume;
    }
}