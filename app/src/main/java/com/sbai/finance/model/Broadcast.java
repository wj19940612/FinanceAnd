package com.sbai.finance.model;

import com.sbai.finance.net.Client;

/**
 * 首页广播
 * {@link Client#getBroadcast()}
 */
public class Broadcast {

    /**
     * content : 111111
     * createTime : 1509010262726
     * endTime : 2017-10-26 17:29:17
     * id : 59f1ab56c2dc25710fcf5e2d
     * link : 11111111111
     * startTime : 2017-10-26 17:29:15
     * status : 0
     * style : h5
     * title : 0
     * type : 1
     */

    private String content;
    private long createTime;
    private String endTime;
    private String id;
    private String link;
    private String startTime;
    private int status;
    private String style;
    private String title;
    private int type;

    public boolean isH5Style() {
        return this.style.equalsIgnoreCase("h5");
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

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
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
}
