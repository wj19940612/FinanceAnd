package com.sbai.finance.model.arena;

import com.sbai.finance.net.Client;

/**
 * Created by ${wangJie} on 2017/10/30.
 * {@link Client #activity/prize/whatCanExchange.do}
 * 活动中本人能兑换什么（hlei）
 */

public class ArenaActivityAwardInfo {

    public static final int ARENA_ACTIVITY_RUNNING = 0;
    public static final int ARENA_ACTIVITY_CAN_EXCHANGE_REWARD = 1;
    public static final int ARENA_ACTIVITY_ALREADY_EXCHANGE_REWARD = 2;

    private static final int AWAED_TYPE_PLATFORM_CURRENCY = 1;
    private static final int AWAED_TYPE_ENTITY_PRODUCT = 2;
    private static final int AWARD_TYPE_VIRTUAL_PRODUCT = 3;

    /**
     * activityCode : future_game
     * amount : 1
     * cost : 10000
     * createTime : 1509334207000
     * dayAmount : 1
     * daysLimit : 1
     * exchanged : 0
     * id : 5
     * modifyTime : 1509334384000
     * prizeCode : iponex
     * prizeName : iphonex
     * prizeSUrl : www.iponex.com.smallpic
     * prizeUrl : www.iponex.com
     * sort : 1
     * status : 1
     * totalAmount : 1
     * type : 2
     * userLimit : 1
     */

    private String activityCode;
    private int amount;         //可兑换数量
    private int cost;           //商品成本
    private long createTime;
    private int dayAmount;      //每日可兑换数量
    private int daysLimit;
    private int exchanged;
    private int id;              //奖品id
    private long modifyTime;
    private String prizeCode;    //奖品code
    private String prizeName;    //奖品名称
    private String prizeSUrl;    //奖品小图
    private String prizeUrl;     //奖品图片
    private int sort;
    private int status;          // 0 下jia  1上架
    private int totalAmount;     //总计可兑换数量
    private int type;            //类型：1平台货币2、实物3、虚拟奖品
    private int userLimit;

    public boolean awardIsVirtual() {
        return getType() == AWARD_TYPE_VIRTUAL_PRODUCT;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getDayAmount() {
        return dayAmount;
    }

    public void setDayAmount(int dayAmount) {
        this.dayAmount = dayAmount;
    }

    public int getDaysLimit() {
        return daysLimit;
    }

    public void setDaysLimit(int daysLimit) {
        this.daysLimit = daysLimit;
    }

    public int getExchanged() {
        return exchanged;
    }

    public void setExchanged(int exchanged) {
        this.exchanged = exchanged;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getPrizeCode() {
        return prizeCode;
    }

    public void setPrizeCode(String prizeCode) {
        this.prizeCode = prizeCode;
    }

    public String getPrizeName() {
        return prizeName;
    }

    public void setPrizeName(String prizeName) {
        this.prizeName = prizeName;
    }

    public String getPrizeSUrl() {
        return prizeSUrl;
    }

    public void setPrizeSUrl(String prizeSUrl) {
        this.prizeSUrl = prizeSUrl;
    }

    public String getPrizeUrl() {
        return prizeUrl;
    }

    public void setPrizeUrl(String prizeUrl) {
        this.prizeUrl = prizeUrl;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserLimit() {
        return userLimit;
    }

    public void setUserLimit(int userLimit) {
        this.userLimit = userLimit;
    }

    @Override
    public String toString() {
        return "ArenaActivityAwardInfo{" +
                "activityCode='" + activityCode + '\'' +
                ", amount=" + amount +
                ", cost=" + cost +
                ", createTime=" + createTime +
                ", dayAmount=" + dayAmount +
                ", daysLimit=" + daysLimit +
                ", exchanged=" + exchanged +
                ", id=" + id +
                ", modifyTime=" + modifyTime +
                ", prizeCode='" + prizeCode + '\'' +
                ", prizeName='" + prizeName + '\'' +
                ", prizeSUrl='" + prizeSUrl + '\'' +
                ", prizeUrl='" + prizeUrl + '\'' +
                ", sort=" + sort +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", type=" + type +
                ", userLimit=" + userLimit +
                '}';
    }
}
