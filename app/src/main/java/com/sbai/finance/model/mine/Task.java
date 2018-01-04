package com.sbai.finance.model.mine;

/**
 * Created by Administrator on 2018\1\4 0004.
 */

public class Task {
    public static final String RULE_FUTURE_BATTLE = "futureswar01"; //期货对战
    public static final String RULE_KLINE_BATTLE = "kline01"; //K线对战
    public static final String RULE_RADIO = "radio01"; //收听电台
    public static final String RULE_GUESS = "guessstock01"; //参与猜大盘
    public static final String RULE_FUTURE_BATTLE = "futureswar01"; //姐说提问
    public static final String RULE_FUTURE_BATTLE = "futureswar01"; //期货对战
    public static final String

    private int completeCount;  //需要完成的次数
    private long createTime;
    private int gain;           //是否已经领取积分
    private int id;
    private int integral;       //可获取积分
    private String jumpContent; //跳转类型
    private long modifyTime;
    private String ruleCode;    //规则code
    private String ruleDetail;
    private String ruleName;    //规则名称
    private int sorted;
    private int taskCount;      //已经完成的次数
    private String taskName;    //任务名称
    private int taskType;       //任务类型   0-新手任务  1-日常任务
    private int typeStatus;

    public int getCompleteCount() {
        return completeCount;
    }

    public void setCompleteCount(int completeCount) {
        this.completeCount = completeCount;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getGain() {
        return gain;
    }

    public void setGain(int gain) {
        this.gain = gain;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public String getJumpContent() {
        return jumpContent;
    }

    public void setJumpContent(String jumpContent) {
        this.jumpContent = jumpContent;
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

    public String getRuleDetail() {
        return ruleDetail;
    }

    public void setRuleDetail(String ruleDetail) {
        this.ruleDetail = ruleDetail;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public int getSorted() {
        return sorted;
    }

    public void setSorted(int sorted) {
        this.sorted = sorted;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public int getTypeStatus() {
        return typeStatus;
    }

    public void setTypeStatus(int typeStatus) {
        this.typeStatus = typeStatus;
    }
}
