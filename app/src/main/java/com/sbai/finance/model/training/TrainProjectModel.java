package com.sbai.finance.model.training;

/**
 * Created by ${wangJie} on 2017/8/3.
 * 推荐训练项目或者我的训练列表model
 */

public class TrainProjectModel {
    /**
     * record : {"createTime":"2017-07-05 17:36:51","finish":14,"id":1,"lastFinishTime":"2017-08-08 11:25:25","lastTarinTime":"2017-08-08 11:25:25","maxLevel":1,"modifyTime":"2017-08-08 11:25:25","socre":5,"trainId":4,"userId":130,"version":34}
     * train : {"createTime":"2017-08-01 16:49:57","finishCount":0,"id":4,"imageUrl":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1501577017042089500.png","knowledgeUrl":"https://www.baidu.com","level":2,"modifyTime":"2017-08-08 17:32:09","recommend":1,"remark":"test","status":1,"time":120,"title":"训练新增（1）测试","type":1}
     */

    private RecordBean record;
    private TrainBean train;

    public RecordBean getRecord() {
        return record;
    }

    public void setRecord(RecordBean record) {
        this.record = record;
    }

    public TrainBean getTrain() {
        return train;
    }

    public void setTrain(TrainBean train) {
        this.train = train;
    }

    public static class RecordBean {
        /**
         * createTime : 2017-07-05 17:36:51
         * finish : 14
         * id : 1
         * lastFinishTime : 2017-08-08 11:25:25
         * lastTarinTime : 2017-08-08 11:25:25
         * maxLevel : 1
         * modifyTime : 2017-08-08 11:25:25
         * socre : 5
         * trainId : 4
         * userId : 130
         * version : 34
         */


        private int finishCount;
        //训练图片
        private String imageUrl;
        private String knowledgeUrl;
        //训练的难度等级
        private int level;
        //是否推荐
        private int recommend;
        private int status;
        //后台配置的完成这项训练需要花费时间
        private long time;
        //训练标题
        private String title;

        private String createTime;
        //当前用户完成次数
        private int finish;
        private int id;
        //最后完成训练的时间
        private String lastFinishTime;
        //最后开始训练的时间
        private String lastTarinTime;
        //完成的最大目标等级
        private int maxLevel;
        private String modifyTime;
        //获得的分数
        private int socre;
        private int trainId;
        private int userId;
        private int version;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getFinish() {
            return finish;
        }

        public void setFinish(int finish) {
            this.finish = finish;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLastFinishTime() {
            return lastFinishTime;
        }

        public void setLastFinishTime(String lastFinishTime) {
            this.lastFinishTime = lastFinishTime;
        }

        public String getLastTarinTime() {
            return lastTarinTime;
        }

        public void setLastTarinTime(String lastTarinTime) {
            this.lastTarinTime = lastTarinTime;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public void setMaxLevel(int maxLevel) {
            this.maxLevel = maxLevel;
        }

        public String getModifyTime() {
            return modifyTime;
        }

        public void setModifyTime(String modifyTime) {
            this.modifyTime = modifyTime;
        }

        public int getSocre() {
            return socre;
        }

        public void setSocre(int socre) {
            this.socre = socre;
        }

        public int getTrainId() {
            return trainId;
        }

        public void setTrainId(int trainId) {
            this.trainId = trainId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        @Override
        public String toString() {
            return "RecordBean{" +
                    "finishCount=" + finishCount +
                    ", imageUrl='" + imageUrl + '\'' +
                    ", knowledgeUrl='" + knowledgeUrl + '\'' +
                    ", level=" + level +
                    ", recommend=" + recommend +
                    ", status=" + status +
                    ", time=" + time +
                    ", title='" + title + '\'' +
                    ", createTime='" + createTime + '\'' +
                    ", finish=" + finish +
                    ", id=" + id +
                    ", lastFinishTime='" + lastFinishTime + '\'' +
                    ", lastTarinTime='" + lastTarinTime + '\'' +
                    ", maxLevel=" + maxLevel +
                    ", modifyTime='" + modifyTime + '\'' +
                    ", socre=" + socre +
                    ", trainId=" + trainId +
                    ", userId=" + userId +
                    ", version=" + version +
                    '}';
        }
    }

    public static class TrainBean {
        /**
         * createTime : 2017-08-01 16:49:57
         * finishCount : 0
         * id : 4
         * imageUrl : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1501577017042089500.png
         * knowledgeUrl : https://www.baidu.com
         * level : 2
         * modifyTime : 2017-08-08 17:32:09
         * recommend : 1
         * remark : test
         * status : 1
         * time : 120
         * title : 训练新增（1）测试
         * type : 1
         */

        private String createTime;
        private int finishCount;
        private int id;
        private String imageUrl;
        private String knowledgeUrl;
        private int level;
        private String modifyTime;
        private int recommend;
        private String remark;
        private int status;
        private int time;
        private String title;
        private int type;


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

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
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

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "TrainBean{" +
                    "createTime='" + createTime + '\'' +
                    ", finishCount=" + finishCount +
                    ", id=" + id +
                    ", imageUrl='" + imageUrl + '\'' +
                    ", knowledgeUrl='" + knowledgeUrl + '\'' +
                    ", level=" + level +
                    ", modifyTime='" + modifyTime + '\'' +
                    ", recommend=" + recommend +
                    ", remark='" + remark + '\'' +
                    ", status=" + status +
                    ", time=" + time +
                    ", title='" + title + '\'' +
                    ", type=" + type +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TrainProjectModel{" +
                "record=" + record +
                ", train=" + train +
                '}';
    }
}
