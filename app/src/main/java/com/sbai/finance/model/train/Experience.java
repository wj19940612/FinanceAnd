package com.sbai.finance.model.train;

import java.util.List;

/**
 * Created by lixiaokuan0819 on 2017/8/9.
 */

public class Experience {

	/**
	 * content : 发表第 [ 10 ] 个训练心得
	 * createDate : 1501637171316
	 * hot : 0
	 * id : 59812a3377c84e2515d18a96
	 * picture : http://img1.gtimg.com/ninja/2/2017/07/ninja150138073790352.jpg
	 * praise : 56
	 * replys : []
	 * star : 2
	 * status : 1
	 * type : 2
	 * userModel : {"age":25,"certificationStatus":1,"createTime":"2017-05-10 10:22:01","id":165,"land":"浙江省-杭州市-上城区","status":0,"userName":"余潇","userPhone":"17767173523","userPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494565703036.png","userSex":1}
	 */

	private String content;
	private long createDate;
	private int hot;
	private String id;
	private String picture;
	private int praise;
	private int star;
	private int status;
	private int type;
	private UserModelBean userModel;
	private List<?> replys;

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

	public int getHot() {
		return hot;
	}

	public void setHot(int hot) {
		this.hot = hot;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public int getPraise() {
		return praise;
	}

	public void setPraise(int praise) {
		this.praise = praise;
	}

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public UserModelBean getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModelBean userModel) {
		this.userModel = userModel;
	}

	public List<?> getReplys() {
		return replys;
	}

	public void setReplys(List<?> replys) {
		this.replys = replys;
	}

	public static class UserModelBean {
		/**
		 * age : 25
		 * certificationStatus : 1
		 * createTime : 2017-05-10 10:22:01
		 * id : 165
		 * land : 浙江省-杭州市-上城区
		 * status : 0
		 * userName : 余潇
		 * userPhone : 17767173523
		 * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494565703036.png
		 * userSex : 1
		 */

		private int age;
		private int certificationStatus;
		private String createTime;
		private int id;
		private String land;
		private int status;
		private String userName;
		private String userPhone;
		private String userPortrait;
		private int userSex;

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public int getCertificationStatus() {
			return certificationStatus;
		}

		public void setCertificationStatus(int certificationStatus) {
			this.certificationStatus = certificationStatus;
		}

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

		public String getLand() {
			return land;
		}

		public void setLand(String land) {
			this.land = land;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
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
	}
}
