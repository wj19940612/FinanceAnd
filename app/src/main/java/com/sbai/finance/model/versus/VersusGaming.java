package com.sbai.finance.model.versus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017-06-21.
 */

public class VersusGaming implements Parcelable {
    //0现金 1元宝 2积分
    public static final int COIN_TYPE_CASH=1;
    public static final int COIN_TYPE_BAO=2;
    public static final int COIN_TYPE_INTEGRAL=3;

    // 0取消 1发起 2对战开始 3对战结束
    public static final int GAME_STATUS_CANCEL=0;
    public static final int GAME_STATUS_MATCH=1;
    public static final int GAME_STATUS_START=2;
    public static final int GAME_STATUS_END=3;
    //0平手 1发起者赢 2应战者赢
    public static final int RESULT_TIE=0;
    public static final int RESULT_CREATE_WIN=1;
    public static final int RESULT_AGAINST_WIN=2;

    public static final int PAGE_RECORD=0;
    public static final int PAGE_VERSUS=1;
    /**
     * againstFrom : 经济圈
     * againstUser : 286
     * againstUserName : 朱哥哥
     * againstUserPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1497320196633.png
     * batchCode : 2017062015
     * coinType : 2
     * createTime : 1497944006000
     * endTime : 1497943999000
     * endline : 600
     * gameStatus : 2
     * id : 1
     * launchUser : 132
     * launchUserName : 12*3
     * launchUserPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1495699927906.png
     * modifyTime : 1497944102000
     * reward : 300
     * startTime : 1497943387000
     * varietyId : 101
     * varietyName : 美黄金
     * varietyType : cn
     * winResult : 1
     */

    private String againstFrom;
    private int againstUser;
    private String againstUserName;
    private String againstUserPortrait;
    private String batchCode;
    private int coinType;
    private long createTime;
    private long endTime;
    private int endline;
    private int gameStatus;
    private int id;
    private int launchUser;
    private String launchUserName;
    private String launchUserPortrait;
    private long modifyTime;
    private int reward;
    private long startTime;
    private int varietyId;
    private String varietyName;
    private String varietyType;
    private int winResult;
    private double launchScore;
    private double againstScore;
    private int pageType;
    private int againstPraise;
    private int launchPraise;

    public int getPageType() {
        return pageType;
    }

    public void setPageType(int pageType) {
        this.pageType = pageType;
    }

    public double getLaunchScore() {
        return launchScore;
    }

    public void setLaunchScore(double launchScore) {
        this.launchScore = launchScore;
    }

    public double getAgainstScore() {
        return againstScore;
    }

    public void setAgainstScore(double againstScore) {
        this.againstScore = againstScore;
    }

    public String getAgainstFrom() {
        return againstFrom;
    }

    public void setAgainstFrom(String againstFrom) {
        this.againstFrom = againstFrom;
    }

    public int getAgainstUser() {
        return againstUser;
    }

    public void setAgainstUser(int againstUser) {
        this.againstUser = againstUser;
    }

    public String getAgainstUserName() {
        return againstUserName;
    }

    public void setAgainstUserName(String againstUserName) {
        this.againstUserName = againstUserName;
    }

    public String getAgainstUserPortrait() {
        return againstUserPortrait;
    }

    public void setAgainstUserPortrait(String againstUserPortrait) {
        this.againstUserPortrait = againstUserPortrait;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public int getCoinType() {
        return coinType;
    }

    public void setCoinType(int coinType) {
        this.coinType = coinType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getEndline() {
        return endline;
    }

    public void setEndline(int endline) {
        this.endline = endline;
    }

    public int getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(int gameStatus) {
        this.gameStatus = gameStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLaunchUser() {
        return launchUser;
    }

    public void setLaunchUser(int launchUser) {
        this.launchUser = launchUser;
    }

    public String getLaunchUserName() {
        return launchUserName;
    }

    public void setLaunchUserName(String launchUserName) {
        this.launchUserName = launchUserName;
    }

    public String getLaunchUserPortrait() {
        return launchUserPortrait;
    }

    public void setLaunchUserPortrait(String launchUserPortrait) {
        this.launchUserPortrait = launchUserPortrait;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getVarietyId() {
        return varietyId;
    }

    public void setVarietyId(int varietyId) {
        this.varietyId = varietyId;
    }

    public String getVarietyName() {
        return varietyName;
    }

    public void setVarietyName(String varietyName) {
        this.varietyName = varietyName;
    }

    public String getVarietyType() {
        return varietyType;
    }

    public void setVarietyType(String varietyType) {
        this.varietyType = varietyType;
    }

    public int getWinResult() {
        return winResult;
    }

    public void setWinResult(int winResult) {
        this.winResult = winResult;
    }

    public int getAgainstPraise() {
        return againstPraise;
    }

    public void setAgainstPraise(int againstPraise) {
        this.againstPraise = againstPraise;
    }

    public int getLaunchPraise() {
        return launchPraise;
    }

    public void setLaunchPraise(int launchPraise) {
        this.launchPraise = launchPraise;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.againstFrom);
        dest.writeInt(this.againstUser);
        dest.writeString(this.againstUserName);
        dest.writeString(this.againstUserPortrait);
        dest.writeString(this.batchCode);
        dest.writeInt(this.coinType);
        dest.writeLong(this.createTime);
        dest.writeLong(this.endTime);
        dest.writeInt(this.endline);
        dest.writeInt(this.gameStatus);
        dest.writeInt(this.id);
        dest.writeInt(this.launchUser);
        dest.writeString(this.launchUserName);
        dest.writeString(this.launchUserPortrait);
        dest.writeLong(this.modifyTime);
        dest.writeInt(this.reward);
        dest.writeLong(this.startTime);
        dest.writeInt(this.varietyId);
        dest.writeString(this.varietyName);
        dest.writeString(this.varietyType);
        dest.writeInt(this.winResult);
        dest.writeDouble(this.launchScore);
        dest.writeDouble(this.againstScore);
        dest.writeInt(this.pageType);
        dest.writeInt(this.againstPraise);
        dest.writeInt(this.launchPraise);
    }

    public VersusGaming() {
    }

    protected VersusGaming(Parcel in) {
        this.againstFrom = in.readString();
        this.againstUser = in.readInt();
        this.againstUserName = in.readString();
        this.againstUserPortrait = in.readString();
        this.batchCode = in.readString();
        this.coinType = in.readInt();
        this.createTime = in.readLong();
        this.endTime = in.readLong();
        this.endline = in.readInt();
        this.gameStatus = in.readInt();
        this.id = in.readInt();
        this.launchUser = in.readInt();
        this.launchUserName = in.readString();
        this.launchUserPortrait = in.readString();
        this.modifyTime = in.readLong();
        this.reward = in.readInt();
        this.startTime = in.readLong();
        this.varietyId = in.readInt();
        this.varietyName = in.readString();
        this.varietyType = in.readString();
        this.winResult = in.readInt();
        this.launchScore = in.readDouble();
        this.againstScore = in.readDouble();
        this.pageType = in.readInt();
        this.againstPraise = in.readInt();
        this.launchPraise = in.readInt();
    }

    public static final Parcelable.Creator<VersusGaming> CREATOR = new Parcelable.Creator<VersusGaming>() {
        @Override
        public VersusGaming createFromParcel(Parcel source) {
            return new VersusGaming(source);
        }

        @Override
        public VersusGaming[] newArray(int size) {
            return new VersusGaming[size];
        }
    };
}
