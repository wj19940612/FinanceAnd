package com.sbai.finance.model;

/**
 * 皮肤兑换记录
 */

public class SkinExchangeRecord {

    /**
     * activityId : 1
     * count : 99
     * createTime : 1504604187000
     * exchange : 0
     * exchangeTime : 1504611616000
     * id : 1
     * modifyTime : 1504680819000
     * price : 7889
     * productName : 千年之狐-李白
     * productUrl : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1495073508890.png
     */

    private int activityId;
    private int count;
    private long createTime;
    private int exchange;
    private long exchangeTime;
    private int id;
    private long modifyTime;
    private int price;
    private String productName;
    private String productUrl;

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getExchange() {
        return exchange;
    }

    public void setExchange(int exchange) {
        this.exchange = exchange;
    }

    public long getExchangeTime() {
        return exchangeTime;
    }

    public void setExchangeTime(long exchangeTime) {
        this.exchangeTime = exchangeTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }
}
