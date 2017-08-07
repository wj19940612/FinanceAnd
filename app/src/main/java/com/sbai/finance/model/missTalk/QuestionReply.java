package com.sbai.finance.model.missTalk;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lixiaokuan0819 on 2017/8/2.
 */

public class QuestionReply {


	/**
	 * data : [{"content":"发表一个评论yoyoyoyoyoyo333333!","createDate":1501664354913,"dataId":2,"id":"5981946377c87b05eb1b3c0b","remark":"内容正常","replyParentId":"5981941977c85d3b0d864f41","replys":[{"content":"发表一个评论yoyoyoyoyoyo333333!","createDate":1501664280341,"dataId":2,"id":"5981941977c85d3b0d864f41","replyParentId":"root_reply","replys":[],"star":2,"status":1,"sysAuditor":1,"type":1,"userAuditor":1,"userModel":{"age":25,"certificationStatus":1,"createTime":1494382921000,"id":165,"land":"浙江省-杭州市-上城区","status":0,"userName":"余潇","userPhone":"17767173523","userPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494565703036.png","userSex":1}}],"status":1,"sysAuditor":1,"type":1,"userAuditor":1},{"content":"发表一个评论yoyoyoyoyoyo333333!","createDate":1501664280341,"dataId":2,"id":"5981941977c85d3b0d864f41","replyCount":1,"replyParentId":"root_reply","replys":[],"star":2,"status":1,"sysAuditor":1,"type":1,"userAuditor":1,"userModel":{"age":25,"certificationStatus":1,"createTime":1494382921000,"id":165,"land":"浙江省-杭州市-上城区","status":0,"userName":"余潇","userPhone":"17767173523","userPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494565703036.png","userSex":1}}]
	 * pageSize : 2
	 * resultCount : 11
	 * start : 0
	 * total : 6
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

	public static class DataBean implements Serializable{
		/**
		 * content : 发表一个评论yoyoyoyoyoyo333333!
		 * createDate : 1501664354913
		 * dataId : 2
		 * id : 5981946377c87b05eb1b3c0b
		 * remark : 内容正常
		 * replyParentId : 5981941977c85d3b0d864f41
		 * replys : [{"content":"发表一个评论yoyoyoyoyoyo333333!","createDate":1501664280341,"dataId":2,"id":"5981941977c85d3b0d864f41","replyParentId":"root_reply","replys":[],"star":2,"status":1,"sysAuditor":1,"type":1,"userAuditor":1,"userModel":{"age":25,"certificationStatus":1,"createTime":1494382921000,"id":165,"land":"浙江省-杭州市-上城区","status":0,"userName":"余潇","userPhone":"17767173523","userPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494565703036.png","userSex":1}}]
		 * status : 1
		 * sysAuditor : 1
		 * type : 1
		 * userAuditor : 1
		 * replyCount : 1
		 * star : 2
		 * userModel : {"age":25,"certificationStatus":1,"createTime":1494382921000,"id":165,"land":"浙江省-杭州市-上城区","status":0,"userName":"余潇","userPhone":"17767173523","userPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494565703036.png","userSex":1}
		 */

		private String content;
		private long createDate;
		private int dataId;
		private String id;
		private String remark;
		private String replyParentId;
		private int status;
		private int sysAuditor;
		private int type;
		private int userAuditor;
		private int replyCount;
		private int star;
		private UserModelBean userModel;
		private List<ReplysBean> replys;

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

		public int getDataId() {
			return dataId;
		}

		public void setDataId(int dataId) {
			this.dataId = dataId;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public String getReplyParentId() {
			return replyParentId;
		}

		public void setReplyParentId(String replyParentId) {
			this.replyParentId = replyParentId;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public int getSysAuditor() {
			return sysAuditor;
		}

		public void setSysAuditor(int sysAuditor) {
			this.sysAuditor = sysAuditor;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public int getUserAuditor() {
			return userAuditor;
		}

		public void setUserAuditor(int userAuditor) {
			this.userAuditor = userAuditor;
		}

		public int getReplyCount() {
			return replyCount;
		}

		public void setReplyCount(int replyCount) {
			this.replyCount = replyCount;
		}

		public int getStar() {
			return star;
		}

		public void setStar(int star) {
			this.star = star;
		}

		public UserModelBean getUserModel() {
			return userModel;
		}

		public void setUserModel(UserModelBean userModel) {
			this.userModel = userModel;
		}

		public List<ReplysBean> getReplys() {
			return replys;
		}

		public void setReplys(List<ReplysBean> replys) {
			this.replys = replys;
		}

		public static class UserModelBean {
			/**
			 * age : 25
			 * certificationStatus : 1
			 * createTime : 1494382921000
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
			private long createTime;
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

		public static class ReplysBean {
			/**
			 * content : 发表一个评论yoyoyoyoyoyo333333!
			 * createDate : 1501664280341
			 * dataId : 2
			 * id : 5981941977c85d3b0d864f41
			 * replyParentId : root_reply
			 * replys : []
			 * star : 2
			 * status : 1
			 * sysAuditor : 1
			 * type : 1
			 * userAuditor : 1
			 * userModel : {"age":25,"certificationStatus":1,"createTime":1494382921000,"id":165,"land":"浙江省-杭州市-上城区","status":0,"userName":"余潇","userPhone":"17767173523","userPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494565703036.png","userSex":1}
			 */

			private String content;
			private long createDate;
			private int dataId;
			private String id;
			private String replyParentId;
			private int star;
			private int status;
			private int sysAuditor;
			private int type;
			private int userAuditor;
			private UserModelBeanX userModel;
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

			public int getDataId() {
				return dataId;
			}

			public void setDataId(int dataId) {
				this.dataId = dataId;
			}

			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}

			public String getReplyParentId() {
				return replyParentId;
			}

			public void setReplyParentId(String replyParentId) {
				this.replyParentId = replyParentId;
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

			public int getSysAuditor() {
				return sysAuditor;
			}

			public void setSysAuditor(int sysAuditor) {
				this.sysAuditor = sysAuditor;
			}

			public int getType() {
				return type;
			}

			public void setType(int type) {
				this.type = type;
			}

			public int getUserAuditor() {
				return userAuditor;
			}

			public void setUserAuditor(int userAuditor) {
				this.userAuditor = userAuditor;
			}

			public UserModelBeanX getUserModel() {
				return userModel;
			}

			public void setUserModel(UserModelBeanX userModel) {
				this.userModel = userModel;
			}

			public List<?> getReplys() {
				return replys;
			}

			public void setReplys(List<?> replys) {
				this.replys = replys;
			}

			public static class UserModelBeanX {
				/**
				 * age : 25
				 * certificationStatus : 1
				 * createTime : 1494382921000
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
				private long createTime;
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
	}
}
