package com.sbai.finance.model.arena;

import com.sbai.finance.net.Client;

/**
 * Created by ${wangJie} on 2017/11/1.
 * {@link Client# /activity/activity/findApplyRule.do}
 */

public class ArenaApplyRule {

    public static final int PAY_FUND_TYPE_INGOT = 1;
    public static final int PAY_FUND_TYPE_CREDIT = 2;
    public static final int PAY_FUND_TYPE_CRASH = 3;

    /**
     * activityCode : future_game
     * createTime : 1509349358000
     * deleted : 0
     * endTime : 1509349450000
     * id : 15
     * includeRank : 0
     * includeScore : 0
     * modifyTime : 1509349545000
     * ruleCode : pre_entered1
     * ruleName : 期货报名
     * ruleParam : { money:1.0;moneyType:1;count:999}
     * score : 0
     * stage : 1
     * startTime : 1509349437000
     * taskDaysLimit : 99
     * taskTotalLimit : 999
     */

    private String activityCode;
    private long createTime;
    private int deleted;          // 是否删除 0否1是
    private long endTime;         //规则结束时间
    private int id;
    private int includeRank;      //是否参加排名
    private int includeScore;     //是否计分
    private long modifyTime;
    private String ruleCode;      //规则code
    private String ruleName;
    private String ruleParam;
    private int score;
    private int stage;            //活动区间
    private long startTime;
    private int taskDaysLimit;
    private int taskTotalLimit;

    private double money;     //报名费用
    private int moneyType;    //1为元宝，2为积分 3为现金，

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getMoneyType() {
        return moneyType;
    }

    public void setMoneyType(int moneyType) {
        this.moneyType = moneyType;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIncludeRank() {
        return includeRank;
    }

    public void setIncludeRank(int includeRank) {
        this.includeRank = includeRank;
    }

    public int getIncludeScore() {
        return includeScore;
    }

    public void setIncludeScore(int includeScore) {
        this.includeScore = includeScore;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleParam() {
        return ruleParam;
    }

    public void setRuleParam(String ruleParam) {
        this.ruleParam = ruleParam;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getTaskDaysLimit() {
        return taskDaysLimit;
    }

    public void setTaskDaysLimit(int taskDaysLimit) {
        this.taskDaysLimit = taskDaysLimit;
    }

    public int getTaskTotalLimit() {
        return taskTotalLimit;
    }

    public void setTaskTotalLimit(int taskTotalLimit) {
        this.taskTotalLimit = taskTotalLimit;
    }
}
