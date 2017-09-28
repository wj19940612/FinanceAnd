package com.sbai.finance.model.miss;

import java.io.Serializable;

/**
 * Created by lixiaokuan0819 on 2017/7/30.
 */

public class Question implements Serializable {

	/**
	 * answerContext : blob:http://var.esongbai.xyz/ed0ea7b6-bd51-4ff1-a631-864d01b9f4c8
	 * answerCustomId : 6
	 * askSavant : 0
	 * awardCount : 0
	 * awardMoney : 0
	 * createTime : 1501570628000
	 * customPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1501549863096062105.png
	 * id : 9
	 * isPrise : 0
	 * listenCount : 0
	 * priseCount : 0
	 * questionContext : 哈个
	 * questionUserId : 145
	 * replyTime : 1501576566000
	 * show : 0
	 * solve : 0
	 * updateTime : 1501576566000
	 * userName : 抽象工作室
	 * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494325795572.png
	 * appointCustomId : 6
	 * replyCount : 6
	 */

	private String answerContext;
	private int answerCustomId;
	private int askSavant;
	private int awardCount;
	private int awardMoney;
	private long createTime;
	private String customPortrait;
	private int id;
	private int isPrise;
	private int listenCount;
	private int priseCount;
	private String questionContext;
	private int questionUserId;
	private long replyTime;
	private int show;
	private int solve;
	private long updateTime;
	private String userName;
	private String userPortrait;
	private int appointCustomId;
	private int replyCount;
	private int soundTime;
	private boolean isPlaying;
	private boolean progressIsZero = true;

	public boolean isProgressIsZero() {
		return progressIsZero;
	}

	public void setProgressIsZero(boolean progressIsZero) {
		this.progressIsZero = progressIsZero;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean playing) {
		isPlaying = playing;
	}

	public int getSoundTime() {
		return soundTime;
	}

	public void setSoundTime(int soundTime) {
		this.soundTime = soundTime;
	}

	public String getAnswerContext() {
		return answerContext;
	}

	public void setAnswerContext(String answerContext) {
		this.answerContext = answerContext;
	}

	public int getAnswerCustomId() {
		return answerCustomId;
	}

	public void setAnswerCustomId(int answerCustomId) {
		this.answerCustomId = answerCustomId;
	}

	public int getAskSavant() {
		return askSavant;
	}

	public void setAskSavant(int askSavant) {
		this.askSavant = askSavant;
	}

	public int getAwardCount() {
		return awardCount;
	}

	public void setAwardCount(int awardCount) {
		this.awardCount = awardCount;
	}

	public int getAwardMoney() {
		return awardMoney;
	}

	public void setAwardMoney(int awardMoney) {
		this.awardMoney = awardMoney;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getCustomPortrait() {
		return customPortrait;
	}

	public void setCustomPortrait(String customPortrait) {
		this.customPortrait = customPortrait;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIsPrise() {
		return isPrise;
	}

	public void setIsPrise(int isPrise) {
		this.isPrise = isPrise;
	}

	public int getListenCount() {
		return listenCount;
	}

	public void setListenCount(int listenCount) {
		this.listenCount = listenCount;
	}

	public int getPriseCount() {
		return priseCount;
	}

	public void setPriseCount(int priseCount) {
		this.priseCount = priseCount;
	}

	public String getQuestionContext() {
		return questionContext;
	}

	public void setQuestionContext(String questionContext) {
		this.questionContext = questionContext;
	}

	public int getQuestionUserId() {
		return questionUserId;
	}

	public void setQuestionUserId(int questionUserId) {
		this.questionUserId = questionUserId;
	}

	public long getReplyTime() {
		return replyTime;
	}

	public void setReplyTime(long replyTime) {
		this.replyTime = replyTime;
	}

	public int getShow() {
		return show;
	}

	public void setShow(int show) {
		this.show = show;
	}

	public int getSolve() {
		return solve;
	}

	public void setSolve(int solve) {
		this.solve = solve;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
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

	public int getAppointCustomId() {
		return appointCustomId;
	}

	public void setAppointCustomId(int appointCustomId) {
		this.appointCustomId = appointCustomId;
	}

	public int getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}

	@Override
	public String toString() {
		return "Question{" +
				"answerContext='" + answerContext + '\'' +
				", answerCustomId=" + answerCustomId +
				", askSavant=" + askSavant +
				", awardCount=" + awardCount +
				", awardMoney=" + awardMoney +
				", createTime=" + createTime +
				", customPortrait='" + customPortrait + '\'' +
				", id=" + id +
				", isPrise=" + isPrise +
				", listenCount=" + listenCount +
				", priseCount=" + priseCount +
				", questionContext='" + questionContext + '\'' +
				", questionUserId=" + questionUserId +
				", replyTime=" + replyTime +
				", show=" + show +
				", solve=" + solve +
				", updateTime=" + updateTime +
				", userName='" + userName + '\'' +
				", userPortrait='" + userPortrait + '\'' +
				", appointCustomId=" + appointCustomId +
				", replyCount=" + replyCount +
				", soundTime=" + soundTime +
				", isPlaying=" + isPlaying +
				'}';
	}
}
