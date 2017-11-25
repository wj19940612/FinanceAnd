package com.sbai.finance.model.mine;

/**
 * Created by ${wangJie} on 2017/9/25.
 * 我的收藏   页面   提问和文章收藏的model
 * 不是很明白为何要把提问和文章搞一块
 */

public class MyCollect {
    //收藏类型  1 提问 2 乐米日报
    public static final int COLLECT_TYPE_QUESTION = 1;
    public static final int COLLECT_TYPE_ARTICLE = 2;
    /**
     * mongoId : 598c7e9e8776e2bc872d2ae2
     * coverUrl : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1505183304405011072.jpg
     * createTime : 1502379678203
     * format : 1
     * clicks : 503
     * id : 15
     * type : 2
     * title : 产品经理如何深入日常工作好
     * userId : 130
     */

    private int id;
    private int type;

    //收藏的文章
    private int clicks;
    private String title;
    private String coverUrl;
    private int format;
    private String mongoId;      //乐米日报id

    //收藏的问题

    private int replyCount;       //回复数
    private int dataId;             //提问id
    private int awardCount;         // 打赏数
    private long createTime;        // 提问时间
    private int priseCount;         //点赞数
    private int userId;             //用户id
    private String content;         // 提问内容
    private int solve;              //是否解决 0 未解决 1 已解决

    //问题是否被解决
    public boolean isQuestionSolved() {
        return getSolve()==1;
    }


    public int getSolve() {
        return solve;
    }

    public void setSolve(int solve) {
        this.solve = solve;
    }

    public String getMongoId() {
        return mongoId;
    }

    public void setMongoId(String mongoId) {
        this.mongoId = mongoId;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public int getClicks() {
        return clicks;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public int getAwardCount() {
        return awardCount;
    }

    public void setAwardCount(int awardCount) {
        this.awardCount = awardCount;
    }

    public int getPriseCount() {
        return priseCount;
    }

    public void setPriseCount(int priseCount) {
        this.priseCount = priseCount;
    }

    @Override
    public String toString() {
        return "MyCollect{" +
                "id=" + id +
                ", type=" + type +
                ", clicks=" + clicks +
                ", title='" + title + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", format=" + format +
                ", mongoId='" + mongoId + '\'' +
                ", replyCount=" + replyCount +
                ", dataId=" + dataId +
                ", awardCount=" + awardCount +
                ", createTime=" + createTime +
                ", priseCount=" + priseCount +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", solve=" + solve +
                '}';
    }
}
