package com.sbai.finance.model.mine;

/**
 * Created by Administrator on 2017-04-28.
 */

public class BorrowOutHis {
    public static final int STATUST_6=6;
    public static final int STATUST_7=7;
    public static final int STATUST_8=8;

    /**
     * content : 我要借钱
     * contentImg : ssa
     * createDate : 1493021623000
     * days : 40
     * endlineTime : 1493064823000
     * failMsg : 借单撤销
     * id : 2
     * interest : 100
     * modifyDate : 1493103894000
     * money : 2000
     * status : 8
     * userId : 10
     */

    private String content;
    private String contentImg;
    private long createDate;
    private int days;
    private long endlineTime;
    private String failMsg;
    private int id;
    private int interest;
    private long modifyDate;
    private int money;
    private int status;
    private int userId;

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
}
