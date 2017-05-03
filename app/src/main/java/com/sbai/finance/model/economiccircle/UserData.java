package com.sbai.finance.model.economiccircle;

/**
 * Created by lixiaokuan0819 on 2017/5/2.
 */

public class UserData {

	/**
	 * certificationStatus : 1
	 * land : 测试内容tr6h
	 * userName : 测试内容9y48
	 * userPhone : 测试内容8778
	 * userPortrait : 测试内容c677
	 * userSex : 85187
	 * user_sign : 测试内容9418
	 */

	private int certificationStatus;
	private String land;
	private String userName;
	private String userPhone;
	private String userPortrait;
	private int userSex;
	private String user_sign;

	public int getCertificationStatus() {
		return certificationStatus;
	}

	public void setCertificationStatus(int certificationStatus) {
		this.certificationStatus = certificationStatus;
	}

	public String getLand() {
		return land;
	}

	public void setLand(String land) {
		this.land = land;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getUserPortrait() {
		return userPortrait;
	}

	public void setUserPortrait(String userPortrait) {
		this.userPortrait = userPortrait;
	}

	public int getUserSex() {
		return userSex;
	}

	public void setUserSex(int userSex) {
		this.userSex = userSex;
	}

	public String getUser_sign() {
		return user_sign;
	}

	public void setUser_sign(String user_sign) {
		this.user_sign = user_sign;
	}

	@Override
	public String toString() {
		return "UserData{" +
				"certificationStatus=" + certificationStatus +
				", land='" + land + '\'' +
				", userName='" + userName + '\'' +
				", userPhone='" + userPhone + '\'' +
				", userPortrait='" + userPortrait + '\'' +
				", userSex=" + userSex +
				", user_sign='" + user_sign + '\'' +
				'}';
	}
}
