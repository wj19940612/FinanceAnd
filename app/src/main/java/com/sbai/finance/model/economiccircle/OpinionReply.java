package com.sbai.finance.model.economiccircle;

/**
 * Created by lixiaokuan0819 on 2017/4/28.
 */

public class OpinionReply {

	/**
	 * content : 好好好
	 * createTime : 1
	 * id : 1
	 * praiseCount : 10
	 * userId : 1
	 * userName : yehx
	 * userPortrait : order/order.png
	 */

	private String content;
	private int createTime;
	private int id;
	private int praiseCount;
	private int userId;
	private String userName;
	private String userPortrait;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getCreateTime() {
		return createTime;
	}

	public void setCreateTime(int createTime) {
		this.createTime = createTime;
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
}
