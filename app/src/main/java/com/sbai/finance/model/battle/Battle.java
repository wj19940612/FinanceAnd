package com.sbai.finance.model.battle;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017-06-21.
 */

public class Battle implements Parcelable {

    //1现金 2元宝 3积分
    public static final int COIN_TYPE_CASH = 1;
    public static final int COIN_TYPE_INGOT = 2;
    public static final int COIN_TYPE_SCORE = 3;

    // 0取消 1发起 2对战开始 3对战结束 4观战  5正在取消
    public static final int GAME_STATUS_CANCELED = 0;
    public static final int GAME_STATUS_CREATED = 1;
    public static final int GAME_STATUS_STARTED = 2;
    public static final int GAME_STATUS_END = 3;

    //0平手 1发起者赢 2应战者赢
    public static final int WIN_RESULT_TIE = 0;
    public static final int WIN_RESULT_CREATOR_WIN = 1;
    public static final int WIN_RESULT_CHALLENGER_WIN = 2;

    // 1房主快速匹配  2应战者快速匹配
    public static final int CREATE_FAST_MATCH = 1;
    public static final int AGAINST_FAST_MATCH = 2;

    public static final String SOURCE_COTERIE = "coterie";
    public static final String SOURCE_HALL = "hall";
    public static final String SOURCE_MATCH = "match";
    public static final String SOURCE_WEIBO = "weibo";
    public static final String SOURCE_FRIEND = "friend";

    public static final int CODE_BATTLE_JOINED_OR_CREATED = 4603;
    public static final int CODE_NO_ENOUGH_MONEY = 2201;

    // 4641发起者匹配超时  4642应战者匹配超时
    public static final int CODE_AGAINST_FAST_MATCH_TIMEOUT = 4642;
    public static final int CODE_CREATE_FAST_MATCH_TIMEOUT = 4641;

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
    private int coinType;     //1 现金 2元宝 3积分
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
    private int againstPraise;
    private int launchPraise;
    //手续费
    private double commission;

    //点赞
    private int battleId;
    private int currentPraise;
    private int praiseUserId;

    //平仓数据
    private double launchUnwindScore;
    private double againstUnwindScore;

    //下单和平仓推送数据
    private String battleBatchCode;
    private String contractsCode;
    private int contractsId;
    private String currencyUnit;
    private int direction;
    private int handsNum;
    private int optLogCount;
    private double orderMarket;
    private double orderPrice;
    private int orderStatus;
    private long orderTime;
    private double ratio;
    private String sign;
    private int userId;
    private double unwindPrice;
    private long unwindTime;

    public double getUnwindPrice() {
        return unwindPrice;
    }

    public void setUnwindPrice(double unwindPrice) {
        this.unwindPrice = unwindPrice;
    }

    public long getUnwindTime() {
        return unwindTime;
    }

    public void setUnwindTime(long unwindTime) {
        this.unwindTime = unwindTime;
    }

    public String getBattleBatchCode() {
        return battleBatchCode;
    }

    public void setBattleBatchCode(String battleBatchCode) {
        this.battleBatchCode = battleBatchCode;
    }

    public String getContractsCode() {
        return contractsCode;
    }

    public void setContractsCode(String contractsCode) {
        this.contractsCode = contractsCode;
    }

    public int getContractsId() {
        return contractsId;
    }

    public void setContractsId(int contractsId) {
        this.contractsId = contractsId;
    }

    public String getCurrencyUnit() {
        return currencyUnit;
    }

