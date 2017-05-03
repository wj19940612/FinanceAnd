package com.sbai.finance.model.economiccircle;

import java.io.Serializable;

/**
 * Created by lixiaokuan0819 on 2017/4/28.
 */

public class OpinionDetails implements Serializable{

	/**
	 * bigVarietyTypeCode : future
	 * bigVarietyTypeName : 期货
	 * content : 看涨
	 * contractsCode : CL1706
	 * createTime : 2017-09-08 00:00:00
	 * direction : 1
	 * guessPass : 0
	 * id : 1
	 * praiseCount : 10
	 * replyCount : 10
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
	private String contractsCode;
	private long createTime;
	private int direction;
	private int guessPass;
	private int id;
	private int praiseCount;
	private int replyCount;
	private int userId;
	private String userName;
	private String userPortrait;
	private int varietyId;
	private String varietyName;
	private String varietyType;
	private int isAttention;

	public int getIsAttention() {
		return isAttention;
	}

	public void setIsAttention(int isAttention) {
		this.isAttention = isAttention;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
		return "OpinionDetails{" +
				"bigVarietyTypeCode='" + bigVarietyTypeCode + '\'' +
				", bigVarietyTypeName='" + bigVarietyTypeName + '\'' +
				", content='" + content + '\'' +
				", contractsCode='" + contractsCode + '\'' +
				", createTime=" + createTime +
				", direction=" + direction +
				", guessPass=" + guessPass +
				", id=" + id +
				", praiseCount=" + praiseCount +
				", replyCount=" + replyCount +
				", userId=" + userId +
				", userName='" + userName + '\'' +
				", userPortrait='" + userPortrait + '\'' +
				", varietyId=" + varietyId +
				", varietyName='" + varietyName + '\'' +
				", varietyType='" + varietyType + '\'' +
				", isAttention=" + isAttention +
				'}';
	}
}
