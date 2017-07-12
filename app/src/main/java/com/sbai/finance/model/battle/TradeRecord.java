package com.sbai.finance.model.battle;

import com.sbai.finance.model.Variety;

import static com.sbai.finance.websocket.PushCode.ORDER_CREATED;

/**
 * Created by linrongfang on 2017/6/23.
 */

public class TradeRecord {

    public static final int DIRECTION_UP = 1;
    public static final int DIRECTION_DOWN = 0;

    public static final int STATUS_TAKE_MORE_POSITION = 1;
    public static final int STATUS_TAKE_SHORT_POSITION = 2;
    public static final int STATUS_TAKE_MORE_CLOSE_POSITION = 3;
    public static final int STATUS_TAKE_SHOET_CLOSE_POSITION = 4;

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

    public static TradeRecord getRecord(Battle battle, Variety variety,int type) {
        TradeRecord record = new TradeRecord();
        record.setContractsCode(battle.getContractsCode());
        record.setHandsNum(battle.getHandsNum());
        record.setMarketPoint(variety.getPriceScale());
        record.setUserId(battle.getUserId());
        record.setVarietyName(battle.getVarietyName());
        record.setVarietyType(battle.getVarietyType());
        if (type == ORDER_CREATED) {
            record.setOptPrice(battle.getOrderPrice());
            record.setOptTime(battle.getOrderTime());
        }else {
            record.setOptPrice(battle.getUnwindPrice());
            record.setOptTime(battle.getUnwindTime());
        }
        int orderStatus = 0;
        if (battle.getDirection() == 1) {
            //买涨
            if (battle.getOrderStatus() == 2) {
                orderStatus = 1;
            } else if (battle.getOrderStatus() == 4) {
                orderStatus = 3;
            }
        } else if (battle.getDirection() == 0) {
            //买跌
            if (battle.getOrderStatus() == 2) {
                orderStatus = 2;
            } else if (battle.getOrderStatus() == 4) {
                orderStatus = 4;
            }
        }
        record.setOptStatus(orderStatus);
        return record;
    }
}
