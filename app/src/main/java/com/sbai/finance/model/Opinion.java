package com.sbai.finance.model;

import java.util.List;

/**
 * Created by lixiaokuan0819 on 2017/4/24.
 */

public class Opinion {

    /**
     * data : [{"content":"看涨","contractsCode":"CL1706","createTime":1,"direction":1,"guessPass":1,"id":1,"isAttention":1,"praiseCount":10,"replyCount":10,"userId":1,"userName":"yehx","userPortrait":"order/order.png","varietyId":1}]
     * pageSize : 10
     * resultCount : 1
     * start : 0
     * total : 1
     */

    private int pageSize;
    private int resultCount;
    private int start;
    private int total;
    private List<OpinionBean> data;

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

    public List<OpinionBean> getData() {
        return data;
    }

    public void setData(List<OpinionBean> data) {
        this.data = data;
    }

    public static class OpinionBean {
        /**
         * content : 看涨
         * contractsCode : CL1706
         * createTime : 1
         * direction : 1
         * guessPass : 1
         * id : 1
         * isAttention : 1
         * praiseCount : 10
         * replyCount : 10
         * userId : 1
         * userName : yehx
         * userPortrait : order/order.png
         * varietyId : 1
         */

        private String content;
        private String contractsCode;
        private long createTime;
        private int direction;
        private int guessPass;
        private int id;
        private int isAttention;
        private int praiseCount;
        private int replyCount;
        private int userId;
        private String userName;
        private String userPortrait;
        private int varietyId;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getContractsCode() {
            return contractsCode;
        }

        public void setContractsCode(String contractsCode) {
            this.contractsCode = contractsCode;
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

        public int getIsAttention() {
            return isAttention;
        }

        public void setIsAttention(int isAttention) {
            this.isAttention = isAttention;
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
    }
}
