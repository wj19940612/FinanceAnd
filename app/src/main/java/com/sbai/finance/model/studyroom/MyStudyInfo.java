package com.sbai.finance.model.studyroom;

import java.util.List;

/**
 * 我的学一学
 */

public class MyStudyInfo {


    /**
     * answer : [{"answerIds":[{"optionId":892553962484412400}],"answerIdsMap":[],"topicId":"5981274668fadb6d8c3e2fa9"}]
     * holdStudy : 1
     * holdStudyMax : 1
     * lastTime : 2017-08-10 19:23:56
     * learn : 1
     * totalReward : 10
     * totalStudy : 1
     * userId : 198
     */

    private int holdStudy;
    private int holdStudyMax;
    private String lastTime;
    private int learn;
    private int totalReward;
    private int totalStudy;
    private int userId;
    private List<AnswerBean> answer;

    public boolean isLearned() {
        return learn == 1;
    }

    public int getHoldStudy() {
        return holdStudy;
    }

    public void setHoldStudy(int holdStudy) {
        this.holdStudy = holdStudy;
    }

    public int getHoldStudyMax() {
        return holdStudyMax;
    }

    public void setHoldStudyMax(int holdStudyMax) {
        this.holdStudyMax = holdStudyMax;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public int getLearn() {
        return learn;
    }

    public void setLearn(int learn) {
        this.learn = learn;
    }

    public int getTotalReward() {
        return totalReward;
    }

    public void setTotalReward(int totalReward) {
        this.totalReward = totalReward;
    }

    public int getTotalStudy() {
        return totalStudy;
    }

    public void setTotalStudy(int totalStudy) {
        this.totalStudy = totalStudy;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<AnswerBean> getAnswer() {
        return answer;
    }

    public void setAnswer(List<AnswerBean> answer) {
        this.answer = answer;
    }

    public static class AnswerBean {
        /**
         * answerIds : [{"optionId":892553962484412400}]
         * answerIdsMap : []
         * topicId : 5981274668fadb6d8c3e2fa9
         */

        private String topicId;
        private List<AnswerIdsBean> answerIds;
        private List<?> answerIdsMap;

        public String getTopicId() {
            return topicId;
        }

        public void setTopicId(String topicId) {
            this.topicId = topicId;
        }

        public List<AnswerIdsBean> getAnswerIds() {
            return answerIds;
        }

        public void setAnswerIds(List<AnswerIdsBean> answerIds) {
            this.answerIds = answerIds;
        }

        public List<?> getAnswerIdsMap() {
            return answerIdsMap;
        }

        public void setAnswerIdsMap(List<?> answerIdsMap) {
            this.answerIdsMap = answerIdsMap;
        }

        public static class AnswerIdsBean {
            /**
             * optionId : 892553962484412400
             */

            private long optionId;

            public long getOptionId() {
                return optionId;
            }

            public void setOptionId(long optionId) {
                this.optionId = optionId;
            }
        }
    }
}
