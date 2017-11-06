package com.sbai.finance.model.battle;

import com.sbai.finance.net.Client;
import com.sbai.finance.utils.FinanceUtil;

import java.util.List;

/**
 * Created by ${wangJie} on 2017/10/31.
 * {@link Client# /game/battle/myBattleGamed.do  GET 我的对战历史（期货对战）}
 * /game/acti/myHistoryList.do     竞技场
 */

public class FutureVersus {
    public static final int HAS_MORE = 0;
    private int end;
    private List<Battle> list;

    private double gold;      //获得金币
    private double integral;  //获得积分
    private double money;     //获得的人民币
    private int winCount;     // 成功次数
    private int lossCount;    //失败次数
    private int totalCount;   // 总的比赛次数

    public boolean hasMore() {
        return end == HAS_MORE;
    }

    public String getBattleWinRate() {
        if (totalCount == 0) {
            return FinanceUtil.formatFloorPercent(0);
        }
        double winRate = FinanceUtil.divide(getWinCount(), getTotalCount(), 5).doubleValue();
        return FinanceUtil.formatFloorPercent(winRate, 2);
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public List<Battle> getList() {
        return list;
    }

    public void setList(List<Battle> list) {
        this.list = list;
    }


    public long getGold() {
        return (long) gold;
    }

    public void setGold(double gold) {
        this.gold = gold;
    }

    public double getIntegral() {
        return integral;
    }

    public void setIntegral(double integral) {
        this.integral = integral;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }

    public int getLossCount() {
        return lossCount;
    }

    public void setLossCount(int lossCount) {
        this.lossCount = lossCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return "MyBattleResult{" +
                "end=" + end +
                ", list=" + list +
                ", gold=" + gold +
                ", integral=" + integral +
                ", money=" + money +
                ", winCount=" + winCount +
                ", lossCount=" + lossCount +
                ", totalCount=" + totalCount +
                '}';
    }
}
