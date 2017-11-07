package com.sbai.finance.model.arena;

import com.sbai.finance.net.Client;

/**
 * Created by ${wangJie} on 2017/11/3.
 * {@link Client#/activity/prize/allPrizeRule.do}
 */

public class ArenaAwardExchangeRule {

    public static final int AWARD_EXCHANGE_RULE_TYPE_CRASH = 1;
    public static final int AWARD_EXCHANGE_RULE_TYPE_INGOT = 2;
    public static final int AWARD_EXCHANGE_RULE_TYPE_CREDIT = 3;
    public static final int AWARD_EXCHANGE_RULE_TYPE_ASSIGN_TASK = 11;
    public static final int AWARD_EXCHANGE_RULE_TYPE_SCORE = 12;
    public static final int AWARD_EXCHANGE_RULE_TYPE_RANK = 13;

    /**
     * activityCode : future_game
     * consume : 1
     * createTime : 1509362035000
     * desc : 总积分大于1200
     * id : 10
     * modifyTime : 1509362234000
     * name : 总积分
     * prizeCode : iphonex
     * prizeId : 11
     * ruleCode : iphonex_1
     * score : 1200
     * type : 12
     */

    private String activityCode;
    private int consume;   //兑奖时是否扣除score 1
    private long createTime;
    private String desc;    //兑换条件描述
    private int id;
    private long modifyTime;
    private String name;
    private String prizeCode; //奖品code
    private int prizeId;      //奖品id
    private String ruleCode;
    private String score;
    private int type;  //1: 现金，2：元宝，3:积分。 11:指定任务，12：得分，13，排行名次
    private String prizeName; //奖品名称

    public boolean isRanking() {
        return getType() == AWARD_EXCHANGE_RULE_TYPE_RANK;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public int getConsume() {
        return consume;
    }

    public void setConsume(int consume) {
        this.consume = consume;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrizeCode() {
        return prizeCode;
    }

    public void setPrizeCode(String prizeCode) {
        this.prizeCode = prizeCode;
    }

    public int getPrizeId() {
        return prizeId;
    }

    public void setPrizeId(int prizeId) {
        this.prizeId = prizeId;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPrizeName() {
        return prizeName;
    }

    public void setPrizeName(String prizeName) {
        this.prizeName = prizeName;
    }

    @Override
    public String toString() {
        return "ArenaAwardExchangeRule{" +
                "activityCode='" + activityCode + '\'' +
                ", consume=" + consume +
                ", createTime=" + createTime +
                ", desc='" + desc + '\'' +
                ", id=" + id +
                ", modifyTime=" + modifyTime +
                ", name='" + name + '\'' +
                ", prizeCode='" + prizeCode + '\'' +
                ", prizeId=" + prizeId +
                ", ruleCode='" + ruleCode + '\'' +
                ", score='" + score + '\'' +
                ", type=" + type +
                ", prizeName='" + prizeName + '\'' +
                '}';
    }
}
