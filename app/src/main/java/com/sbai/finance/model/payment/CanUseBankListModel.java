package com.sbai.finance.model.payment;

/**
 * Created by ${wangJie} on 2017/6/16.
 * 可使用的银行列表model
 */

public class CanUseBankListModel {

    /**
     * appIcon : https://hystock.oss-cn-qingdao.aliyuncs.com/ueditor/1486633661724033370.png
     * createTime : 1468230183000
     * icon : https://hystock.oss-cn-qingdao.aliyuncs.com/ueditor/1473647614249030116.png
     * name : 中国工商银行
     * updateTime : 1497095695000
     * id : 1
     * status : 1
     */

    //app图标存放位置
    private String appIcon;
    private long createTime;
    //银行图标
    private String icon;
    //银行名称
    private String name;
    private long updateTime;
    //银行ID
    private int id;
    //0  不可用 1 可用
    private int status;
    /**
     * payRule : {"0-99":"qtwxscan","100-600":"qtwxscan"}
     * limitSingle : 5000
     * limitDay : 100000
     */

    private String payRule;
    //单笔限额
    private int limitSingle;
    //单日限额
    private int limitDay;

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
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

    @Override
    public String toString() {
        return "CanUserBankListModel{" +
                "appIcon='" + appIcon + '\'' +
                ", createTime=" + createTime +
                ", icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                ", updateTime=" + updateTime +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    public String getPayRule() {
        return payRule;
    }

    public void setPayRule(String payRule) {
        this.payRule = payRule;
    }

    public int getLimitSingle() {
        return limitSingle;
    }

    public void setLimitSingle(int limitSingle) {
        this.limitSingle = limitSingle;
    }

    public int getLimitDay() {
        return limitDay;
    }

    public void setLimitDay(int limitDay) {
        this.limitDay = limitDay;
    }
}
