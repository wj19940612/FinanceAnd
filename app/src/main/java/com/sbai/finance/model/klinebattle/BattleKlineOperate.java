package com.sbai.finance.model.klinebattle;

/**
 * 对决操盘数据
 */

public class BattleKlineOperate {

    /**
     * battleId : 13
     * battleStatus : 1
     * code : win
     * next : {"bettleId":13,"closePrice":83.8,"day":"2017-11-13","id":42,"mark":"Y","nowVolume":800,"userId":1070}
     * operate : true
     * positions : 0.0041921188166248475
     * profit : 0.0041921188166248475
     * sort : 0
     * status : 1
     * userId : 1070
     */

    private int battleId;
    private int battleStatus;
    private String code;
    private BattleKlineData next;
    private boolean operate;
    private double positions;
    private double profit;
    private int sort;
    private int status;
    private int userId;

    public int getBattleId() {
        return battleId;
    }

    public void setBattleId(int battleId) {
        this.battleId = battleId;
    }

    public int getBattleStatus() {
        return battleStatus;
    }

    public void setBattleStatus(int battleStatus) {
        this.battleStatus = battleStatus;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BattleKlineData getNext() {
        return next;
    }

    public void setNext(BattleKlineData next) {
        this.next = next;
    }

    public boolean isOperate() {
        return operate;
    }

    public void setOperate(boolean operate) {
        this.operate = operate;
    }

    public double getPositions() {
        return positions;
    }

    public void setPositions(double positions) {
        this.positions = positions;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
