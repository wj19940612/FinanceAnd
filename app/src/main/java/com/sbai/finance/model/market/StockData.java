package com.sbai.finance.model.market;

public class StockData  {

    /**
     * code_name : 厦门钨业
     * high_price : 21.54
     * last_price : 21.36
     * low_price : 20.97
     * rise_pre : 2.10
     * rise_price : 0.44
     * stock_code : 600549
     */
    //名称
    private String code_name;
    //最高价
    private String high_price;
    //最新价
    private String last_price;
    //最低价
    private String low_price;
    //涨幅百分比
    private String rise_pre;
    //涨幅价格
    private String rise_price;
    //股票代码
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

    @Override
    public String toString() {
        return "StockData{" +
                "code_name='" + code_name + '\'' +
                ", high_price='" + high_price + '\'' +
                ", last_price='" + last_price + '\'' +
                ", low_price='" + low_price + '\'' +
                ", rise_pre='" + rise_pre + '\'' +
                ", rise_price='" + rise_price + '\'' +
                ", stock_code='" + stock_code + '\'' +
                '}';
    }


}
