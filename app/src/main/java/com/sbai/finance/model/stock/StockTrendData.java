package com.sbai.finance.model.stock;

public class StockTrendData {

    private String nowVolume;
    private String closePrice;
    private String time;

    private String business_amount;
    private String last_price;

    // extra local value
    private long businessVolume;


    public String getNowVolume() {
        return nowVolume;
    }

    public void setNowVolume(String nowVolume) {
        this.nowVolume = nowVolume;
    }

    public String getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(String closePrice) {
        this.closePrice = closePrice;
    }

    public long getNow_Volume(){
        return Long.valueOf(nowVolume).longValue();
    }

    public float getClose_Price(){
        return Float.valueOf(closePrice).floatValue();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getBusinessVolume() {
        return businessVolume;
    }

    public void setBusinessVolume(long businessVolume) {
        this.businessVolume = businessVolume;
    }



    public boolean isSameData(StockTrendData unstableData) {
        return nowVolume.equals(unstableData.getNowVolume())
                && closePrice.equals(unstableData.getClosePrice());
    }
}