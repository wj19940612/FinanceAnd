package com.sbai.finance.model.mine;

/**
 * Created by ${wangJie} on 2017/4/28.
 */

public class UserAttentionModel {

    /**
     * createTime : 20211
     * followUserId : 16765
     * followUserPortrait : 测试内容87i3
     * followuserName : 测试内容bgw7
     * id : 80825
     */

    private int createTime;
    //	关注用户id
    private int followUserId;
    private String followUserPortrait;
    private String followuserName;
    private int id;

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public int getFollowUserId() {
        return followUserId;
    }

    public void setFollowUserId(int followUserId) {
        this.followUserId = followUserId;
    }

    public String getFollowUserPortrait() {
        return followUserPortrait;
    }

    public void setFollowUserPortrait(String followUserPortrait) {
        this.followUserPortrait = followUserPortrait;
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

    @Override
    public String toString() {
        return "UserAttentionModel{" +
                "createTime=" + createTime +
                ", followUserId=" + followUserId +
                ", followUserPortrait='" + followUserPortrait + '\'' +
                ", followuserName='" + followuserName + '\'' +
                ", id=" + id +
                '}';
    }
}
