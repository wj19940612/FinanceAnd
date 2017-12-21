package com.sbai.finance.model.klinebattle;

/**
 * 对战用户
 */

public class BattleKlineUserInfo {

    private int userId;
    private String userName;
    private String userPortrait;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }
}
