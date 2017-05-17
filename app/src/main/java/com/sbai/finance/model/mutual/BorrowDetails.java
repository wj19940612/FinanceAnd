package com.sbai.finance.model.mutual;

/**
 * Created by Administrator on 2017-05-12.
 */

public class BorrowDetails {
    public static final int WOMEN=1;
    public static final int MAN=2;
    public static final int NO_ATTENTION=1;
    public static final int ATTENTION=2;
    /**
     * aduitActorId : 0
     * auditTime : 1494296441000
     * confirmTime : 1494296441000
     * content : 最近手头紧，第3次
     * contentImg : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494228851372.png
     * createDate : 1494228890000
     * days : 35
     * endlineTime : 1494296441000
     * failMsg : 借单撤销
     * id : 51
     * intentionCount : 0
     * interest : 30
     * isAttention : 1
     * isIntention : 70023
     * location : 天津市-天津市-静海县
     * modifyDate : 1494304209000
     * money : 1800
     * portrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494305494899.png
     * remark : 我看了
     * selectedLocation : null-北京市-东城区
     * selectedPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1493109274765.png
     * selectedUserId : 19
     * selectedUserName : 用户59
     * sex : 0
     * status : 8
     * userId : 150
     * userName : 三十个黄三
     */

    private int aduitActorId;
    private long auditTime;
    private long confirmTime;
    private String content;
    private String contentImg;
    private long createDate;
    private int days;
    private long endlineTime;
    private String failMsg;
    private int id;
    private int intentionCount;
    private long interest;
    private int isAttention;
    private int isIntention;
    private int isAttentionSelected;
    private String location;
    private long modifyDate;
    private int money;
    private String portrait;
    private String remark;
    private String selectedLocation;
    private String selectedPortrait;
    private int selectedUserId;
    private String selectedUserName;
    private int sex;
    private int status;
    private int userId;
    private String userName;

    public int getIsAttentionSelected() {
        return isAttentionSelected;
    }

    public void setIsAttentionSelected(int isAttentionSelected) {
        this.isAttentionSelected = isAttentionSelected;
    }

    public int getAduitActorId() {
        return aduitActorId;
    }

    public void setAduitActorId(int aduitActorId) {
        this.aduitActorId = aduitActorId;
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

    public int getIsAttention() {
        return isAttention;
    }

    public void setIsAttention(int isAttention) {
        this.isAttention = isAttention;
    }

    public int getIsIntention() {
        return isIntention;
    }

    public void setIsIntention(int isIntention) {
        this.isIntention = isIntention;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(String selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public String getSelectedPortrait() {
        return selectedPortrait;
    }

    public void setSelectedPortrait(String selectedPortrait) {
        this.selectedPortrait = selectedPortrait;
    }

    public int getSelectedUserId() {
        return selectedUserId;
    }

    public void setSelectedUserId(int selectedUserId) {
        this.selectedUserId = selectedUserId;
    }

    public String getSelectedUserName() {
        return selectedUserName;
    }

    public void setSelectedUserName(String selectedUserName) {
        this.selectedUserName = selectedUserName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
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
