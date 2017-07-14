package com.sbai.finance.model.economiccircle;

import android.os.Parcel;
import android.os.Parcelable;

public class EconomicCircle implements Parcelable {

	//1借钱 2观点 3期货对战

	public static final int TYPE_BORROW_MONEY = 1;
	public static final int TYPE_OPINION = 2;
	public static final int TYPE_FUTURES_BATTLE = 3;

	//赏金类型 0现金 1元宝 2积分
	public static final int COIN_TYPE_CASH = 1;
	public static final int COIN_TYPE_INGOT = 2;
	public static final int COIN_TYPE_INTEGRAL = 3;

	//游戏状态 0取消 1发起 2对战开始 3对战结束
	public static final int GAME_STATUS_CANCELED = 0;
	public static final int GAME_STATUS_CREATED = 1;
	public static final int GAME_STATUS_STARTED = 2;
	public static final int GAME_STATUS_END = 3;


	private int type;
	private int dataId;
	private int userId;
	private int auditStatus; //1 未审核 2 审核通过 0 不通过
	private String content;
	private long createTime;
	private String id;
	private int isAttention;
	private String land;
	private String userName;
	private String userPortrait;

	// 借款相关字段
	private double money;
	private double interest;
	private int days;
	private String location;
	private String contentImg;

	// 观点相关
	private String varietyName;
	private String contractsCode;
	private String bigVarietyTypeName;
	private String bigVarietyTypeCode;
	private int direction;
	private int varietyId;
	private String varietyType;
	private int praiseCount;
	private int replyCount;
	private int guessPass; //预测  0等待结果  1成功  2失败
	private String lastPrice;
	private String risePre;
	private String risePrice;

	// 游戏对战相关
	private String batchCode; // '批次号码， 也用于短连接。',
	private int coinType;// '对战类型， 1，现金，2，元宝， 3积分',
	private int reward; //'赏金',
	private int endline;// '对战时长， 按照s 来计算',
	private int gameStatus;// '1 发起， 2，开始， 3 结束。0,取消.',
	private int launchUser;// '发起用户',
	private double launchScore; //'发起用户得分',
	private int launchPraise;// '发起用户被赞次数',
	private int againstUser;// '迎战人',
	private String againstFrom; // '迎战着来源来源',
	private double againstScore; //'应战者得分',
	private int againstPraise;// '应战者被点赞次数',
	private long startTime; //'开始时间',
	private long endTime; //'结束时间',
	private int winResult; // 胜利的人 0:平手， 1，发起人赢，2，对战者赢
	private String againstUserName; // 迎战人名称
	private String againstUserPortrait; //  迎战人头像'

	public int getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(int auditStatus) {
		this.auditStatus = auditStatus;
	}

	public String getBigVarietyTypeCode() {
		return bigVarietyTypeCode;
	}

	public void setBigVarietyTypeCode(String bigVarietyTypeCode) {
		this.bigVarietyTypeCode = bigVarietyTypeCode;
	}

	public String getBigVarietyTypeName() {
		return bigVarietyTypeName;
	}

	public void setBigVarietyTypeName(String bigVarietyTypeName) {
		this.bigVarietyTypeName = bigVarietyTypeName;
	}

	public String getContractsCode() {
		return contractsCode;
	}

