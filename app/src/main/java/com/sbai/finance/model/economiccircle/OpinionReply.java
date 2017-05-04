package com.sbai.finance.model.economiccircle;

import java.util.List;

/**
 * Created by lixiaokuan0819 on 2017/4/28.
 */

public class OpinionReply {

	/**
	 * data : [{"content":"好好好","createTime":1,"id":1,"isAttention":1,"praiseCount":10,"userId":1,"userName":"yehx","userPortrait":"order/order.png"}]
	 * pageSize : 10
	 * resultCount : 1
	 * start : 0
	 * total : 1
	 */

	private int pageSize;
	private int resultCount;
	private int start;
	private int total;
	private List<DataBean> data;

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<DataBean> getData() {
		return data;
	}

	public void setData(List<DataBean> data) {
		this.data = data;
	}

	public static class DataBean {
		/**
		 * content : 好好好
		 * createTime : 1
		 * id : 1
		 * isAttention : 1
		 * praiseCount : 10
		 * userId : 1
		 * userName : yehx
		 * userPortrait : order/order.png
		 */

		private String content;
		private long createTime;
		private int id;
		private int isAttention;
		private int praiseCount;
		private int userId;
		private String userName;
		private String userPortrait;
		private int isPraise;

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

		public int getIsPraise() {
			return isPraise;
		}

		public void setIsPraise(int isPraise) {
			this.isPraise = isPraise;
		}

		@Override
		public String toString() {
			return "DataBean{" +
					"content='" + content + '\'' +
					", createTime=" + createTime +
					", id=" + id +
					", isAttention=" + isAttention +
					", praiseCount=" + praiseCount +
					", userId=" + userId +
					", userName='" + userName + '\'' +
					", userPortrait='" + userPortrait + '\'' +
					", isPraise=" + isPraise +
					'}';
		}
	}

	@Override
	public String toString() {
		return "OpinionReply{" +
				"pageSize=" + pageSize +
				", resultCount=" + resultCount +
				", start=" + start +
				", total=" + total +
				", data=" + data +
				'}';
	}
}
