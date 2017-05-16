package com.sbai.finance.model.mutual;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017-04-28.
 */

public class BorrowOut implements Parcelable {


    /**
     * auditActorId : 0
     * auditTime : 1494232710000
     * confirmTime : 1494232604000
     * content : 再来一辆，two
     * contentImg : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494232604479.png
     * createDate : 1494232604000
     * days : 30
     * endlineTime : 1494319110000
     * id : 52
     * intentionCount : 1
     * intentionTime : 2017-05-08 16:39:34.0
     * interest : 201
     * modifyDate : 1494232774000
     * money : 2000
     * status : 3
     * userId : 157
     * userName : 用户1670
     * location:杭州
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

    public int getLoadId() {
        return loadId;
    }

    public void setLoadId(int loadId) {
        this.loadId = loadId;
    }

    private int loadId;
    private int intentionCount;
    private long intentionTime;
    private int interest;
    private long modifyDate;
    private int money;
    private int status;
    private int userId;
    private String userName;
    private String location;
    private String portrait;

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public long getIntentionTime() {
        return intentionTime;
    }

    public void setIntentionTime(long intentionTime) {
        this.intentionTime = intentionTime;
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
        dest.writeLong(this.intentionTime);
        dest.writeInt(this.interest);
        dest.writeLong(this.modifyDate);
        dest.writeInt(this.money);
        dest.writeInt(this.status);
        dest.writeInt(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.location);
        dest.writeString(this.portrait);
    }

    public BorrowOut() {
    }

    protected BorrowOut(Parcel in) {
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
        this.intentionTime = in.readLong();
        this.interest = in.readInt();
        this.modifyDate = in.readLong();
        this.money = in.readInt();
        this.status = in.readInt();
        this.userId = in.readInt();
        this.userName = in.readString();
        this.location = in.readString();
        this.portrait = in.readString();
    }

    public static final Parcelable.Creator<BorrowOut> CREATOR = new Parcelable.Creator<BorrowOut>() {
        @Override
        public BorrowOut createFromParcel(Parcel source) {
            return new BorrowOut(source);
        }

        @Override
        public BorrowOut[] newArray(int size) {
            return new BorrowOut[size];
        }
    };
}
