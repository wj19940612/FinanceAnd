package com.sbai.finance.model.fund;

/**
 * Created by ${wangJie} on 2017/6/16.
 * 用户资金信息
 */

public class UserFundInfo {


    /**
     * money : 0
     * yuanbao : 0
     * credit : 0
     * userId : 130
     */
    //现金
    private double money;
    //元宝
    private double yuanbao;
    //积分
    private double credit;
    private int userId;

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public long getYuanbao() {
        return (long) yuanbao;
    }

    public void setYuanbao(double yuanbao) {
        this.yuanbao = yuanbao;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserFundInfoModel{" +
                "money=" + money +
                ", yuanbao=" + yuanbao +
                ", credit=" + credit +
                ", userId=" + userId +
                '}';
    }
}
