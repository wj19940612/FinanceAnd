package com.sbai.finance.model.leaderboard;

/**
 *排行榜数据
 */

public class LeaderBoardRank {
    private String userPortrait;
    private String userName;
    private int rank;
    private int ingot;
    private int type;

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getIngot() {
        return ingot;
    }

    public void setIngot(int ingot) {
        this.ingot = ingot;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
