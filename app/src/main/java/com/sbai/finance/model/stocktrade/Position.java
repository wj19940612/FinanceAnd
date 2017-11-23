package com.sbai.finance.model.stocktrade;

/**
 * 持仓
 */

public class Position {
    /**
     * 1实盘 2模拟盘 3活动模拟盘
     */
    public static final int TYPE_ACTUAL = 1;
    public static final int TYPE_SIMULATE = 2;
    public static final int TYPE_SIMULATE_ACTIVITY = 3;
    /**
     * id : 1
     * version : 1
     * positionType : 2
     * virtualType : 1
     * userId : 689
     * userAccount : jf100009
     * market : 4609
     * varietyCode : 002158
     * varietyName : 汉钟精机
     * avgBuyPrice : 14.043
     * totalQty : 4600
     * usableQty : 4600
     * activityCode : null
     * frozenQty : 0
     * createTime : 1508477842000
     * updateTime : 1510128262000
     * todayAvgPrice : 11.114285714285714
     */

    private int id;
    private int version;
    private int positionType;
    private int virtualType;
    private int userId;
    private String userAccount;
    private String market;
    private String varietyCode;
    private String varietyName;
    private double avgBuyPrice;
    private int totalQty;
    private int usableQty;
    private Object activityCode;
    private int frozenQty;
    private long createTime;
    private long updateTime;
    private double todayAvgPrice;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getPositionType() {
        return positionType;
    }

    public void setPositionType(int positionType) {
        this.positionType = positionType;
    }

    public int getVirtualType() {
        return virtualType;
    }

    public void setVirtualType(int virtualType) {
        this.virtualType = virtualType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getVarietyCode() {
        return varietyCode;
    }

    public void setVarietyCode(String varietyCode) {
        this.varietyCode = varietyCode;
    }

    public String getVarietyName() {
        return varietyName;
    }

    public void setVarietyName(String varietyName) {
        this.varietyName = varietyName;
    }

    public double getAvgBuyPrice() {
        return avgBuyPrice;
    }

    public void setAvgBuyPrice(double avgBuyPrice) {
        this.avgBuyPrice = avgBuyPrice;
    }

    public int getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(int totalQty) {
        this.totalQty = totalQty;
    }

    public int getUsableQty() {
        return usableQty;
    }

    public void setUsableQty(int usableQty) {
        this.usableQty = usableQty;
    }

    public Object getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(Object activityCode) {
        this.activityCode = activityCode;
    }

    public int getFrozenQty() {
        return frozenQty;
    }

    public void setFrozenQty(int frozenQty) {
        this.frozenQty = frozenQty;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public double getTodayAvgPrice() {
        return todayAvgPrice;
    }

    public void setTodayAvgPrice(double todayAvgPrice) {
        this.todayAvgPrice = todayAvgPrice;
    }
}
