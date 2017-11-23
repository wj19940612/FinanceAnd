package com.sbai.finance.model.stocktrade;

/**
 * 委托
 */

public class Entrust {
    // 1 已报待成 2部成 3已成
    public static final int ENTRUST_STATUS_NO_BUSINESS = 1;
    public static final int ENTRUST_STATUS_PART_BUSINESS = 2;
    public static final int ENTRUST_STATUS_ALL_BUSINESS = 3;
    public static final int ENTRUST_BS_SELL = 0;
    public static final int ENTRUST_BS_BUY = 1;
    /**
     * id : 147
     * direction : 0
     * varietyCode : 000011
     * varietyName : 深物业A
     * quantity : 100
     * bargainPrice : 3213.53
     * price : 18
     * totalBargain : null
     * moiety : 2
     * succQuantity : null
     * bargainTime : null
     */

    private int id;
    private int direction;
    private String varietyCode;
    private String varietyName;
    private int quantity;
    private double bargainPrice;
    private int price;
    private double totalBargain;
    private int moiety;
    private int succQuantity;
    private long bargainTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getVarietyCode() {
        return varietyCode;
    }

    public void setVarietyCode(String varietyCode) {
        this.varietyCode = varietyCode;
    }

    public String getVarietyName() {
        return varietyName;
    }

    public void setVarietyName(String varietyName) {
        this.varietyName = varietyName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getBargainPrice() {
        return bargainPrice;
    }

    public void setBargainPrice(double bargainPrice) {
        this.bargainPrice = bargainPrice;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public double getTotalBargain() {
        return totalBargain;
    }

    public void setTotalBargain(double totalBargain) {
        this.totalBargain = totalBargain;
    }

    public int getMoiety() {
        return moiety;
    }

    public void setMoiety(int moiety) {
        this.moiety = moiety;
    }

    public int getSuccQuantity() {
        return succQuantity;
    }

    public void setSuccQuantity(int succQuantity) {
        this.succQuantity = succQuantity;
    }

    public long getBargainTime() {
        return bargainTime;
    }

    public void setBargainTime(long bargainTime) {
        this.bargainTime = bargainTime;
    }
}
