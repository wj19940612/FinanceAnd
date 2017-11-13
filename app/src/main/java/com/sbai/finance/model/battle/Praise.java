package com.sbai.finance.model.battle;

/**
 * Modified by john on 10/11/2017
 * <p>
 * Description 对战点赞数据结构:
 * <p>
 * APIs:
 */
public class Praise {
    private int battleId;           //对战记录id
    private int currentPraise;      //被点赞的用户当前的被赞数
    private int praiseUserId;       //被点赞的用户ID

    public int getBattleId() {
        return battleId;
    }

    public int getCurrentPraise() {
        return currentPraise;
    }

    public int getPraiseUserId() {
        return praiseUserId;
    }
}
