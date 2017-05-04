package com.sbai.finance.model.push;

/**
 * Created by ${wangJie} on 2017/5/4.
 */

public class PushMessageModel {

    /**
     * classify : 51047
     * createTime : 测试内容fuv2
     * msg : 测试内容i1c6
     * title : 测试内容ptms
     * type : 14450
     */

    private int classify;
    private long createTime;
    private String msg;
    private String title;
    private int type;

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
}
