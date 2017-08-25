package com.sbai.chart.domain;

public class TrendViewData {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * cu1610,37230.0,20160815101000
     */

    private float closePrice;
    private String time;

    public TrendViewData(float closePrice, String time) {
        this.closePrice = closePrice;
        this.time = time;
    }

    public String getHHmm() {
        int hourIndex = DATE_FORMAT.indexOf("HH");
        String result = "";
        if (hourIndex > -1) {
            result += time.substring(hourIndex, hourIndex + "HH".length());
        }

        int minIndex = DATE_FORMAT.indexOf("mm");
        if (!result.isEmpty() && minIndex > -1) {
            result += ":" + time.substring(minIndex, minIndex + "mm".length());
        }

        return result;
    }

    public float getLastPrice() {
        return closePrice;
    }

    public void setLastPrice(float lastPrice) {
        this.closePrice = lastPrice;
    }

    public String getTime() {
        return time;
    }

    public boolean isSameData(TrendViewData data) {
        return closePrice == data.getLastPrice() && time.equals(data.getTime());
    }
}
