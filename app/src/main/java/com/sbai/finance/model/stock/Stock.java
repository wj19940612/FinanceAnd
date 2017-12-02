package com.sbai.finance.model.stock;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Modified by john on 29/11/2017
 * <p>
 * Description: 新的股票 model
 * <p>
 * APIs:
 */
public class Stock implements Parcelable {

    public static final String EXPEND = "expend";

    public static final int EXCHANGE_STATUS_CLOSE = 0;
    public static final int EXCHANGE_STATUS_OPEN = 1;

    public static final int OPTIONAL = 1;

    public static final String OPTIONAL_TYPE_STOCK = "stock";
    public static final String OPTIONAL_TYPE_PLATE = "plate";

    public static final int PRICE_SCALE = 2;

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
    private int varietyStatus;  //0停牌 1正常
    private int exchangeOpened;//0休市 1开市
    private int option; //0不是自选  1 自选
    private String exchangeCode;

    public String getExchangeCode() {
        return exchangeCode;
    }

    public boolean isOptional() {
        return option == 1;
    }

    public void setOption(int option) {
        this.option = option;
    }

    public void setExchangeOpened(int exchangeOpened) {
        this.exchangeOpened = exchangeOpened;
    }

    public int getOption() {
        return option;
    }

    public int getVarietyStatus() {
        return varietyStatus;
    }

    public int getExchangeOpened() {
        return exchangeOpened;
    }

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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.exchangeId);
        dest.writeInt(this.id);
        dest.writeString(this.stockType);
        dest.writeString(this.type);
        dest.writeString(this.varietyCode);
        dest.writeString(this.varietyName);
        dest.writeString(this.varietyType);
        dest.writeInt(this.varietyStatus);
        dest.writeInt(this.exchangeOpened);
        dest.writeInt(this.option);
        dest.writeString(this.exchangeCode);
    }

    public Stock() {
    }

    protected Stock(Parcel in) {
        this.exchangeId = in.readInt();
        this.id = in.readInt();
        this.stockType = in.readString();
        this.type = in.readString();
        this.varietyCode = in.readString();
        this.varietyName = in.readString();
        this.varietyType = in.readString();
        this.varietyStatus = in.readInt();
        this.exchangeOpened = in.readInt();
        this.option = in.readInt();
        this.exchangeCode = in.readString();
    }

    public static final Parcelable.Creator<Stock> CREATOR = new Parcelable.Creator<Stock>() {
        @Override
        public Stock createFromParcel(Parcel source) {
            return new Stock(source);
        }

        @Override
        public Stock[] newArray(int size) {
            return new Stock[size];
        }
    };
}
