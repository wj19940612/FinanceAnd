package com.sbai.finance.model.mine;

/**
 * Created by ${wangJie} on 2017/4/19.
 */

public class UserFansModel {

    /**
     * createTime : 71335
     * id : 60372
     * userId : 42286
     * userName : 测试内容o634
     * userPortrait : 测试内容6cro
     */

    private long createTime;
    private int id;
    private int userId;
    private String userName;
    private String userPortrait;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    @Override
    public String toString() {
        return "UserFansModel{" +
                "createTime=" + createTime +
                ", id=" + id +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userPortrait='" + userPortrait + '\'' +
                '}';
    }
}
