package com.sbai.finance.model.economiccircle;

/**
 * Created by lixiaokuan0819 on 2017/5/9.
 */

public class BorrowMoney {

	/**
	 * auditStatus : 2
	 * content : 最近手头紧，第3次
	 * contentImg : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494208903839.png
	 * createTime : 1494208903969
	 * dataId : 50
	 * days : 35
	 * endlineTime : 1494295527032
	 * id : 590fd1873c81a76de0efe813
	 * interest : 30
	 * isAttention : 1
	 * land : null-北京市-东城区
	 * money : 1800
	 * type : 1
	 * userId : 19
	 * userName : 用户59
	 * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1493109274765.png
	 */

	private int auditStatus;
	private String content;
	private String contentImg;
	private long createTime;
	private int dataId;
	private int days;
	private long endlineTime;
	private String id;
	private double interest;
	private int isAttention;
	private String land;
	private double money;
	private int type;
	private int userId;
	private String userName;
	private String userPortrait;

	public int getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(int auditStatus) {
		this.auditStatus = auditStatus;
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

	public long getEndlineTime() {
		return endlineTime;
	}

	public void setEndlineTime(long endlineTime) {
		this.endlineTime = endlineTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getInterest() {
		return interest;
	}

	public void setInterest(double interest) {
		this.interest = interest;
	}

	public int getIsAttention() {
		return isAttention;
	}

	public void setIsAttention(int isAttention) {
		this.isAttention = isAttention;
	}

	public String getLand() {
		return land;
	}

	public void setLand(String land) {
		this.land = land;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
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

	@Override
	public String toString() {
		return "BorrowMoney{" +
				"auditStatus=" + auditStatus +
				", content='" + content + '\'' +
				", contentImg='" + contentImg + '\'' +
				", createTime=" + createTime +
				", dataId=" + dataId +
				", days=" + days +
				", endlineTime=" + endlineTime +
				", id='" + id + '\'' +
				", interest=" + interest +
				", isAttention=" + isAttention +
				", land='" + land + '\'' +
				", money=" + money +
				", type=" + type +
				", userId=" + userId +
				", userName='" + userName + '\'' +
				", userPortrait='" + userPortrait + '\'' +
				'}';
	}
}
