package com.sbai.finance.model.stock;

import android.os.Parcel;
import android.os.Parcelable;

import com.sbai.finance.utils.FinanceUtil;

/**
 * Created by linrongfang on 2017/6/16.
 */

public class StockRTData implements Parcelable {

    public static final String STATUS_DELIST = "0";

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
    private String downLimitPrice; // 跌停板
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
    private String turnover; // 成交额
    private String upDropPrice;
    private double upDropSpeed; // 涨幅比例
    private String upLimitPrice; // 涨停板
    private long upTime;
    private String upTimeFormat;
    private String volume; // 成交量
    private String turnoverRate; // 换手率
    private String pe; // 市盈率
    private String volRate; // 量比
    private String totalShares; // 总股本
    private String marketValue; // 总市值
    // 振幅 =（最高价-最低价） / 昨收价


    public String getAskPrice() {
        return askPrice;
    }

    public String getAskPrice2() {
        return askPrice2;
    }

    public String getAskPrice3() {
        return askPrice3;
    }

    public String getAskPrice4() {
        return askPrice4;
    }

    public String getAskPrice5() {
        return askPrice5;
    }

    public String getAskVolume() {
        return askVolume;
    }

    public String getAskVolume2() {
        return askVolume2;
    }

    public String getAskVolume3() {
        return askVolume3;
    }

    public String getAskVolume4() {
        return askVolume4;
    }

    public String getAskVolume5() {
        return askVolume5;
    }

    public String getBidPrice() {
        return bidPrice;
    }

    public String getBidPrice2() {
        return bidPrice2;
    }

    public String getBidPrice3() {
        return bidPrice3;
    }

    public String getBidPrice4() {
        return bidPrice4;
    }

    public String getBidPrice5() {
        return bidPrice5;
    }

    public String getBidVolume() {
        return bidVolume;
    }

    public String getBidVolume2() {
        return bidVolume2;
    }

    public String getBidVolume3() {
        return bidVolume3;
    }

    public String getBidVolume4() {
        return bidVolume4;
    }

    public String getBidVolume5() {
        return bidVolume5;
    }

    public String getDownLimitPrice() {
        return downLimitPrice;
    }

    public String getExchangeId() {
        return exchangeId;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public String getHighestPrice() {
        return highestPrice;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public String getLowestPrice() {
        return lowestPrice;
    }

    public String getOpenPrice() {
        return openPrice;
    }

    public String getPreClsPrice() {
        if (preClsPrice != null) {
            return preClsPrice;
        }
        return getPreSetPrice();
    }

    public String getPreSetPrice() {
        return preSetPrice;
    }

    public String getSettlePrice() {
        return settlePrice;
    }

    public String getStatus() {
        return status;
    }

    public String getTradeDay() {
        return tradeDay;
    }

    public String getTurnover() {
        return turnover;
    }

    public String getUpDropPrice() {
        return upDropPrice;
    }

    public double getUpDropSpeed() {
        return upDropSpeed;
    }

    public String getUpLimitPrice() {
        return upLimitPrice;
    }

    public long getUpTime() {
        return upTime;
    }

    public String getUpTimeFormat() {
        return upTimeFormat;
    }

    public String getVolume() {
        return volume;
    }

    public String getTurnoverRate() {
        return turnoverRate;
    }

    public String getPe() {
        return pe;
    }

    public String getVolRate() {
        return volRate;
    }

    public String getTotalShares() {
        return totalShares;
    }

    public String getMarketValue() {
        return marketValue;
    }

    public String getAmplitude() {
        if (highestPrice != null && lowestPrice != null) {
            double amplitude = Double.parseDouble(highestPrice) - Double.parseDouble(lowestPrice);
            if (preClsPrice != null) {
                if (Double.parseDouble(preClsPrice) != 0) {
                    return FinanceUtil.formatToPercentage(amplitude / Double.parseDouble(preClsPrice));
                }
            } else {
                if (Double.parseDouble(preSetPrice) != 0) {
                    return FinanceUtil.formatToPercentage(amplitude / Double.parseDouble(preSetPrice));
                }
            }
        }
        return null;
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
        dest.writeString(this.turnoverRate);
        dest.writeString(this.pe);
        dest.writeString(this.volRate);
        dest.writeString(this.totalShares);
        dest.writeString(this.marketValue);
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
        this.turnoverRate = in.readString();
        this.pe = in.readString();
        this.volRate = in.readString();
        this.totalShares = in.readString();
        this.marketValue = in.readString();
    }

    public static final Creator<StockRTData> CREATOR = new Creator<StockRTData>() {
        @Override
        public StockRTData createFromParcel(Parcel source) {
            return new StockRTData(source);
        }

        @Override
        public StockRTData[] newArray(int size) {
            return new StockRTData[size];
        }
    };

    @Override
    public String toString() {
        return "StockRTData{" +
                "askPrice='" + askPrice + '\'' +
                ", askPrice2='" + askPrice2 + '\'' +
                ", askPrice3='" + askPrice3 + '\'' +
                ", askPrice4='" + askPrice4 + '\'' +
                ", askPrice5='" + askPrice5 + '\'' +
                ", askVolume='" + askVolume + '\'' +
                ", askVolume2='" + askVolume2 + '\'' +
                ", askVolume3='" + askVolume3 + '\'' +
                ", askVolume4='" + askVolume4 + '\'' +
                ", askVolume5='" + askVolume5 + '\'' +
                ", bidPrice='" + bidPrice + '\'' +
                ", bidPrice2='" + bidPrice2 + '\'' +
                ", bidPrice3='" + bidPrice3 + '\'' +
                ", bidPrice4='" + bidPrice4 + '\'' +
                ", bidPrice5='" + bidPrice5 + '\'' +
                ", bidVolume='" + bidVolume + '\'' +
                ", bidVolume2='" + bidVolume2 + '\'' +
                ", bidVolume3='" + bidVolume3 + '\'' +
                ", bidVolume4='" + bidVolume4 + '\'' +
                ", bidVolume5='" + bidVolume5 + '\'' +
                ", downLimitPrice='" + downLimitPrice + '\'' +
                ", exchangeId='" + exchangeId + '\'' +
                ", highestPrice='" + highestPrice + '\'' +
                ", instrumentId='" + instrumentId + '\'' +
                ", lastPrice='" + lastPrice + '\'' +
                ", lowestPrice='" + lowestPrice + '\'' +
                ", openPrice='" + openPrice + '\'' +
                ", preClsPrice='" + preClsPrice + '\'' +
                ", preSetPrice='" + preSetPrice + '\'' +
                ", settlePrice='" + settlePrice + '\'' +
                ", status='" + status + '\'' +
                ", tradeDay='" + tradeDay + '\'' +
                ", turnover='" + turnover + '\'' +
                ", upDropPrice='" + upDropPrice + '\'' +
                ", upDropSpeed=" + upDropSpeed +
                ", upLimitPrice='" + upLimitPrice + '\'' +
                ", upTime=" + upTime +
                ", upTimeFormat='" + upTimeFormat + '\'' +
                ", volume='" + volume + '\'' +
                ", turnoverRate='" + turnoverRate + '\'' +
                ", pe='" + pe + '\'' +
                ", volRate='" + volRate + '\'' +
                ", totalShares='" + totalShares + '\'' +
                ", marketValue='" + marketValue + '\'' +
                '}';
    }
}
