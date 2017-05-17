package com.sbai.finance.model.mutual;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017-04-28.
 */

public class BorrowOutHistory implements Parcelable {
    public static final int STATUS_PAY_FIVE=5;
    public static final int STATUS_PAY_INTENTION=6;
    public static final int STATUS_SUCCESS=7;
    public static final int STATUS_ALREADY_REPAY=8;

    /**
     * auditActorId : 0
     * auditTime : 1494397076000
     * confirmTime : 1494418161000
     * content : 到底说了什么穿山甲
     * contentImg : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494396996935.png
     * createDate : 1494397037000
     * days : 35
     * endlineTime : 1494483476000
     * id : 84
     * intentionCount : 5
     * intentionTime : 2017-05-10 14:58:46.0
     * interest : 50
     * location : 北京市-北京市-东城区
     * modifyDate : 1494418202000
     * money : 2000
     * portrait : ../../img/head_visitor.png
     * remark :
     * selectedUserId : 150
     * status : 5
     * userId : 187
     * userName : 233
     */

    private int auditActorId;
    private long auditTime;
    private long confirmTime;
    private String content;
    private String contentImg;
    private long createDate;
    private int days;
    private long endlineTime;
    private int id;
    private int intentionCount;
    private String intentionTime;
    private long interest;
    private String location;
    private long modifyDate;
    private int money;
    private String portrait;
    private String remark;
    private int selectedUserId;
    private int status;
    private int userId;
    private String userName;

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

    public int getIntentionCount() {
        return intentionCount;
    }

    public void setIntentionCount(int intentionCount) {
        this.intentionCount = intentionCount;
    }

    public String getIntentionTime() {
        return intentionTime;
    }

    public void setIntentionTime(String intentionTime) {
        this.intentionTime = intentionTime;
    }

    public long getInterest() {
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.auditActorId);
        dest.writeLong(this.auditTime);
        dest.writeLong(this.confirmTime);
        dest.writeString(this.content);
        dest.writeString(this.contentImg);
        dest.writeLong(this.createDate);
        dest.writeInt(this.days);
        dest.writeLong(this.endlineTime);
        dest.writeInt(this.id);
        dest.writeInt(this.intentionCount);
        dest.writeString(this.intentionTime);
        dest.writeLong(this.interest);
        dest.writeString(this.location);
        dest.writeLong(this.modifyDate);
        dest.writeInt(this.money);
        dest.writeString(this.portrait);
        dest.writeString(this.remark);
        dest.writeInt(this.selectedUserId);
        dest.writeInt(this.status);
        dest.writeInt(this.userId);
        dest.writeString(this.userName);
    }

    public BorrowOutHistory() {
    }

    protected BorrowOutHistory(Parcel in) {
        this.auditActorId = in.readInt();
        this.auditTime = in.readLong();
        this.confirmTime = in.readLong();
        this.content = in.readString();
        this.contentImg = in.readString();
        this.createDate = in.readLong();
        this.days = in.readInt();
        this.endlineTime = in.readLong();
        this.id = in.readInt();
        this.intentionCount = in.readInt();
        this.intentionTime = in.readString();
        this.interest = in.readLong();
        this.location = in.readString();
        this.modifyDate = in.readLong();
        this.money = in.readInt();
        this.portrait = in.readString();
        this.remark = in.readString();
        this.selectedUserId = in.readInt();
        this.status = in.readInt();
        this.userId = in.readInt();
        this.userName = in.readString();
    }

    public static final Parcelable.Creator<BorrowOutHistory> CREATOR = new Parcelable.Creator<BorrowOutHistory>() {
        @Override
        public BorrowOutHistory createFromParcel(Parcel source) {
            return new BorrowOutHistory(source);
        }

        @Override
        public BorrowOutHistory[] newArray(int size) {
            return new BorrowOutHistory[size];
        }
    };
}
