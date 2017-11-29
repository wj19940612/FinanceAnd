package com.sbai.finance.model.mine;

/**
 * Created by Administrator on 2017\11\21 0021.
 */

public class Answer {
    /**
     * appointCustomId : 12
     * askSavant : 0
     * awardCount : 0
     * awardMoney : 0
     * click : 0
     * createTime : 1511491495000
     * del : 0
     * hidden : 0
     * hot : 0
     * id : 597
     * listenCount : 0
     * priseCount : 0
     * questionContext : 凤凰花
     * questionUserId : 1512
     * replyCount : 0
     * show : 0
     * solve : 0
     * soundTime : 0
     * top : 0
     * updateTime : 1511491495000
     */

    private int appointCustomId;
    private int askSavant;             //是否请求专家回复 0未求助 1已求助 2 已回复
    private int awardCount;            //打赏次数
    private int awardMoney;            //打赏金额
    private int click;                 //点击次数
    private long createTime;
    private int del;                   //是否删除
    private int hidden;
    private int hot;                  //是否热推
    private int id;
    private int listenCount;          //收听次数
    private int priseCount;           //点赞次数
    private String questionContext;   //问题内容
    private int questionUserId;       //提问人
    private int replyCount;           //回复次数
    private int show;                 //是否显示
    private int solve;                //已解决
    private int soundTime;            //语音时长
    private int top;                  //是否置顶
    private long updateTime;          //更新时间
    private int readStatus;                 //0-未读 1-已读

    public int getAppointCustomId() {
        return appointCustomId;
    }

    public void setAppointCustomId(int appointCustomId) {
        this.appointCustomId = appointCustomId;
    }

    public int getAskSavant() {
        return askSavant;
    }

    public void setAskSavant(int askSavant) {
        this.askSavant = askSavant;
    }

    public int getAwardCount() {
        return awardCount;
    }

    public void setAwardCount(int awardCount) {
        this.awardCount = awardCount;
    }

    public int getAwardMoney() {
        return awardMoney;
    }

    public void setAwardMoney(int awardMoney) {
        this.awardMoney = awardMoney;
    }

    public int getClick() {
        return click;
    }

    public void setClick(int click) {
        this.click = click;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getDel() {
        return del;
    }

    public void setDel(int del) {
        this.del = del;
    }

    public int getHidden() {
        return hidden;
    }

    public void setHidden(int hidden) {
        this.hidden = hidden;
    }

    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getListenCount() {
        return listenCount;
    }

    public void setListenCount(int listenCount) {
        this.listenCount = listenCount;
    }

    public int getPriseCount() {
        return priseCount;
    }

    public void setPriseCount(int priseCount) {
        this.priseCount = priseCount;
    }

    public String getQuestionContext() {
        return questionContext;
    }

    public void setQuestionContext(String questionContext) {
        this.questionContext = questionContext;
    }

    public int getQuestionUserId() {
        return questionUserId;
    }

    public void setQuestionUserId(int questionUserId) {
        this.questionUserId = questionUserId;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public int getShow() {
        return show;
    }

    public void setShow(int show) {
        this.show = show;
    }

    public int getSolve() {
        return solve;
    }

    public void setSolve(int solve) {
        this.solve = solve;
    }

    public int getSoundTime() {
        return soundTime;
    }

    public void setSoundTime(int soundTime) {
        this.soundTime = soundTime;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }
}
