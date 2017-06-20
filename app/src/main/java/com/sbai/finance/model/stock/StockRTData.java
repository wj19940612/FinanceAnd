package com.sbai.finance.model.stock;

import android.os.Parcel;
import android.os.Parcelable;

import com.sbai.finance.utils.FinanceUtil;

/**
 * Created by linrongfang on 2017/6/16.
 */

public class StockRTData implements Parcelable {

    public static final String STATUS_HALT = "0";

    /**
     * askPrice : 9.07
     * askPrice2 : 9.08
     * askPrice3 : 9.09
     * askPrice4 : 9.1
     * askPrice5 : 9.11
     * askVolume : 801553
     * askVolume2 : 830046
     * askVolume3 : 733443
     * askVolume4 : 608901
     * askVolume5 : 352500
     * bidPrice : 9.06
     * bidPrice2 : 9.05
     * bidPrice3 : 9.04
     * bidPrice4 : 9.03
     * bidPrice5 : 9.02
     * bidVolume : 85800
     * bidVolume2 : 981401
     * bidVolume3 : 601400
     * bidVolume4 : 746900
     * bidVolume5 : 1021000
     * downLimitPrice : 8.14
     * exchangeId : 4609
     * highestPrice : 9.08
     * instrumentId : 000001
     * lastPrice : 9.06
     * lowestPrice : 9.03
     * openPrice : 9.04
     * preClsPrice : 9.04
     * preSetPrice : 9.04
     * settlePrice : 0
     * status : 1
     * tradeDay : 2017-06-16
     * turnover : 6.183961493E7
     * upDropPrice : 0.02
     * upDropSpeed : 0.0022123894
     * upLimitPrice : 9.94
     * upTime : 1497579588000
     * upTimeFormat : 2017-06-16 10:19:48
     * volume : 6827570
     */

    private String askPrice;
    private String askPrice2;
    private String askPrice3;
    private String askPrice4;
    private String askPrice5;
    private String askVolume;
    private String askVolume2;
    private String askVolume3;
    private String askVolume4;
    private String askVolume5;
    private String bidPrice;
    private String bidPrice2;
    private String bidPrice3;
    private String bidPrice4;
    private String bidPrice5;
    private String bidVolume;
    private String bidVolume2;
    private String bidVolume3;
    private String bidVolume4;
    private String bidVolume5;
    private String downLimitPrice;
    private String exchangeId;
    private String highestPrice;
    private String instrumentId;
    private String lastPrice;
    private String lowestPrice;
    private String openPrice;
    private String preClsPrice;
    private String preSetPrice;
    private String settlePrice;
    private String status;
    private String tradeDay;
    private String turnover;
    private String upDropPrice;
    private double upDropSpeed;
    private String upLimitPrice;
    private long upTime;
    private String upTimeFormat;
    private String volume;

    public String getAskPrice() {
        return FinanceUtil.accurateToString(askPrice);
    }

    public void setAskPrice(String askPrice) {
        this.askPrice = askPrice;
    }

    public String getAskPrice2() {
        return FinanceUtil.accurateToString(askPrice2);
    }

    public void setAskPrice2(String askPrice2) {
        this.askPrice2 = askPrice2;
    }

    public String getAskPrice3() {
        return FinanceUtil.accurateToString(askPrice3);
    }

    public void setAskPrice3(String askPrice3) {
        this.askPrice3 = askPrice3;
    }

    public String getAskPrice4() {
        return FinanceUtil.accurateToString(askPrice4);
    }

    public void setAskPrice4(String askPrice4) {
        this.askPrice4 = askPrice4;
    }

    public String getAskPrice5() {
        return FinanceUtil.accurateToString(askPrice5);
    }

    public void setAskPrice5(String askPrice5) {
        this.askPrice5 = askPrice5;
    }

    public String getAskVolume() {
        return askVolume;
    }

    public void setAskVolume(String askVolume) {
        this.askVolume = askVolume;
    }

    public String getAskVolume2() {
        return askVolume2;
    }

    public void setAskVolume2(String askVolume2) {
        this.askVolume2 = askVolume2;
    }

