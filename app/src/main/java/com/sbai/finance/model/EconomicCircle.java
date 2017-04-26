package com.sbai.finance.model;

/**
 * 经济圈首页列表
 */

public class EconomicCircle {

	/**
	 * bigVarietyTypeCode : future
	 * bigVarietyTypeName : 期货
	 * content : 看涨
	 * contentImg : order/order.png
	 * contractsCode : CL1706
	 * createTime : 2017-09-08 00:00:00
	 * dataId : 1
	 * days : 10
	 * direction : 1
	 * guessPass : 0
	 * interest : 10
	 * isAttention : 1
	 * money : 10
	 * praiseCount : 10
	 * replyCount : 10
	 * type : 1
	 * userId : 1
	 * userName : yehx
	 * userPortrait : order/order.png
	 * varietyId : 1
	 * varietyName : 美原油
	 * varietyType : CL
	 */

	private String bigVarietyTypeCode;
	private String bigVarietyTypeName;
	private String content;
	private String contentImg;
	private String contractsCode;
	private long createTime;
	private int dataId;
	private int days;
	private int direction;
	private int guessPass;
	private int interest;
	private int isAttention;
	private int money;
	private int praiseCount;
	private int replyCount;
	private int type;
	private int userId;
	private String userName;
	private String userPortrait;
	private int varietyId;
	private String varietyName;
	private String varietyType;

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContentImg() {
		return contentImg;
	}

	public void setContentImg(String contentImg) {
		this.contentImg = contentImg;
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

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
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

	public int getInterest() {
		return interest;
	}

	public void setInterest(int interest) {
		this.interest = interest;
	}

	public int getIsAttention() {
		return isAttention;
	}

	public void setIsAttention(int isAttention) {
		this.isAttention = isAttention;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
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

	@Override
	public String toString() {
		return "EconomicCircle{" +
				"bigVarietyTypeCode='" + bigVarietyTypeCode + '\'' +
				", bigVarietyTypeName='" + bigVarietyTypeName + '\'' +
				", content='" + content + '\'' +
				", contentImg='" + contentImg + '\'' +
				", contractsCode='" + contractsCode + '\'' +
				", createTime='" + createTime + '\'' +
				", dataId=" + dataId +
				", days=" + days +
				", direction=" + direction +
				", guessPass=" + guessPass +
				", interest=" + interest +
				", isAttention=" + isAttention +
				", money=" + money +
				", praiseCount=" + praiseCount +
				", replyCount=" + replyCount +
				", type=" + type +
				", userId=" + userId +
				", userName='" + userName + '\'' +
				", userPortrait='" + userPortrait + '\'' +
				", varietyId=" + varietyId +
				", varietyName='" + varietyName + '\'' +
				", varietyType='" + varietyType + '\'' +
				'}';
	}
}
