package com.sbai.finance.model.fund;

import com.sbai.finance.model.mine.cornucopia.AccountFundDetail;

import static android.R.attr.type;

/**
 * Created by ${wangJie} on 2017/9/7.
 * 元宝 、积分 等虚拟产品可兑换信息
 */

public class VirtualProductInfo {

    private boolean isSelect;


    //兑换来源金额  价格
    private double fromMoney;
    //货币类型  0 现金 1元宝
    private int fromType;
    //兑换配置Id
    private int id;

    //要兑换的金额  产品
    private double toMoney;

    //货币类型  1元宝 3 积分
    private int toType;

    public boolean isIngot() {
        return getToType() == AccountFundDetail.TYPE_INGOT;
    }

    public double getFromMoney() {
        return fromMoney;
    }

    public void setFromMoney(double fromMoney) {
        this.fromMoney = fromMoney;
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

    public double getToMoney() {
        return toMoney;
    }

    public void setToMoney(double toMoney) {
        this.toMoney = toMoney;
    }

    public int getToType() {
        return toType;
    }

    public void setToType(int toType) {
        this.toType = toType;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public String toString() {
        return "VirtualProductInfo{" +
                "isSelect=" + isSelect +
                ", type=" + type +
                ", fromMoney=" + fromMoney +
                ", fromType=" + fromType +
                ", id=" + id +
                ", toMoney=" + toMoney +
                ", toType=" + toType +
                '}';
    }
}
