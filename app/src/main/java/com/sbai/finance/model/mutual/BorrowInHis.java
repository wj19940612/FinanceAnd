package com.sbai.finance.model.mutual;

/**
 * Created by Administrator on 2017-04-28.
 */

public class BorrowInHis {
    public static final int STATUS_FAIL_CHECK=2;
    public static final int STATUS_FAIL=4;
    public static final int STATUS_TIMEOUT=41;
    public static final int STATUS_NO_CHOICE=42;
    public static final int STATUS_CANCEL=43;
    public static final int STATUS_PAY_INTENTION=6;
    public static final int STATUS_SUCCESS=7;
    public static final int STATUS_ALREADY_REPAY=8;
    public static final int STATUS_NO_REPAY=0;
    public static final int STATUS_REPAY=1;
    /**
     * auditActorId : 0
     * auditTime : 1493945778000
     * confirmTime : 1493956164000
     * content : 开奖了
     * contentImg : http://p3.music.126.net/Pn49qeCkJnXJ-HzJqne-RQ==/109951162920777044.jpg
     * createDate : 1493890409000
     * days : 20
     * failMsg :
     * id : 42
     * intentionCount : 0
     * interest : 200
     * location :
     * modifyDate : 1493976242000
     * money : 2000
     * portrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1493890323050.png
     * selectedUserId : 19
     * status : 7
     * userId : 111
     * userName : 用户2340
     */
    private int loanInRepay;
    private int auditActorId;
    private long auditTime;
    private long confirmTime;
    private String content;
    private String contentImg;
    private long createDate;
    private int days;
    private String failMsg;
    private int id;
    private int intentionCount;
    private long interest;
    private String location;
    private long modifyDate;
    private int money;
    private String portrait;
    private int selectedUserId;
    private int status;
    private int userId;
    private String userName;

    public int getLoanInRepay() {
        return loanInRepay;
    }

    public void setLoanInRepay(int loanInRepay) {
        this.loanInRepay = loanInRepay;
    }

    public int getAuditActorId() {
        return auditActorId;
    }

    public void setAuditActorId(int auditActorId) {
        this.auditActorId = auditActorId;
    }

    public long getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(long auditTime) {
        this.auditTime = auditTime;
    }

    public long getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(long confirmTime) {
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

    public int getIntentionCount() {
        return intentionCount;
    }

    public void setIntentionCount(int intentionCount) {
        this.intentionCount = intentionCount;
    }

    public long getInterest() {
        return interest;
    }

    public void setInterest(long interest) {
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

    public int getSelectedUserId() {
        return selectedUserId;
    }

    public void setSelectedUserId(int selectedUserId) {
        this.selectedUserId = selectedUserId;
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
