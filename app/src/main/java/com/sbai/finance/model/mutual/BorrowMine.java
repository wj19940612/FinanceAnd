package com.sbai.finance.model.mutual;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017-06-06.
 */

public class BorrowMine implements Parcelable {
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

    /**
     * content : 测试
     * contentImg :
     * createDate : 1496826276000
     * days : 30
     * id : 416
     * intentionCount : 0
     * interest : 20
     * loanInRepay : 0
     * loanOutRepay : 0
     * location : 杭州市 滨江区
     * locationLat : 30.210255
     * locationLng : 120.216326
     * modifyDate : 1496826275000
     * money : 600
     * portrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494325795572.png
     * status : 1
     * userId : 145
     * userName : 666
     */

    private String content;
    private String contentImg;
    private long createDate;
    private int days;
    private int id;
    private int intentionCount;
    private int interest;
    private int loanInRepay;
    private int loanOutRepay;
    private String location;
    private double locationLat;
    private double locationLng;
    private long modifyDate;
    private int money;
    private String portrait;
    private int status;
    private int userId;
    private String userName;
    private int isAttention;

    public int getIsAttention() {
        return isAttention;
    }

    public void setIsAttention(int isAttention) {
        this.isAttention = isAttention;
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

    public int getInterest() {
        return interest;
    }

    public void setInterest(int interest) {
        this.interest = interest;
    }

    public int getLoanInRepay() {
        return loanInRepay;
    }

    public void setLoanInRepay(int loanInRepay) {
        this.loanInRepay = loanInRepay;
    }

    public int getLoanOutRepay() {
        return loanOutRepay;
    }

    public void setLoanOutRepay(int loanOutRepay) {
        this.loanOutRepay = loanOutRepay;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(double locationLat) {
        this.locationLat = locationLat;
    }

    public double getLocationLng() {
        return locationLng;
    }

    public void setLocationLng(double locationLng) {
        this.locationLng = locationLng;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeString(this.contentImg);
        dest.writeLong(this.createDate);
        dest.writeInt(this.days);
        dest.writeInt(this.id);
        dest.writeInt(this.intentionCount);
        dest.writeInt(this.interest);
        dest.writeInt(this.loanInRepay);
        dest.writeInt(this.loanOutRepay);
        dest.writeString(this.location);
        dest.writeDouble(this.locationLat);
        dest.writeDouble(this.locationLng);
        dest.writeLong(this.modifyDate);
        dest.writeInt(this.money);
        dest.writeString(this.portrait);
        dest.writeInt(this.status);
        dest.writeInt(this.userId);
        dest.writeString(this.userName);
    }

    public BorrowMine() {
    }

    protected BorrowMine(Parcel in) {
        this.content = in.readString();
        this.contentImg = in.readString();
        this.createDate = in.readLong();
        this.days = in.readInt();
        this.id = in.readInt();
        this.intentionCount = in.readInt();
        this.interest = in.readInt();
        this.loanInRepay = in.readInt();
        this.loanOutRepay = in.readInt();
        this.location = in.readString();
        this.locationLat = in.readDouble();
        this.locationLng = in.readDouble();
        this.modifyDate = in.readLong();
        this.money = in.readInt();
        this.portrait = in.readString();
        this.status = in.readInt();
        this.userId = in.readInt();
        this.userName = in.readString();
    }

    public static final Parcelable.Creator<BorrowMine> CREATOR = new Parcelable.Creator<BorrowMine>() {
        @Override
        public BorrowMine createFromParcel(Parcel source) {
            return new BorrowMine(source);
        }

        @Override
        public BorrowMine[] newArray(int size) {
            return new BorrowMine[size];
        }
    };
}
