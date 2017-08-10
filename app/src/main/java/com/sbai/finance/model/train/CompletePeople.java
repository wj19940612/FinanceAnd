package com.sbai.finance.model.train;

/**
 * Created by lixiaokuan0819 on 2017/8/10.
 */

public class CompletePeople {

	/**
	 * record : {"createTime":"2017-07-05 17:36:51","finish":14,"id":1,"lastFinishTime":"2017-08-08 11:25:25","lastTarinTime":"2017-08-08 11:25:25","maxLevel":1,"modifyTime":"2017-08-08 11:25:25","socre":5,"trainId":4,"userId":130,"version":34}
	 * user : {"age":1,"id":130,"land":"浙江省-杭州市-临安市","userName":"溺水的鱼","userPhone":"15868423484","userPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/upload/20170706/130i1499304941437.png"}
	 */

	private RecordBean record;
	private UserBean user;

	public RecordBean getRecord() {
		return record;
	}

	public void setRecord(RecordBean record) {
		this.record = record;
	}

	public UserBean getUser() {
		return user;
	}

	public void setUser(UserBean user) {
		this.user = user;
	}

	public static class RecordBean {
		/**
		 * createTime : 2017-07-05 17:36:51
		 * finish : 14
		 * id : 1
		 * lastFinishTime : 2017-08-08 11:25:25
		 * lastTarinTime : 2017-08-08 11:25:25
		 * maxLevel : 1
		 * modifyTime : 2017-08-08 11:25:25
		 * socre : 5
		 * trainId : 4
		 * userId : 130
		 * version : 34
		 */

		private String createTime;
		private int finish;
		private int id;
		private String lastFinishTime;
		private String lastTarinTime;
		private int maxLevel;
		private String modifyTime;
		private int socre;
		private int trainId;
		private int userId;
		private int version;

		public String getCreateTime() {
			return createTime;
		}

		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}

		public int getFinish() {
			return finish;
		}

		public void setFinish(int finish) {
			this.finish = finish;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getLastFinishTime() {
			return lastFinishTime;
		}

		public void setLastFinishTime(String lastFinishTime) {
			this.lastFinishTime = lastFinishTime;
		}

		public String getLastTarinTime() {
			return lastTarinTime;
		}

		public void setLastTarinTime(String lastTarinTime) {
			this.lastTarinTime = lastTarinTime;
		}

		public int getMaxLevel() {
			return maxLevel;
		}

		public void setMaxLevel(int maxLevel) {
			this.maxLevel = maxLevel;
		}

		public String getModifyTime() {
			return modifyTime;
		}

		public void setModifyTime(String modifyTime) {
			this.modifyTime = modifyTime;
		}

		public int getSocre() {
			return socre;
		}

		public void setSocre(int socre) {
			this.socre = socre;
		}

		public int getTrainId() {
			return trainId;
		}

		public void setTrainId(int trainId) {
			this.trainId = trainId;
		}

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

		public int getVersion() {
			return version;
		}

		public void setVersion(int version) {
			this.version = version;
		}
	}

	public static class UserBean {
		/**
		 * age : 1
		 * id : 130
		 * land : 浙江省-杭州市-临安市
		 * userName : 溺水的鱼
		 * userPhone : 15868423484
		 * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/upload/20170706/130i1499304941437.png
		 */

		private int age;
		private int id;
		private String land;
		private String userName;
		private String userPhone;
		private String userPortrait;

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
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
	}
}
