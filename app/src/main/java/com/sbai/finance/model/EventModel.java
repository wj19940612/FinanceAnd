package com.sbai.finance.model;

import java.io.Serializable;

public class EventModel implements Serializable{

	/**
	 * clicks : 221
	 * content : <p>1</p>
	 * createTime : 1493885840106
	 * format : 1
	 * id : 590ae39055ce5b00c9f272b1
	 * operator : admin
	 * status : 1
	 * title : 特朗普访菲，两大嘴炮对轰
	 */
	private int clicks;
	private String content;
	private long createTime;
	private int format;
	private String id;
	private String operator;
	private int status;
	private String title;

	private String source;
	private String url;

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

	public boolean isH5Style(){
		return this.format == 2;
	}
	public int getClicks() {
		return clicks;
	}

	public void setClicks(int clicks) {
		this.clicks = clicks;
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
