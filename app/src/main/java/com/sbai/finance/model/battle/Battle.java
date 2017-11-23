package com.sbai.finance.model.battle;

import android.os.Parcel;
import android.os.Parcelable;

import com.sbai.finance.net.Client;

/**
 * Modified by john on 31/10/2017
 * <p>
 * APIs:
 * {@link Client#requestBannerInfo(String)} 获取对战信息
 * <p>
 * {@link Client#getMyVersusRecord(Long)} 获取对战记录
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
    public static final int WIN_RESULT_OWNER_WIN = 1;
    public static final int WIN_RESULT_CHALLENGER_WIN = 2;

    // 1房主快速匹配  2应战者快速匹配
    public static final int CREATE_FAST_MATCH = 1;
    public static final int AGAINST_FAST_MATCH = 2;

    public static final String SOURCE_COTERIE = "coterie";
    public static final String SOURCE_HALL = "hall";
    public static final String SOURCE_MATCH = "match";
    public static final String SOURCE_WEIBO = "weibo";
    public static final String SOURCE_FRIEND = "friend";

    public static final int GAME_TYPE_NORMAL = 1;
    public static final int GAME_TYPE_ARENA = 2;


    public static final int CODE_BATTLE_JOINED_OR_CREATED = 4603;
    public static final int CODE_NO_ENOUGH_MONEY = 2201;

    // 4641发起者匹配超时  4642应战者匹配超时
    public static final int CODE_AGAINST_FAST_MATCH_TIMEOUT = 4642;
    public static final int CODE_CREATE_FAST_MATCH_TIMEOUT = 4641;

    /**
     * 对战信息数据结构
     * <p>
     * "againstFrom":"1",
     * "againstPraise":27,
     * "againstScore":0,
     * "againstUnwindScore":0,
     * "againstUser":141,
     * "againstUserName":"思想有多远",
     * "againstUserPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494407270799.png",
     * "batchCode":"e2tmM42b",
     * "bigVarietyType":"future",
     * "coinType":2,
     * "createTime":1498640841000,
     * "endline":600,
     * "gameStatus":2,
     * "id":184,
     * "launchPraise":7,
     * "launchScore":0,
     * "launchUnwindScore":0,
     * "launchUser":124,
     * "launchUserName":"用户2972",
     * "launchUserPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1497510090054.png",
     * "loginUser":217,
     * "modifyTime":1498640890000,
     * "reward":300,
     * "startTime":1498640891000,
     * "varietyId":1,
     * "varietyName":"美原油",
     * "varietyType":"CL"
     */
    private String againstFrom;   //应战者来源
    private int againstPraise;
    private double againstScore;
    private double againstUnwindScore; // 平仓之后挑战者当前累计盈亏
    private int againstUser;      //应战者ID
    private String againstUserName; //应战者用户名
    private String againstUserPortrait;  //应战者头像

    private String batchCode;    //房间代码
    private String bigVarietyType;
    private int coinType;        //1 现金 2元宝 3积分
    private long createTime;     //创建时间
    private long endTime;         // TODO: 31/10/2017 unknown fix cm
    private int endline;         // 游戏存在时常 对战时间，单位秒
    private int gameStatus;
    private int id;

    private int launchPraise;
    private double launchScore;       // 持仓状态下包含了实时的盈亏 值 >= launchUnwindScore
    private double launchUnwindScore; // 平仓之后当前累计盈亏
    private int launchUser;            //发起人ID
    private String launchUserName;     //发起人名字
    private String launchUserPortrait; //发起人头像
    //private int loginUser;
    private long modifyTime;           //修改时间
    private int reward;                //赏金
    private long startTime;            //游戏开始时间
    private int varietyId;             //品种ID
    private String varietyName;
    private String varietyType;
    private int gameType;               // 1 普通对战 2 竞技场
    /* 以上属性为 查询 Battle 信息获取到的属性 */


    private int winResult;             // 0 平手  1 发起者赢  2 应战者赢
    private double commission;         // 平台收取手续费
    /* 以上属性为 对战记录里面 Battle 新增加 的属性 */


    //自己加的属性 todo 标注下这个属性是干什么的
    public int type;

    //对战正在发起
    public boolean isBattleCreated() {
        return gameStatus == GAME_STATUS_CREATED;
    }

    //对战开始
    public boolean isBattleStarted() {
        return getGameStatus() == GAME_STATUS_STARTED;
    }

    //对战结束
    public boolean isBattleOver() {
        return getGameStatus() == GAME_STATUS_END;
    }

    public boolean isBattleCanceled() {
        return getGameStatus() == GAME_STATUS_CANCELED;
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

    public double getLaunchUnwindScore() {
        return launchUnwindScore;
    }

    public double getAgainstUnwindScore() {
        return againstUnwindScore;
    }

    public int getGameType() {
        return gameType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Battle{" +
                "againstFrom='" + againstFrom + '\'' +
                ", againstPraise=" + againstPraise +
                ", againstScore=" + againstScore +
                ", againstUnwindScore=" + againstUnwindScore +
                ", againstUser=" + againstUser +
                ", againstUserName='" + againstUserName + '\'' +
                ", againstUserPortrait='" + againstUserPortrait + '\'' +
                ", batchCode='" + batchCode + '\'' +
                ", bigVarietyType='" + bigVarietyType + '\'' +
                ", coinType=" + coinType +
                ", createTime=" + createTime +
                ", endTime=" + endTime +
                ", endline=" + endline +
                ", gameStatus=" + gameStatus +
                ", id=" + id +
                ", launchPraise=" + launchPraise +
                ", launchScore=" + launchScore +
                ", launchUnwindScore=" + launchUnwindScore +
                ", launchUser=" + launchUser +
                ", launchUserName='" + launchUserName + '\'' +
                ", launchUserPortrait='" + launchUserPortrait + '\'' +
                ", modifyTime=" + modifyTime +
                ", reward=" + reward +
                ", startTime=" + startTime +
                ", varietyId=" + varietyId +
                ", varietyName='" + varietyName + '\'' +
                ", varietyType='" + varietyType + '\'' +
                ", gameType=" + gameType +
                ", winResult=" + winResult +
                ", commission=" + commission +
                ", type=" + type +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.againstFrom);
        dest.writeInt(this.againstPraise);
        dest.writeDouble(this.againstScore);
        dest.writeDouble(this.againstUnwindScore);
        dest.writeInt(this.againstUser);
        dest.writeString(this.againstUserName);
        dest.writeString(this.againstUserPortrait);
        dest.writeString(this.batchCode);
        dest.writeString(this.bigVarietyType);
        dest.writeInt(this.coinType);
        dest.writeLong(this.createTime);
        dest.writeLong(this.endTime);
        dest.writeInt(this.endline);
        dest.writeInt(this.gameStatus);
        dest.writeInt(this.id);
        dest.writeInt(this.launchPraise);
        dest.writeDouble(this.launchScore);
        dest.writeDouble(this.launchUnwindScore);
        dest.writeInt(this.launchUser);
        dest.writeString(this.launchUserName);
        dest.writeString(this.launchUserPortrait);
        dest.writeLong(this.modifyTime);
        dest.writeInt(this.reward);
        dest.writeLong(this.startTime);
        dest.writeInt(this.varietyId);
        dest.writeString(this.varietyName);
        dest.writeString(this.varietyType);
        dest.writeInt(this.gameType);
        dest.writeInt(this.winResult);
        dest.writeDouble(this.commission);
        dest.writeInt(this.type);
    }

    public Battle() {
    }

    protected Battle(Parcel in) {
        this.againstFrom = in.readString();
        this.againstPraise = in.readInt();
        this.againstScore = in.readDouble();
        this.againstUnwindScore = in.readDouble();
        this.againstUser = in.readInt();
        this.againstUserName = in.readString();
        this.againstUserPortrait = in.readString();
        this.batchCode = in.readString();
        this.bigVarietyType = in.readString();
        this.coinType = in.readInt();
        this.createTime = in.readLong();
        this.endTime = in.readLong();
        this.endline = in.readInt();
        this.gameStatus = in.readInt();
        this.id = in.readInt();
        this.launchPraise = in.readInt();
        this.launchScore = in.readDouble();
        this.launchUnwindScore = in.readDouble();
        this.launchUser = in.readInt();
        this.launchUserName = in.readString();
        this.launchUserPortrait = in.readString();
        this.modifyTime = in.readLong();
        this.reward = in.readInt();
        this.startTime = in.readLong();
        this.varietyId = in.readInt();
        this.varietyName = in.readString();
        this.varietyType = in.readString();
        this.gameType = in.readInt();
        this.winResult = in.readInt();
        this.commission = in.readDouble();
        this.type = in.readInt();
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
