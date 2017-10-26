package com.sbai.finance.model.arena;

/**
 * Created by ${wangJie} on 2017/10/25.
 * 用户参加竞赛的状态
 */

public class UserBattleResult {

    public static final int JOIN_ARENA_STATUS_UN_JOIN = 0;//没有加入
    public static final int JOIN_ARENA_STATUS_JOINED = 1;//加入对战

    private int status; //加入对战状态  0 没有加入 1 加入

    private int ranking;   //排名

    private double profit;  //我的盈利

    private int battleCount; // 对战场次

    private String arenaIcon;

    public boolean userIsJoinArena() {
        return getStatus() == JOIN_ARENA_STATUS_JOINED;
    }

    public String getArenaIcon() {
        return "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1508933335479&di=4b7e020fac079462206c0a629fa7b91f&imgtype=0&src=http%3A%2F%2Fc.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F8c1001e93901213fe293621f5ee736d12e2e9554.jpg";
    }

    public void setArenaIcon(String arenaIcon) {
        this.arenaIcon = arenaIcon;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
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

    @Override
    public String toString() {
        return "UserBattleResult{" +
                "status=" + status +
                ", ranking=" + ranking +
                ", profit=" + profit +
                ", battleCount=" + battleCount +
                '}';
    }
}
