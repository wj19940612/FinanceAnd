package com.sbai.finance.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-04-25.
 */

public class TopicDetailModel {

	/**
	 * SubjectDetailModelList : [{"bigVarietyTypeCode":1,"createActor":0,"createDate":1492145998000,"id":1,"modifyActor":0,"modifyDate":1492145998000,"smallVarietyTypeCode":1,"sort":1,"subjectId":18,"varietyType":"股票","varityId":1,"varityName":"雷鸣次"},{"bigVarietyTypeCode":2,"createActor":0,"createDate":1492145998000,"id":2,"modifyActor":0,"modifyDate":1492145998000,"smallVarietyTypeCode":2,"sort":2,"subjectId":18,"varietyType":"期货","varityId":2,"varityName":"累的地方"}]
	 * subjectModel : {"click":1,"count":2,"createActor":0,"createDate":1492145990000,"id":18,"modifyActor":0,"modifyDate":1492145990000,"sort":1,"status":1,"subTitle":"哈哈","title":"测试"}
	 */

	private SubjectModelBean subjectModel;
	private ArrayList<SubjectDetailModelListBean> SubjectDetailModelList;

	public SubjectModelBean getSubjectModel() {
		return subjectModel;
	}

	public void setSubjectModel(SubjectModelBean subjectModel) {
		this.subjectModel = subjectModel;
	}

	public ArrayList<SubjectDetailModelListBean> getSubjectDetailModelList() {
		return SubjectDetailModelList;
	}

	public void setSubjectDetailModelList(ArrayList<SubjectDetailModelListBean> SubjectDetailModelList) {
		this.SubjectDetailModelList = SubjectDetailModelList;
	}

	public static class SubjectModelBean {
		/**
		 * click : 1
		 * count : 2
		 * createActor : 0
		 * createDate : 1492145990000
		 * id : 18
		 * modifyActor : 0
		 * modifyDate : 1492145990000
		 * sort : 1
		 * status : 1
		 * subTitle : 哈哈
		 * title : 测试
		 */

		private int click;
		private int count;
		private int createActor;
		private long createDate;
		private int id;
		private int modifyActor;
		private long modifyDate;
		private int sort;
		private int status;
		private String subTitle;
		private String title;

		public int getClick() {
			return click;
		}

		public void setClick(int click) {
			this.click = click;
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
	}

	public static class SubjectDetailModelListBean {
		/**
		 * bigVarietyTypeCode : 1
		 * createActor : 0
		 * createDate : 1492145998000
		 * id : 1
		 * modifyActor : 0
		 * modifyDate : 1492145998000
		 * smallVarietyTypeCode : 1
		 * sort : 1
		 * subjectId : 18
		 * varietyType : 股票
		 * varityId : 1
		 * varityName : 雷鸣次
		 */

		private int bigVarietyTypeCode;
		private int createActor;
		private long createDate;
		private int id;
		private int modifyActor;
		private long modifyDate;
		private int smallVarietyTypeCode;
		private int sort;
		private int subjectId;
		private String varietyType;
		private int varityId;
		private String varityName;

		public int getBigVarietyTypeCode() {
			return bigVarietyTypeCode;
		}

		public void setBigVarietyTypeCode(int bigVarietyTypeCode) {
			this.bigVarietyTypeCode = bigVarietyTypeCode;
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

		public int getSmallVarietyTypeCode() {
			return smallVarietyTypeCode;
		}

		public void setSmallVarietyTypeCode(int smallVarietyTypeCode) {
			this.smallVarietyTypeCode = smallVarietyTypeCode;
		}

		public int getSort() {
			return sort;
		}

		public void setSort(int sort) {
			this.sort = sort;
		}

		public int getSubjectId() {
			return subjectId;
		}

		public void setSubjectId(int subjectId) {
			this.subjectId = subjectId;
		}

		public String getVarietyType() {
			return varietyType;
		}

		public void setVarietyType(String varietyType) {
			this.varietyType = varietyType;
		}

		public int getVarityId() {
			return varityId;
		}

		public void setVarityId(int varityId) {
			this.varityId = varityId;
		}

		public String getVarityName() {
			return varityName;
		}

		public void setVarityName(String varityName) {
			this.varityName = varityName;
		}
	}
}
