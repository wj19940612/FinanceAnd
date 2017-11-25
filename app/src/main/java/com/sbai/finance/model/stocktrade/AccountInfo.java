package com.sbai.finance.model.stocktrade;

/**
 * 股票账户
 */

public class AccountInfo {

    /**
     * account : jf100009
     * accountName : 米宝104123
     * active : 1
     * createTime : 1508472179000
     * frozen : 0
     * fund : 1148775.72
     * id : 16
     * status : 1
     * totalProfit : -3647.501571
     * type : 2
     * updateTime : 1511508467000
     * usableMoney : 645802.390429
     * userId : 689
     * version : 70277
     */

    private String account;
    private String accountName;
    private int active;
    private long createTime;
    private int frozen;
    private double fund;
    private int id;
    private int status;
    private double totalProfit;
    private int type;
    private long updateTime;
    private double usableMoney;
    private int userId;
    private int version;
    private String activityCode;
    private String activityName;

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getFrozen() {
        return frozen;
    }

    public void setFrozen(int frozen) {
        this.frozen = frozen;
    }

    public double getFund() {
        return fund;
    }

    public void setFund(double fund) {
        this.fund = fund;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(double totalProfit) {
        this.totalProfit = totalProfit;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public double getUsableMoney() {
        return usableMoney;
    }

    public void setUsableMoney(double usableMoney) {
        this.usableMoney = usableMoney;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
