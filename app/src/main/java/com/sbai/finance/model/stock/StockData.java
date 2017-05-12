package com.sbai.finance.model.stock;

public class StockData {

    /**
     * code_name : 厦门钨业
     * high_price : 21.54
     * last_price : 21.36
     * low_price : 20.97
     * rise_pre : 2.10
     * rise_price : 0.44
     * stock_code : 600549
     */

    private String code_name;
    private String high_price;
    private String last_price;
    private String low_price;
    private String rise_pre;
    private String rise_price;
    private String stock_code;

    public String getCode_name() {
        return code_name;
    }

    public void setCode_name(String code_name) {
        this.code_name = code_name;
    }

    public String getHigh_price() {
        return high_price;
    }

    public void setHigh_price(String high_price) {
        this.high_price = high_price;
    }

    public String getLast_price() {
        return last_price;
    }

    public void setLast_price(String last_price) {
        this.last_price = last_price;
    }

    public String getLow_price() {
        return low_price;
    }

    public void setLow_price(String low_price) {
        this.low_price = low_price;
    }

    public String getRise_pre() {
        return rise_pre;
    }

    public void setRise_pre(String rise_pre) {
        this.rise_pre = rise_pre;
    }

    public String getRise_price() {
        return rise_price;
    }

    public void setRise_price(String rise_price) {
        this.rise_price = rise_price;
    }

    public String getStock_code() {
        return stock_code;
    }

    public void setStock_code(String stock_code) {
        this.stock_code = stock_code;
    }
}
