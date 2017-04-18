package com.sbai.chart.domain;

public class TrendViewData {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private String contractId;
    private float closePrice;
    private String time; // time format 2017-03-03 07:00:59

    public TrendViewData(String contractId, float closePrice, String time) {
        this.contractId = contractId;
        this.closePrice = closePrice;
        this.time = time;
    }

    public String getHHmm() {
        int spaceIndex = time.indexOf(" ");
        if (spaceIndex > -1) {
            return time.substring(spaceIndex + 1, spaceIndex + 6);
        }
        return "";
    }

    public String getYyyyMMdd() {
        int spaceIndex = time.indexOf(" ");
        if (spaceIndex > -1) {
            return time.substring(0, spaceIndex).replace("-", "/");
        }
        return "";
    }

    public float getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(float closePrice) {
        this.closePrice = closePrice;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSameData(TrendViewData data) {
        return this.closePrice == data.getClosePrice()
                && this.time.equals(data.getTime());
    }

    public String getContractId() {
        return contractId;
    }
}
