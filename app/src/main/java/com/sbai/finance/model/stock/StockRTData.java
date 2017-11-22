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
    private String turnover; // 成交额
    private String upDropPrice;
    private double upDropSpeed; // 涨幅比例
    private String upLimitPrice;
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
        if (askPrice != null) {
            return FinanceUtil.formatWithScale(askPrice);
        }
        return null;
    }

    public String getAskPrice2() {
        if (askPrice2 != null) {
            return FinanceUtil.formatWithScale(askPrice2);
        }
        return null;
    }

    public String getAskPrice3() {
        if (askPrice3 != null) {
            return FinanceUtil.formatWithScale(askPrice3);
        }
        return null;
    }

    public String getAskPrice4() {
        if (askPrice4 != null) {
            return FinanceUtil.formatWithScale(askPrice4);
        }
        return null;
    }

    public String getAskPrice5() {
        if (askPrice5 != null) {
            return FinanceUtil.formatWithScale(askPrice5);
        }
        return null;
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
        if (bidPrice != null) {
            return FinanceUtil.formatWithScale(bidPrice);
        }
        return null;
    }

    public String getBidPrice2() {
        if (bidPrice2 != null) {
            return FinanceUtil.formatWithScale(bidPrice2);
        }
        return null;
    }

    public String getBidPrice3() {
        if (bidPrice3 != null) {
            return FinanceUtil.formatWithScale(bidPrice3);
        }
        return null;
    }

    public String getBidPrice4() {
        if (bidPrice4 != null) {
            return FinanceUtil.formatWithScale(bidPrice4);
        }
        return null;
    }

    public String getBidPrice5() {
        if (bidPrice5 != null) {
            return FinanceUtil.formatWithScale(bidPrice5);
        }
        return null;
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

    public String getHighestPrice() {
        if (highestPrice != null) {
            return FinanceUtil.formatWithScale(highestPrice);
        }
        return null;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public String getLastPrice() {
        if (lastPrice != null) {
            return FinanceUtil.formatWithScale(lastPrice);
        }
        return null;
    }

    public String getLowestPrice() {
        if (lowestPrice != null) {
            return FinanceUtil.formatWithScale(lowestPrice);
        }
        return null;
    }

    public String getOpenPrice() {
        if (openPrice != null) {
            return FinanceUtil.formatWithScale(openPrice);
        }
        return null;
    }

    public String getPreClsPrice() {
        if (preClsPrice != null) {
            return FinanceUtil.formatWithScale(preClsPrice);
        }
        return getPreSetPrice();
    }

    public String getPreSetPrice() {
        return FinanceUtil.formatWithScale(preSetPrice);
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
        if (turnover != null) {
            return FinanceUtil.addUnitWithoutSeparator(Double.parseDouble(turnover));
        }
        return null;
    }

    public String getUpDropPrice() {
        return FinanceUtil.formatWithScale(upDropPrice);
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
        if (volume != null) {
            return FinanceUtil.addUnitWithoutSeparator(Double.parseDouble(volume));
        }
        return null;
    }

    public String getTurnoverRate() {
        if (turnoverRate != null) {
            return FinanceUtil.formatToPercentage(Double.parseDouble(turnoverRate));
        }
        return null;
    }

    public String getPe() {
        if (pe != null) {
            return FinanceUtil.formatWithScale(pe);
        }
        return null;
    }

    public String getVolRate() {
        if (volRate != null) {
            return FinanceUtil.formatWithScale(volRate);
        }
        return null;
    }

    public String getTotalShares() {
        if (totalShares != null) {
            return FinanceUtil.addUnitWithoutSeparator(Double.parseDouble(totalShares));
        }
        return null;
    }

    public String getMarketValue() {
        if (marketValue != null) {
            return FinanceUtil.addUnitWithoutSeparator(Double.parseDouble(marketValue));
        }
        return null;
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
}
