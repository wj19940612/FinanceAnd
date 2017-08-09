package com.sbai.finance.model.train;

/**
 * 训练项目
 */

public class TrainProgram {
    /**
     * createTime : 2017-08-01 16:49:57
     * finishCount : 0
     * id : 4
     * imageUrl : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1501577017042089500.png
     * knowledgeUrl : https://www.baidu.com
     * level : 2
     * modifyTime : 2017-08-08 15:24:57
     * recommend : 1
     * status : 1
     * time : 120
     * title : 训练新增（1）测试
     */

    private String createTime;
    private int finishCount;
    private int id;
    private String imageUrl;
    private String knowledgeUrl;
    private int level;
    private String modifyTime;
    private int recommend;
    private int status;
    private int time;
    private String title;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getFinishCount() {
        return finishCount;
    }

    public void setFinishCount(int finishCount) {
        this.finishCount = finishCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getKnowledgeUrl() {
        return knowledgeUrl;
    }

    public void setKnowledgeUrl(String knowledgeUrl) {
        this.knowledgeUrl = knowledgeUrl;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
