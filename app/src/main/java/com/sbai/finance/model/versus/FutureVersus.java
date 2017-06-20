package com.sbai.finance.model.versus;

/**
 * Created by Administrator on 2017-06-19.
 */

public class FutureVersus {
    public static final int COIN_TYPE_CASH=1;
    public static final int COIN_TYPE_BAO=2;
    public static final int COIN_TYPE_INTEGRAL=3;

    public static final int GAME_STATUS_MATCH=1;
    public static final int GAME_STATUS_VERSUS=2;
    public static final int GAME_STATUS_END=3;
    private int id;
    private int batchCode;
    private int vartietyId;
    private String vartietyType;
    private int coinType;
    private double reward;
    private long endtime;
    private int gameStatus;
    private int launchUser;
    private double launchScore;
    private int launchPraise;
    private int againstUser;
    private String againstFrom;
    private double againstScore;
    private int againstPraise;
    private long startTime;
    private long endTime;
    private long createTime;
    private long modifyTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(int batchCode) {
        this.batchCode = batchCode;
    }

    public int getVartietyId() {
        return vartietyId;
    }

    public void setVartietyId(int vartietyId) {
        this.vartietyId = vartietyId;
    }

    public String getVartietyType() {
        return vartietyType;
    }

    public void setVartietyType(String vartietyType) {
        this.vartietyType = vartietyType;
    }

    public int getCoinType() {
        return coinType;
    }

    public void setCoinType(int coinType) {
        this.coinType = coinType;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public int getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(int gameStatus) {
        this.gameStatus = gameStatus;
    }

    public int getLaunchUser() {
        return launchUser;
    }

    public void setLaunchUser(int launchUser) {
        this.launchUser = launchUser;
    }

    public double getLaunchScore() {
        return launchScore;
    }

    public void setLaunchScore(double launchScore) {
        this.launchScore = launchScore;
    }

    public int getLaunchPraise() {
        return launchPraise;
    }

    public void setLaunchPraise(int launchPraise) {
        this.launchPraise = launchPraise;
    }

    public int getAgainstUser() {
        return againstUser;
    }

    public void setAgainstUser(int againstUser) {
        this.againstUser = againstUser;
    }

    public String getAgainstFrom() {
        return againstFrom;
    }

    public void setAgainstFrom(String againstFrom) {
        this.againstFrom = againstFrom;
    }

    public double getAgainstScore() {
        return againstScore;
    }

    public void setAgainstScore(double againstScore) {
        this.againstScore = againstScore;
    }

    public int getAgainstPraise() {
        return againstPraise;
    }

    public void setAgainstPraise(int againstPraise) {
        this.againstPraise = againstPraise;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }
}
