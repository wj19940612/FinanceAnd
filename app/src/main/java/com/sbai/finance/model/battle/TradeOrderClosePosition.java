package com.sbai.finance.model.battle;

/**
 * Created by linrongfang on 2017/6/27.
 */

public class TradeOrderClosePosition {

    /**
     * battleBatchCode : g4QvWXFI
     * battleId : 30
     * contractsCode : CL1708
     * contractsId : 120
     * createTime : 1498541896000
     * currencyUnit : 美元
     * direction : 1
     * handsNum : 1
     * id : 69
     * modifyTime : 1498542389000
     * orderMarket : 4345
     * orderPrice : 43.45
     * orderStatus : 4
     * orderTime : 1498541897000
     * ratio : 7.5
     * sign : $
     * unwindMarket : 4345
     * unwindPrice : 43.45
     * unwindTime : 1498542390000
     * unwindType : 1
     * userId : 155
     * varietyId : 1
     * varietyName : 美原油
     * varietyType : CL
     * winOrLoss : 0
     */

    private String battleBatchCode;
    private int battleId;
    private String contractsCode;
    private int contractsId;
    private long createTime;
    private String currencyUnit;
    private int direction;
    private int handsNum;
    private int id;
    private long modifyTime;
    private int orderMarket;
    private double orderPrice;
    private int orderStatus;
    private long orderTime;
    private double ratio;
    private String sign;
    private int unwindMarket;
    private double unwindPrice;
    private long unwindTime;
    private int unwindType;
    private int userId;
    private int varietyId;
    private String varietyName;
    private String varietyType;
    private int winOrLoss;

    public String getBattleBatchCode() {
        return battleBatchCode;
    }

    public void setBattleBatchCode(String battleBatchCode) {
        this.battleBatchCode = battleBatchCode;
    }

    public int getBattleId() {
        return battleId;
    }

    public void setBattleId(int battleId) {
        this.battleId = battleId;
    }

    public String getContractsCode() {
        return contractsCode;
    }

    public void setContractsCode(String contractsCode) {
        this.contractsCode = contractsCode;
    }

    public int getContractsId() {
        return contractsId;
    }

    public void setContractsId(int contractsId) {
        this.contractsId = contractsId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getCurrencyUnit() {
        return currencyUnit;
    }

    public void setCurrencyUnit(String currencyUnit) {
        this.currencyUnit = currencyUnit;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getHandsNum() {
        return handsNum;
    }

    public void setHandsNum(int handsNum) {
        this.handsNum = handsNum;
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

    public int getOrderMarket() {
        return orderMarket;
    }

    public void setOrderMarket(int orderMarket) {
        this.orderMarket = orderMarket;
    }

    public double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(long orderTime) {
        this.orderTime = orderTime;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getUnwindMarket() {
        return unwindMarket;
    }

    public void setUnwindMarket(int unwindMarket) {
        this.unwindMarket = unwindMarket;
    }

    public double getUnwindPrice() {
        return unwindPrice;
    }

    public void setUnwindPrice(double unwindPrice) {
        this.unwindPrice = unwindPrice;
    }

    public long getUnwindTime() {
        return unwindTime;
    }

    public void setUnwindTime(long unwindTime) {
        this.unwindTime = unwindTime;
    }

    public int getUnwindType() {
        return unwindType;
    }

    public void setUnwindType(int unwindType) {
        this.unwindType = unwindType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getVarietyId() {
        return varietyId;
    }

    public void setVarietyId(int varietyId) {
        this.varietyId = varietyId;
    }

    public String getVarietyName() {
        return varietyName;
    }

    public void setVarietyName(String varietyName) {
        this.varietyName = varietyName;
    }

    public String getVarietyType() {
        return varietyType;
    }

    public void setVarietyType(String varietyType) {
        this.varietyType = varietyType;
    }

    public int getWinOrLoss() {
        return winOrLoss;
    }

    public void setWinOrLoss(int winOrLoss) {
        this.winOrLoss = winOrLoss;
    }
}
