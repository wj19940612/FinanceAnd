package com.sbai.finance.model.mine.cornucopia;

/**
 * Created by ${wangJie} on 2017/6/21.
 * 虚拟的元宝或者积分商品model
 */

public class VirtualProductModel {

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
    private double fromMoney;
    //真实的兑换来源金额
    private double fromRealMoney;
    //货币类型  0 现金 1元宝
    private int fromType;
    //兑换配置Id
    private int id;
    private int sort;
    //状态  0 不可用 1可用
    private int status;
    //要兑换的金额
    private double toMoney;
    //要兑换的真实金额
    private double toRealMoney;
    //货币类型  1元宝 3 积分
    private int toType;

    //自己加的标识
    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isDiscount() {
        return getFromMoney() != getFromRealMoney();
    }

    public boolean isIngot() {
        return getToType() == 1;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public double getFromMoney() {
        return fromMoney;
    }

    public void setFromMoney(double fromMoney) {
        this.fromMoney = fromMoney;
    }

    public double getFromRealMoney() {
        return fromRealMoney;
    }

    public void setFromRealMoney(double fromRealMoney) {
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

    public double getToMoney() {
        return toMoney;
    }

    public void setToMoney(int toMoney) {
        this.toMoney = toMoney;
    }

    public double getToRealMoney() {
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
