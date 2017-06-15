package com.sbai.finance.model.mutual;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017-06-14.
 */

public class BorrowDetail implements Parcelable {
    //待审核
    public static final int STATUS_NO_CHECKED=1;
    // 给予帮助  状态:已提交(借出人)
    public static final int STATUS_GIVE_HELP=3;
    //等待互助
    public static final  int STATUS_WAIT_HELP=3;
    //审批不通过  已结束
    public static final int STATUS_END_NO_ALLOW=2;
    //无人帮助超时 已结束
    public static final int STATUS_END_FIIL=4;
    //确认接受帮助
    public static final int STATUS_ACCEPTY=5;
    public static final int STASTU_END_NO_HELP=41;
    //未选择好心人超时  已结束
    public static final int STATUS_END_NO_CHOICE_HELP=42;
    //主动撤销 已结束
    public static final int STATUS_END_CANCEL=43;
    //缴纳意向金
    public static final  int STATUS_INTENTION = 7;
    //已还款
    public static final int STATUS_END_REPAY=8;
    //缴纳意向金超时
    public static final int STATUS_INTENTION_OVER_TIME=9;

    public static final int NO_ATTENTION=1;
    public static final int ATTENTION=2;

    public static final int INTENTIONED=2;
    /**
     * auditActorId : 0
     * auditTime : 1497231066000
     * confirmDays : 1
     * confirmTime : 1497233361000
     * content : 大声道多撒大所多所
     * createDate : 1497231046000
     * days : 12
     * endlineTime : 1497317467000
     * id : 532
     * intentionCount : 1
     * interest : 12
     * isAttention : 1
     * isIntention : 1
     * location : 浙江省 杭州市 江干区
     * modifyDate : 1497233361000
     * money : 550
     * portrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1497002179711.png
     * selectedUserId : 127
     * status : 7
     * userId : 122
     * userName : 用户82120
     */

    private int auditActorId;
    private long auditTime;
    private int confirmDays;
    private long confirmTime;
    private String content;
    private long createDate;
    private int days;
    private long endlineTime;
    private int id;
    private int intentionCount;
    private double interest;
    private int isAttention;
    private int isIntention;
    private String location;
    private long modifyDate;
    private double money;
    private String portrait;
    private int selectedUserId;
    private int status;
    private int userId;
    private String userName;
    private String contentImg;

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

    public int getConfirmDays() {
        return confirmDays;
    }

    public void setConfirmDays(int confirmDays) {
        this.confirmDays = confirmDays;
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

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
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

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
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

    public String getContentImg() {
        return contentImg;
    }

    public void setContentImg(String contentImg) {
        this.contentImg = contentImg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.auditActorId);
        dest.writeLong(this.auditTime);
        dest.writeInt(this.confirmDays);
        dest.writeLong(this.confirmTime);
        dest.writeString(this.content);
        dest.writeLong(this.createDate);
        dest.writeInt(this.days);
        dest.writeLong(this.endlineTime);
        dest.writeInt(this.id);
        dest.writeInt(this.intentionCount);
        dest.writeDouble(this.interest);
        dest.writeInt(this.isAttention);
        dest.writeInt(this.isIntention);
        dest.writeString(this.location);
        dest.writeLong(this.modifyDate);
        dest.writeDouble(this.money);
        dest.writeString(this.portrait);
        dest.writeInt(this.selectedUserId);
        dest.writeInt(this.status);
        dest.writeInt(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.contentImg);
    }

    public BorrowDetail() {
    }

    protected BorrowDetail(Parcel in) {
        this.auditActorId = in.readInt();
        this.auditTime = in.readLong();
        this.confirmDays = in.readInt();
        this.confirmTime = in.readLong();
        this.content = in.readString();
        this.createDate = in.readLong();
        this.days = in.readInt();
        this.endlineTime = in.readLong();
        this.id = in.readInt();
        this.intentionCount = in.readInt();
        this.interest = in.readDouble();
        this.isAttention = in.readInt();
        this.isIntention = in.readInt();
        this.location = in.readString();
        this.modifyDate = in.readLong();
        this.money = in.readDouble();
        this.portrait = in.readString();
        this.selectedUserId = in.readInt();
        this.status = in.readInt();
        this.userId = in.readInt();
        this.userName = in.readString();
        this.contentImg = in.readString();
    }

    public static final Parcelable.Creator<BorrowDetail> CREATOR = new Parcelable.Creator<BorrowDetail>() {
        @Override
        public BorrowDetail createFromParcel(Parcel source) {
            return new BorrowDetail(source);
        }

        @Override
        public BorrowDetail[] newArray(int size) {
            return new BorrowDetail[size];
        }
    };
}
