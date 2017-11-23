package com.sbai.finance.model.miss;

/**
 * Created by Administrator on 2017\11\21 0021.
 */

public class RadioInfo {

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
}
