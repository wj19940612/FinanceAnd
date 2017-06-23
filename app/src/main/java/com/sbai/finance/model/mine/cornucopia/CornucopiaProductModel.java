package com.sbai.finance.model.mine.cornucopia;

/**
 * Created by ${wangJie} on 2017/6/21.
 * 聚宝盆 可兑换元宝或者积分列表model
 */

public class CornucopiaProductModel {

    //元宝
    public static final int TYPE_VCOIN = 0;
    //积分
    public static final int TYPE_INTEGRATION = 1;

    /**
     * discount : 90
     * fromMoney : 2
     * fromRealMoney : 0
     * fromType : 1
     * id : 2
     * sort : 2
     * status : 1
     * toMoney : 1200
     * toRealMoney : 0
     * toType : 3
     */
    //折扣比例 100 ， 70为7折
    private int discount;
    //兑换来源金额
    private int fromMoney;
    //真实的兑换来源金额
    private int fromRealMoney;
    //货币类型  0 现金 1元宝
    private int fromType;
    //兑换配置Id
    private int id;
    private int sort;
    //状态  0 不可用 1可用
    private int status;
    //要兑换的金额
    private int toMoney;
    //要兑换的真实金额
    private int toRealMoney;
    //货币类型  1元宝 3 积分
    private int toType;

    public boolean isNotDiscount() {
        return getDiscount() == 100;
    }

    public boolean isVcoin() {
        return getToType() == 1;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getFromMoney() {
        return fromMoney;
    }

    public void setFromMoney(int fromMoney) {
        this.fromMoney = fromMoney;
    }

    public int getFromRealMoney() {
        return fromRealMoney;
    }

    public void setFromRealMoney(int fromRealMoney) {
        this.fromRealMoney = fromRealMoney;
    }

    public int getFromType() {
        return fromType;
    }

    public void setFromType(int fromType) {
        this.fromType = fromType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getToMoney() {
        return toMoney;
    }

    public void setToMoney(int toMoney) {
        this.toMoney = toMoney;
    }

    public int getToRealMoney() {
        return toRealMoney;
    }

    public void setToRealMoney(int toRealMoney) {
        this.toRealMoney = toRealMoney;
    }

    public int getToType() {
        return toType;
    }

    public void setToType(int toType) {
        this.toType = toType;
    }
}