	public void setContractsCode(String contractsCode) {
		this.contractsCode = contractsCode;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getDataId() {
		return dataId;
	}

	public void setDataId(int dataId) {
		this.dataId = dataId;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getGuessPass() {
		return guessPass;
	}

	public void setGuessPass(int guessPass) {
		this.guessPass = guessPass;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getIsAttention() {
		return isAttention;
	}

	public void setIsAttention(int isAttention) {
		this.isAttention = isAttention;
	}

	public int getPraiseCount() {
		return praiseCount;
	}

	public void setPraiseCount(int praiseCount) {
		this.praiseCount = praiseCount;
	}

	public int getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPortrait() {
		return userPortrait;
	}

	public void setUserPortrait(String userPortrait) {
		this.userPortrait = userPortrait;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLand() {
		return land;
	}

	public void setLand(String land) {
		this.land = land;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public double getInterest() {
		return interest;
	}

	public void setInterest(double interest) {
		this.interest = interest;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public String getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(String lastPrice) {
		this.lastPrice = lastPrice;
	}

	public String getRisePre() {
		return risePre;
	}

	public void setRisePre(String risePre) {
		this.risePre = risePre;
	}

	public String getRisePrice() {
		return risePrice;
	}

	public void setRisePrice(String risePrice) {
		this.risePrice = risePrice;
	}

	public String getContentImg() {
		return contentImg;
	}

	public void setContentImg(String contentImg) {
		this.contentImg = contentImg;
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

	public int getReward() {
		return reward;
}

	public void setReward(int reward) {
		this.reward = reward;
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

	public int getWinResult() {
		return winResult;
	}

	public void setWinResult(int winResult) {
		this.winResult = winResult;
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

	@Override
	public String toString() {
		return "EconomicCircle{" +
				"auditStatus=" + auditStatus +
				", bigVarietyTypeCode='" + bigVarietyTypeCode + '\'' +
				", bigVarietyTypeName='" + bigVarietyTypeName + '\'' +
				", contractsCode='" + contractsCode + '\'' +
				", createTime=" + createTime +
				", dataId=" + dataId +
				", direction=" + direction +
				", guessPass=" + guessPass +
				", id='" + id + '\'' +
				", isAttention=" + isAttention +
				", praiseCount=" + praiseCount +
				", replyCount=" + replyCount +
				", type=" + type +
				", userId=" + userId +
				", userName='" + userName + '\'' +
				", userPortrait='" + userPortrait + '\'' +
				", varietyId=" + varietyId +
				", varietyName='" + varietyName + '\'' +
				", varietyType='" + varietyType + '\'' +
				", content='" + content + '\'' +
				", land='" + land + '\'' +
				", location='" + location + '\'' +
				", money=" + money +
				", interest=" + interest +
				", days=" + days +
				", lastPrice='" + lastPrice + '\'' +
				", risePre='" + risePre + '\'' +
				", risePrice='" + risePrice + '\'' +
				", contentImg='" + contentImg + '\'' +
				", batchCode='" + batchCode + '\'' +
				", coinType=" + coinType +
				", reward=" + reward +
				", endline=" + endline +
				", gameStatus=" + gameStatus +
				", launchUser=" + launchUser +
				", launchScore=" + launchScore +
				", launchPraise=" + launchPraise +
				", againstUser=" + againstUser +
				", againstFrom='" + againstFrom + '\'' +
				", againstScore=" + againstScore +
				", againstPraise=" + againstPraise +
				", startTime=" + startTime +
				", endTime=" + endTime +
				", winResult=" + winResult +
				", againstUserName='" + againstUserName + '\'' +
				", againstUserPortrait='" + againstUserPortrait + '\'' +
				'}';
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.auditStatus);
		dest.writeString(this.bigVarietyTypeCode);
		dest.writeString(this.bigVarietyTypeName);
		dest.writeString(this.contractsCode);
		dest.writeLong(this.createTime);
		dest.writeInt(this.dataId);
		dest.writeInt(this.direction);
		dest.writeInt(this.guessPass);
		dest.writeString(this.id);
		dest.writeInt(this.isAttention);
		dest.writeInt(this.praiseCount);
		dest.writeInt(this.replyCount);
		dest.writeInt(this.type);
		dest.writeInt(this.userId);
		dest.writeString(this.userName);
		dest.writeString(this.userPortrait);
		dest.writeInt(this.varietyId);
		dest.writeString(this.varietyName);
		dest.writeString(this.varietyType);
		dest.writeString(this.content);
		dest.writeString(this.land);
		dest.writeString(this.location);
		dest.writeDouble(this.money);
		dest.writeDouble(this.interest);
		dest.writeInt(this.days);
		dest.writeString(this.lastPrice);
		dest.writeString(this.risePre);
		dest.writeString(this.risePrice);
		dest.writeString(this.contentImg);
		dest.writeString(this.batchCode);
		dest.writeInt(this.coinType);
		dest.writeInt(this.reward);
		dest.writeInt(this.endline);
		dest.writeInt(this.gameStatus);
		dest.writeInt(this.launchUser);
		dest.writeDouble(this.launchScore);
		dest.writeInt(this.launchPraise);
		dest.writeInt(this.againstUser);
		dest.writeString(this.againstFrom);
		dest.writeDouble(this.againstScore);
		dest.writeInt(this.againstPraise);
		dest.writeLong(this.startTime);
		dest.writeLong(this.endTime);
		dest.writeInt(this.winResult);
		dest.writeString(this.againstUserName);
		dest.writeString(this.againstUserPortrait);
	}

	public EconomicCircle() {
	}

	protected EconomicCircle(Parcel in) {
		this.auditStatus = in.readInt();
		this.bigVarietyTypeCode = in.readString();
		this.bigVarietyTypeName = in.readString();
		this.contractsCode = in.readString();
		this.createTime = in.readLong();
		this.dataId = in.readInt();
		this.direction = in.readInt();
		this.guessPass = in.readInt();
		this.id = in.readString();
		this.isAttention = in.readInt();
		this.praiseCount = in.readInt();
		this.replyCount = in.readInt();
		this.type = in.readInt();
		this.userId = in.readInt();
		this.userName = in.readString();
		this.userPortrait = in.readString();
		this.varietyId = in.readInt();
		this.varietyName = in.readString();
		this.varietyType = in.readString();
		this.content = in.readString();
		this.land = in.readString();
		this.location = in.readString();
		this.money = in.readDouble();
		this.interest = in.readDouble();
		this.days = in.readInt();
		this.lastPrice = in.readString();
		this.risePre = in.readString();
		this.risePrice = in.readString();
		this.contentImg = in.readString();
		this.batchCode = in.readString();
		this.coinType = in.readInt();
		this.reward = in.readInt();
		this.endline = in.readInt();
		this.gameStatus = in.readInt();
		this.launchUser = in.readInt();
		this.launchScore = in.readDouble();
		this.launchPraise = in.readInt();
		this.againstUser = in.readInt();
		this.againstFrom = in.readString();
		this.againstScore = in.readDouble();
		this.againstPraise = in.readInt();
		this.startTime = in.readLong();
		this.endTime = in.readLong();
		this.winResult = in.readInt();
		this.againstUserName = in.readString();
		this.againstUserPortrait = in.readString();
	}

	public static final Creator<EconomicCircle> CREATOR = new Creator<EconomicCircle>() {
		@Override
		public EconomicCircle createFromParcel(Parcel source) {
			return new EconomicCircle(source);
		}

		@Override
		public EconomicCircle[] newArray(int size) {
			return new EconomicCircle[size];
		}
	};
}
