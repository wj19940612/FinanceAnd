package com.sbai.finance.model.training;

/**
 * 训练记录
 */
public class TrainingRecord {

    /**
     * finish : 14
     * lastFinishTime : 2017-08-08 11:25:25
     * lastTarinTime : 2017-08-08 11:25:25
     * maxLevel : 1
     * socre : 5
     * userId: 130
     */

    private int finish;
    private String lastFinishTime;
    private String lastTarinTime;
    private int maxLevel;
    private int socre;
    private int userId;

    public int getFinish() {
        return finish;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    public String getLastFinishTime() {
        return lastFinishTime;
    }

    public void setLastFinishTime(String lastFinishTime) {
        this.lastFinishTime = lastFinishTime;
    }

    public String getLastTarinTime() {
        return lastTarinTime;
    }

    public void setLastTarinTime(String lastTarinTime) {
        this.lastTarinTime = lastTarinTime;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getSocre() {
        return socre;
    }

    public void setSocre(int socre) {
        this.socre = socre;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "TrainingRecord{" +
                "finish=" + finish +
                ", lastFinishTime='" + lastFinishTime + '\'' +
                ", lastTarinTime='" + lastTarinTime + '\'' +
                ", maxLevel=" + maxLevel +
                ", socre=" + socre +
                ", userId=" + userId +
                '}';
    }
}
