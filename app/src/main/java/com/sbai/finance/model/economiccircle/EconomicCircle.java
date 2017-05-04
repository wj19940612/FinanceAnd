package com.sbai.finance.model.economiccircle;

import java.io.Serializable;
import java.util.List;

public class EconomicCircle implements Serializable{

	/**
	 * data : [{"auditStatus":2,"bigVarietyTypeCode":"future","bigVarietyTypeName":"期货","calcuId":51,"content":"丁丹丹","createTime":1493876911159,"dataId":112,"direction":1,"guessPass":0,"id":"590ac0af55cef8b807e4ce6d","isAttention":2,"isPraise":0,"praiseCount":0,"replyCount":0,"type":2,"userId":115,"userName":"用户1392","varietyId":1,"varietyName":"美原油","varietyType":"CL"}]
	 * pageSize : 10
	 * resultCount : 4
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
		 * auditStatus : 2
		 * bigVarietyTypeCode : future
		 * bigVarietyTypeName : 期货
		 * calcuId : 51
		 * content : 丁丹丹
		 * createTime : 1493876911159
		 * dataId : 112
		 * direction : 1
		 * guessPass : 0
		 * id : 590ac0af55cef8b807e4ce6d
		 * isAttention : 2
		 * isPraise : 0
		 * praiseCount : 0
		 * replyCount : 0
		 * type : 2
		 * userId : 115
		 * userName : 用户1392
		 * varietyId : 1
		 * varietyName : 美原油
		 * varietyType : CL
		 */

		private int auditStatus;
		private String bigVarietyTypeCode;
		private String bigVarietyTypeName;
		private int calcuId;
		private String content;
		private long createTime;
		private int dataId;
		private int direction;
		private int guessPass;
		private String id;
		private int isAttention;
		private int isPraise;
		private int praiseCount;
		private int replyCount;
		private int type;
		private int userId;
		private String userName;
		private int varietyId;
		private String varietyName;
		private String varietyType;
		private String userPortrait;
		private String land;
		private int days;
		private int  interest;
		private int money;
		private String lastPrice;
		private String risePre;
		private String risePrice;

		public int getDays() {
			return days;
		}

		public void setDays(int days) {
			this.days = days;
		}

		public int getInterest() {
			return interest;
		}

		public void setInterest(int interest) {
			this.interest = interest;
		}

		public int getMoney() {
			return money;
		}

		public void setMoney(int money) {
			this.money = money;
		}

		public String getLastPrice() {
			return lastPrice;
		}

		public void setLastPrice(String lastPrice) {
			this.lastPrice = lastPrice;
		}

		public String getRisePre() {
			return risePre;
		}

		public void setRisePre(String risePre) {
			this.risePre = risePre;
		}

		public String getRisePrice() {
			return risePrice;
		}

		public void setRisePrice(String risePrice) {
			this.risePrice = risePrice;
		}

		public String getLand() {
			return land;
		}

		public void setLand(String land) {
			this.land = land;
		}

		public String getUserPortrait() {
			return userPortrait;
		}

		public void setUserPortrait(String userPortrait) {
			this.userPortrait = userPortrait;
		}

		public int getAuditStatus() {
			return auditStatus;
		}

		public void setAuditStatus(int auditStatus) {
			this.auditStatus = auditStatus;
		}

		public String getBigVarietyTypeCode() {
			return bigVarietyTypeCode;
		}

		public void setBigVarietyTypeCode(String bigVarietyTypeCode) {
			this.bigVarietyTypeCode = bigVarietyTypeCode;
		}

		public String getBigVarietyTypeName() {
			return bigVarietyTypeName;
		}

		public void setBigVarietyTypeName(String bigVarietyTypeName) {
			this.bigVarietyTypeName = bigVarietyTypeName;
		}

		public int getCalcuId() {
			return calcuId;
		}

		public void setCalcuId(int calcuId) {
			this.calcuId = calcuId;
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

		public int getDataId() {
			return dataId;
		}

		public void setDataId(int dataId) {
			this.dataId = dataId;
		}

		public int getDirection() {
			return direction;
		}

		public void setDirection(int direction) {
			this.direction = direction;
		}

		public int getGuessPass() {
			return guessPass;
		}

		public void setGuessPass(int guessPass) {
			this.guessPass = guessPass;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
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

		public int getVarietyId() {
			return varietyId;
		}

		public void setVarietyId(int varietyId) {
			this.varietyId = varietyId;
		}

		public String getVarietyName() {
			return varietyName;
		}

		public void setVarietyName(String varietyName) {
			this.varietyName = varietyName;
		}

		public String getVarietyType() {
			return varietyType;
		}

		public void setVarietyType(String varietyType) {
			this.varietyType = varietyType;
		}

		@Override
		public String toString() {
			return "DataBean{" +
					"auditStatus=" + auditStatus +
					", bigVarietyTypeCode='" + bigVarietyTypeCode + '\'' +
					", bigVarietyTypeName='" + bigVarietyTypeName + '\'' +
					", calcuId=" + calcuId +
					", content='" + content + '\'' +
					", createTime=" + createTime +
					", dataId=" + dataId +
					", direction=" + direction +
					", guessPass=" + guessPass +
					", id='" + id + '\'' +
					", isAttention=" + isAttention +
					", isPraise=" + isPraise +
					", praiseCount=" + praiseCount +
					", replyCount=" + replyCount +
					", type=" + type +
					", userId=" + userId +
					", userName='" + userName + '\'' +
					", varietyId=" + varietyId +
					", varietyName='" + varietyName + '\'' +
					", varietyType='" + varietyType + '\'' +
					", userPortrait='" + userPortrait + '\'' +
					", land='" + land + '\'' +
					", days=" + days +
					", interest=" + interest +
					", money=" + money +
					", lastPrice='" + lastPrice + '\'' +
					", risePre='" + risePre + '\'' +
					", risePrice='" + risePrice + '\'' +
					'}';
		}
	}

	@Override
	public String toString() {
		return "EconomicCircle{" +
				"pageSize=" + pageSize +
				", resultCount=" + resultCount +
				", start=" + start +
				", total=" + total +
				", data=" + data +
				'}';
	}
}
