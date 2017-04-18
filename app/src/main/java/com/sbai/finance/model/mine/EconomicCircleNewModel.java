package com.sbai.finance.model.mine;

/**
 * Created by ${wangJie} on 2017/4/18.
 * 经济圈的item的model
 */

public class EconomicCircleNewModel {

    private String userImage;
    private String userName;
    private String content;
    private long time;

    public EconomicCircleNewModel(String userImage, String userName) {
        this.userImage = userImage;
        this.userName = userName;
    }

    public EconomicCircleNewModel(String userImage, String userName, String content) {
        this.userImage = userImage;
        this.userName = userName;
        this.content = content;
    }

    public EconomicCircleNewModel(String userImage, String userName, String content, long time) {
        this.userImage = userImage;
        this.userName = userName;
        this.content = content;
        this.time = time;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
