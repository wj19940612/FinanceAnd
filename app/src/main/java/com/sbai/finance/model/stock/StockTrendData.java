package com.sbai.finance.model.stock;

import com.sbai.finance.utils.DateUtil;

public class StockTrendData {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private String nowVolume;
    private String closePrice; // 最新价
    private String time;

    public long getNowVolume() {
        return Long.valueOf(nowVolume).longValue();
    }

    public float getClosePrice(){
        return Float.valueOf(closePrice).floatValue();
    }

    public String getRawClosePrice() {
        return closePrice;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSameData(StockTrendData unstableData) {
        return nowVolume.equals(unstableData.getNowVolume())
                && closePrice.equals(unstableData.getRawClosePrice());
    }

    public String getHHmm() {
        return DateUtil.format(time, DATE_FORMAT, "HH:mm");
    }
}