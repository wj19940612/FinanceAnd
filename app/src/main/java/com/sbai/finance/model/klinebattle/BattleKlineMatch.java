package com.sbai.finance.model.klinebattle;

import java.util.List;

/**
 * 匹配推送
 */

public class BattleKlineMatch {
    private int code;
    private int battleId;
    private int battleStatus;
    private String battleType;
    private List<BattleKlineUserInfo> userMatchList;

    public int getBattleId() {
        return battleId;
    }

    public void setBattleId(int battleId) {
        this.battleId = battleId;
    }

    public int getBattleStatus() {
        return battleStatus;
    }

    public void setBattleStatus(int battleStatus) {
        this.battleStatus = battleStatus;
    }

    public String getBattleType() {
        return battleType;
    }

    public void setBattleType(String battleType) {
        this.battleType = battleType;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<BattleKlineUserInfo> getUserMatchList() {
        return userMatchList;
    }

    public void setUserMatchList(List<BattleKlineUserInfo> userMatchList) {
        this.userMatchList = userMatchList;
    }
}
