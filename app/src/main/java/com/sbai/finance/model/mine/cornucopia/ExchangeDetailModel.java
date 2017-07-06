package com.sbai.finance.model.mine.cornucopia;

/**
 * Created by ${wangJie} on 2017/6/21.
 */

public class ExchangeDetailModel {

    //元宝
    public static final int TYPE_INGOT = 1;
    //积分
    public static final int TYPE_INTEGRATE = 2;

    //收入 1 支出2
    public static final int DIRECTION_EARNINGS = 1;
    public static final int DIRECTION_EXTEND = 2;

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

    //流水类型
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
    //流水类型
    private int flowType;

    public boolean isVcoin() {
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

    public int getFlowType() {
        return flowType;
    }

    public void setFlowType(int flowType) {
        this.flowType = flowType;
    }

    @Override
    public String toString() {
        return "ExchangeDetailModel{" +
                "currencyType=" + currencyType +
                ", money=" + money +
                ", createTime=" + createTime +
                ", remark='" + remark + '\'' +
                ", id=" + id +
                ", typeDetail=" + typeDetail +
                ", userId=" + userId +
                ", moneyLeft=" + moneyLeft +
                ", flowType=" + flowType +
                '}';
    }
}
