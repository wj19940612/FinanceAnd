package com.sbai.finance.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017-04-25.
 */

public class Topic implements Serializable{
	/**
	 * click : 1
	 * count : 2
	 * createActor : 0
	 * createDate : 1492151289000
	 * id : 26
	 * modifyActor : 0
	 * modifyDate : 1492151289000
	 * sort : 1
	 * status : 1
	 * subTitle : 哈哈
	 * title : 不了啊
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
