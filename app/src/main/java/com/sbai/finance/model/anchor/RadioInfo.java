package com.sbai.finance.model.anchor;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017\11\21 0021.
 */

public class RadioInfo implements Parcelable {

    /**
     * createTime : 1510911789000
     * id : 2
     * listenNumber : 2
     * modifyTime : 1511230915000
     * radioCover : http://news.xinhuanet.com/politics/2017-11/20/129744796_15111383153861n.jpg
     * radioHost : 691
     * radioIntroduction : 2
     * radioName : 2
     * radioNumber : 2
     * radioStatus : 1
     * recommend : 1
     * recommendHits : 0
     * subscribe : 30
     * updateUserId : 0
     */

    private long createTime;
    private int id;
    private int isSubscriber;//是否订阅
    private int listenNumber;//收听人数
    private long modifyTime;//修改时间
    private String radioCover;//封面图
    private int radioHost;//主播id
    private String radioIntroduction;//电台简介
    private String radioName;//电台名称
    private int radioNumber;//语音数
    private int radioStatus;//0 隐藏，1 显示
    private int recommend;//是否推荐
    private int recommendHits;//推荐点击量
    private int subscribe;//订阅数
    private int updateUserId;//审核人员
    private String userPortrait;//电台主播头像
    private String radioHostName;//电台主播名称
    private long showTime;       //展示时间
    private int show;            //1-展示 0-下架

    private int paid;          //音频是否收费：0免费,1收费
    private int radioPaid;     // 电台是否收费
    private double radioPrice;
    private int userPayment;   //1 是否已经付款

    public int getPaid() {
        return paid;
    }

    public int getRadioPaid() {
        return radioPaid;
    }

    public double getRadioPrice() {
        return radioPrice;
    }

    public int getUserPayment() {
        return userPayment;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getListenNumber() {
        return listenNumber;
    }

    public void setListenNumber(int listenNumber) {
        this.listenNumber = listenNumber;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getRadioCover() {
        return radioCover;
    }

    public void setRadioCover(String radioCover) {
        this.radioCover = radioCover;
    }

    public int getRadioHost() {
        return radioHost;
    }

    public void setRadioHost(int radioHost) {
        this.radioHost = radioHost;
    }

    public String getRadioIntroduction() {
        return radioIntroduction;
    }

    public void setRadioIntroduction(String radioIntroduction) {
        this.radioIntroduction = radioIntroduction;
    }

    public String getRadioName() {
        return radioName;
    }

    public void setRadioName(String radioName) {
        this.radioName = radioName;
    }

    public int getRadioNumber() {
        return radioNumber;
    }

    public void setRadioNumber(int radioNumber) {
        this.radioNumber = radioNumber;
    }

    public int getRadioStatus() {
        return radioStatus;
    }

    public void setRadioStatus(int radioStatus) {
        this.radioStatus = radioStatus;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }

    public int getRecommendHits() {
        return recommendHits;
    }

    public void setRecommendHits(int recommendHits) {
        this.recommendHits = recommendHits;
    }

    public int getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(int subscribe) {
        this.subscribe = subscribe;
    }

    public int getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(int updateUserId) {
        this.updateUserId = updateUserId;
    }

    public int getIsSubscriber() {
        return isSubscriber;
    }

    public void setIsSubscriber(int isSubscriber) {
        this.isSubscriber = isSubscriber;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }

    public String getRadioHostName() {
        return radioHostName;
    }

    public void setRadioHostName(String radioHostName) {
        this.radioHostName = radioHostName;
    }

    public long getShowTime() {
        return showTime;
    }

    public void setShowTime(long showTime) {
        this.showTime = showTime;
    }

    public int getShow() {
        return show;
    }

    public void setShow(int show) {
        this.show = show;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.createTime);
        dest.writeInt(this.id);
        dest.writeInt(this.isSubscriber);
        dest.writeInt(this.listenNumber);
        dest.writeLong(this.modifyTime);
        dest.writeString(this.radioCover);
        dest.writeInt(this.radioHost);
        dest.writeString(this.radioIntroduction);
        dest.writeString(this.radioName);
        dest.writeInt(this.radioNumber);
        dest.writeInt(this.radioStatus);
        dest.writeInt(this.recommend);
        dest.writeInt(this.recommendHits);
        dest.writeInt(this.subscribe);
        dest.writeInt(this.updateUserId);
        dest.writeString(this.userPortrait);
        dest.writeString(this.radioHostName);
        dest.writeLong(this.showTime);
        dest.writeInt(this.show);
        dest.writeInt(this.paid);
        dest.writeInt(this.radioPaid);
        dest.writeDouble(this.radioPrice);
        dest.writeInt(this.userPayment);
    }

    public RadioInfo() {
    }

    protected RadioInfo(Parcel in) {
        this.createTime = in.readLong();
        this.id = in.readInt();
        this.isSubscriber = in.readInt();
        this.listenNumber = in.readInt();
        this.modifyTime = in.readLong();
        this.radioCover = in.readString();
        this.radioHost = in.readInt();
        this.radioIntroduction = in.readString();
        this.radioName = in.readString();
        this.radioNumber = in.readInt();
        this.radioStatus = in.readInt();
        this.recommend = in.readInt();
        this.recommendHits = in.readInt();
        this.subscribe = in.readInt();
        this.updateUserId = in.readInt();
        this.userPortrait = in.readString();
        this.radioHostName = in.readString();
        this.showTime = in.readLong();
        this.show = in.readInt();
        this.paid = in.readInt();
        this.radioPaid = in.readInt();
        this.radioPrice = in.readDouble();
        this.userPayment = in.readInt();
    }

    public static final Parcelable.Creator<RadioInfo> CREATOR = new Parcelable.Creator<RadioInfo>() {
        @Override
        public RadioInfo createFromParcel(Parcel source) {
            return new RadioInfo(source);
        }

        @Override
        public RadioInfo[] newArray(int size) {
            return new RadioInfo[size];
        }
    };
}
