package com.sbai.finance.model;

/**
 * Created by Administrator on 2017-04-28.
 */

public class BorrowOutHistory {
    public static final int STATUS_PAY_INTENTION=6;
    public static final int STATUS_SUCCESS=7;
    public static final int STATUS_ALREADY_REPAY=8;

    /**
     * confirmTime : 77055
     * content : 我要借钱
     * contentImg : ssa
     * createDate : 1493021623000
     * days : 40
     * endlineTime : 1493064823000
     * failMsg : 借单撤销
     * id : 2
     * interest : 100
     * location : 测试内容6z1p
     * modifyDate : 1493103894000
     * money : 2000
     * portrait : 测试内容l022
     * status : 8
     * userId : 10
     * userName : 测试内容iw7c
     */

    private int confirmTime;
    private String content;
    private String contentImg;
    private long createDate;
    private int days;
    private long endlineTime;
    private String failMsg;
    private int id;
    private int interest;
    private String location;
    private long modifyDate;
    private int money;
    private String portrait;
    private int status;
    private int userId;
    private String userName;

    public int getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(int confirmTime) {
        this.confirmTime = confirmTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentImg() {
        return contentImg;
    }

    public void setContentImg(String contentImg) {
        this.contentImg = contentImg;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public long getEndlineTime() {
        return endlineTime;
    }

    public void setEndlineTime(long endlineTime) {
        this.endlineTime = endlineTime;
    }

    public String getFailMsg() {
        return failMsg;
    }

    public void setFailMsg(String failMsg) {
        this.failMsg = failMsg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInterest() {
        return interest;
    }

    public void setInterest(int interest) {
        this.interest = interest;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(long modifyDate) {
        this.modifyDate = modifyDate;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
}
