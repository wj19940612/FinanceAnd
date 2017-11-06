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
    public static final int GAME_TYPE_MATCH = 2;


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


    private int battleId;           //对战记录id
    private int currentPraise;      //被点赞的用户当前的被赞数
    private int praiseUserId;       //被点赞的用户ID
    /* 以上为点赞 Push#Content#Data 数据机构 */


    /**
     * 创建订单的推送 数据
     * <p>
     * "battleBatchCode":"ibYUtouN",
     * "battleId":807,
     * "contractsCode":"CL1708",
     * "contractsId":120,
     * "currencyUnit":"美元",
     * "direction":1,
     * "handsNum":1,
     * "id":2934,
     * "marketPoint":2,
     * "optLogCount":40,
     * "orderMarket":45780,
     * "orderPrice":45.78,
     * "orderStatus":2,
     * "orderTime":1499842365903,
     * "ratio":7.5,
     * "sign":"$",
     * "userId":800184,
     * "varietyId":1,
     * "varietyName":"美原油",
     * "varietyType":"CL"
     */
    //下单 推送 属性
    private String battleBatchCode;   //  批次号码 lXGrHpSk
    // private int battleId;         // 点赞数据已加
    private String contractsCode;     // CL1708  合约代码
    private int contractsId;
    private String currencyUnit;     // 美元 币种单位
    private int direction;           //买入方向  1 买涨  0买跌
    private int handsNum;
    // private int id;                // 上面数据已加
    private int marketPoint;         //	行情小数位数
    private int optLogCount;         // 30  订单房间操作次数
    private double orderMarket;      // 44160  下单市值
    private double orderPrice;       // 44.16  下单价格
    private int orderStatus;         // 订单状态    -1 失败  0 代支付  1 已支付，待持仓  2 持仓中  3 平仓处理中  4 结算完成
    private long orderTime;          // 下单时间
    private double ratio;            // 汇率
    private String sign;             // $ 币种符号
    private int userId;
    // private int varietyId;
    // private String varietyName;
    // private String varietyType; // 这 3 对战数据里面有

    // 平仓 新加 推送属性
    private double unwindMarket;
    private double unwindPrice;     // 44.16  平仓价格
    private long unwindTime;        // 1499777803000  平仓时间
    private int unwindType;         // 1 用户平仓 2 超时平仓
    private double winOrLoss;       // 用户盈亏

    /**
     * 平仓的推送 数据
     * "battleBatchCode":"ibYUtouN",
     * "battleId":807,
     * "contractsCode":"CL1708",
     * "contractsId":120,
     * "createTime":1499841976000,
     * "currencyUnit":"美元",
     * "direction":1,
     * "handsNum":1,
     * "id":2923,
     * "marketPoint":2,
     * "modifyTime":1499842155000,
     * "optLogCount":33,
     * "orderMarket":45850,
     * "orderPrice":45.85,
     * "orderStatus":4,
     * "orderTime":1499841976000,
     * "ratio":7.5,
     * "sign":"$",
     * "unwindMarket":45810,
     * "unwindPrice":45.81,
     * "unwindTime":1499842155000,
     * "unwindType":1,
     * "userId":800184,
     * "varietyId":1,
     * "varietyName":"美原油",
     * "varietyType":"CL",
     * "winOrLoss":-40
     */

    /* 订单数据结构
    * private String battleBatchCode;
    private int battleId;
    private String contractsCode;
    private int contractsId;
    private long createTime;
    private int direction;
    private int handsNum;
    private int id;
    private long modifyTime;
    private double orderPrice;
    private int orderStatus;
    private long orderTime;
    private int userId;
    private int varietyId;
    private String varietyName;
    private String varietyType;
    * */

    /* 交易记录数据结构
    private int handsNum;
    private double optPrice;
    private int optStatus;
    private long optTime;
    private int userId;
    private String contractsCode;
    private String varietyName;
    private String varietyType;
    private int marketPoint;
    */

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

    public double getUnwindPrice() {
        return unwindPrice;
    }

    public long getUnwindTime() {
        return unwindTime;
    }

    public int getUnwindType() {
        return unwindType;
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

    public int getMarketPoint() {
        return marketPoint;
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

    public int getGameType() {
        return gameType;
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
                ", battleId=" + battleId +
                ", currentPraise=" + currentPraise +
                ", praiseUserId=" + praiseUserId +
                ", battleBatchCode='" + battleBatchCode + '\'' +
                ", contractsCode='" + contractsCode + '\'' +
                ", contractsId=" + contractsId +
                ", currencyUnit='" + currencyUnit + '\'' +
                ", direction=" + direction +
                ", handsNum=" + handsNum +
                ", marketPoint=" + marketPoint +
                ", optLogCount=" + optLogCount +
                ", orderMarket=" + orderMarket +
                ", orderPrice=" + orderPrice +
                ", orderStatus=" + orderStatus +
                ", orderTime=" + orderTime +
                ", ratio=" + ratio +
                ", sign='" + sign + '\'' +
                ", userId=" + userId +
                ", unwindMarket=" + unwindMarket +
                ", unwindPrice=" + unwindPrice +
                ", unwindTime=" + unwindTime +
                ", unwindType=" + unwindType +
                ", winOrLoss=" + winOrLoss +
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
        dest.writeInt(this.battleId);
        dest.writeInt(this.currentPraise);
        dest.writeInt(this.praiseUserId);
        dest.writeString(this.battleBatchCode);
        dest.writeString(this.contractsCode);
        dest.writeInt(this.contractsId);
        dest.writeString(this.currencyUnit);
        dest.writeInt(this.direction);
        dest.writeInt(this.handsNum);
        dest.writeInt(this.marketPoint);
        dest.writeInt(this.optLogCount);
        dest.writeDouble(this.orderMarket);
        dest.writeDouble(this.orderPrice);
        dest.writeInt(this.orderStatus);
        dest.writeLong(this.orderTime);
        dest.writeDouble(this.ratio);
        dest.writeString(this.sign);
        dest.writeInt(this.userId);
        dest.writeDouble(this.unwindMarket);
        dest.writeDouble(this.unwindPrice);
        dest.writeLong(this.unwindTime);
        dest.writeInt(this.unwindType);
        dest.writeDouble(this.winOrLoss);
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
        this.battleId = in.readInt();
        this.currentPraise = in.readInt();
        this.praiseUserId = in.readInt();
        this.battleBatchCode = in.readString();
        this.contractsCode = in.readString();
        this.contractsId = in.readInt();
        this.currencyUnit = in.readString();
        this.direction = in.readInt();
        this.handsNum = in.readInt();
        this.marketPoint = in.readInt();
        this.optLogCount = in.readInt();
        this.orderMarket = in.readDouble();
        this.orderPrice = in.readDouble();
        this.orderStatus = in.readInt();
        this.orderTime = in.readLong();
        this.ratio = in.readDouble();
        this.sign = in.readString();
        this.userId = in.readInt();
        this.unwindMarket = in.readDouble();
        this.unwindPrice = in.readDouble();
        this.unwindTime = in.readLong();
        this.unwindType = in.readInt();
        this.winOrLoss = in.readDouble();
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
