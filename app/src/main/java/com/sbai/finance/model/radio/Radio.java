package com.sbai.finance.model.radio;

import android.os.Parcel;
import android.os.Parcelable;

import com.sbai.finance.net.Client;
import com.sbai.finance.utils.MissAudioManager;

/**
 * Created by ${wangJie} on 2017/11/20.
 * {@link Client# /explain/audioManage/getRecommendLatestAudio.do}
 * /explain/audioManage/queryAudioByAudioIdForApp.do
 * 姐说主页电台信息
 */

public class Radio implements Parcelable, MissAudioManager.IAudio {

    //  /user/user/collect.do 1问答2、日报3、音频4、电台
    private static final int USER_COLLECT_TYPE_QUESTION = 1;
    private static final int USER_COLLECT_TYPE_REPORT = 2;
    public static final int USER_COLLECT_TYPE_VOICE = 3;
    public static final int USER_COLLECT_TYPE_RADIO = 4;

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
     * radioHostName : 2
     * radioId : 2
     * reviewStatus : 1
     * totalPrise : 1
     * updateUserId : 112
     * viewNumber : 1
     */

    private String audio;           //音频链接
    private int audioComment;       //评论数
    private String audioCover;       //音频封面
    private String audioIntroduction;//内容简介
    private String audioName;       //音频名称
    private int audioTime;          //时长
    private int collect;
    private long createTime;
    private int displayStatus;      //显示状态
    private int goldAwardMoney;     //打赏（元宝）
    private int id;                 //音频id
    private long modifyTime;
    private int radioHost;          //主播
    private String radioHostName;  //主播名
    private int radioId;           //所属电台id
    private int reviewStatus;      //审核状态
    private int totalPrise;        //点赞
    private int updateUserId;
    private int viewNumber;        //观看人数
    private String radioName;     //电台名称
    /**
     * radioCover : http://news.xinhuanet.com/politics/2017-11/20/129744796_15111383153861n.jpg
     * reviewTime : 1511690767000
     * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/upload/20171120/useri1511172165802.png
     */

    private String radioCover;  //电台封面
    private long reviewTime;
    private String userPortrait; //用户头像
    /**
     * deleted : 0
     */

    private int deleted;

    @Override
    public int getAudioId() {
        return id;
    }

    @Override
    public String getAudioUrl() {
        return audio;
    }

    public String getRadioName() {
        return radioName;
    }

    public void setRadioName(String radioName) {
        this.radioName = radioName;
    }

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

    public String getRadioHostName() {
        return radioHostName;
    }

    public void setRadioHostName(String radioHostName) {
        this.radioHostName = radioHostName;
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

    public Radio() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.audio);
        dest.writeInt(this.audioComment);
        dest.writeString(this.audioCover);
        dest.writeString(this.audioIntroduction);
        dest.writeString(this.audioName);
        dest.writeInt(this.audioTime);
        dest.writeInt(this.collect);
        dest.writeLong(this.createTime);
        dest.writeInt(this.displayStatus);
        dest.writeInt(this.goldAwardMoney);
        dest.writeInt(this.id);
        dest.writeLong(this.modifyTime);
        dest.writeInt(this.radioHost);
        dest.writeString(this.radioHostName);
        dest.writeInt(this.radioId);
        dest.writeInt(this.reviewStatus);
        dest.writeInt(this.totalPrise);
        dest.writeInt(this.updateUserId);
        dest.writeInt(this.viewNumber);
        dest.writeString(this.radioName);
    }

    protected Radio(Parcel in) {
        this.audio = in.readString();
        this.audioComment = in.readInt();
        this.audioCover = in.readString();
        this.audioIntroduction = in.readString();
        this.audioName = in.readString();
        this.audioTime = in.readInt();
        this.collect = in.readInt();
        this.createTime = in.readLong();
        this.displayStatus = in.readInt();
        this.goldAwardMoney = in.readInt();
        this.id = in.readInt();
        this.modifyTime = in.readLong();
        this.radioHost = in.readInt();
        this.radioHostName = in.readString();
        this.radioId = in.readInt();
        this.reviewStatus = in.readInt();
        this.totalPrise = in.readInt();
        this.updateUserId = in.readInt();
        this.viewNumber = in.readInt();
        this.radioName = in.readString();
    }

    public static final Creator<Radio> CREATOR = new Creator<Radio>() {
        @Override
        public Radio createFromParcel(Parcel source) {
            return new Radio(source);
        }

        @Override
        public Radio[] newArray(int size) {
            return new Radio[size];
        }
    };


    @Override
    public String toString() {
        return "Radio{" +
                "audio='" + audio + '\'' +
                ", audioComment=" + audioComment +
                ", audioCover='" + audioCover + '\'' +
                ", audioIntroduction='" + audioIntroduction + '\'' +
                ", audioName='" + audioName + '\'' +
                ", audioTime=" + audioTime +
                ", collect=" + collect +
                ", createTime=" + createTime +
                ", displayStatus=" + displayStatus +
                ", goldAwardMoney=" + goldAwardMoney +
                ", id=" + id +
                ", modifyTime=" + modifyTime +
                ", radioHost=" + radioHost +
                ", radioHostName='" + radioHostName + '\'' +
                ", radioId=" + radioId +
                ", reviewStatus=" + reviewStatus +
                ", totalPrise=" + totalPrise +
                ", updateUserId=" + updateUserId +
                ", viewNumber=" + viewNumber +
                ", radioName='" + radioName + '\'' +
                '}';
    }

    public String getRadioCover() {
        return radioCover;
    }

    public void setRadioCover(String radioCover) {
        this.radioCover = radioCover;
    }

    public long getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(long reviewTime) {
        this.reviewTime = reviewTime;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }
}
