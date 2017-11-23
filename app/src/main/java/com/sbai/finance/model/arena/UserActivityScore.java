package com.sbai.finance.model.arena;

import com.sbai.finance.net.Client;

/**
 * Created by ${wangJie} on 2017/10/30.
 * {@link Client# /activity/activity/getMyScore.do}
 * 竞技场个人得分
 */

public class UserActivityScore {

    /**
     * rank : 1
     * score : 2056
     * count : 56
     */

    private int rank;   //排行
    private int score;  //盈利（得分）
    private int totalCount;  //总场次

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return "UserActivityScore{" +
                "rank=" + rank +
                ", score=" + score +
                ", totalCount=" + totalCount +
                '}';
    }
}
