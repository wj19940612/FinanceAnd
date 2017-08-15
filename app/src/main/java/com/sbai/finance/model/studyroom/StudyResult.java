package com.sbai.finance.model.studyroom;

import java.util.List;

/**
 * 提交试卷json格式对应格式
 */

public class StudyResult {

    /**
     * answers : [{"answerIds":[{"optionId":892311984186454016}],"topicId":"59802eaa68fad7db045c8982"}]
     * dataId : fdfw324554f
     */

    private String dataId;
    private List<AnswersBean> answers;

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public List<AnswersBean> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswersBean> answers) {
        this.answers = answers;
    }

    public static class AnswersBean {
        /**
         * answerIds : [{"optionId":892311984186454016}]
         * topicId : 59802eaa68fad7db045c8982
         */

        private String topicId;
        private List<AnswerIdsBean> answerIds;

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

        public static class AnswerIdsBean {
            /**
             * optionId : 892311984186454016
             */

            private String optionId;

            public String getOptionId() {
                return optionId;
            }

            public void setOptionId(String optionId) {
                this.optionId = optionId;
            }
        }
    }
}
