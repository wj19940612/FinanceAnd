package com.sbai.finance.model.studyroom;

/**
 * 我的学一学
 */

public class MyStudyInfo {

    /**
     * holdStudy : 1
     * holdStudyMax : 3
     * lastTime : 2017-08-06 23:46:27
     * learn : 0
     * modifyTime : 2017-08-06 23:45:26
     * totalReward : 60
     * totalStudy : 6
     * userId : 167
     */

    private int holdStudy;
    private int holdStudyMax;
    private String lastTime;
    private int learn;
    private String modifyTime;
    private int totalReward;
    private int totalStudy;
    private int userId;

    public int getHoldStudy() {
        return holdStudy;
    }

    public void setHoldStudy(int holdStudy) {
        this.holdStudy = holdStudy;
    }

    public int getHoldStudyMax() {
        return holdStudyMax;
    }

    public void setHoldStudyMax(int holdStudyMax) {
        this.holdStudyMax = holdStudyMax;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public int getLearn() {
        return learn;
    }

    public void setLearn(int learn) {
        this.learn = learn;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public int getTotalReward() {
        return totalReward;
    }

    public void setTotalReward(int totalReward) {
        this.totalReward = totalReward;
    }

    public int getTotalStudy() {
        return totalStudy;
    }

    public void setTotalStudy(int totalStudy) {
        this.totalStudy = totalStudy;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
