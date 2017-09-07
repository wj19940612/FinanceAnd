package com.sbai.finance.model.fund;

/**
 * Created by ${wangJie} on 2017/6/19.
 */

public class BankLimit {


    /**
     * bankId : 1
     * createTime : 1497836717000
     * id : 5
     * limitDay : 100000
     * limitSingle : 5000
     * payRule : {"0-99":"qtwxscan","100-600":"qtwxscan"}
     * status : 1
     * updateTime : 1497836717000
     */

    //银行ID
    private int bankId;
    private long createTime;
    private int id;
    //   //每日限额
    private int limitDay;
    //单笔限额
    private int limitSingle;
    //支付规则
    private String payRule;
    private int status;
    private long updateTime;


    public int getBankId() {
        return bankId;
    }

    public void setBankId(int bankId) {
        this.bankId = bankId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLimitDay() {
        return limitDay;
    }

    public void setLimitDay(int limitDay) {
        this.limitDay = limitDay;
    }

    public int getLimitSingle() {
        return limitSingle;
    }

    public void setLimitSingle(int limitSingle) {
        this.limitSingle = limitSingle;
    }

    public String getPayRule() {
        return payRule;
    }

    public void setPayRule(String payRule) {
        this.payRule = payRule;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "BankLimit{" +
                "bankId=" + bankId +
                ", createTime=" + createTime +
                ", id=" + id +
                ", limitDay=" + limitDay +
                ", limitSingle=" + limitSingle +
                ", payRule='" + payRule + '\'' +
                ", status=" + status +
                ", updateTime=" + updateTime +
                '}';
    }
}
