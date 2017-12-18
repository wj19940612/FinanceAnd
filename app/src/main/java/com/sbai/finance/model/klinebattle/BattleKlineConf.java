package com.sbai.finance.model.klinebattle;

/**
 * 游戏配置信息
 */

public class BattleKlineConf {

    /**
     * allNum : 61
     * battleType : 1v1
     * bounty : 100
     * fourMoney : 0
     * id : 1
     * lineNum : 40
     * oneMoney : 2000
     * roundTime : 1
     * threeMoney : 1500
     * twoMoney : 1800
     */

    private int allNum;
    private String battleType;
    private int bounty;
    private int fourMoney;
    private int id;
    private int lineNum;
    private int oneMoney;
    private int roundTime;
    private int threeMoney;
    private int twoMoney;

    public int getAllNum() {
        return allNum;
    }

    public void setAllNum(int allNum) {
        this.allNum = allNum;
    }

    public String getBattleType() {
        return battleType;
    }

    public void setBattleType(String battleType) {
        this.battleType = battleType;
    }

    public int getBounty() {
        return bounty;
    }

    public void setBounty(int bounty) {
        this.bounty = bounty;
    }

    public int getFourMoney() {
        return fourMoney;
    }

    public void setFourMoney(int fourMoney) {
        this.fourMoney = fourMoney;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public int getOneMoney() {
        return oneMoney;
    }

    public void setOneMoney(int oneMoney) {
        this.oneMoney = oneMoney;
    }

    public int getRoundTime() {
        return roundTime;
    }

    public void setRoundTime(int roundTime) {
        this.roundTime = roundTime;
    }

    public int getThreeMoney() {
        return threeMoney;
    }

    public void setThreeMoney(int threeMoney) {
        this.threeMoney = threeMoney;
    }

    public int getTwoMoney() {
        return twoMoney;
    }

    public void setTwoMoney(int twoMoney) {
        this.twoMoney = twoMoney;
    }
}
