package com.sbai.finance.model.economiccircle;

public class GoodHeartPeople {

	/**
	 * location : 测试内容i457
	 * portrait : 测试内容4y5d
	 * userId : 48233
	 * userName : 测试内容5xl1
	 */

	private String location;
	private String portrait;
	private int userId;
	private String userName;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPortrait() {
		return portrait;
	}

	public void setPortrait(String portrait) {
		this.portrait = portrait;
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

	@Override
	public String toString() {
		return "GoodHeartPeople{" +
				"location='" + location + '\'' +
				", portrait='" + portrait + '\'' +
				", userId=" + userId +
				", userName='" + userName + '\'' +
				'}';
	}
}
