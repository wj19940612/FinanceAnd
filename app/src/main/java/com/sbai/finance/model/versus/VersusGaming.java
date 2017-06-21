package com.sbai.finance.model.versus;

/**
 * Created by Administrator on 2017-06-21.
 */

public class VersusGaming {
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
}