    public void setCurrencyUnit(String currencyUnit) {
        this.currencyUnit = currencyUnit;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getHandsNum() {
        return handsNum;
    }

    public void setHandsNum(int handsNum) {
        this.handsNum = handsNum;
    }

    public int getOptLogCount() {
        return optLogCount;
    }

    public void setOptLogCount(int optLogCount) {
        this.optLogCount = optLogCount;
    }

    public double getOrderMarket() {
        return orderMarket;
    }

    public void setOrderMarket(double orderMarket) {
        this.orderMarket = orderMarket;
    }

    public double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(long orderTime) {
        this.orderTime = orderTime;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isBattleOver() {
        return getGameStatus() == GAME_STATUS_END;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
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

    public int getBattleId() {
        return battleId;
    }

    public void setBattleId(int battleId) {
        this.battleId = battleId;
    }

    public int getCurrentPraise() {
        return currentPraise;
    }

    public void setCurrentPraise(int currentPraise) {
        this.currentPraise = currentPraise;
    }

    public int getPraiseUserId() {
        return praiseUserId;
    }

    public void setPraiseUserId(int praiseUserId) {
        this.praiseUserId = praiseUserId;
    }

    public double getLaunchUnwindScore() {
        return launchUnwindScore;
    }

    public void setLaunchUnwindScore(double launchUnwindScore) {
        this.launchUnwindScore = launchUnwindScore;
    }

    public double getAgainstUnwindScore() {
        return againstUnwindScore;
    }

    public void setAgainstUnwindScore(double againstUnwindScore) {
        this.againstUnwindScore = againstUnwindScore;
    }

    public Battle() {
    }

    @Override
    public String toString() {
        return "Battle{" +
                "againstFrom='" + againstFrom + '\'' +
                ", againstUser=" + againstUser +
                ", againstUserName='" + againstUserName + '\'' +
                ", againstUserPortrait='" + againstUserPortrait + '\'' +
                ", batchCode='" + batchCode + '\'' +
                ", coinType=" + coinType +
                ", createTime=" + createTime +
                ", endTime=" + endTime +
                ", endline=" + endline +
                ", gameStatus=" + gameStatus +
                ", id=" + id +
                ", launchUser=" + launchUser +
                ", launchUserName='" + launchUserName + '\'' +
                ", launchUserPortrait='" + launchUserPortrait + '\'' +
                ", modifyTime=" + modifyTime +
                ", reward=" + reward +
                ", startTime=" + startTime +
                ", varietyId=" + varietyId +
                ", varietyName='" + varietyName + '\'' +
                ", varietyType='" + varietyType + '\'' +
                ", winResult=" + winResult +
                ", launchScore=" + launchScore +
                ", againstScore=" + againstScore +
                ", againstPraise=" + againstPraise +
                ", launchPraise=" + launchPraise +
                ", battleId=" + battleId +
                ", currentPraise=" + currentPraise +
                ", praiseUserId=" + praiseUserId +
                '}';
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
        dest.writeInt(this.againstPraise);
        dest.writeInt(this.launchPraise);
        dest.writeDouble(this.commission);
        dest.writeInt(this.battleId);
        dest.writeInt(this.currentPraise);
        dest.writeInt(this.praiseUserId);
        dest.writeDouble(this.launchUnwindScore);
        dest.writeDouble(this.againstUnwindScore);
        dest.writeString(this.battleBatchCode);
        dest.writeString(this.contractsCode);
        dest.writeInt(this.contractsId);
        dest.writeString(this.currencyUnit);
        dest.writeInt(this.direction);
        dest.writeInt(this.handsNum);
        dest.writeInt(this.optLogCount);
        dest.writeDouble(this.orderMarket);
        dest.writeDouble(this.orderPrice);
        dest.writeInt(this.orderStatus);
        dest.writeLong(this.orderTime);
        dest.writeDouble(this.ratio);
        dest.writeString(this.sign);
        dest.writeInt(this.userId);
        dest.writeDouble(this.unwindPrice);
        dest.writeLong(this.unwindTime);
    }

    protected Battle(Parcel in) {
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
        this.againstPraise = in.readInt();
        this.launchPraise = in.readInt();
        this.commission = in.readDouble();
        this.battleId = in.readInt();
        this.currentPraise = in.readInt();
        this.praiseUserId = in.readInt();
        this.launchUnwindScore = in.readDouble();
        this.againstUnwindScore = in.readDouble();
        this.battleBatchCode = in.readString();
        this.contractsCode = in.readString();
        this.contractsId = in.readInt();
        this.currencyUnit = in.readString();
        this.direction = in.readInt();
        this.handsNum = in.readInt();
        this.optLogCount = in.readInt();
        this.orderMarket = in.readDouble();
        this.orderPrice = in.readDouble();
        this.orderStatus = in.readInt();
        this.orderTime = in.readLong();
        this.ratio = in.readDouble();
        this.sign = in.readString();
        this.userId = in.readInt();
        this.unwindPrice = in.readDouble();
        this.unwindTime = in.readLong();
    }

    public static final Creator<Battle> CREATOR = new Creator<Battle>() {
        @Override
        public Battle createFromParcel(Parcel source) {
            return new Battle(source);
        }

        @Override
        public Battle[] newArray(int size) {
            return new Battle[size];
        }
    };
}
