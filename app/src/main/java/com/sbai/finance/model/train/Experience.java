package com.sbai.finance.model.train;

public class Experience {

	/**
	 * content : 发表一个训练心得
	 * createDate : 1501598260768
	 * hot : 1
	 * id : 5980923477c89a30126482bb
	 * isPraise : 0
	 * picture : http://img1.gtimg.com/ninja/2/2017/07/ninja150138073790352.jpg
	 * praise : 2
	 * targetId : 1
	 * type : 2
	 * userModel : {"id":165,"userName":"余潇","userPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494565703036.png"}
	 * star : 2
	 */

	private String content;
	private long createDate;
	private int hot;
	private String id;
	private int isPraise;
	private String picture;
	private int praise;
	private int targetId;
	private int type;
	private UserModelBean userModel;
	private int star;

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

	public int getIsPraise() {
		return isPraise;
	}

	public void setIsPraise(int isPraise) {
		this.isPraise = isPraise;
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

	public int getTargetId() {
		return targetId;
	}

	public void setTargetId(int targetId) {
		this.targetId = targetId;
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

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

	public static class UserModelBean {
		/**
		 * id : 165
		 * userName : 余潇
		 * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494565703036.png
		 */

		private int id;
		private String userName;
		private String userPortrait;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
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
			return "UserModelBean{" +
					"id=" + id +
					", userName='" + userName + '\'' +
					", userPortrait='" + userPortrait + '\'' +
					'}';
		}
	}

	@Override
	public String toString() {
		return "Experience{" +
				"content='" + content + '\'' +
				", createDate=" + createDate +
				", hot=" + hot +
				", id='" + id + '\'' +
				", isPraise=" + isPraise +
				", picture='" + picture + '\'' +
				", praise=" + praise +
				", targetId=" + targetId +
				", type=" + type +
				", userModel=" + userModel +
				", star=" + star +
				'}';
	}
}
