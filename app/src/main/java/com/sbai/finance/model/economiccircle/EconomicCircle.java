package com.sbai.finance.model.economiccircle;

import java.io.Serializable;

public class EconomicCircle implements Serializable {

	//赏金类型 0现金 1元宝 2积分
	public static final int COIN_TYPE_CASH = 1;
	public static final int COIN_TYPE_INGOT = 2;
	public static final int COIN_TYPE_INTEGRAL = 3;

	//游戏状态 0取消 1发起 2对战开始 3对战结束
	public static final int GAME_STATUS_CANCELED = 0;
	public static final int GAME_STATUS_CREATED = 1;
	public static final int GAME_STATUS_STARTED = 2;
	public static final int GAME_STATUS_END = 3;

	private int auditStatus;
	private String bigVarietyTypeCode;
	private String bigVarietyTypeName;
	private String contractsCode;
	private long createTime;
	private int dataId;
	private int direction;
	private int guessPass;
	private String id;
	private int isAttention;
	private int praiseCount;
	private int replyCount;
	private int type;
	private int userId;
	private String userName;
	private String userPortrait;
	private int varietyId;
	private String varietyName;
	private String varietyType;
	private String content;
	private String land;
	private String location;
	private double money;
	private double interest;
	private int days;
	private String lastPrice;
	private String risePre;
	private String risePrice;
	private String contentImg;
	private int coinType;// '对战类型， 1，现金，2，元宝， 3积分',
	private int reward; //'赏金',
	private int gameStatus;// '1 发起， 2，开始， 3 结束。0,取消.',
	private String againstUserName; // 迎战人名称
	private String againstUserPortrait; //  迎战人头像
	private int endline;// '对战时长， 按照s 来计算',
	private int winResult; // 胜利的人 0:平手， 1，发起人赢，2，对战者赢
	private double launchScore; //'发起用户得分'
	private double againstScore; //'应战者得分'

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

	public int getWinResult() {
		return winResult;
	}

	public void setWinResult(int winResult) {
		this.winResult = winResult;
	}

	public int getEndline() {
		return endline;
	}

	public void setEndline(int endline) {
		this.endline = endline;
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

	public int getGameStatus() {
		return gameStatus;
	}

	public void setGameStatus(int gameStatus) {
		this.gameStatus = gameStatus;
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

	public String getContentImg() {
		return contentImg;
	}

	public void setContentImg(String contentImg) {
		this.contentImg = contentImg;
	}

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
				'}';
	}
}
