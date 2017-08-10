package com.sbai.finance.model.train;

import java.util.List;

/**
 * Created by lixiaokuan0819 on 2017/8/10.
 */

public class TrainDetail {

	/**
	 * classifies : []
	 * targets : [{"createTime":"2017-08-02 13:58:03","id":5,"level":1,"rate":0.5,"score":5,"time":100,"trainId":5,"type":1}]
	 * train : {"createTime":"2017-08-02 13:58:03","finishCount":0,"id":5,"imageUrl":"http://baidu.com","knowledgeUrl":"http://baidu.com","level":1,"modifyTime":"2017-08-08 17:25:08","recommend":1,"remark":"test remark","status":1,"time":1000,"title":"test ","type":2}
	 */

	private TrainBean train;
	private List<?> classifies;
	private List<TargetsBean> targets;

	public TrainBean getTrain() {
		return train;
	}

	public void setTrain(TrainBean train) {
		this.train = train;
	}

	public List<?> getClassifies() {
		return classifies;
	}

	public void setClassifies(List<?> classifies) {
		this.classifies = classifies;
	}

	public List<TargetsBean> getTargets() {
		return targets;
	}

	public void setTargets(List<TargetsBean> targets) {
		this.targets = targets;
	}

	public static class TrainBean {
		/**
		 * createTime : 2017-08-02 13:58:03
		 * finishCount : 0
		 * id : 5
		 * imageUrl : http://baidu.com
		 * knowledgeUrl : http://baidu.com
		 * level : 1
		 * modifyTime : 2017-08-08 17:25:08
		 * recommend : 1
		 * remark : test remark
		 * status : 1
		 * time : 1000
		 * title : test
		 * type : 2
		 */

		private String createTime;
		private int finishCount;
		private int id;
		private String imageUrl;
		private String knowledgeUrl;
		private int level;
		private String modifyTime;
		private int recommend;
		private String remark;
		private int status;
		private int time;
		private String title;
		private int type;

		public String getCreateTime() {
			return createTime;
		}

		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}

		public int getFinishCount() {
			return finishCount;
		}

		public void setFinishCount(int finishCount) {
			this.finishCount = finishCount;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public String getKnowledgeUrl() {
			return knowledgeUrl;
		}

		public void setKnowledgeUrl(String knowledgeUrl) {
			this.knowledgeUrl = knowledgeUrl;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public String getModifyTime() {
			return modifyTime;
		}

		public void setModifyTime(String modifyTime) {
			this.modifyTime = modifyTime;
		}

		public int getRecommend() {
			return recommend;
		}

		public void setRecommend(int recommend) {
			this.recommend = recommend;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public int getTime() {
			return time;
		}

		public void setTime(int time) {
			this.time = time;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		@Override
		public String toString() {
			return "TrainBean{" +
					"createTime='" + createTime + '\'' +
					", finishCount=" + finishCount +
					", id=" + id +
					", imageUrl='" + imageUrl + '\'' +
					", knowledgeUrl='" + knowledgeUrl + '\'' +
					", level=" + level +
					", modifyTime='" + modifyTime + '\'' +
					", recommend=" + recommend +
					", remark='" + remark + '\'' +
					", status=" + status +
					", time=" + time +
					", title='" + title + '\'' +
					", type=" + type +
					'}';
		}
	}

	public static class TargetsBean {
		/**
		 * createTime : 2017-08-02 13:58:03
		 * id : 5
		 * level : 1
		 * rate : 0.5
		 * score : 5
		 * time : 100
		 * trainId : 5
		 * type : 1
		 */

		private String createTime;
		private int id;
		private int level;
		private double rate;
		private int score;
		private int time;
		private int trainId;
		private int type;

		public String getCreateTime() {
			return createTime;
		}

		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public double getRate() {
			return rate;
		}

		public void setRate(double rate) {
			this.rate = rate;
		}

		public int getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}

		public int getTime() {
			return time;
		}

		public void setTime(int time) {
			this.time = time;
		}

		public int getTrainId() {
			return trainId;
		}

		public void setTrainId(int trainId) {
			this.trainId = trainId;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		@Override
		public String toString() {
			return "TargetsBean{" +
					"createTime='" + createTime + '\'' +
					", id=" + id +
					", level=" + level +
					", rate=" + rate +
					", score=" + score +
					", time=" + time +
					", trainId=" + trainId +
					", type=" + type +
					'}';
		}
	}

	@Override
	public String toString() {
		return "TrainDetail{" +
				"train=" + train +
				", classifies=" + classifies +
				", targets=" + targets +
				'}';
	}
}
