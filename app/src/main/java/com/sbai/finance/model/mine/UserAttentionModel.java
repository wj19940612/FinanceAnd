package com.sbai.finance.model.mine;

/**
 * Created by ${wangJie} on 2017/4/28.
 */

public class UserAttentionModel {


    /**
     * createTime : 44808
     * followUserId : 11880
     * followUserPortrait : 测试内容2431
     * followuserName : 测试内容0ax2
     * id : 43540
     */

    //	创建时间
    private long createTime;
    //关注用户id
    private int followUserId;
    //	关注用户头像
    private String followUserPortrait;
    //	关注用户名
    private String followuserName;
    private int id;

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
}
