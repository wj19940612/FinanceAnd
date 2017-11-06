package com.sbai.finance.model.battle;

import com.sbai.finance.net.Client;

/**
 * Modified by john on 31/10/2017
 *
 * APIs: {@link Client#requestCurrentOrders(int)} 获取当前对战持仓订单
 *
 */
public class TradeOrder {

    public static final int DIRECTION_LONG = 1;
    public static final int DIRECTION_SHORT = 0;

    public static final int ORDER_STATUS_HOLDING = 2;
    public static final int ORDER_STATUS_CLOSED = 4;

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
    private double orderPrice;
    private int orderStatus;
    private long orderTime;
    private int userId;
    private int varietyId;
    private String varietyName;
    private String varietyType;


    public String getContractsCode() {
        return contractsCode;
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

    public int getDirection() {
        return direction;
    }

    public int getHandsNum() {
        return handsNum;
    }

    public int getId() {
        return id;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public double getOrderPrice() {
        return orderPrice;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public int getUserId() {
        return userId;
    }

    public int getVarietyId() {
        return varietyId;
    }

    public String getVarietyName() {
        return varietyName;
    }

    public String getVarietyType() {
        return varietyType;
    }

    public static TradeOrder getTradeOrder(Battle battle){
        TradeOrder order = new TradeOrder();
        order.battleBatchCode = battle.getBattleBatchCode();
        order.battleId = battle.getBattleId();
        order.contractsCode = battle.getContractsCode();
        order.contractsId = battle.getContractsId();
//        order.createTime = battle.getCreateTime(); 推送数据 无
        order.handsNum = battle.getHandsNum();
        order.id = battle.getId();
//        order.modifyTime = battle.getModifyTime(); 推送数据 无
        order.orderPrice = battle.getOrderPrice();
        order.orderStatus = battle.getOrderStatus();
        order.orderTime = battle.getOrderTime();
        order.userId = battle.getUserId();
        order.varietyId = battle.getVarietyId();
        order.varietyName = battle.getVarietyName();
        order.varietyType = battle.getVarietyType();
        order.direction = battle.getDirection();
        order.id = battle.getId();
        return order;
    }

    @Override
    public String toString() {
        return "TradeOrder{" +
                "battleBatchCode='" + battleBatchCode + '\'' +
                ", battleId=" + battleId +
                ", contractsCode='" + contractsCode + '\'' +
                ", contractsId=" + contractsId +
                ", createTime=" + createTime +
                ", direction=" + direction +
                ", handsNum=" + handsNum +
                ", id=" + id +
                ", modifyTime=" + modifyTime +
                ", orderPrice=" + orderPrice +
                ", orderStatus=" + orderStatus +
                ", orderTime=" + orderTime +
                ", userId=" + userId +
                ", varietyId=" + varietyId +
                ", varietyName='" + varietyName + '\'' +
                ", varietyType='" + varietyType + '\'' +
                '}';
    }
}
