package com.sbai.finance.model.stock;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(String lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getTradeDay() {
        return tradeDay;
    }

    public void setTradeDay(String tradeDay) {
        this.tradeDay = tradeDay;
    }

    public String getUpDropPrice() {
        return upDropPrice;
    }

    public void setUpDropPrice(String upDropPrice) {
        this.upDropPrice = upDropPrice;
    }

    public double getUpDropSpeed() {
        return upDropSpeed;
    }

    public void setUpDropSpeed(double upDropSpeed) {
        this.upDropSpeed = upDropSpeed;
    }

    public long getUpTime() {
        return upTime;
    }

    public void setUpTime(long upTime) {
        this.upTime = upTime;
    }

    public String getUpTimeFormat() {
        return upTimeFormat;
    }

    public void setUpTimeFormat(String upTimeFormat) {
        this.upTimeFormat = upTimeFormat;
    }
}
