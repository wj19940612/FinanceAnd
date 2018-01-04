package com.sbai.finance.model.anchor;

import java.io.Serializable;

/**
 * 小姐姐列表
 */

public class Anchor implements Serializable {

    /**
     * briefingSound : aaa
     * portrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1501549863096062105.png
     * roleType : 0
     * totalAttention : 0
     * createTime : 1500954915000
     * totalPrise : 1
     * totalAward : 15
     * totalListen : 4
     * name : 草榴姐
     * online : 0
     * id : 6
     * brifeingText : aaaa
     * status : 0
     */

    private String briefingSound;
    private String portrait;
    private int roleType;
    private int totalAttention;
    private long createTime;
    private int totalPrise;
    private int totalAward;
    private int totalListen;
    private String name;
    private int online;
    private int id;
    private String briefingText;
    private int status;
    private int soundTime;
    private int isAttention;

    public int isAttention() {
        return isAttention;
    }

    public void setAttention(int attention) {
        isAttention = attention;
    }

    public int getSoundTime() {
        return soundTime;
    }

    public void setSoundTime(int soundTime) {
        this.soundTime = soundTime;
    }

    public String getBriefingSound() {
        return briefingSound;
    }

    public void setBriefingSound(String briefingSound) {
        this.briefingSound = briefingSound;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public int getRoleType() {
        return roleType;
    }

    public void setRoleType(int roleType) {
        this.roleType = roleType;
    }

    public int getTotalAttention() {
        return totalAttention;
    }

    public void setTotalAttention(int totalAttention) {
        this.totalAttention = totalAttention;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getTotalPrise() {
        return totalPrise;
    }

    public void setTotalPrise(int totalPrise) {
        this.totalPrise = totalPrise;
    }

    public int getTotalAward() {
        return totalAward;
    }

    public void setTotalAward(int totalAward) {
        this.totalAward = totalAward;
    }

    public int getTotalListen() {
        return totalListen;
    }

    public void setTotalListen(int totalListen) {
        this.totalListen = totalListen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBriefingText() {
        return briefingText;
    }

    public void setBriefingText(String briefingText) {
        this.briefingText = briefingText;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Miss{" +
                "briefingSound='" + briefingSound + '\'' +
                ", portrait='" + portrait + '\'' +
                ", roleType=" + roleType +
                ", totalAttention=" + totalAttention +
                ", createTime=" + createTime +
                ", totalPrise=" + totalPrise +
                ", totalAward=" + totalAward +
                ", totalListen=" + totalListen +
                ", name='" + name + '\'' +
                ", online=" + online +
                ", id=" + id +
                ", brifeingText='" + briefingText + '\'' +
                ", status=" + status +
                ", soundTime=" + soundTime +
                ", isAttention=" + isAttention +
                '}';
    }

}
