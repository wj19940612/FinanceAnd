package com.sbai.finance.model.arena;

import com.sbai.finance.net.Client;

/**
 * Created by ${wangJie} on 2017/10/26.
 {@link Client# /activity/activity/getRank.do}
 */

public class ArenaAwardRanking {

    private String avatar;

    private String name;

    private double profit;

    private int battleCount;

    private String award;

    private int ranking;

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public int getBattleCount() {
        return battleCount;
    }

    public void setBattleCount(int battleCount) {
        this.battleCount = battleCount;
    }

    public String getAward() {
        return award;
    }

    public void setAward(String award) {
        this.award = award;
    }
}
