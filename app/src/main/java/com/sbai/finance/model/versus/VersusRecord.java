package com.sbai.finance.model.versus;

/**
 * Created by Administrator on 2017-06-20.
 */

public class VersusRecord {
    private int userId;
    private String userName;
    private String varietyName;

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

    public String getVarietyName() {
        return varietyName;
    }

    public void setVarietyName(String varietyName) {
        this.varietyName = varietyName;
    }
}
