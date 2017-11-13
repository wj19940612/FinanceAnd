package com.sbai.finance.model.battle;

import com.sbai.finance.net.Client;

/**
 * Modified by john on 31/10/2017
 * <p>
 * APIs: {@link Client#requestCurrentOrders(int)} 获取当前对战持仓订单
 */
public class TradeOrder {

    public static final int DIRECTION_LONG = 1;
    public static final int DIRECTION_SHORT = 0;

    public static final int ORDER_STATUS_HOLDING = 2;
    public static final int ORDER_STATUS_CLOSED = 4;

    /**
     * "battleBatchCode":"ibYUtouN",
     * "battleId":807,
     * "contractsCode":"CL1708",
     * "contractsId":120,
     * "currencyUnit":"美元",
     * "direction":1,
     * "handsNum":1,
     * "id":2934,
     * "marketPoint":2,
     * "optLogCount":40,
     * "orderMarket":45780,
     * "orderPrice":45.78,
     * "orderStatus":2,
     * "orderTime":1499842365903,
     * "ratio":7.5,
     * "sign":"$",
     * "userId":800184,
     * "varietyId":1,
     * "varietyName":"美原油",
     * "varietyType":"CL"
     */

/* 交易记录数据结构
    private int handsNum;
    private double optPrice;
    private int optStatus;
    private long optTime;
    private int userId;
    private String contractsCode;
    private String varietyName;
    private String varietyType;
    private int marketPoint;
    */

    private String battleBatchCode; // 批次号码 lXGrHpSk
    private int battleId;           // 点赞数据已加
    private String contractsCode;   // CL1708  合约代码
    private int contractsId;
    private String currencyUnit;     // 美元 币种单位
    private long createTime;
    private int direction;
    private int handsNum;
    private int id;
    private long modifyTime;
    private int marketPoint;         //	行情小数位数
    private int optLogCount;         // 30  订单房间操作次数
    private double orderMarket;     // 44160  下单市值
    private double orderPrice;      // 44.16  下单价格
    private int orderStatus;        // 订单状态    -1 失败  0 代支付  1 已支付，待持仓  2 持仓中  3 平仓处理中  4 结算完成
    private long orderTime;         // 下单时间
    private double ratio;            // 汇率
    private String sign;             // $ 币种符号
    private int userId;
    private int varietyId;
    private String varietyName;
    private String varietyType;

    // 下面为平仓相关的属性
    private double unwindMarket;
    private double unwindPrice;     // 44.16  平仓价格
    private long unwindTime;        // 1499777803000  平仓时间
    private int unwindType;         // 1 用户平仓 2 超时平仓
    private double winOrLoss;       // 用户盈亏

    public int getBattleId() {
        return battleId;
    }

    public String getContractsCode() {
        return contractsCode;
    }

    public int getContractsId() {
        return contractsId;
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

    public int getMarketPoint() {
        return marketPoint;
    }

    public int getOptLogCount() {
        return optLogCount;
    }

    public double getUnwindMarket() {
        return unwindMarket;
    }

    public double getUnwindPrice() {
        return unwindPrice;
    }

    public long getUnwindTime() {
        return unwindTime;
    }

    public int getUnwindType() {
        return unwindType;
    }

    public double getWinOrLoss() {
        return winOrLoss;
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
