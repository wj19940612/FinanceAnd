package com.sbai.finance.model;

/**
 * Created by houcc on 2017-08-01.
 */

public class DailyReport {
    public static final int HTML = 1;

    /**
     * clicks : 0
     * id : 597ab2a97ceab9ecb6f9b144
     * source : 今日财经
     * title : (更新)乐米专访:葛卫东,从百万到百亿
     * coverUrl : https://wpimg.wallstcn.com/467f9917-b371-4830-bbc9-2ed9eda9d69f.jpg
     */

    private int clicks;
    private String id;
    private String source;
    private String title;
    private String coverUrl;
    private long createTime;
    private int format;
    private String content;
    private String url;
    private int collect;  //0 未收藏  1 已收藏

    public boolean isCollected() {
        return getCollect() == 1;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isHtml() {
        return format == 1;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
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

    public int getClicks() {
        return clicks;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public int getCollect() {
        return collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }
}