    public String getAskVolume3() {
        return askVolume3;
    }

    public void setAskVolume3(String askVolume3) {
        this.askVolume3 = askVolume3;
    }

    public String getAskVolume4() {
        return askVolume4;
    }

    public void setAskVolume4(String askVolume4) {
        this.askVolume4 = askVolume4;
    }

    public String getAskVolume5() {
        return askVolume5;
    }

    public void setAskVolume5(String askVolume5) {
        this.askVolume5 = askVolume5;
    }

    public String getBidPrice() {
        return FinanceUtil.accurateToString(bidPrice);
    }

    public void setBidPrice(String bidPrice) {
        this.bidPrice = bidPrice;
    }

    public String getBidPrice2() {
        return FinanceUtil.accurateToString(bidPrice2);
    }

    public void setBidPrice2(String bidPrice2) {
        this.bidPrice2 = bidPrice2;
    }

    public String getBidPrice3() {
        return FinanceUtil.accurateToString(bidPrice3);
    }

    public void setBidPrice3(String bidPrice3) {
        this.bidPrice3 = bidPrice3;
    }

    public String getBidPrice4() {
        return FinanceUtil.accurateToString(bidPrice4);
    }

    public void setBidPrice4(String bidPrice4) {
        this.bidPrice4 = bidPrice4;
    }

    public String getBidPrice5() {
        return FinanceUtil.accurateToString(bidPrice5);
    }

    public void setBidPrice5(String bidPrice5) {
        this.bidPrice5 = bidPrice5;
    }

    public String getBidVolume() {
        return bidVolume;
    }

    public void setBidVolume(String bidVolume) {
        this.bidVolume = bidVolume;
    }

    public String getBidVolume2() {
        return bidVolume2;
    }

    public void setBidVolume2(String bidVolume2) {
        this.bidVolume2 = bidVolume2;
    }

    public String getBidVolume3() {
        return bidVolume3;
    }

    public void setBidVolume3(String bidVolume3) {
        this.bidVolume3 = bidVolume3;
    }

    public String getBidVolume4() {
        return bidVolume4;
    }

    public void setBidVolume4(String bidVolume4) {
        this.bidVolume4 = bidVolume4;
    }

    public String getBidVolume5() {
        return bidVolume5;
    }

    public void setBidVolume5(String bidVolume5) {
        this.bidVolume5 = bidVolume5;
    }

    public String getDownLimitPrice() {
        return downLimitPrice;
    }

    public void setDownLimitPrice(String downLimitPrice) {
        this.downLimitPrice = downLimitPrice;
    }

    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getHighestPrice() {
        return FinanceUtil.accurateToString(highestPrice);
    }

