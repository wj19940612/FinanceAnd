package com.sbai.finance.model.stock;

/**
 * Modified by john on 29/11/2017
 * <p>
 * Description: 新的股票 model
 * <p>
 * APIs:
 */
public class Stock {

    /**
     * exchangeId : 2
     * id : 3037
     * stockType : PUB
     * type : stk
     * varietyCode : 000001
     * varietyName : 平安银行
     * varietyType : A
     */

    private int exchangeId;
    private int id;
    private String stockType; // 股票类型 (N新股，ST：ST 股，S：S 股，PUB：普通股)
    private String type; // 股票标识tye (stk: 股票，plate：板块 )
    private String varietyCode; // 代码
    private String varietyName;
    private String varietyType; // 股票类型（A：A 股，B：B 股，H：H 股，expend：指数，fund：基金，bonds：债券）

    public int getExchangeId() {
        return exchangeId;
    }

    public int getId() {
        return id;
    }

    public String getStockType() {
        return stockType;
    }

    public String getType() {
        return type;
    }

    public String getVarietyCode() {
        return varietyCode;
    }

    public String getVarietyName() {
        return varietyName;
    }

    public String getVarietyType() {
        return varietyType;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "exchangeId=" + exchangeId +
                ", id=" + id +
                ", stockType='" + stockType + '\'' +
                ", type='" + type + '\'' +
                ", varietyCode='" + varietyCode + '\'' +
                ", varietyName='" + varietyName + '\'' +
                ", varietyType='" + varietyType + '\'' +
                '}';
    }
}
