package com.sbai.finance.model.mine;

import com.google.gson.annotations.SerializedName;

/**
 * Created by linrongfang on 2017/5/8.
 */

public class Feedback {

    public static int CONTENT_TYPE_TEXT = 1;
    public static int CONTENT_TYPE_PICTURE = 2;


    /**
     * replyName : 1
     * content : 的撒多
     * contentType : 1
     * createDate : 1494235551000
     * id : 258
     * status : 0
     * type : 0
     * userName : 用户2408
     * userPhone : 13458962548
     * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494233728845.png
     */

    private String replyName;
    private String content;
    private int contentType;
    private long createDate;
    private String createTime;
    private int id;
    private int status;
    private int type;
    private String userName;
    private String userPhone;
    private String userPortrait;
    private String portrait;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getReplyName() {
        return replyName;
    }

    public void setReplyName(String replyName) {
        this.replyName = replyName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }
}
