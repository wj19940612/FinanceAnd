package com.sbai.finance.model.mine;

import java.util.List;

/**
 * Created by ${wangJie} on 2017/4/19.
 */

public class UserPublishModel {

    /**
     * data : [{"followStatus":0,"userName":"溺水的鱼","userPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1493707651242.png"}]
     * pageSize : 15
     * resultCount : 0
     * start : 0
     * total : 0
     */

    private int pageSize;
    private int resultCount;
    private int start;
    private int total;
    private List<DataBean> data;


    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * followStatus : 0
         * userName : 溺水的鱼
         * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1493707651242.png
         */

        private int followStatus;
        private String userName;
        private String userPortrait;
        /**
         * emptyIdentifier : 1
         * content : 测试内容2818
         * praiseCount : 80780
         * replyCount : 42008
         * userName : 1
         * varietyId : 16018
         * varietyType : 测试内容1lqe
         */

        private int emptyIdentifier;
        private String content;
        private int praiseCount;
        private int replyCount;
        private int varietyId;
        private String varietyType;

        public int getFollowStatus() {
            return followStatus;
        }

        public void setFollowStatus(int followStatus) {
            this.followStatus = followStatus;
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

        public int getEmptyIdentifier() {
            return emptyIdentifier;
        }

        public void setEmptyIdentifier(int emptyIdentifier) {
            this.emptyIdentifier = emptyIdentifier;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
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

        public int getVarietyId() {
            return varietyId;
        }

        public void setVarietyId(int varietyId) {
            this.varietyId = varietyId;
        }

        public String getVarietyType() {
            return varietyType;
        }

        public void setVarietyType(String varietyType) {
            this.varietyType = varietyType;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "followStatus=" + followStatus +
                    ", userName='" + userName + '\'' +
                    ", userPortrait='" + userPortrait + '\'' +
                    ", emptyIdentifier=" + emptyIdentifier +
                    ", content='" + content + '\'' +
                    ", praiseCount=" + praiseCount +
                    ", replyCount=" + replyCount +
                    ", varietyId=" + varietyId +
                    ", varietyType='" + varietyType + '\'' +
                    '}';
        }
    }
}
