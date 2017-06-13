package com.sbai.finance.model.economiccircle;

/**
 * Created by lixiaokuan0819 on 2017/5/10.
 */

public class BorrowMoneyDetails {

	/**
	 * aduitActorId : 0
	 * auditTime : 1494296441000
	 * confirmTime : 1494296441000
	 * content : 最近手头紧，第3次
	 * contentImg : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494228851372.png
	 * createDate : 1494228890000
	 * days : 35
	 * endlineTime : 1494296441000
	 * failMsg : 借单撤销
	 * id : 51
	 * intentionCount : 0
	 * interest : 30
	 * isAttention : 1
	 * location : 天津市-天津市-静海县
	 * modifyDate : 1494304209000
	 * money : 1800
	 * portrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494305494899.png
	 * remark : 我看了
	 * selectedLocation : null-北京市-东城区
	 * selectedPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1493109274765.png
	 * selectedUserId : 19
	 * selectedUserName : 用户59
	 * sex : 0
	 * status : 8
	 * userId : 150
	 * userName : 三十个黄三
	 */
	private String content;
	private long createDate;
	private int days;
	private int id;
	private double interest;
	private int isAttention;
	private double money;
	private String portrait;
	private int status;
	private int userId;
	private String userName;
	private String contentImg;
	private String location;
	private int confirmDays;
	private String phoneNum;

	public int getIsIntention() {
		return isIntention;
	}

	public void setIsIntention(int isIntention) {
		this.isIntention = isIntention;
	}

	private int isIntention;
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public String getPortrait() {
		return portrait;
	}

	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public String getContentImg() {
		return contentImg;
	}

	public void setContentImg(String contentImg) {
		this.contentImg = contentImg;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getConfirmDays() {
		return confirmDays;
	}

	public void setConfirmDays(int confirmDays) {
		this.confirmDays = confirmDays;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
}
