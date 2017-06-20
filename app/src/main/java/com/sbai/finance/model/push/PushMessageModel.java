package com.sbai.finance.model.push;

/**
 * Created by ${wangJie} on 2017/5/4.
 */

public class PushMessageModel {
    public static final int CLASSIFY_SYS=0;
    public static final int CLASSIFY_USER=1;
    public static final int CLASSIFY_MUTURAL=2;

    public static final int TYPE_EVENT=0;

    /**
     * classify : 14761
     * createTime : 54111
     * data : {}
     * dataId : 10734
     * iconUrl : 测试内容q9s8
     * msg : 测试内容ln2q
     * title : 测试内容84hj
     * type : 64687
     * url : 测试内容cn15
     */

    private int classify;
    private long createTime;
    private String dataId;
    private String iconUrl;
    private String msg;
    private String title;
    private int type;
    private String url;


    public int getClassify() {
        return classify;
    }

    public void setClassify(int classify) {
        this.classify = classify;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }


    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "PushMessageModel{" +
                "classify=" + classify +
                ", createTime=" + createTime +
                ", dataId=" + dataId +
                ", iconUrl='" + iconUrl + '\'' +
                ", msg='" + msg + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", url='" + url + '\'' +
                '}';
    }
}
