package com.sbai.finance.model;

/**
 * Created by Administrator on 2017-04-28.
 */

public class BorrowInHis {

    public static final int STATUS_FAIL=4;
    public static final int STATUS_SUCCESS=7;
    public static final int STATUS_PAY_INTENTION=6;
    /**
     * content : 测试内容4ph4
     * days : 41666
     * failMsg : 测试内容e84h
     * interest : 63547
     * modifyTime : 21384
     * money : 15207
     * status : 32417
     * userId : 66115
     * userName : 测试内容5bt4
     */

    private String content;
    private int days;
    private String failMsg;
    private int interest;
    private int modifyTime;
    private int money;
    private int status;
    private int userId;
    private String userName;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getFailMsg() {
        return failMsg;
    }

    public void setFailMsg(String failMsg) {
        this.failMsg = failMsg;
    }

    public int getInterest() {
        return interest;
    }

    public void setInterest(int interest) {
        this.interest = interest;
    }

    public int getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(int modifyTime) {
        this.modifyTime = modifyTime;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
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
