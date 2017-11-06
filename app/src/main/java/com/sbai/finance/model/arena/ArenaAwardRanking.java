package com.sbai.finance.model.arena;

import com.sbai.finance.net.Client;

/**
 * Created by ${wangJie} on 2017/10/26.
 * {@link Client# /activity/activity/getRank.do}
 */

public class ArenaAwardRanking {
    //            "rank": 1,
//            "score": 2056.00,
//            "totalCount": 56，
//            "prizeName":奖品名称
    private int rank;
    private double score;
    private int totalCount;
    private String prizeName; //预计可获得奖品
    private String userName;
    private int userId;
    private String userPortrait;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getPrizeName() {
        return prizeName;
    }

    public void setPrizeName(String prizeName) {
        this.prizeName = prizeName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }
}
