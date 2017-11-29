package com.sbai.finance.model.local;

import static com.sbai.finance.utils.StockUtil.FEE_RATE;
import static com.sbai.finance.utils.StockUtil.STAMP_TAX_RATE;

/**
 * Modified by john on 27/11/2017
 * <p>
 * Description: 买入卖出时候用于存放数据的对象
 * <p>
 * APIs:
 */
public class StockOrder {

    public static final int DEPUTE_TYPE_MARKET_BUY = 6;
    public static final int DEPUTE_TYPE_MARKET_SELL = 7;
    public static final int DEPUTE_TYPE_ENTRUST_BUY = 8;
    public static final int DEPUTE_TYPE_ENTRUST_SELL = 9;

    private int positionType;
    private String userAccount;
    private String activityCode;
    private String varietyCode;
    private String varietyName;
    private int quantity;
    private double price;
    private int deputeType;
    private String signId;

    private StockOrder(Builder builder) {
        positionType = builder.positionType;
        userAccount = builder.userAccount;
        activityCode = builder.activityCode;
        varietyCode = builder.varietyCode;
        varietyName = builder.varietyName;
        quantity = builder.quantity;
        price = builder.price;
        deputeType = builder.deputeType;
        signId = builder.signId;
    }

    public String getVarietyCode() {
        return varietyCode;
    }

    public String getVarietyName() {
        return varietyName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public int getDeputeType() {
        return deputeType;
    }

    public double getFee() {
        if (deputeType == DEPUTE_TYPE_ENTRUST_SELL) {
            return price * quantity * FEE_RATE + price * quantity * STAMP_TAX_RATE;
        }
        return price * quantity * FEE_RATE;
    }

    public double getValue() {
        return getFee() + price * quantity;
    }

    public static final class Builder {
        private int positionType;
        private String userAccount;
        private String activityCode;
        private String varietyCode;
        private String varietyName;
        private int quantity;
        private double price;
        private int deputeType;
        private String signId;

        public Builder() {
        }

        public Builder positionType(int val) {
            positionType = val;
            return this;
        }

        public Builder userAccount(String val) {
            userAccount = val;
            return this;
        }

        public Builder activityCode(String val) {
            activityCode = val;
            return this;
        }

        public Builder varietyCode(String val) {
            varietyCode = val;
            return this;
        }

        public Builder varietyName(String val) {
            varietyName = val;
            return this;
        }

        public Builder quantity(int val) {
            quantity = val;
            return this;
        }

        public Builder price(double val) {
            price = val;
            return this;
        }

        public Builder deputeType(int val) {
            deputeType = val;
            return this;
        }

        public Builder signId(String val) {
            signId = val;
            return this;
        }

        public StockOrder build() {
            return new StockOrder(this);
        }
    }
}
