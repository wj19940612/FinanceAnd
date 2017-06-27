package com.sbai.finance.model.versus;

/**
 * Created by linrongfang on 2017/6/27.
 */

public class TradeOrder {

    /**
     * battleBatchCode : g4QvWXFI
     * battleId : 30
     * contractsCode : CL1707
     * contractsId : 120
     * createTime : 1498204848000
     * direction : 1
     * handsNum : 1
     * id : 7
     * modifyTime : 1498204848000
     * orderMarket : 4334
     * orderPrice : 43.34
     * orderStatus : 2
     * orderTime : 1498204840000
     * userId : 145
     * varietyId : 1
     * varietyName : 美原油
     * varietyType : CL
     */

    private String battleBatchCode;
    private int battleId;
    private String contractsCode;
    private int contractsId;
    private long createTime;
    private int direction;
    private int handsNum;
    private int id;
    private long modifyTime;
    private int orderMarket;
    private double orderPrice;
    private int orderStatus;
    private long orderTime;
    private int userId;
    private int varietyId;
    private String varietyName;
    private String varietyType;

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
}
