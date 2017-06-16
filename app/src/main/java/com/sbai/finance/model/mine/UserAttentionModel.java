package com.sbai.finance.model.mine;

/**
 * Created by ${wangJie} on 2017/4/28.
 */

public class UserAttentionModel {

    /**
     * createTime : 1493654400000
     * followUserId : 71
     * followuserName : 用户213
     * id : 63
     * userId : 86
     * userName : 溺水的鱼
     * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1493707651242.png
     */

    private long createTime;
    private int followUserId;
    private String followUserPortrait;
    private String followuserName;
    private int id;
    private int userId;
    private String userName;
    private String userPortrait;
    //	是否互相关注 0以关注1未关注
    private int status;
    // 是否解除关注 0 解除 1 不解除  本地加的
    private int other;

    public int getOther() {
        return other;
    }

    public void setOther(int other) {
        this.other = other;
    }

    public boolean isRelieve() {
        return getOther() == 0;
    }

    public boolean isAttention() {
        return getStatus() == 0;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getFollowUserId() {
        return followUserId;
    }

    public void setFollowUserId(int followUserId) {
        this.followUserId = followUserId;
    }

    public String getFollowuserName() {
        return followuserName;
    }

    public void setFollowuserName(String followuserName) {
        this.followuserName = followuserName;
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

    public String getFollowUserPortrait() {
        return followUserPortrait;
    }

    public void setFollowUserPortrait(String followUserPortrait) {
        this.followUserPortrait = followUserPortrait;
    }

    @Override
    public String toString() {
        return "UserAttentionModel{" +
                "createTime=" + createTime +
                ", followUserId=" + followUserId +
                ", followUserPortrait='" + followUserPortrait + '\'' +
                ", followuserName='" + followuserName + '\'' +
                ", id=" + id +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userPortrait='" + userPortrait + '\'' +
                ", status=" + status +
                ", other=" + other +
                '}';
    }
}