    public void setHighestPrice(String highestPrice) {
        this.highestPrice = highestPrice;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getLastPrice() {
        return FinanceUtil.accurateToString(lastPrice);
    }

    public void setLastPrice(String lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getLowestPrice() {
        return FinanceUtil.accurateToString(lowestPrice);
    }

    public void setLowestPrice(String lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public String getOpenPrice() {
        return FinanceUtil.accurateToString(openPrice);
    }

    public void setOpenPrice(String openPrice) {
        this.openPrice = openPrice;
    }

    public String getPreClsPrice() {
        return preClsPrice;
    }

    public void setPreClsPrice(String preClsPrice) {
        this.preClsPrice = preClsPrice;
    }

    public String getPreSetPrice() {
        return FinanceUtil.accurateToString(preSetPrice);
    }

    public void setPreSetPrice(String preSetPrice) {
        this.preSetPrice = preSetPrice;
    }

    public String getSettlePrice() {
        return settlePrice;
    }

    public void setSettlePrice(String settlePrice) {
        this.settlePrice = settlePrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTradeDay() {
        return tradeDay;
    }

    public void setTradeDay(String tradeDay) {
        this.tradeDay = tradeDay;
    }

    public String getTurnover() {
        return turnover;
    }

    public void setTurnover(String turnover) {
        this.turnover = turnover;
    }

    public String getUpDropPrice() {
        return FinanceUtil.accurateToString(upDropPrice);
    }

    public void setUpDropPrice(String upDropPrice) {
        this.upDropPrice = upDropPrice;
    }

    public double getUpDropSpeed() {
        return upDropSpeed;
    }

    public void setUpDropSpeed(double upDropSpeed) {
        this.upDropSpeed = upDropSpeed;
    }

    public String getUpLimitPrice() {
        return upLimitPrice;
    }

    public void setUpLimitPrice(String upLimitPrice) {
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

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.askPrice);
        dest.writeString(this.askPrice2);
        dest.writeString(this.askPrice3);
        dest.writeString(this.askPrice4);
        dest.writeString(this.askPrice5);
        dest.writeString(this.askVolume);
        dest.writeString(this.askVolume2);
        dest.writeString(this.askVolume3);
        dest.writeString(this.askVolume4);
        dest.writeString(this.askVolume5);
        dest.writeString(this.bidPrice);
        dest.writeString(this.bidPrice2);
        dest.writeString(this.bidPrice3);
        dest.writeString(this.bidPrice4);
        dest.writeString(this.bidPrice5);
        dest.writeString(this.bidVolume);
        dest.writeString(this.bidVolume2);
        dest.writeString(this.bidVolume3);
        dest.writeString(this.bidVolume4);
        dest.writeString(this.bidVolume5);
        dest.writeString(this.downLimitPrice);
        dest.writeString(this.exchangeId);
        dest.writeString(this.highestPrice);
        dest.writeString(this.instrumentId);
        dest.writeString(this.lastPrice);
        dest.writeString(this.lowestPrice);
        dest.writeString(this.openPrice);
        dest.writeString(this.preClsPrice);
        dest.writeString(this.preSetPrice);
        dest.writeString(this.settlePrice);
        dest.writeString(this.status);
        dest.writeString(this.tradeDay);
        dest.writeString(this.turnover);
        dest.writeString(this.upDropPrice);
        dest.writeDouble(this.upDropSpeed);
        dest.writeString(this.upLimitPrice);
        dest.writeLong(this.upTime);
        dest.writeString(this.upTimeFormat);
        dest.writeString(this.volume);
    }

    public StockRTData() {
    }

    protected StockRTData(Parcel in) {
        this.askPrice = in.readString();
        this.askPrice2 = in.readString();
        this.askPrice3 = in.readString();
        this.askPrice4 = in.readString();
        this.askPrice5 = in.readString();
        this.askVolume = in.readString();
        this.askVolume2 = in.readString();
        this.askVolume3 = in.readString();
        this.askVolume4 = in.readString();
        this.askVolume5 = in.readString();
        this.bidPrice = in.readString();
        this.bidPrice2 = in.readString();
        this.bidPrice3 = in.readString();
        this.bidPrice4 = in.readString();
        this.bidPrice5 = in.readString();
        this.bidVolume = in.readString();
        this.bidVolume2 = in.readString();
        this.bidVolume3 = in.readString();
        this.bidVolume4 = in.readString();
        this.bidVolume5 = in.readString();
        this.downLimitPrice = in.readString();
        this.exchangeId = in.readString();
        this.highestPrice = in.readString();
        this.instrumentId = in.readString();
        this.lastPrice = in.readString();
        this.lowestPrice = in.readString();
        this.openPrice = in.readString();
        this.preClsPrice = in.readString();
        this.preSetPrice = in.readString();
        this.settlePrice = in.readString();
        this.status = in.readString();
        this.tradeDay = in.readString();
        this.turnover = in.readString();
        this.upDropPrice = in.readString();
        this.upDropSpeed = in.readDouble();
        this.upLimitPrice = in.readString();
        this.upTime = in.readLong();
        this.upTimeFormat = in.readString();
        this.volume = in.readString();
    }

    public static final Parcelable.Creator<StockRTData> CREATOR = new Parcelable.Creator<StockRTData>() {
        @Override
        public StockRTData createFromParcel(Parcel source) {
            return new StockRTData(source);
        }

        @Override
        public StockRTData[] newArray(int size) {
            return new StockRTData[size];
        }
    };
}
