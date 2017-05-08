package com.sbai.finance.model.mine;

import java.io.Serializable;

/**
 * Created by ${wangJie} on 2017/4/26.
 */

public class AttentionAndFansNumberModel implements Serializable{

    /**
     * attention : 72025
     * follower : 83261
     * reply : 44187
     * userId : 72820
     * viewpoint : 38516
     */
    //	关注数
    private int attention;
    //粉丝数
    private int follower;
    //回复数
    private int reply;
    //用户id
    private int userId;
    //观点消息
    private int viewpoint;

    public int getAttention() {
        return attention;
    }

    public void setAttention(int attention) {
        this.attention = attention;
    }

    public int getFollower() {
        return follower;
    }

    public void setFollower(int follower) {
        this.follower = follower;
    }

    public int getReply() {
        return reply;
    }

    public void setReply(int reply) {
        this.reply = reply;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getViewpoint() {
        return viewpoint;
    }

    public void setViewpoint(int viewpoint) {
        this.viewpoint = viewpoint;
    }

    @Override
    public String toString() {
        return "AttentionAndFansNumber{" +
                "attention=" + attention +
                ", follower=" + follower +
                ", reply=" + reply +
                ", userId=" + userId +
                ", viewpoint=" + viewpoint +
                '}';
    }

}
