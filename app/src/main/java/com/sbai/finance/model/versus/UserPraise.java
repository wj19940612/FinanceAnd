package com.sbai.finance.model.versus;

/**
 * Created by linrongfang on 2017/6/28.
 */

public class UserPraise {
    int battleId;
    int currentPraise;
    int praiseUserId;

    public int getBattleId() {
        return battleId;
    }

    public void setBattleId(int battleId) {
        this.battleId = battleId;
    }

    public int getCurrentPraise() {
        return currentPraise;
    }

    public void setCurrentPraise(int currentPraise) {
        this.currentPraise = currentPraise;
    }

    public int getPraiseUserId() {
        return praiseUserId;
    }

    public void setPraiseUserId(int praiseUserId) {
        this.praiseUserId = praiseUserId;
    }
}
