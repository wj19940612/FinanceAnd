package com.sbai.finance.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Variety implements Parcelable {

    public static final String VAR_FUTURE = "future";
    public static final String VAR_FOREX = "forex";
    public static final String VAR_STOCK = "stock";

    public static final String FUTURE_CHINA = "china";
    public static final String FUTURE_FOREIGN = "foreign";
    public static final String STOCK_EXPONENT_SH = "1A0001";
    public static final String STOCK_EXPONENT_GE = "399006";
    public static final String STOCK_EXPONENT_SZ = "399001";
    public static final String STOCK_EXPONENT = "exponent";

    public static final int EXCHANGE_STATUS_CLOSE = 0;
    public static final int EXCHANGE_STATUS_OPEN = 1;

    public static final int OPTIONAL = 1;

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
    private int baseline; //	基线
    private String bigVarietyTypeCode; // 期货：future 外汇：forex 股票：stock
    private String contractsCode;   //	合约代码（只对期货有用）
    private double decimalScale;     //	分时图曲线比例
    private String displayMarketTimes;      //	可显示的开闭市时间
    private int exchangeId;            //	交易所id
    private int exchangeStatus;          //市场状态0：休市 1 开市
    private double flashChartPriceInterval;   //	闪电图区间
    private int marketPoint;                //	行情小数位数
    private String openMarketTime;            //交易所时间
    private String smallVarietyTypeCode; //期货小类 smallVarietyTypeCode china 国内 foreign 国外 //外汇小类 directPlate 直盘 noDirectPlate 非直盘 crossPlate 交叉盘 //股票 exponent 指数
    private int sort;               //	排序
    private int varietyId;
    private String varietyName;
    private String varietyType;
    private String exchangeCode;
    private int checkOptional;

    /**
     * decimalScale : 0.2
     * sign : $
     * flashChartPriceInterval : 14
     * beatFewPoints : 0.01
     * currency : USD
     * eachPointMoney : 1000
     * currencyUnit : 美元
     * ratio : 7.5
     */
   /* 游戏所需 */
    private String sign;  //	汇率符号
    private double beatFewPoints; //每次跳几个点
    private String currency;    //汇率单位
    private int eachPointMoney;  //每个点多少钱
    private String currencyUnit; //单位名称
    private double ratio;    //汇率


    public int getCheckOptional() {
        return checkOptional;
    }

    public void setCheckOptional(int checkOptional) {
        this.checkOptional = checkOptional;
    }

    public boolean isStock() {
        return getBigVarietyTypeCode().equalsIgnoreCase(VAR_STOCK);
    }

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

    public double getFlashChartPriceInterval() {
        return flashChartPriceInterval;
    }

    public void setFlashChartPriceInterval(double flashChartPriceInterval) {
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

    public int getPriceScale() {
        return marketPoint;
    }

    public double getLimitUpPercent() {
        return decimalScale;
    }

    public String getExchangeCode() {
        return exchangeCode;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public Variety() {
    }

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
                ", exchangeCode=" + exchangeCode +
                '}';
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
        dest.writeDouble(this.flashChartPriceInterval);
        dest.writeInt(this.marketPoint);
        dest.writeString(this.openMarketTime);
        dest.writeString(this.smallVarietyTypeCode);
        dest.writeInt(this.sort);
        dest.writeInt(this.varietyId);
        dest.writeString(this.varietyName);
        dest.writeString(this.varietyType);
        dest.writeString(this.exchangeCode);
    }

    protected Variety(Parcel in) {
        this.baseline = in.readInt();
        this.bigVarietyTypeCode = in.readString();
        this.contractsCode = in.readString();
        this.decimalScale = in.readDouble();
        this.displayMarketTimes = in.readString();
        this.exchangeId = in.readInt();
        this.exchangeStatus = in.readInt();
        this.flashChartPriceInterval = in.readDouble();
        this.marketPoint = in.readInt();
        this.openMarketTime = in.readString();
        this.smallVarietyTypeCode = in.readString();
        this.sort = in.readInt();
        this.varietyId = in.readInt();
        this.varietyName = in.readString();
        this.varietyType = in.readString();
        this.exchangeCode = in.readString();
    }

    public static final Creator<Variety> CREATOR = new Creator<Variety>() {
        @Override
        public Variety createFromParcel(Parcel source) {
            return new Variety(source);
        }

        @Override
        public Variety[] newArray(int size) {
            return new Variety[size];
        }
    };

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public double getBeatFewPoints() {
        return beatFewPoints;
    }

    public void setBeatFewPoints(double beatFewPoints) {
        this.beatFewPoints = beatFewPoints;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getEachPointMoney() {
        return eachPointMoney;
    }

    public void setEachPointMoney(int eachPointMoney) {
        this.eachPointMoney = eachPointMoney;
    }

    public String getCurrencyUnit() {
        return currencyUnit;
    }

    public void setCurrencyUnit(String currencyUnit) {
        this.currencyUnit = currencyUnit;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }
}
