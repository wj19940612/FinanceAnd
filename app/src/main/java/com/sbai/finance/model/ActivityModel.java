package com.sbai.finance.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * app启动弹窗
 */
public class ActivityModel implements Parcelable {
    public static final String LINK_TYPE_MODEL = "module";
    public static final String LINK_TYPE_H5 = "H5";
    public static final String LINK_TYPE_BANNER = "banner";
    /**
     * buttonUrl : cccc
     * createTime : 1506480971000
     * id : 3
     * link : banner
     * linkType : banner
     * status : 1
     * updateTime : 1506480971000
     * windowUrl : bbb
     */

    private String buttonUrl;
    private long createTime;
    private int id;
    private String link;
    private String linkType;
    private int status;
    private long updateTime;
    private String windowUrl;
    private String style;
    private String title;
    private String content;

    public boolean isH5Style() {
        return this.style.equalsIgnoreCase("h5");
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getButtonUrl() {
        return buttonUrl;
    }

    public void setButtonUrl(String buttonUrl) {
        this.buttonUrl = buttonUrl;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getWindowUrl() {
        return windowUrl;
    }

    public void setWindowUrl(String windowUrl) {
        this.windowUrl = windowUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.buttonUrl);
        dest.writeLong(this.createTime);
        dest.writeInt(this.id);
        dest.writeString(this.link);
        dest.writeString(this.linkType);
        dest.writeInt(this.status);
        dest.writeLong(this.updateTime);
        dest.writeString(this.windowUrl);
    }

    public ActivityModel() {
    }

    protected ActivityModel(Parcel in) {
        this.buttonUrl = in.readString();
        this.createTime = in.readLong();
        this.id = in.readInt();
        this.link = in.readString();
        this.linkType = in.readString();
        this.status = in.readInt();
        this.updateTime = in.readLong();
        this.windowUrl = in.readString();
    }

    public static final Parcelable.Creator<ActivityModel> CREATOR = new Parcelable.Creator<ActivityModel>() {
        @Override
        public ActivityModel createFromParcel(Parcel source) {
            return new ActivityModel(source);
        }

        @Override
        public ActivityModel[] newArray(int size) {
            return new ActivityModel[size];
        }
    };
}
