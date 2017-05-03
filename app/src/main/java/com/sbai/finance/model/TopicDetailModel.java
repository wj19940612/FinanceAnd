package com.sbai.finance.model;

import java.util.List;

/**
 * Created by Administrator on 2017-04-25.
 */

public class TopicDetailModel {

	/**
	 * subjectModel : {"backgroundImg":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1493709712809021409.png","count":2,"createActor":0,"createDate":1493709736000,"id":54,"introduction":"大专题大专题大专题大专题","modifyActor":0,"modifyDate":1493713139000,"sort":2,"status":1,"subTitle":"小专题","title":"大专题","type":"future"}
	 * SubjectDetailModelList : [{"displayMarketTimes":"06:00;05:00","smallVarietyTypeCode":"foreign","decimalScale":0.2,"varietyType":"CL","bigVarietyTypeCode":"future","sort":1,"baseline":2,"exchangeId":1,"openMarketTime":"07:00;06:00","flashChartPriceInterval":14,"varietyId":1,"exchangeStatus":1,"contractsCode":"CL1706","marketPoint":3,"varietyName":"美原油"},{"displayMarketTimes":"21:00;00:00;09:00;13:30;15:00","smallVarietyTypeCode":"china","decimalScale":0.2,"varietyType":"ag","bigVarietyTypeCode":"future","sort":2,"baseline":2,"exchangeId":7,"openMarketTime":"21:00;02:30;09:00;10:15;10:30;11:30;13:30;15:00","flashChartPriceInterval":9,"varietyId":8,"exchangeStatus":1,"contractsCode":"ag1709","marketPoint":0,"varietyName":"沪银"}]
	 */

	private SubjectModelBean subjectModel;
	private List<SubjectDetailModelListBean> SubjectDetailModelList;

	public SubjectModelBean getSubjectModel() {
		return subjectModel;
	}

	public void setSubjectModel(SubjectModelBean subjectModel) {
		this.subjectModel = subjectModel;
	}

	public List<SubjectDetailModelListBean> getSubjectDetailModelList() {
		return SubjectDetailModelList;
	}

	public void setSubjectDetailModelList(List<SubjectDetailModelListBean> SubjectDetailModelList) {
		this.SubjectDetailModelList = SubjectDetailModelList;
	}

	public static class SubjectModelBean {
		/**
		 * backgroundImg : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1493709712809021409.png
		 * count : 2
		 * createActor : 0
		 * createDate : 1493709736000
		 * id : 54
		 * introduction : 大专题大专题大专题大专题
		 * modifyActor : 0
		 * modifyDate : 1493713139000
		 * sort : 2
		 * status : 1
		 * subTitle : 小专题
		 * title : 大专题
		 * type : future
		 */

		private String backgroundImg;
		private int count;
		private int createActor;
		private long createDate;
		private int id;
		private String introduction;
		private int modifyActor;
		private long modifyDate;
		private int sort;
		private int status;
		private String subTitle;
		private String title;
		private String type;

		public String getBackgroundImg() {
			return backgroundImg;
		}

