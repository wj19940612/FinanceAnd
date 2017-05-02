package com.sbai.finance.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MarketData implements Parcelable {

    public static final String EX_MARKET_DATA = "marketData";

    /**
     * askPrice : 113.697
     * bidPrice : 113.677
     * dataSource : mt4
     * highestPrice : 113.839
     * instrumentId : USDJPY
     * lastPrice : 113.677
     * lowestPrice : 113.651
     * openPrice : 113.914
     * preClsPrice : 113.677
     * preSetPrice : 113.677
     * settlePrice : 113.677
     * tradeDay : 2017-03-08
     * upDropPrice : 0
     * upDropSpeed : 0
     * upTime : 1488943979000
     * upTimeFormat : 2017-03-08 11:32:59.000
     */
    private double askPrice; // 卖一价
    private double bidPrice;  // 买一价
    private String dataSource;
    private double highestPrice;  //当日最高价
    private String instrumentId;    // 合约代码
    private double lastPrice;
    private double lowestPrice;  //当日最低价
    private double openPrice;  //开盘价
    private double preClsPrice; //昨日收盘价
    private double preSetPrice; //昨日结算价
    private double settlePrice;  //结算价
    private String tradeDay;  // 交易日
    private int upDropPrice;    //涨跌值
    private int upDropSpeed;    //涨跌幅
    private long upTime;        // 行情时间戳
    private String upTimeFormat;
    private double basePrice; //基准货币对美元价格
    private double invoicePrice; //计价货币对美元价格

    public double getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(double askPrice) {
        this.askPrice = askPrice;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public double getHighestPrice() {
        return highestPrice;
    }

    public void setHighestPrice(double highestPrice) {
        this.highestPrice = highestPrice;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public double getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(double lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public double getPreClsPrice() {
        return preClsPrice;
    }

    public void setPreClsPrice(double preClsPrice) {
        this.preClsPrice = preClsPrice;
    }

    public double getPreSetPrice() {
        return preSetPrice;
    }

    public void setPreSetPrice(double preSetPrice) {
        this.preSetPrice = preSetPrice;
    }

    public double getSettlePrice() {
        return settlePrice;
    }

    public void setSettlePrice(double settlePrice) {
        this.settlePrice = settlePrice;
    }

    public String getTradeDay() {
        return tradeDay;
    }

    public void setTradeDay(String tradeDay) {
        this.tradeDay = tradeDay;
    }

    public int getUpDropPrice() {
        return upDropPrice;
    }

    public void setUpDropPrice(int upDropPrice) {
        this.upDropPrice = upDropPrice;
    }

    public int getUpDropSpeed() {
        return upDropSpeed;
    }

    public void setUpDropSpeed(int upDropSpeed) {
        this.upDropSpeed = upDropSpeed;
    }

    public long getUpTime() {
        return upTime;
    }

    public void setUpTime(long upTime) {
        this.upTime = upTime;
    }

    public String getUpTimeFormat() {
        return upTimeFormat;
    }

    public void setUpTimeFormat(String upTimeFormat) {
        this.upTimeFormat = upTimeFormat;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getInvoicePrice() {
        return invoicePrice;
    }

    public void setInvoicePrice(double invoicePrice) {
        this.invoicePrice = invoicePrice;
    }

    @Override
    public String toString() {
        return "MarketData{" +
                "askPrice=" + askPrice +
                ", bidPrice=" + bidPrice +
                ", dataSource='" + dataSource + '\'' +
                ", highestPrice=" + highestPrice +
                ", instrumentId='" + instrumentId + '\'' +
                ", lastPrice=" + lastPrice +
                ", lowestPrice=" + lowestPrice +
                ", openPrice=" + openPrice +
                ", preClsPrice=" + preClsPrice +
                ", preSetPrice=" + preSetPrice +
                ", settlePrice=" + settlePrice +
                ", tradeDay='" + tradeDay + '\'' +
                ", upDropPrice=" + upDropPrice +
                ", upDropSpeed=" + upDropSpeed +
                ", upTime=" + upTime +
                ", upTimeFormat='" + upTimeFormat + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.askPrice);
        dest.writeDouble(this.bidPrice);
        dest.writeString(this.dataSource);
        dest.writeDouble(this.highestPrice);
        dest.writeString(this.instrumentId);
        dest.writeDouble(this.lastPrice);
        dest.writeDouble(this.lowestPrice);
        dest.writeDouble(this.openPrice);
        dest.writeDouble(this.preClsPrice);
        dest.writeDouble(this.preSetPrice);
        dest.writeDouble(this.settlePrice);
        dest.writeString(this.tradeDay);
        dest.writeInt(this.upDropPrice);
        dest.writeInt(this.upDropSpeed);
        dest.writeLong(this.upTime);
        dest.writeString(this.upTimeFormat);
        dest.writeDouble(this.basePrice);
        dest.writeDouble(this.invoicePrice);
    }

    public MarketData() {
    }

    protected MarketData(Parcel in) {
        this.askPrice = in.readDouble();
        this.bidPrice = in.readDouble();
        this.dataSource = in.readString();
        this.highestPrice = in.readDouble();
        this.instrumentId = in.readString();
        this.lastPrice = in.readDouble();
        this.lowestPrice = in.readDouble();
        this.openPrice = in.readDouble();
        this.preClsPrice = in.readDouble();
        this.preSetPrice = in.readDouble();
        this.settlePrice = in.readDouble();
        this.tradeDay = in.readString();
        this.upDropPrice = in.readInt();
        this.upDropSpeed = in.readInt();
        this.upTime = in.readLong();
        this.upTimeFormat = in.readString();
        this.basePrice = in.readDouble();
        this.invoicePrice = in.readDouble();
    }

    public static final Creator<MarketData> CREATOR = new Creator<MarketData>() {
        @Override
        public MarketData createFromParcel(Parcel source) {
            return new MarketData(source);
        }

        @Override
        public MarketData[] newArray(int size) {
            return new MarketData[size];
        }
    };
}
