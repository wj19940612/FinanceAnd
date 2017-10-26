package com.sbai.finance.model.arena;

/**
 * Created by ${wangJie} on 2017/10/25.
 * 竞技场的信息 标题 时间 可获得奖品等信息
 */

public class ArenaInfo {
    private String title;
    private long startTime;
    private long endTime;
    private String reward;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }
}
