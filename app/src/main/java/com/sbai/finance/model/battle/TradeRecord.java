package com.sbai.finance.model.battle;

import com.sbai.finance.net.Client;

/**
 * Modified by john on 31/10/2017
 * <p>
 * APIs: {@link Client#getTradeOperationRecords(int)}
 * <p>
 * 对战交易操作记录
 */
public class TradeRecord {

    public static final int DIRECTION_LONG = 1;
    public static final int DIRECTION_SHORT = 0;

    public static final int OPT_STATUS_OPEN_POSITION_LONG = 1; // 买涨建仓
    public static final int OPT_STATUS_OPEN_POSITION_SHORT = 2; // 买跌建仓
    public static final int OPT_STATUS_CLOSE_POSITION_LONG = 3; // 买涨平仓
    public static final int OPT_STATUS_CLOSE_POSITION_SHORT = 4; // 买跌平仓

    private int handsNum;
    private double optPrice;
    private int optStatus;
    private long optTime;
    private int userId;
    private String contractsCode;
    private String varietyName;
    private String varietyType;
    private int marketPoint;

    public int getMarketPoint() {
        return marketPoint;
    }

    public void setMarketPoint(int marketPoint) {
        this.marketPoint = marketPoint;
    }

    public String getContractsCode() {
        return contractsCode;
    }

    public void setContractsCode(String contractsCode) {
        this.contractsCode = contractsCode;
    }

    public int getHandsNum() {
        return handsNum;
    }

    public void setHandsNum(int handsNum) {
        this.handsNum = handsNum;
    }

    public double getOptPrice() {
        return optPrice;
    }

    public void setOptPrice(double optPrice) {
        this.optPrice = optPrice;
    }

    public int getOptStatus() {
        return optStatus;
    }

    public void setOptStatus(int optStatus) {
        this.optStatus = optStatus;
    }

    public long getOptTime() {
        return optTime;
    }

    public void setOptTime(long optTime) {
        this.optTime = optTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public static TradeRecord getRecord(Battle battle) {
        TradeRecord record = new TradeRecord();
        record.handsNum = battle.getHandsNum();
        record.userId = battle.getUserId();
        record.contractsCode = battle.getContractsCode();
        record.varietyName = battle.getVarietyName();
        record.varietyType = battle.getVarietyType();
        record.marketPoint = battle.getMarketPoint();

        if (battle.getUnwindType() > 0) { // 平仓
            record.optPrice =  battle.getUnwindPrice();
            record.optTime = battle.getUnwindTime();
        } else {
            record.optPrice = battle.getOrderPrice();
            record.optTime = battle.getOrderTime();
        }

        int orderStatus = 0;
        if (battle.getDirection() == DIRECTION_LONG) { //买涨
            if (battle.getOrderStatus() == TradeOrder.ORDER_STATUS_HOLDING) {
                orderStatus = OPT_STATUS_OPEN_POSITION_LONG;
            } else if (battle.getOrderStatus() == TradeOrder.ORDER_STATUS_CLOSED) {
                orderStatus = OPT_STATUS_CLOSE_POSITION_LONG;
            }
        } else if (battle.getDirection() == DIRECTION_SHORT) { //买跌
            if (battle.getOrderStatus() == TradeOrder.ORDER_STATUS_HOLDING) {
                orderStatus = OPT_STATUS_OPEN_POSITION_SHORT;
            } else if (battle.getOrderStatus() == TradeOrder.ORDER_STATUS_CLOSED) {
                orderStatus = OPT_STATUS_CLOSE_POSITION_SHORT;
            }
        }
        record.optStatus = orderStatus;
        return record;
    }
}
