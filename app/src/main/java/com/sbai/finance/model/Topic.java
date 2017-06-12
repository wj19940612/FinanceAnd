package com.sbai.finance.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017-04-25.
 */

public class Topic implements Parcelable {
	public static final int CONTEXT_TYPE_VARIETY=1;
	public static final int CONTEXT_TYPE_H5=2;
	public static final int CONTEXT_TYPE_TEXT=3;

	/**
	 * backgroundImg : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1496907625107013973.png
	 * click : 7
	 * context : 4
	 * contextType : 3
	 * count : 0
	 * createDate : 1496907628000
	 * id : 100
	 * introduction : 大文本专题简介
	 * modifyDate : 1496908299000
	 * sort : 3
	 * status : 1
	 * subTitle : 大文本专题标题
	 * title : 大文本专题
	 * type : future
	 */

	private String backgroundImg;
	private int click;
	private String context;
	private int contextType;
	private int count;
	private long createDate;
	private int id;
	private String introduction;
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

	public int getClick() {
		return click;
	}

	public void setClick(int click) {
		this.click = click;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public int getContextType() {
		return contextType;
	}

	public void setContextType(int contextType) {
		this.contextType = contextType;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.backgroundImg);
		dest.writeInt(this.click);
		dest.writeString(this.context);
		dest.writeInt(this.contextType);
		dest.writeInt(this.count);
		dest.writeLong(this.createDate);
		dest.writeInt(this.id);
		dest.writeString(this.introduction);
		dest.writeLong(this.modifyDate);
		dest.writeInt(this.sort);
		dest.writeInt(this.status);
		dest.writeString(this.subTitle);
		dest.writeString(this.title);
		dest.writeString(this.type);
	}

	public Topic() {
	}

	protected Topic(Parcel in) {
		this.backgroundImg = in.readString();
		this.click = in.readInt();
		this.context = in.readString();
		this.contextType = in.readInt();
		this.count = in.readInt();
		this.createDate = in.readLong();
		this.id = in.readInt();
		this.introduction = in.readString();
		this.modifyDate = in.readLong();
		this.sort = in.readInt();
		this.status = in.readInt();
		this.subTitle = in.readString();
		this.title = in.readString();
		this.type = in.readString();
	}

	public static final Parcelable.Creator<Topic> CREATOR = new Parcelable.Creator<Topic>() {
		@Override
		public Topic createFromParcel(Parcel source) {
			return new Topic(source);
		}

		@Override
		public Topic[] newArray(int size) {
			return new Topic[size];
		}
	};
}
