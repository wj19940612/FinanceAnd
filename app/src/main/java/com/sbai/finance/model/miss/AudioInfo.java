package com.sbai.finance.model.miss;

/**
 * Created by Administrator on 2017\11\22 0022.
 */

public class AudioInfo{

    /**
     * audio : https://esongtest.oss-cn-shanghai.aliyuncs.com/upload/20171012/Free-Converter.com-20170815035134-6148604140.m4a
     * audioComment : 10
     * audioCover : http://news.xinhuanet.com/politics/2017-11/20/129744796_15111383153861n.jpg
     * audioIntroduction : xx
     * audioName : xx
     * audioTime : 10
     * collect : 1
     * createTime : 1511148719000
     * displayStatus : 1
     * goldAwardMoney : 10
     * id : 2
     * modifyTime : 1511235619000
     * radioHost : 691
     * radioId : 2
     * reviewStatus : 1
     * totalPrise : 1
     * updateUserId : 112
     * viewNumber : 1
     */

    private String audio; //音频地址
    private int audioComment; //评论数
    private String audioCover; //音频封面
    private String audioIntroduction;//内容简介
    private String audioName;//音频名称
    private int audioTime;//时长
    private int collect;//收藏
    private long createTime;//创建时间
    private int displayStatus;//显示状态
    private int goldAwardMoney;//打赏（元宝）
    private int id;//音频id
    private long modifyTime;//修改时间
    private int radioHost;//主播id
    private int radioId;//所属电台id
    private int reviewStatus;//审核状态
    private int totalPrise;//点赞数
    private int updateUserId;//审核人员
    private int viewNumber;//观看人数

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public int getAudioComment() {
        return audioComment;
    }

    public void setAudioComment(int audioComment) {
        this.audioComment = audioComment;
    }

    public String getAudioCover() {
        return audioCover;
    }

    public void setAudioCover(String audioCover) {
        this.audioCover = audioCover;
    }

    public String getAudioIntroduction() {
        return audioIntroduction;
    }

    public void setAudioIntroduction(String audioIntroduction) {
        this.audioIntroduction = audioIntroduction;
    }

    public String getAudioName() {
        return audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public int getAudioTime() {
        return audioTime;
    }

    public void setAudioTime(int audioTime) {
        this.audioTime = audioTime;
    }

    public int getCollect() {
        return collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(int displayStatus) {
        this.displayStatus = displayStatus;
    }

    public int getGoldAwardMoney() {
        return goldAwardMoney;
    }

    public void setGoldAwardMoney(int goldAwardMoney) {
        this.goldAwardMoney = goldAwardMoney;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public int getRadioHost() {
        return radioHost;
    }

    public void setRadioHost(int radioHost) {
        this.radioHost = radioHost;
    }

    public int getRadioId() {
        return radioId;
    }

    public void setRadioId(int radioId) {
        this.radioId = radioId;
    }

    public int getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(int reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public int getTotalPrise() {
        return totalPrise;
    }

    public void setTotalPrise(int totalPrise) {
        this.totalPrise = totalPrise;
    }

    public int getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(int updateUserId) {
        this.updateUserId = updateUserId;
    }

    public int getViewNumber() {
        return viewNumber;
    }

    public void setViewNumber(int viewNumber) {
        this.viewNumber = viewNumber;
    }
}

