package com.sbai.finance.model;

/**
 * Created by linrongfang on 2017/5/2.
 */
public class FutureIntroduce {

    /**
     * deliveryTime : 交割时间
     * everydayPriceMaxFluctuateLimit : 每日价格最大波动限制
     * id : 1
     * lowestMargin : 最低保证金
     * opsitionTime : 持仓时间
     * reportPriceUnit : 报价单位
     * tradeRegime : 交易制度
     * tradeTime : 交易时间
     * tradeType : 交易方式
     * tradeUnit : 交易单位
     * varietyId : 1
     * varietyName : 美原油
     * varietyType : 1
     */
    private String deliveryTime;
    private String everydayPriceMaxFluctuateLimit;
    private int id;
    private String lowestMargin;
    private String opsitionTime;
    private String reportPriceUnit;
    private String tradeRegime;
    private String tradeTime;
    private String tradeType;
    private String tradeUnit;
    private int varietyId;
    private String varietyName;
    private String varietyType;

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getEverydayPriceMaxFluctuateLimit() {
        return everydayPriceMaxFluctuateLimit;
    }

    public void setEverydayPriceMaxFluctuateLimit(String everydayPriceMaxFluctuateLimit) {
        this.everydayPriceMaxFluctuateLimit = everydayPriceMaxFluctuateLimit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLowestMargin() {
        return lowestMargin;
    }

    public void setLowestMargin(String lowestMargin) {
        this.lowestMargin = lowestMargin;
    }

    public String getOpsitionTime() {
        return opsitionTime;
    }

    public void setOpsitionTime(String opsitionTime) {
        this.opsitionTime = opsitionTime;
    }

    public String getReportPriceUnit() {
        return reportPriceUnit;
    }

    public void setReportPriceUnit(String reportPriceUnit) {
        this.reportPriceUnit = reportPriceUnit;
    }

    public String getTradeRegime() {
        return tradeRegime;
    }

    public void setTradeRegime(String tradeRegime) {
        this.tradeRegime = tradeRegime;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getTradeUnit() {
        return tradeUnit;
    }

    public void setTradeUnit(String tradeUnit) {
        this.tradeUnit = tradeUnit;
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
}
