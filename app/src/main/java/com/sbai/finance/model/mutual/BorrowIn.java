package com.sbai.finance.model.mutual;

/**
 * Created by Administrator on 2017-04-27.
 */

public class BorrowIn {
    public static final int STATUS_NO_CHECK=1;
    public static final int STATUS_CHECKED=3;
    /**
     * content : 测试借款
     * contentImg : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1493363773198.png,https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1493363773530.png,https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1493363773621.png,https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1493363773636.png,
     * createDate : 1493363773000
     * days : 6
     * endlineTime : 1493450173000
     * id : 13
     * interest : 66
     * modifyDate : 1493363773000
     * money : 666
     * status : 1
     * userId : 83
     */

    private String content;
    private String contentImg;
    private long createDate;
    private int days;
    private long endlineTime;
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
