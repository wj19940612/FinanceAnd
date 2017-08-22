package com.sbai.finance.model.levelevaluation;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ${wangJie} on 2017/8/2.
 * 测评结果
 */

public class EvaluationResult implements Parcelable {


    //正确率
    private double allAccuracy;
    //完成时间
    private String createTime;
    //完成状态   0  未完成  1 完成
    private int finishStatus;
    private int id;
    // 0
    //等级 评测等级 0 未测  1 入门   2 初级  3 中级   4 高级 5 精通
    private int level;
    //超过多少人
    private double passPercent;
    //基本面分析 正确率
    private double baseAccuracy;
    //盈利能力 正确率
    private double profitAccuracy;
    //风险控制 正确率
    private double riskAccuracy;
    //技术分析 正确率
    private double skillAccuracy;
    //理论掌握 正确率
    private double theoryAccuracy;
    private int topicId;
    private String updateTime;
    private int userId;
    private int maxLevel; //最高测评等级

    private double totalCredit;

    public int getTotalCredit() {
        return (int) totalCredit;
    }

    public void setTotalCredit(double totalCredit) {
        this.totalCredit = totalCredit;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public double getAllAccuracy() {
        return allAccuracy;
    }

    public void setAllAccuracy(double allAccuracy) {
        this.allAccuracy = allAccuracy;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getFinishStatus() {
        return finishStatus;
    }

    public void setFinishStatus(int finishStatus) {
        this.finishStatus = finishStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getPassPercent() {
        return passPercent;
    }

    public void setPassPercent(double passPercent) {
        this.passPercent = passPercent;
    }

    public double getBaseAccuracy() {
        return baseAccuracy;
    }

    public void setBaseAccuracy(double baseAccuracy) {
        this.baseAccuracy = baseAccuracy;
    }

    public double getProfitAccuracy() {
        return profitAccuracy;
    }

    public void setProfitAccuracy(double profitAccuracy) {
        this.profitAccuracy = profitAccuracy;
    }

    public double getRiskAccuracy() {
        return riskAccuracy;
    }

    public void setRiskAccuracy(double riskAccuracy) {
        this.riskAccuracy = riskAccuracy;
    }

    public double getSkillAccuracy() {
        return skillAccuracy;
    }

    public void setSkillAccuracy(double skillAccuracy) {
        this.skillAccuracy = skillAccuracy;
    }

    public double getTheoryAccuracy() {
        return theoryAccuracy;
    }

    public void setTheoryAccuracy(double theoryAccuracy) {
        this.theoryAccuracy = theoryAccuracy;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "EvaluationResult{" +
                "allAccuracy=" + allAccuracy +
                ", createTime='" + createTime + '\'' +
                ", finishStatus=" + finishStatus +
                ", id=" + id +
                ", level=" + level +
                ", passPercent=" + passPercent +
                ", baseAccuracy=" + baseAccuracy +
                ", profitAccuracy=" + profitAccuracy +
                ", riskAccuracy=" + riskAccuracy +
                ", skillAccuracy=" + skillAccuracy +
                ", theoryAccuracy=" + theoryAccuracy +
                ", topicId=" + topicId +
                ", updateTime='" + updateTime + '\'' +
                ", userId=" + userId +
                ", maxLevel=" + maxLevel +
                '}';
    }

    /**
     * allAccuracy : 0.7
     * baseAccuracy : 0
     * createTime : 2017-08-04 20:35:08
     * finishStatus : 0
     * id : 11
     * level : 0
     * passPercent : 1
     * profitAccuracy : 0
     * riskAccuracy : 0
     * skillAccuracy : 0
     * theoryAccuracy : 0
     * topicId : 1
     * updateTime : 2017-08-05 15:06:40
     * userId : 800329
     */


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.allAccuracy);
        dest.writeString(this.createTime);
        dest.writeInt(this.finishStatus);
        dest.writeInt(this.id);
        dest.writeInt(this.level);
        dest.writeDouble(this.passPercent);
        dest.writeDouble(this.baseAccuracy);
        dest.writeDouble(this.profitAccuracy);
        dest.writeDouble(this.riskAccuracy);
        dest.writeDouble(this.skillAccuracy);
        dest.writeDouble(this.theoryAccuracy);
        dest.writeInt(this.topicId);
        dest.writeString(this.updateTime);
        dest.writeInt(this.userId);
        dest.writeInt(this.maxLevel);
    }

    public EvaluationResult() {
    }

    protected EvaluationResult(Parcel in) {
        this.allAccuracy = in.readDouble();
        this.createTime = in.readString();
        this.finishStatus = in.readInt();
        this.id = in.readInt();
        this.level = in.readInt();
        this.passPercent = in.readDouble();
        this.baseAccuracy = in.readDouble();
        this.profitAccuracy = in.readDouble();
        this.riskAccuracy = in.readDouble();
        this.skillAccuracy = in.readDouble();
        this.theoryAccuracy = in.readDouble();
        this.topicId = in.readInt();
        this.updateTime = in.readString();
        this.userId = in.readInt();
        this.maxLevel = in.readInt();
    }

    public static final Creator<EvaluationResult> CREATOR = new Creator<EvaluationResult>() {
        @Override
        public EvaluationResult createFromParcel(Parcel source) {
            return new EvaluationResult(source);
        }

        @Override
        public EvaluationResult[] newArray(int size) {
            return new EvaluationResult[size];
        }
    };
}
