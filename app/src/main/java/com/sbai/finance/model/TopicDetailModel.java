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

	private Topic subjectModel;
	private List<Variety> SubjectDetailModelList;

	public Topic getSubjectModel() {
		return subjectModel;
	}

	public void setSubjectModel(Topic subjectModel) {
		this.subjectModel = subjectModel;
	}

	public List<Variety> getSubjectDetailModelList() {
		return SubjectDetailModelList;
	}

	public void setSubjectDetailModelList(List<Variety> subjectDetailModelList) {
		SubjectDetailModelList = subjectDetailModelList;
	}
}
