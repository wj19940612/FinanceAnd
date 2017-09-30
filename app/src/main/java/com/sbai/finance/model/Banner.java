package com.sbai.finance.model;

import android.os.Parcel;
import android.os.Parcelable;


public class Banner implements Parcelable {

    private static final long serialVersionUID = -7266947195942724446L;

    /**
     * 首页banner
     */
    public static final int TYPE_BANNER = 0;
    public static final int STATUS_HIDE = 0;
    public static final int STATUS_SHOW = 1;
    /**
     * summary : 测试内容q550
     * content : N多美图
     * cover : 好图
     * createTime : 1492590335553
     * id : 58f71effa7670c2852bf2e29
     * index : 1
     * operator : admin
     * status : 1
     * style : html
     * title : 轮播标ssss
     * type : 1
     * updateTime : 1492590335553
     */
    private String summary;
    private String content;
    private String cover;   //封面
    private long createTime;
    private String id;
    private int index;
    private String operator;
    private int status;
    private String style;
    private String title;
    private int type;
    private long updateTime;

    //gift 独有的
    private int clicks; //点击次数
    private String smallPic;  //小图


    public boolean isH5Style() {
        return this.style.equalsIgnoreCase("h5");
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getClicks() {
        return clicks;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }


    public String getSmallPic() {
        return smallPic;
    }

    public void setSmallPic(String smallPic) {
        this.smallPic = smallPic;
    }


    @Override
    public String toString() {
        return "Banner{" +
                "summary='" + summary + '\'' +
                ", content='" + content + '\'' +
                ", cover='" + cover + '\'' +
                ", createTime=" + createTime +
                ", id='" + id + '\'' +
                ", index=" + index +
                ", operator='" + operator + '\'' +
                ", status=" + status +
                ", style='" + style + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", updateTime=" + updateTime +
                ", clicks=" + clicks +
                ", smallPic='" + smallPic + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.summary);
        dest.writeString(this.content);
        dest.writeString(this.cover);
        dest.writeLong(this.createTime);
        dest.writeString(this.id);
        dest.writeInt(this.index);
        dest.writeString(this.operator);
        dest.writeInt(this.status);
        dest.writeString(this.style);
        dest.writeString(this.title);
        dest.writeInt(this.type);
        dest.writeLong(this.updateTime);
        dest.writeInt(this.clicks);
        dest.writeString(this.smallPic);
    }

    public Banner() {
    }

    protected Banner(Parcel in) {
        this.summary = in.readString();
        this.content = in.readString();
        this.cover = in.readString();
        this.createTime = in.readLong();
        this.id = in.readString();
        this.index = in.readInt();
        this.operator = in.readString();
        this.status = in.readInt();
        this.style = in.readString();
        this.title = in.readString();
        this.type = in.readInt();
        this.updateTime = in.readLong();
        this.clicks = in.readInt();
        this.smallPic = in.readString();
    }

    public static final Creator<Banner> CREATOR = new Creator<Banner>() {
        @Override
        public Banner createFromParcel(Parcel source) {
            return new Banner(source);
        }

        @Override
        public Banner[] newArray(int size) {
            return new Banner[size];
        }
    };
}
