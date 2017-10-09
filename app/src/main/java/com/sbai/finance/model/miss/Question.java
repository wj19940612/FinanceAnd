package com.sbai.finance.model.miss;

import java.io.Serializable;

/**
 * Created by lixiaokuan0819 on 2017/7/30.
 */

public class Question implements Serializable {

    private static final int TYPE_DEFAULT = 0;
    public static final int TYPE_HOT = 1;
    public static final int TYPE_LATEST = 2;


    /**
     * answerContext : blob:http://var.esongbai.xyz/ed0ea7b6-bd51-4ff1-a631-864d01b9f4c8
     * answerCustomId : 6
     * askSavant : 0
     * awardCount : 0
     * awardMoney : 0
     * createTime : 1501570628000
     * customPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1501549863096062105.png
     * id : 9
     * isPrise : 0
     * listenCount : 0
     * priseCount : 0
     * questionContext : 哈个
     * questionUserId : 145
     * replyTime : 1501576566000
     * show : 0
     * solve : 0
     * updateTime : 1501576566000
     * userName : 抽象工作室
     * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1494325795572.png
     * appointCustomId : 6
     * replyCount : 6
     */

    private String answerContext;   //小姐姐回答内容
    private int answerCustomId;     //回答的小姐姐ID
    private int askSavant;          //是否请求专家回复   0 未求助 1 已求助 2 已回复
    private int awardCount;         // 打赏数
    private int awardMoney;         // 打赏金额
    private long createTime;        // 提问时间
    private String customPortrait;  //小姐姐头像
    private int id;                 //提问ID
    private int isPrise;            //是否点赞
    private int listenCount;        //偷听人数
    private int priseCount;         //点赞数
    private String questionContext; //提问内容
    private int questionUserId;     //提问人ID
    private long replyTime;         //回复时间
    private int show;               //是否显示  0 显示 1 隐藏
    private int solve;              //是否解决 0 未解决 1 已解决
    private long updateTime;
    private String userName;
    private String userPortrait;   //提问人头像
    private int appointCustomId;   //提问时指定的小姐姐id
    private int replyCount;       //回复数
    private int soundTime;
    private int collect;
    private boolean isPlaying;
    private boolean isPause;
    private boolean isCountDown;
    private boolean progressIsZero = true;

    // 我的问答中的数据
    private String content;        //问题内容
    private int dataId;            //问题id
    private int type;              //解说界面自定义的type  0 表示普通的 1 表示出现热门提问标签 2 最新提问
    private String commentId;      //回复的id


    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public int getCollect() {
        return collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public boolean isQuestionSolved() {
        return getSolve() == 1;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    public boolean isCountDown() {
        return isCountDown;
    }

    public void setCountDown(boolean countDown) {
        isCountDown = countDown;
    }

    public boolean isProgressIsZero() {
        return progressIsZero;
    }

    public void setProgressIsZero(boolean progressIsZero) {
        this.progressIsZero = progressIsZero;
    }


    public int getSoundTime() {
        return soundTime;
    }

    public void setSoundTime(int soundTime) {
        this.soundTime = soundTime;
    }

    public String getAnswerContext() {
        return answerContext;
    }

    public void setAnswerContext(String answerContext) {
        this.answerContext = answerContext;
    }

    public int getAnswerCustomId() {
        return answerCustomId;
    }

    public void setAnswerCustomId(int answerCustomId) {
        this.answerCustomId = answerCustomId;
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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getCustomPortrait() {
        return customPortrait;
    }

    public void setCustomPortrait(String customPortrait) {
        this.customPortrait = customPortrait;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsPrise() {
        return isPrise;
    }

    public void setIsPrise(int isPrise) {
        this.isPrise = isPrise;
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

    public long getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(long replyTime) {
        this.replyTime = replyTime;
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

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }

    public int getAppointCustomId() {
        return appointCustomId;
    }

    public void setAppointCustomId(int appointCustomId) {
        this.appointCustomId = appointCustomId;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    @Override
    public String toString() {
        return "Question{" +
                "answerContext='" + answerContext + '\'' +
                ", answerCustomId=" + answerCustomId +
                ", askSavant=" + askSavant +
                ", awardCount=" + awardCount +
                ", awardMoney=" + awardMoney +
                ", createTime=" + createTime +
                ", customPortrait='" + customPortrait + '\'' +
                ", id=" + id +
                ", isPrise=" + isPrise +
                ", listenCount=" + listenCount +
                ", priseCount=" + priseCount +
                ", questionContext='" + questionContext + '\'' +
                ", questionUserId=" + questionUserId +
                ", replyTime=" + replyTime +
                ", show=" + show +
                ", solve=" + solve +
                ", updateTime=" + updateTime +
                ", userName='" + userName + '\'' +
                ", userPortrait='" + userPortrait + '\'' +
                ", appointCustomId=" + appointCustomId +
                ", replyCount=" + replyCount +
                ", soundTime=" + soundTime +
                ", collect=" + collect +
                ", isPlaying=" + isPlaying +
                ", isPause=" + isPause +
                ", isCountDown=" + isCountDown +
                ", progressIsZero=" + progressIsZero +
                ", content='" + content + '\'' +
                ", dataId=" + dataId +
                ", type=" + type +
                ", commentId='" + commentId + '\'' +
                '}';
    }

}
