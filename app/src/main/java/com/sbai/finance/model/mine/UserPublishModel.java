package com.sbai.finance.model.mine;

/**
 * Created by ${wangJie} on 2017/4/19.
 */

public class UserPublishModel {

    /**
     * data : [{"followStatus":0,"userName":"溺水的鱼","userPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1493707651242.png"}]
     * pageSize : 15
     * resultCount : 0
     * show : 0
     * total : 0
     */


        /**
         * auditStatus : 1
         * bigVarietyTypeCode : future
         * calcuId : 49
         * content : 好多话好多好多的话感动好的环境
         * createTime : 1493800236000
         * direction : 1
         * guessPass : 0
         * id : 96
         * lastPrice : 48.010
         * praiseCount : 0
         * replyCount : 0
         * risePre : +0.73%
         * risePrice : +0.35
         * updateTime : 1493800236000
         * userId : 98
         * userName : 我想自己
         * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1493793238368.png
         * varietyId : 1
         * varietyName : 美原油
         * varietyType : CL
         */

        private int auditStatus;
        //大类代码
        private String bigVarietyTypeCode;
        //大类名称
        private String bigVarietyTypeName;
        private int calcuId;
        private String content;
        private long createTime;
        private int direction;
        //        预测状态 0等待结果 1成功 2失败
        private int guessPass;
        private int id;
        //	最新价
        private String lastPrice;
        //点赞数
        private int praiseCount;
        //回复数
        private int replyCount;
        //涨幅百分比
        private String risePre;
        //
        private String risePrice;
        private long updateTime;
        private int userId;
        private String userName;
        private String userPortrait;
        private int varietyId;
        private String varietyName;
        private String varietyType;
        /**
         * bigVarietyTypeName : 测试内容3z0m
         * createTime : 测试内容051b
         * isAttention : 18117
         * lastPrice : 84672
         * risePre : 16208
         */

        //2 关注 1不关注
        private int isAttention;

        public boolean isAttention() {
            return getIsAttention() == 2;
        }

        public int getAuditStatus() {
            return auditStatus;
        }

        public void setAuditStatus(int auditStatus) {
            this.auditStatus = auditStatus;
        }

        public String getBigVarietyTypeCode() {
            return bigVarietyTypeCode;
        }

        public void setBigVarietyTypeCode(String bigVarietyTypeCode) {
            this.bigVarietyTypeCode = bigVarietyTypeCode;
        }

        public int getCalcuId() {
            return calcuId;
        }

        public void setCalcuId(int calcuId) {
            this.calcuId = calcuId;
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

        public int getDirection() {
            return direction;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        public int getGuessPass() {
            return guessPass;
        }

        public void setGuessPass(int guessPass) {
            this.guessPass = guessPass;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLastPrice() {
            return lastPrice;
        }

        public void setLastPrice(String lastPrice) {
            this.lastPrice = lastPrice;
        }

        public int getPraiseCount() {
            return praiseCount;
        }

        public void setPraiseCount(int praiseCount) {
            this.praiseCount = praiseCount;
        }

        public int getReplyCount() {
            return replyCount;
        }

        public void setReplyCount(int replyCount) {
            this.replyCount = replyCount;
        }

        public String getRisePre() {
            return risePre;
        }

        public void setRisePre(String risePre) {
            this.risePre = risePre;
        }

        public String getRisePrice() {
            return risePrice;
        }

        public void setRisePrice(String risePrice) {
            this.risePrice = risePrice;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
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

        public int getVarietyId() {
            return varietyId;
        }

        public void setVarietyId(int varietyId) {
            this.varietyId = varietyId;
        }

        public String getVarietyName() {
            return varietyName;
        }

        public void setVarietyName(String varietyName) {
            this.varietyName = varietyName;
        }

        public String getVarietyType() {
            return varietyType;
        }

        public void setVarietyType(String varietyType) {
            this.varietyType = varietyType;
        }

        public String getBigVarietyTypeName() {
            return bigVarietyTypeName;
        }

        public void setBigVarietyTypeName(String bigVarietyTypeName) {
            this.bigVarietyTypeName = bigVarietyTypeName;
        }

        public int getIsAttention() {
            return isAttention;
        }

        public void setIsAttention(int isAttention) {
            this.isAttention = isAttention;
        }

    @Override
    public String toString() {
        return "UserPublishModel{" +
                "auditStatus=" + auditStatus +
                ", bigVarietyTypeCode='" + bigVarietyTypeCode + '\'' +
                ", bigVarietyTypeName='" + bigVarietyTypeName + '\'' +
                ", calcuId=" + calcuId +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", direction=" + direction +
                ", guessPass=" + guessPass +
                ", id=" + id +
                ", lastPrice='" + lastPrice + '\'' +
                ", praiseCount=" + praiseCount +
                ", replyCount=" + replyCount +
                ", risePre='" + risePre + '\'' +
                ", risePrice='" + risePrice + '\'' +
                ", updateTime=" + updateTime +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userPortrait='" + userPortrait + '\'' +
                ", varietyId=" + varietyId +
                ", varietyName='" + varietyName + '\'' +
                ", varietyType='" + varietyType + '\'' +
                ", isAttention=" + isAttention +
                '}';
    }
}
