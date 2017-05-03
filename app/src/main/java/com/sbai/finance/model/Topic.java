package com.sbai.finance.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017-04-25.
 */

public class Topic implements Serializable{
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
