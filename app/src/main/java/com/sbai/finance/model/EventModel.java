package com.sbai.finance.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017-04-25.
 */

public class EventModel implements Serializable{
	/**
	 * data : [{"clicks":2001,"source":"测试内容2268","url":"测试内容hsj2","content":"sjahshagshga8suh","createTime":1492565130496,"format":1,"id":"58f6bc8a3c81657096b37e07","operator":"admin","status":1,"title":"萨斯往往"},{"clicks":2001,"source":"测试内容2268","url":"测试内容hsj2","content":"OOXXXX","createTime":1492494448142,"format":1,"id":"58f5a870ca2920e93b639018","operator":"admin","status":1,"title":"shuishuishui"}]
	 * pageSize : 10
	 * resultCount : 2
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

	public static class DataBean implements Serializable  {
		/**
		 * clicks : 2001
		 * source : 测试内容2268
		 * url : 测试内容hsj2
		 * content : sjahshagshga8suh
		 * createTime : 1492565130496
		 * format : 1
		 * id : 58f6bc8a3c81657096b37e07
		 * operator : admin
		 * status : 1
		 * title : 萨斯往往
		 */

		private int clicks;
		private String source;
		private String url;
		private String content;
		private long createTime;
		private int format;
		private String id;
		private String operator;
		private int status;
		private String title;
		public boolean isH5Style() {
			return this.format == 2;
		}
		public int getClicks() {
			return clicks;
		}

		public void setClicks(int clicks) {
			this.clicks = clicks;
		}

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
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

		public int getFormat() {
			return format;
		}

		public void setFormat(int format) {
			this.format = format;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getOperator() {
			return operator;
		}

		public void setOperator(String operator) {
			this.operator = operator;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}
}