		public void setBackgroundImg(String backgroundImg) {
			this.backgroundImg = backgroundImg;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public int getCreateActor() {
			return createActor;
		}

		public void setCreateActor(int createActor) {
			this.createActor = createActor;
		}

		public long getCreateDate() {
			return createDate;
		}

		public void setCreateDate(long createDate) {
			this.createDate = createDate;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getIntroduction() {
			return introduction;
		}

		public void setIntroduction(String introduction) {
			this.introduction = introduction;
		}

		public int getModifyActor() {
			return modifyActor;
		}

		public void setModifyActor(int modifyActor) {
			this.modifyActor = modifyActor;
		}

		public long getModifyDate() {
			return modifyDate;
		}

		public void setModifyDate(long modifyDate) {
			this.modifyDate = modifyDate;
		}

		public int getSort() {
			return sort;
		}

		public void setSort(int sort) {
			this.sort = sort;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public String getSubTitle() {
			return subTitle;
		}

		public void setSubTitle(String subTitle) {
			this.subTitle = subTitle;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}

	public static class SubjectDetailModelListBean {
		/**
		 * displayMarketTimes : 06:00;05:00
		 * smallVarietyTypeCode : foreign
		 * decimalScale : 0.2
		 * varietyType : CL
		 * bigVarietyTypeCode : future
		 * sort : 1
		 * baseline : 2
		 * exchangeId : 1
		 * openMarketTime : 07:00;06:00
		 * flashChartPriceInterval : 14
		 * varietyId : 1
		 * exchangeStatus : 1
		 * contractsCode : CL1706
		 * marketPoint : 3
		 * varietyName : 美原油
		 */

		private String displayMarketTimes;
		private String smallVarietyTypeCode;
		private double decimalScale;
		private String varietyType;
		private String bigVarietyTypeCode;
		private int sort;
		private int baseline;
		private int exchangeId;
		private String openMarketTime;
		private int flashChartPriceInterval;
		private int varietyId;
		private int exchangeStatus;
		private String contractsCode;
		private int marketPoint;
		private String varietyName;

		public String getDisplayMarketTimes() {
			return displayMarketTimes;
		}

		public void setDisplayMarketTimes(String displayMarketTimes) {
			this.displayMarketTimes = displayMarketTimes;
		}

		public String getSmallVarietyTypeCode() {
			return smallVarietyTypeCode;
		}

		public void setSmallVarietyTypeCode(String smallVarietyTypeCode) {
			this.smallVarietyTypeCode = smallVarietyTypeCode;
		}

		public double getDecimalScale() {
			return decimalScale;
		}

		public void setDecimalScale(double decimalScale) {
			this.decimalScale = decimalScale;
		}

		public String getVarietyType() {
			return varietyType;
		}

		public void setVarietyType(String varietyType) {
			this.varietyType = varietyType;
		}

		public String getBigVarietyTypeCode() {
			return bigVarietyTypeCode;
		}

		public void setBigVarietyTypeCode(String bigVarietyTypeCode) {
			this.bigVarietyTypeCode = bigVarietyTypeCode;
		}

		public int getSort() {
			return sort;
		}

		public void setSort(int sort) {
			this.sort = sort;
		}

		public int getBaseline() {
			return baseline;
		}

		public void setBaseline(int baseline) {
			this.baseline = baseline;
		}

		public int getExchangeId() {
			return exchangeId;
		}

		public void setExchangeId(int exchangeId) {
			this.exchangeId = exchangeId;
		}

		public String getOpenMarketTime() {
			return openMarketTime;
		}

		public void setOpenMarketTime(String openMarketTime) {
			this.openMarketTime = openMarketTime;
		}

		public int getFlashChartPriceInterval() {
			return flashChartPriceInterval;
		}

		public void setFlashChartPriceInterval(int flashChartPriceInterval) {
			this.flashChartPriceInterval = flashChartPriceInterval;
		}

		public int getVarietyId() {
			return varietyId;
		}

		public void setVarietyId(int varietyId) {
			this.varietyId = varietyId;
		}

		public int getExchangeStatus() {
			return exchangeStatus;
		}

		public void setExchangeStatus(int exchangeStatus) {
			this.exchangeStatus = exchangeStatus;
		}

		public String getContractsCode() {
			return contractsCode;
		}

		public void setContractsCode(String contractsCode) {
			this.contractsCode = contractsCode;
		}

		public int getMarketPoint() {
			return marketPoint;
		}

		public void setMarketPoint(int marketPoint) {
			this.marketPoint = marketPoint;
		}

		public String getVarietyName() {
			return varietyName;
		}

		public void setVarietyName(String varietyName) {
			this.varietyName = varietyName;
		}
	}
}
