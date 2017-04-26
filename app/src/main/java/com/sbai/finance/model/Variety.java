package com.sbai.finance.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Variety implements Parcelable {

    public static final String VAR_FUTURE = "future";
    public static final String VAR_FOREX = "forex";
    public static final String VAR_STOCK = "stock";

    public static final String FUTURE_CHINA = "china";
    public static final String FUTURE_FOREIGN = "foreign";

    public static final int EXCHANGE_STATUS_CLOSE = 0;
    public static final int EXCHANGE_STATUS_OPEN = 1;

    public static final String EX_PRODUCT = "product";
    public static final String EX_FUND_TYPE = "fund";
    public static final String EX_PRODUCT_LIST = "productList";


    /**
     * baseline : 2
     * bigVarietyTypeCode : 测试内容65aj
     * contractsCode : ag1706
     * decimalScale : 0.2
     * displayMarketTimes : 06:00;05:00
     * exchangeId : 1
     * exchangeStatus : 1
     * flashChartPriceInterval : 14
     * marketPoint : 3
     * openMarketTime : 06:00;05:00
     * smallVarietyTypeCode : china
     * sort : 1
     * varietyId : 1
     * varietyName : 沪银
     * varietyType : ag
     */

    private int baseline;
    private String bigVarietyTypeCode;
    private String contractsCode;
    private double decimalScale;
    private String displayMarketTimes;
    private int exchangeId;
    private int exchangeStatus;
    private int flashChartPriceInterval;
    private int marketPoint;
    private String openMarketTime;
    private String smallVarietyTypeCode;
    private int sort;
    private int varietyId;
    private String varietyName;
    private String varietyType;

    public int getBaseline() {
        return baseline;
    }

    public void setBaseline(int baseline) {
        this.baseline = baseline;
    }

    public String getBigVarietyTypeCode() {
        return bigVarietyTypeCode;
    }

    public void setBigVarietyTypeCode(String bigVarietyTypeCode) {
        this.bigVarietyTypeCode = bigVarietyTypeCode;
    }

    public String getContractsCode() {
        return contractsCode;
    }

    public void setContractsCode(String contractsCode) {
        this.contractsCode = contractsCode;
    }

    public String getDisplayMarketTimes() {
        return displayMarketTimes;
    }

    public void setDisplayMarketTimes(String displayMarketTimes) {
        this.displayMarketTimes = displayMarketTimes;
    }

    public int getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(int exchangeId) {
        this.exchangeId = exchangeId;
    }

    public int getExchangeStatus() {
        return exchangeStatus;
    }

    public void setExchangeStatus(int exchangeStatus) {
        this.exchangeStatus = exchangeStatus;
    }

    public int getFlashChartPriceInterval() {
        return flashChartPriceInterval;
    }

    public void setFlashChartPriceInterval(int flashChartPriceInterval) {
        this.flashChartPriceInterval = flashChartPriceInterval;
    }

    public String getOpenMarketTime() {
        return openMarketTime;
    }

    public void setOpenMarketTime(String openMarketTime) {
        this.openMarketTime = openMarketTime;
    }

    public String getSmallVarietyTypeCode() {
        return smallVarietyTypeCode;
    }

    public void setSmallVarietyTypeCode(String smallVarietyTypeCode) {
        this.smallVarietyTypeCode = smallVarietyTypeCode;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getVarietyId() {
        return varietyId;
    }

    public void setVarietyId(int varietyId) {
        this.varietyId = varietyId;
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

    public int getPriceDecimalScale() {
        return marketPoint;
    }

    public double getLimitUpPercent() {
        return decimalScale;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.baseline);
        dest.writeString(this.bigVarietyTypeCode);
        dest.writeString(this.contractsCode);
        dest.writeDouble(this.decimalScale);
        dest.writeString(this.displayMarketTimes);
        dest.writeInt(this.exchangeId);
        dest.writeInt(this.exchangeStatus);
        dest.writeInt(this.flashChartPriceInterval);
        dest.writeInt(this.marketPoint);
        dest.writeString(this.openMarketTime);
        dest.writeString(this.smallVarietyTypeCode);
        dest.writeInt(this.sort);
        dest.writeInt(this.varietyId);
        dest.writeString(this.varietyName);
        dest.writeString(this.varietyType);
    }

    public Variety() {
    }

    protected Variety(Parcel in) {
        this.baseline = in.readInt();
        this.bigVarietyTypeCode = in.readString();
        this.contractsCode = in.readString();
        this.decimalScale = in.readDouble();
        this.displayMarketTimes = in.readString();
        this.exchangeId = in.readInt();
        this.exchangeStatus = in.readInt();
        this.flashChartPriceInterval = in.readInt();
        this.marketPoint = in.readInt();
        this.openMarketTime = in.readString();
        this.smallVarietyTypeCode = in.readString();
        this.sort = in.readInt();
        this.varietyId = in.readInt();
        this.varietyName = in.readString();
        this.varietyType = in.readString();
    }

    public static final Parcelable.Creator<Variety> CREATOR = new Parcelable.Creator<Variety>() {
        @Override
        public Variety createFromParcel(Parcel source) {
            return new Variety(source);
        }

        @Override
        public Variety[] newArray(int size) {
            return new Variety[size];
        }
    };

    @Override
    public String toString() {
        return "Variety{" +
                "baseline=" + baseline +
                ", bigVarietyTypeCode='" + bigVarietyTypeCode + '\'' +
                ", contractsCode='" + contractsCode + '\'' +
                ", decimalScale=" + decimalScale +
                ", displayMarketTimes='" + displayMarketTimes + '\'' +
                ", exchangeId=" + exchangeId +
                ", exchangeStatus=" + exchangeStatus +
                ", flashChartPriceInterval=" + flashChartPriceInterval +
                ", marketPoint=" + marketPoint +
                ", openMarketTime='" + openMarketTime + '\'' +
                ", smallVarietyTypeCode='" + smallVarietyTypeCode + '\'' +
                ", sort=" + sort +
                ", varietyId=" + varietyId +
                ", varietyName='" + varietyName + '\'' +
                ", varietyType='" + varietyType + '\'' +
                '}';
    }
}
