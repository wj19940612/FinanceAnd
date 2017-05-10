package com.sbai.finance.model.economiccircle;

/**
 * Created by lixiaokuan0819 on 2017/4/28.
 */

public class OpinionReply {


	/**
	 * auditActorId : 0
	 * auditStatus : 2
	 * auditTime : 1493894671000
	 * content : 转账中
	 * createTime : 1493894566000
	 * id : 212
	 * isAttention : 1
	 * isPraise : 0
	 * praiseCount : 0
	 * replyCount : 0
	 * type : 1
	 * updateTime : 1493895431000
	 * userId : 104
	 * userName : 用户313
	 * viewpointId : 106
	 * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1493791711157.png
	 */

	private int auditActorId;
	private int auditStatus;
	private long auditTime;
	private String content;
	private long createTime;
	private int id;
	private int isAttention;
	private int isPraise;
	private int praiseCount;
	private int replyCount;
	private int type;
	private long updateTime;
	private int userId;
	private String userName;
	private int viewpointId;
	private String userPortrait;

	public int getAuditActorId() {
		return auditActorId;
	}

	public void setAuditActorId(int auditActorId) {
		this.auditActorId = auditActorId;
	}

	public int getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(int auditStatus) {
		this.auditStatus = auditStatus;
	}

	public long getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(long auditTime) {
		this.auditTime = auditTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIsAttention() {
		return isAttention;
	}

	public void setIsAttention(int isAttention) {
		this.isAttention = isAttention;
	}

	public int getIsPraise() {
		return isPraise;
	}

	public void setIsPraise(int isPraise) {
		this.isPraise = isPraise;
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

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
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

	public int getViewpointId() {
		return viewpointId;
	}

	public void setViewpointId(int viewpointId) {
		this.viewpointId = viewpointId;
	}

	public String getUserPortrait() {
		return userPortrait;
	}

	public void setUserPortrait(String userPortrait) {
		this.userPortrait = userPortrait;
	}

	@Override
	public String toString() {
		return "OpinionReply{" +
				"auditActorId=" + auditActorId +
				", auditStatus=" + auditStatus +
				", auditTime=" + auditTime +
				", content='" + content + '\'' +
				", createTime=" + createTime +
				", id=" + id +
				", isAttention=" + isAttention +
				", isPraise=" + isPraise +
				", praiseCount=" + praiseCount +
				", replyCount=" + replyCount +
				", type=" + type +
				", updateTime=" + updateTime +
				", userId=" + userId +
				", userName='" + userName + '\'' +
				", viewpointId=" + viewpointId +
				", userPortrait='" + userPortrait + '\'' +
				'}';
	}
}
