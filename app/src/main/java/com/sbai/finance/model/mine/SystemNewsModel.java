package com.sbai.finance.model.mine;

/**
 * Created by ${wangJie} on 2017/4/18.
 */

public class SystemNewsModel {

    private String title;
    private long time;
    private String content;

    public SystemNewsModel(String title, long time, String content) {
        this.title = title;
        this.time = time;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
