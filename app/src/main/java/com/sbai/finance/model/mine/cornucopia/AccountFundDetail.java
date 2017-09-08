package com.sbai.finance.model.mine.cornucopia;

/**
 * Created by ${wangJie} on 2017/6/21.
 * 用户现金 元宝 积分model
 */

public class AccountFundDetail {
    //现金
    public static final int TYPE_CRASH = 0;
    //元宝
    public static final int TYPE_INGOT = 1;
    //积分
    public static final int TYPE_SCORE = 2;


    /**
     * currencyType : 2
     * money : 1200
     * createTime : 1498015474000
     * remark : 元宝兑换积分
     * id : 56
     * typeDetail : 4501
     * userId : 800082
     * moneyLeft : 1300
     * flowType : 45
     */

    //流水类型  1元宝 2 积分 0 现金
    private int currencyType;
    private double money;
    private long createTime;
    //流水说明
    private String remark;
    private int id;
    //流水详细类型
    private int typeDetail;
    private int userId;
    //流水后的金额
    private double moneyLeft;

    //用来判断正负
    private int type;

    public boolean isIngot() {
        return getCurrencyType() == TYPE_INGOT;
    }

    public int getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(int currencyType) {
        this.currencyType = currencyType;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypeDetail() {
        return typeDetail;
    }

    public void setTypeDetail(int typeDetail) {
        this.typeDetail = typeDetail;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getMoneyLeft() {
        return moneyLeft;
    }

    public void setMoneyLeft(double moneyLeft) {
        this.moneyLeft = moneyLeft;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "AccountFundDetail{" +
                "currencyType=" + currencyType +
                ", money=" + money +
                ", createTime=" + createTime +
                ", remark='" + remark + '\'' +
                ", id=" + id +
                ", typeDetail=" + typeDetail +
                ", userId=" + userId +
                ", moneyLeft=" + moneyLeft +
                ", type=" + type +
                '}';
    }
}
