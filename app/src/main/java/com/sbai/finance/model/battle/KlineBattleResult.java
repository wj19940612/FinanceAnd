package com.sbai.finance.model.battle;

import java.util.List;

/**
 * Created by Administrator on 2017\12\13 0013.
 */

public class KlineBattleResult {

    private List<Ranking> ranking;
    private String battleStockCode;
    private String battleStockName;
    private String battleStockStartTime;
    private String battleStockEndTime;
    private double rise;

    public String getBattleStockCode() {
        return battleStockCode;
    }

    public void setBattleStockCode(String battleStockCode) {
        this.battleStockCode = battleStockCode;
    }

    public String getBattleStockName() {
        return battleStockName;
    }

    public void setBattleStockName(String battleStockName) {
        this.battleStockName = battleStockName;
    }

    public String getBattleStockStartTime() {
        return battleStockStartTime;
    }

    public void setBattleStockStartTime(String battleStockStartTime) {
        this.battleStockStartTime = battleStockStartTime;
    }

    public String getBattleStockEndTime() {
        return battleStockEndTime;
    }

    public void setBattleStockEndTime(String battleStockEndTime) {
        this.battleStockEndTime = battleStockEndTime;
    }

    public double getRise() {
        return rise;
    }

    public void setRise(double rise) {
        this.rise = rise;
    }

    public List<Ranking> getRanking() {
        return ranking;
    }

    public void setRanking(List<Ranking> ranking) {
        this.ranking = ranking;
    }

    public class Ranking{
        private String userPortrait;
        private int money;
        private int draw;//平局标记 1-不是平局 0-是平局
        private int sort;//排名
        private String userName;
        private double profit;
        private int userId;//用户Id

        public String getUserPortrait() {
            return userPortrait;
        }

        public void setUserPortrait(String userPortrait) {
            this.userPortrait = userPortrait;
        }

        public int getMoney() {
            return money;
        }

        public void setMoney(int money) {
            this.money = money;
        }

        public int getDraw() {
            return draw;
        }

        public void setDraw(int draw) {
            this.draw = draw;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public double getProfit() {
            return profit;
        }

        public void setProfit(double profit) {
            this.profit = profit;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }
    }

}
