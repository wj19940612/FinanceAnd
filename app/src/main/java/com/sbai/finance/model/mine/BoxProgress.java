package com.sbai.finance.model.mine;

/**
 * Created by Administrator on 2018\1\5 0005.
 */

public class BoxProgress {
    private int completeCount;  //完成任务的数量
    private int gainLast;       //最后一个宝箱是否领取
    private int gainThree;      //完成三个任务宝箱是否领取
    private int todayIntegral;  //今日获得的总积分

    public int getCompleteCount() {
        return completeCount;
    }

    public void setCompleteCount(int completeCount) {
        this.completeCount = completeCount;
    }

    public int getGainLast() {
        return gainLast;
    }

    public void setGainLast(int gainLast) {
        this.gainLast = gainLast;
    }

    public int getGainThree() {
        return gainThree;
    }

    public void setGainThree(int gainThree) {
        this.gainThree = gainThree;
    }

    public int getTodayIntegral() {
        return todayIntegral;
    }

    public void setTodayIntegral(int todayIntegral) {
        this.todayIntegral = todayIntegral;
    }
}
