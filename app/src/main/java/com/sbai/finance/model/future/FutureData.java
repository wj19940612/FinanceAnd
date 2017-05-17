package com.sbai.finance.model.future;

import android.os.Parcel;
import android.os.Parcelable;

public class FutureData implements Parcelable {

    /**
     * askPrice : 49.34
     * askVolume : 67
     * bidPrice : 49.33
     * bidVolume : 50
     * downLimitPrice : 38.97
     * exchangeId : NYMEX
     * highestPrice : 49.63
     * instrumentId : CL1706
     * inventory : 609652
     * lastPrice : 49.34
     * lastVolume : 1
     * lowestPrice : 49.13
     * openPrice : 49.27
     * preClsPrice : 48.97
     * preInventory : 0
     * preSetPrice : 48.97
     * settlePrice : 48.97
     * tradeDay : 2017-04-28
     * turnover : 3313925.84
     * upDropPrice : 0.37
     * upDropSpeed : 0.0075556463
     * upLimitPrice : 58.97
     * upTime : 1493367653606
     * upTimeFormat : 2017-04-28 16:20:53.606
     * volume : 67135
     */

    private double askPrice;
    private int askVolume;
    private double bidPrice;
    private int bidVolume;
    private double downLimitPrice;
    private String exchangeId;
    private double highestPrice;
    private String instrumentId;
    private int inventory;
    private double lastPrice;
    private int lastVolume;
    private double lowestPrice;
    private double openPrice;
    private double preClsPrice;
    private int preInventory;
    private double preSetPrice;
    private double settlePrice;
    private String tradeDay;
    private double turnover;
    private double upDropPrice;
    private double upDropSpeed;
    private double upLimitPrice;
    private long upTime;
    private String upTimeFormat;
    private int volume;

    public double getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(double askPrice) {
        this.askPrice = askPrice;
    }

    public int getAskVolume() {
        return askVolume;
    }

    public void setAskVolume(int askVolume) {
        this.askVolume = askVolume;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public int getBidVolume() {
        return bidVolume;
    }

    public void setBidVolume(int bidVolume) {
        this.bidVolume = bidVolume;
    }

    public double getDownLimitPrice() {
        return downLimitPrice;
    }

    public void setDownLimitPrice(double downLimitPrice) {
        this.downLimitPrice = downLimitPrice;
    }

    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
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

    public int getInventory() {
        return inventory;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public int getLastVolume() {
        return lastVolume;
    }

    public void setLastVolume(int lastVolume) {
        this.lastVolume = lastVolume;
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

    public int getPreInventory() {
        return preInventory;
    }

    public void setPreInventory(int preInventory) {
        this.preInventory = preInventory;
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

    public double getTurnover() {
        return turnover;
    }

    public void setTurnover(double turnover) {
        this.turnover = turnover;
    }

    public double getUpDropPrice() {
        return upDropPrice;
    }

    public void setUpDropPrice(double upDropPrice) {
        this.upDropPrice = upDropPrice;
    }

    public double getUpDropSpeed() {
        return upDropSpeed;
    }

    public void setUpDropSpeed(double upDropSpeed) {
        this.upDropSpeed = upDropSpeed;
    }

    public double getUpLimitPrice() {
        return upLimitPrice;
    }

    public void setUpLimitPrice(double upLimitPrice) {
        this.upLimitPrice = upLimitPrice;
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

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.askPrice);
        dest.writeInt(this.askVolume);
        dest.writeDouble(this.bidPrice);
        dest.writeInt(this.bidVolume);
        dest.writeDouble(this.downLimitPrice);
        dest.writeString(this.exchangeId);
        dest.writeDouble(this.highestPrice);
        dest.writeString(this.instrumentId);
        dest.writeInt(this.inventory);
        dest.writeDouble(this.lastPrice);
        dest.writeInt(this.lastVolume);
        dest.writeDouble(this.lowestPrice);
        dest.writeDouble(this.openPrice);
        dest.writeDouble(this.preClsPrice);
        dest.writeInt(this.preInventory);
        dest.writeDouble(this.preSetPrice);
        dest.writeDouble(this.settlePrice);
        dest.writeString(this.tradeDay);
        dest.writeDouble(this.turnover);
        dest.writeDouble(this.upDropPrice);
        dest.writeDouble(this.upDropSpeed);
        dest.writeDouble(this.upLimitPrice);
        dest.writeLong(this.upTime);
        dest.writeString(this.upTimeFormat);
        dest.writeInt(this.volume);
    }

    public FutureData() {
    }

    protected FutureData(Parcel in) {
        this.askPrice = in.readDouble();
        this.askVolume = in.readInt();
        this.bidPrice = in.readDouble();
        this.bidVolume = in.readInt();
        this.downLimitPrice = in.readDouble();
        this.exchangeId = in.readString();
        this.highestPrice = in.readDouble();
        this.instrumentId = in.readString();
        this.inventory = in.readInt();
        this.lastPrice = in.readDouble();
        this.lastVolume = in.readInt();
        this.lowestPrice = in.readDouble();
        this.openPrice = in.readDouble();
        this.preClsPrice = in.readDouble();
        this.preInventory = in.readInt();
        this.preSetPrice = in.readDouble();
        this.settlePrice = in.readDouble();
        this.tradeDay = in.readString();
        this.turnover = in.readDouble();
        this.upDropPrice = in.readDouble();
        this.upDropSpeed = in.readDouble();
        this.upLimitPrice = in.readDouble();
        this.upTime = in.readLong();
        this.upTimeFormat = in.readString();
        this.volume = in.readInt();
    }

    public static final Parcelable.Creator<FutureData> CREATOR = new Parcelable.Creator<FutureData>() {
        @Override
        public FutureData createFromParcel(Parcel source) {
            return new FutureData(source);
        }

        @Override
        public FutureData[] newArray(int size) {
            return new FutureData[size];
        }
    };

    @Override
    public String toString() {
        return "FutureData{" +
                "askPrice=" + askPrice +
                ", askVolume=" + askVolume +
                ", bidPrice=" + bidPrice +
                ", bidVolume=" + bidVolume +
                ", downLimitPrice=" + downLimitPrice +
                ", exchangeId='" + exchangeId + '\'' +
                ", highestPrice=" + highestPrice +
                ", instrumentId='" + instrumentId + '\'' +
                ", inventory=" + inventory +
                ", lastPrice=" + lastPrice +
                ", lastVolume=" + lastVolume +
                ", lowestPrice=" + lowestPrice +
                ", openPrice=" + openPrice +
                ", preClsPrice=" + preClsPrice +
                ", preInventory=" + preInventory +
                ", preSetPrice=" + preSetPrice +
                ", settlePrice=" + settlePrice +
                ", tradeDay='" + tradeDay + '\'' +
                ", turnover=" + turnover +
                ", upDropPrice=" + upDropPrice +
                ", upDropSpeed=" + upDropSpeed +
                ", upLimitPrice=" + upLimitPrice +
                ", upTime=" + upTime +
                ", upTimeFormat='" + upTimeFormat + '\'' +
                ", volume=" + volume +
                '}';
    }
}
